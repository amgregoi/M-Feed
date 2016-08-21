package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.teioh.m_feed.BuildConfig;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.Utils.MFDBHelper;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterPresenter implements IReader.FragmentPresenter {
    public static final String TAG = ChapterPresenter.class.getSimpleName();
    public static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":CURRENT";
    public static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":POSITION";

    private IReader.FragmentView mChapterReaderMapper;

    private ArrayList<String> mChapterUrlList;
    private int mPosition, mPageOffsetCount;
    private boolean mIsToolbarShowing, mIsForwardChapter, mIsLazyLoading;
    private Chapter mChapter;
    private Subscription mImageListSubscription, mLoadImageUrlSubscription;
    private ImagePageAdapter mChapterPageAdapter;


    public ChapterPresenter(IReader.FragmentView aMap, Bundle aBundle) {
        mChapterReaderMapper = aMap;

        mPosition = aBundle.getInt(ChapterPageAdapter.POSITION_KEY);
        mChapter = aBundle.getParcelable(Chapter.TAG + ":" + mPosition);
        mIsToolbarShowing = true;
    }

    @Override
    public void init(Bundle aBundle) {
        mIsLazyLoading = true;

        if (mChapterUrlList == null) this.getImageUrls();
        else updateImageUrlList(mChapterUrlList);

        mPageOffsetCount = 0;
        mChapterReaderMapper.setupOnSingleTapListener();
    }

    @Override
    public void onSaveState(Bundle aSave) {
        if (mChapterUrlList != null) {
            aSave.putStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY, mChapterUrlList);
        }
        aSave.putInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY, mPosition);
    }

    @Override
    public void onRestoreState(Bundle aRestore) {
        if (aRestore.containsKey(CURRENT_URL_LIST_PARCELABLE_KEY)) {
            mChapterUrlList = aRestore.getStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY);
        }

        mPosition = aRestore.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
    }

    @Override
    public void getImageUrls() {
        mChapterUrlList = new ArrayList<>();
        updateToolbarLoading();

        mImageListSubscription = new SourceFactory().getSource().getChapterImageListObservable(new RequestWrapper(mChapter))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        preLoadImagesToCache();
                        mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mChapterUrlList);
                        mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
                        mChapter.setTotalPages(mChapterUrlList.size());
                        updateToolbarComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        updateToolbarFailed();
                        Toast.makeText(mChapterReaderMapper.getContext(), "Failed, please try again.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(String imageUrl) {
                        if (imageUrl != null) {
                            mChapterUrlList.add(imageUrl);
                            updateToolbarLoading();
                        }
                    }
                });
    }

    private void preLoadImagesToCache() {
        if (!mIsLazyLoading) {
            if (mLoadImageUrlSubscription != null) {
                mLoadImageUrlSubscription.unsubscribe();
                mLoadImageUrlSubscription = null;
            }

            if (mChapterUrlList != null) {
                mLoadImageUrlSubscription = new SourceFactory().getSource()
                        .cacheFromImagesOfSize(mChapterUrlList)
                        .subscribe(new Observer<GlideDrawable>() {
                            @Override
                            public void onCompleted() {
                                mLoadImageUrlSubscription.unsubscribe();
                                mLoadImageUrlSubscription = null;
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (BuildConfig.DEBUG) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNext(GlideDrawable glideDrawable) {
                            }
                        });
            }
        }
    }

    @Override
    public void onPause() {
        Glide.get(mChapterReaderMapper.getContext()).clearMemory();
    }

    @Override
    public void onResume() {
//        updateToolbarComplete();
//        updateCurrentPage(mChapter.getCurrentPage());
    }

    @Override
    public void onDestroy() {
        if (mImageListSubscription != null) {
            mImageListSubscription.unsubscribe();
            mImageListSubscription = null;
        }

        if (mLoadImageUrlSubscription != null) {
            mLoadImageUrlSubscription.unsubscribe();
            mLoadImageUrlSubscription = null;
        }

        mChapterReaderMapper = null;
    }

    @Override
    public void toggleToolbar() {
        if (mIsToolbarShowing) {
            mIsToolbarShowing = false;
            mChapterReaderMapper.hideToolbar(0);
        } else {
            mIsToolbarShowing = true;
            mChapterReaderMapper.showToolbar();
        }
    }

    @Override
    public void setToNextChapter() {
        mChapterReaderMapper.incrementChapter();
    }

    @Override
    public void setToPreviousChapter() {
        mChapterReaderMapper.decrementChapter();
    }

    @Override
    public void updateOffsetCounter(int aOffset, int aPosition) {
        if (aPosition == 0 || aPosition == mChapterUrlList.size() - 1) {
            mPageOffsetCount++;
            mIsForwardChapter = aPosition != 0;
        } else mPageOffsetCount = 0;
    }

    @Override
    public void updateState(int aState) {
        if (mPageOffsetCount > 40 && aState == 0) {
            if (mIsForwardChapter) setToNextChapter();
            else setToPreviousChapter();
        }
        mPageOffsetCount = 0;
    }

    @Override
    public void updateToolbarComplete() {
        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), mChapter.getChapterTitle(), mChapterUrlList.size(), mPosition);
    }

    private void updateToolbarFailed() {
        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), "Failed to load chapter, refresh", 1, mPosition);
    }

    private void updateToolbarLoading() {
        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), "Pages loaded: " + mChapterUrlList.size(), 1, mPosition);
    }

    @Override
    public void updateCurrentPage(int aPosition) {
        mPosition = aPosition;
        mChapterReaderMapper.updateCurrentPage(aPosition + 1); //update page by 1
        mChapter.setCurrentPage(aPosition);
    }

    @Override
    public void updateChapterViewStatus() {
        Chapter viewedChapter = cupboard().withDatabase(MFDBHelper.getInstance().getReadableDatabase())
                .query(Chapter.class)
                .withSelection("mangaTitle = ? AND chapterNumber = ?", mChapter.getMangaTitle(), Integer.toString(mChapter.getChapterNumber()))
                .get();

        if (viewedChapter == null)
            cupboard().withDatabase(MFDBHelper.getInstance().getWritableDatabase()).put(mChapter);
    }

    @Override
    public void onRefresh(int aPosition) {
        mChapterPageAdapter.refreshView(aPosition);
    }

    private void updateImageUrlList(List<String> aUrlList) {
        if (mChapterReaderMapper.getContext() != null) {
            updateToolbarComplete();
            mChapterUrlList = new ArrayList<>(aUrlList);
            mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mChapterUrlList);
            mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
            mChapterReaderMapper.setCurrentChapterPage(mChapter.getCurrentPage());
        }
    }
}
