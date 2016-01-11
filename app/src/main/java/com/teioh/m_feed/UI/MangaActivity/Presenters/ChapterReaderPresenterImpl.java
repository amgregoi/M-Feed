package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterReaderPresenterImpl implements ChapterReaderPresenter {
    public static final String TAG = ChapterReaderPresenterImpl.class.getSimpleName();
    private static final String NEXT_URL_LIST_PARCELABLE_KEY = TAG + ":NEXT";
    private static final String PREV_URL_LIST_PARCELABLE_KEY = TAG + ":PREV";
    private static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":CURRENT";
    private static final String CHAPTER_LIST_PARCELABLE_KEY = TAG + ":CHAPTER";
    private static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":POSITION";

    private ChapterReaderMapper mChapterReaderMapper;
    private ChapterPageAdapter mChapterPageAdapter;

    private ArrayList<String> mNextUrlList, mCurUrlList, mPrevUrlList;
    private ArrayList<Chapter> mChapterList;
    private int mPosition, mCurChapterPageCount, mPageOffsetCount, mChapterDirection;
    private boolean mToolbarShowing, isInit;

    private Observable<List<String>> mNextChapterObservable, mPrevChapterObservable;

    public ChapterReaderPresenterImpl(ChapterReaderMapper map, Bundle b) {
        mChapterReaderMapper = map;
        mChapterList = b.getParcelableArrayList(ChapterListPresenterImpl.CHAPTER_LIST_KEY);
        mPosition = b.getInt(ChapterListPresenterImpl.LIST_POSITION_KEY);

        isInit = true;
        mToolbarShowing = true;

        Chapter ch = mChapterList.get(mPosition);
        Chapter viewedChapter = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                .query(Chapter.class)
                .withSelection("mTitle = ? AND cNumber = ?", ch.getMangaTitle(), Integer.toString(ch.getChapterNumber()))
                .get();

        if (viewedChapter == null)
            cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(ch);
    }

    @Override
    public void init() {
        if (mCurUrlList == null) this.getImageUrls();
        else updateImageUrlList(mCurUrlList);

        if (mPrevUrlList == null) this.getPrevList();
        if (mNextUrlList == null) this.getNextList();

        mChapterReaderMapper.setupOnSingleTapListener();

    }

    @Override
    public Bundle onSaveState(Bundle bundle) {
        if (mNextUrlList != null) {
            bundle.putStringArrayList(NEXT_URL_LIST_PARCELABLE_KEY, mNextUrlList);
        }
        if (mPrevUrlList != null) {
            bundle.putStringArrayList(PREV_URL_LIST_PARCELABLE_KEY, mPrevUrlList);
        }
        if (mCurUrlList != null) {
            bundle.putStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY, mCurUrlList);
        }
        if (mChapterList != null) {
            bundle.putParcelableArrayList(CHAPTER_LIST_PARCELABLE_KEY, mChapterList);
        }

        bundle.putInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY, mPosition);

        return bundle;
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(NEXT_URL_LIST_PARCELABLE_KEY)) {
            mNextUrlList = bundle.getStringArrayList(NEXT_URL_LIST_PARCELABLE_KEY);
        }
        if (bundle.containsKey(PREV_URL_LIST_PARCELABLE_KEY)) {
            mPrevUrlList = bundle.getStringArrayList(PREV_URL_LIST_PARCELABLE_KEY);
        }
        if (bundle.containsKey(CURRENT_URL_LIST_PARCELABLE_KEY)) {
            mCurUrlList = bundle.getStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY);
        }
        if (bundle.containsKey(CHAPTER_LIST_PARCELABLE_KEY)) {
            mChapterList = bundle.getParcelableArrayList(CHAPTER_LIST_PARCELABLE_KEY);
        }
        mPosition = bundle.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
    }

    @Override
    public void getImageUrls() {
        Observable<List<String>> observableImageUrlList = WebSource.getChapterImageListObservable(mChapterList.get(mPosition).getChapterUrl());
        observableImageUrlList.subscribe(urlList -> updateImageUrlList(urlList));
    }

    @Override
    public void onPause() {
        if (mNextChapterObservable != null) {
            mNextChapterObservable.unsubscribeOn(Schedulers.io());
            mNextChapterObservable = null;
        }
        if (mPrevChapterObservable != null) {
            mPrevChapterObservable.unsubscribeOn(Schedulers.io());
            mPrevChapterObservable = null;
        }
    }

    @Override
    public void updateOffsetCounter(int offset, int position) {
        if (position == 0 || position == mCurChapterPageCount - 1) {
            if (offset == 0) {
                mPageOffsetCount++;
            } else mPageOffsetCount = 0;

            if (position == 0) mChapterDirection = 0;
            else mChapterDirection = 1;
        } else mPageOffsetCount = 0;
    }

    @Override
    public void updateState(int state) {
        if (mPageOffsetCount > 50 && state == 0) {
            if (mChapterDirection == 0) {
                setToPreviousChapter();
            } else if (mChapterDirection == 1) {
                setToNextChapter();
            }
        }
        mPageOffsetCount = 0;
    }

    @Override
    public void toggleToolbar() {
        if (mToolbarShowing) {
            mToolbarShowing = false;
            mChapterReaderMapper.hideToolbar(0);
        } else {
            mToolbarShowing = true;
            mChapterReaderMapper.showToolbar();
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mChapterReaderMapper);
    }

    @Override
    public void setToNextChapter() {
        if (mPosition > 0) {
            mChapterList.get(mPosition).toString();
            if (mNextUrlList != null && mCurUrlList != null) {
                mPosition--;
                mPrevUrlList = new ArrayList<>(mCurUrlList);
                updateImageUrlList(mNextUrlList);
                getNextList();
            }
        }
    }

    @Override
    public void setToPreviousChapter() {
        if (mPrevUrlList != null && mCurUrlList != null) {
            if (mPosition < mChapterList.size() - 1) {
                mPosition++;
                mNextUrlList = new ArrayList<>(mCurUrlList);
                updateImageUrlList(mPrevUrlList);
                getPrevList();
            }
        }
    }

    private void getNextList() {
        mNextUrlList = null;
        if (mPosition > 0) {
            if (mNextChapterObservable != null) {
                mNextChapterObservable.unsubscribeOn(Schedulers.io());
            }
            mNextChapterObservable = WebSource.getChapterImageListObservable(mChapterList.get(mPosition - 1).getChapterUrl());
            mNextChapterObservable.subscribe(urlList -> setNextList(urlList));
        }
    }

    private void setNextList(List<String> urlList) {
        if (urlList != null) {
            mNextUrlList = new ArrayList<>(urlList);
            mNextChapterObservable = null;
        }
    }

    private void getPrevList() {
        mPrevUrlList = null;
        if (mPosition < mChapterList.size() - 1) {
            if (mPrevChapterObservable != null) {
                mPrevChapterObservable.unsubscribeOn(Schedulers.io());
            }
            mPrevChapterObservable = WebSource.getChapterImageListObservable(mChapterList.get(mPosition + 1).getChapterUrl());
            mPrevChapterObservable.subscribe(urlList -> setPrevList(urlList));
        }
    }

    private void setPrevList(List<String> urlList) {
        if (urlList != null) {
            mPrevUrlList = new ArrayList<>(urlList);
            mPrevChapterObservable = null;
        }
    }

    private void updateImageUrlList(List<String> urlList) {
        if (mChapterReaderMapper.getContext() != null) {
            mCurUrlList = new ArrayList<>(urlList);
            mCurChapterPageCount = mCurUrlList.size();
            mChapterPageAdapter = new ChapterPageAdapter(mChapterReaderMapper.getContext(), mCurUrlList);
            mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
            mChapterReaderMapper.setupToolbar(mChapterList.get(mPosition).toString(), mCurUrlList.size());
            mToolbarShowing = false;

            if (isInit) {
                mChapterReaderMapper.hideToolbar(40);
                isInit = false;
            }

        }
    }

}
