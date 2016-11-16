package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.teioh.m_feed.BuildConfig;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.UI.ReaderActivity.ReaderEnum;
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

public class ChapterPresenter implements IReader.FragmentPresenter
{
    public static final String TAG = ChapterPresenter.class.getSimpleName();
    public static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":CURRENT";
    public static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":POSITION";
    public static final String ACTIVE_CHAPTER = TAG + ":ACTIVE_CHAPTER";
    public static final String CHAPTER = TAG + ":CHAPTER";
    public static final String LOADING_STATUS = TAG + ":LOADING";

    private IReader.FragmentView mChapterReaderMapper;

    private ArrayList<String> mChapterUrlList;
    private int mPosition, mPageOffsetCount;
    private boolean mIsToolbarShowing, mIsForwardChapter, mLazyLoading;
    private Chapter mChapter;
    private Subscription mImageListSubscription, mLoadImageUrlSubscription;
    private ImagePageAdapter mChapterPageAdapter;

    private boolean mActiveChapter;
    private ReaderEnum.LoadingStatus mLoadingStatus;


    public ChapterPresenter(IReader.FragmentView aMap, Bundle aBundle)
    {
        mChapterReaderMapper = aMap;
        mPosition = aBundle.getInt(ChapterPageAdapter.POSITION_KEY);
        mChapter = aBundle.getParcelable(Chapter.TAG + ":" + mPosition);
        mActiveChapter = mChapterReaderMapper.checkActiveChapter(mPosition);
        mIsToolbarShowing = true;
        mLoadingStatus = ReaderEnum.LoadingStatus.LOADING;
    }

    @Override
    public void init(Bundle aBundle)
    {
        try
        {
            mLazyLoading = true;

            if (mChapterUrlList == null) this.getImageUrls();
            else updateImageUrlList(mChapterUrlList);

            mPageOffsetCount = 0;
            mChapterReaderMapper.setupOnSingleTapListener();
            mChapterReaderMapper.setCurrentChapterPage(mPosition);
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }

    @Override
    public void onSaveState(Bundle aSave)
    {
        if (mChapterUrlList != null)
        {
            aSave.putStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY, mChapterUrlList);
        }
        if (mChapter != null)
        {
            aSave.putParcelable(CHAPTER, mChapter);
        }
        aSave.putInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY, mPosition);
        aSave.putBoolean(ACTIVE_CHAPTER, mActiveChapter);
        aSave.putInt(LOADING_STATUS, ReaderEnum.LoadingStatus.getLoadingStatu(mLoadingStatus));
    }

    @Override
    public void onRestoreState(Bundle aRestore)
    {
        if (aRestore.containsKey(CURRENT_URL_LIST_PARCELABLE_KEY))
        {
            mChapterUrlList = aRestore.getStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY);
        }
        if (aRestore.containsKey(CHAPTER))
        {
            mChapter = aRestore.getParcelable(CHAPTER);
        }
        if (aRestore.containsKey(CHAPTER_POSITION_LIST_PARCELABLE_KEY))
        {
            mPosition = aRestore.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
        }
        if (aRestore.containsKey(ACTIVE_CHAPTER))
        {
            mActiveChapter = aRestore.getBoolean(ACTIVE_CHAPTER);
        }
        if (aRestore.containsKey(LOADING_STATUS))
        {
            mLoadingStatus = ReaderEnum.LoadingStatus.getLoadingStatus(aRestore.getInt(LOADING_STATUS));
        }
    }

    @Override
    public void getImageUrls()
    {
        mChapterUrlList = new ArrayList<>();
        updateReaderToolbar();

        mImageListSubscription = new SourceFactory().getSource().getChapterImageListObservable(new RequestWrapper(mChapter)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>()
        {
            @Override
            public void onCompleted()
            {
                preLoadImagesToCache();
                mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mChapterUrlList);
                mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
                mChapter.setTotalPages(mChapterUrlList.size());
                mLoadingStatus = ReaderEnum.LoadingStatus.COMPLETE;
                Log.e(TAG, "completed successfully: " + mChapter.getChapterTitle());
                updateReaderToolbar();

            }

            @Override
            public void onError(Throwable e)
            {
                mLoadingStatus = ReaderEnum.LoadingStatus.ERROR;
                Log.e(TAG, "error while getting images");
                updateReaderToolbar();
                Toast.makeText(mChapterReaderMapper.getContext(), "Failed, please try refreshing.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String imageUrl)
            {
                if (imageUrl != null)
                {
                    mLoadingStatus = ReaderEnum.LoadingStatus.LOADING;
                    mChapterUrlList.add(imageUrl);
                    updateReaderToolbar();
                }
            }
        });
    }

    private void preLoadImagesToCache()
    {
        if (!mLazyLoading)
        {
            if (mLoadImageUrlSubscription != null)
            {
                mLoadImageUrlSubscription.unsubscribe();
                mLoadImageUrlSubscription = null;
            }

            if (mChapterUrlList != null)
            {
                mLoadImageUrlSubscription = new SourceFactory().getSource().cacheFromImagesOfSize(mChapterUrlList).subscribe(new Observer<GlideDrawable>()
                {
                    @Override
                    public void onCompleted()
                    {
                        mLoadImageUrlSubscription.unsubscribe();
                        mLoadImageUrlSubscription = null;
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        if (BuildConfig.DEBUG)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(GlideDrawable glideDrawable)
                    {
                    }
                });
            }
        }
    }

    @Override
    public void onPause()
    {
        try
        {
            Glide.get(mChapterReaderMapper.getContext()).clearMemory();
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }

    @Override
    public void onResume()
    {
//        updateReaderToolbar();
//        updateCurrentPage(mChapter.getCurrentPage());
    }

    @Override
    public void onDestroy()
    {
        cleanupSubscribers();
        mChapterReaderMapper = null;
    }

    @Override
    public void toggleToolbar()
    {
        try
        {
            if (mIsToolbarShowing)
            {
                mIsToolbarShowing = false;
                mChapterReaderMapper.hideToolbar(0);
            }
            else
            {
                mIsToolbarShowing = true;
                mChapterReaderMapper.showToolbar();
            }
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }

    @Override
    public void setToNextChapter()
    {
        try
        {
            mChapterReaderMapper.incrementChapter();
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }

    @Override
    public void setToPreviousChapter()
    {
        try
        {
            mChapterReaderMapper.decrementChapter();
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }

    @Override
    public void updateOffsetCounter(int aOffset, int aPosition)
    {
        if (aPosition == 0 || aPosition == mChapterUrlList.size() - 1)
        {
            mPageOffsetCount++;
            mIsForwardChapter = aPosition != 0;
        }
        else mPageOffsetCount = 0;
    }

    @Override
    public void updateState(int aState)
    {
        if (mPageOffsetCount > 40 && aState == 0)
        {
            if (mIsForwardChapter) setToNextChapter();
            else setToPreviousChapter();
        }
        mPageOffsetCount = 0;
    }

    @Override
    public void updateActiveChapter()
    {
        mActiveChapter = true;
        updateReaderToolbar();
    }

    @Override
    public void updateReaderToolbar()
    {
        try
        {
            if (mActiveChapter)
            {
                switch (mLoadingStatus)
                {
                    case COMPLETE:
                        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), mChapter.getChapterTitle(), mChapterUrlList.size(), mPosition);
                        break;
                    case LOADING:
                        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), "Pages loaded: " + mChapterUrlList.size(), 1, mPosition);
                        break;
                    case ERROR:
                        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), "Failed to load chapter, refresh", 1, mPosition);
                        break;
                }
            }
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }

    @Override
    public void updateCurrentPage(int aPosition)
    {
        try
        {
            mPosition = aPosition;
            mChapterReaderMapper.updateCurrentPage(aPosition + 1); //update page by 1
            mChapter.setCurrentPage(aPosition);
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }

    @Override
    public void updateChapterViewStatus()
    {
        Chapter viewedChapter = cupboard().withDatabase(MFDBHelper.getInstance().getReadableDatabase()).query(Chapter.class).withSelection("mangaTitle = ? AND chapterNumber = ?", mChapter.getMangaTitle(), Integer.toString(mChapter.getChapterNumber())).get();
        if (viewedChapter == null) cupboard().withDatabase(MFDBHelper.getInstance().getWritableDatabase()).put(mChapter);
//        updateReaderToolbar();
    }

    @Override
    public void onRefresh(int aPosition)
    {
        try
        {
            cleanupSubscribers();

            if (mChapterPageAdapter != null && !mChapterUrlList.isEmpty() && mChapterUrlList != null)
                mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
            else getImageUrls();
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }

    private void cleanupSubscribers()
    {
        if (mImageListSubscription != null)
        {
            mImageListSubscription.unsubscribe();
            mImageListSubscription = null;
        }

        if (mLoadImageUrlSubscription != null)
        {
            mLoadImageUrlSubscription.unsubscribe();
            mLoadImageUrlSubscription = null;
        }
    }

    private void updateImageUrlList(List<String> aUrlList)
    {
        try
        {
            if (mChapterReaderMapper != null && mChapterReaderMapper.getContext() != null)
            {
                updateReaderToolbar();
                mChapterUrlList = new ArrayList<>(aUrlList);
                mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mChapterUrlList);
                mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
                mChapterReaderMapper.setCurrentChapterPage(mChapter.getCurrentPage());
                updateCurrentPage(mChapter.getCurrentPage());
            }
        }
        catch (Exception aException)
        {
            Log.e(TAG, aException.getMessage());
        }
    }
}
