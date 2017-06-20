package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChapterPresenter implements IReader.FragmentPresenter
{
    public static final String TAG = ChapterPresenter.class.getSimpleName();
    public static final String CHAPTER_PARENT_FOLLOWING = TAG + ":CHAPTER_PARENT_FOLLOWING";

    private static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":CURRENT";
    public static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":POSITION";
    private static final String ACTIVE_CHAPTER = TAG + ":ACTIVE_CHAPTER";
    public static final String CHAPTER = TAG + ":CHAPTER";
    private static final String LOADING_STATUS = TAG + ":LOADING";
    private static final String IMAGE_SUB_FLAG = TAG + ":IMAGE_SUB_FLAG";

    private IReader.FragmentView mChapterReaderMapper;

    private ArrayList<String> mChapterUrlList;
    private int mPosition, mPageOffsetCount;
    private boolean mIsToolbarShowing, mIsForwardChapter, mLazyLoading, mChapterParentFollowing;
    private boolean mActiveChapter, mImageSubFlag = false;
    private Chapter mChapter;


    private Observable<String> mImageRequest;
    private Subscription mImageListSubscription, mLoadImageUrlSubscription;
    private ImagePageAdapter mChapterPageAdapter;
    private MangaEnums.eLoadingStatus mLoadingStatus;


    /***
     * This is the constructor for the Chapter Presenter.
     *
     * @param aMap
     * @param aBundle
     */
    public ChapterPresenter(IReader.FragmentView aMap, Bundle aBundle)
    {
        mChapterReaderMapper = aMap;
        mPosition = aBundle.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
        mChapter = aBundle.getParcelable(Chapter.TAG + ":" + mPosition);
        mActiveChapter = mChapterReaderMapper.checkActiveChapter(mPosition);
        mChapterParentFollowing = aBundle.getBoolean(CHAPTER_PARENT_FOLLOWING, false);
        mIsToolbarShowing = true;
        mLoadingStatus = MangaEnums.eLoadingStatus.LOADING;
    }

    /***
     * This function initializes the chapter presenter.
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mLazyLoading = true;

            if (!mImageSubFlag) this.getImageUrls();
            else updateImageUrlList(mChapterUrlList);

            mPageOffsetCount = 0;
            mChapterReaderMapper.setupOnSingleTapListener();
            mChapterReaderMapper.setCurrentChapterPage(mPosition);

        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aSave
     */
    @Override
    public void onSaveState(Bundle aSave)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
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
            aSave.putInt(LOADING_STATUS, MangaEnums.eLoadingStatus.getLoadingStatu(mLoadingStatus));
            aSave.putBoolean(IMAGE_SUB_FLAG, mImageSubFlag);

            mImageListSubscription.unsubscribe();

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * This function restores data that needed to persist between device state changes.
     *
     * @param aRestore
     */
    @Override
    public void onRestoreState(Bundle aRestore)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
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
                mLoadingStatus = MangaEnums.eLoadingStatus.getLoadingStatus(aRestore.getInt(LOADING_STATUS));
            }
            if (aRestore.containsKey(IMAGE_SUB_FLAG))
            {
                mImageSubFlag = aRestore.getBoolean(IMAGE_SUB_FLAG);
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    public void onPause()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        //TODO.. Might be moving away from cupboard in the future

        try
        {
            MangaDB.getInstance().updateChapter(mChapter);
            Glide.get(mChapterReaderMapper.getContext()).clearMemory();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    public void onResume()
    {
        //DO STUFF..
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    public void onDestroy()
    {
        cleanupSubscribers();
        mChapterReaderMapper = null;
    }

    /***
     * This function retrieves the image urls of the current chapter.
     */
    @Override
    public void getImageUrls()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mChapterUrlList == null) mChapterUrlList = new ArrayList<>();
            updateReaderToolbar();

            mImageListSubscription = new SourceFactory().getSource().getChapterImageListObservable(new RequestWrapper(mChapter)).cache()
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new Observer<String>()
                                                        {

                                                            @Override
                                                            public void onCompleted()
                                                            {
                                                                preLoadImagesToCache();
                                                                mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper
                                                                                                                   .getContext(), mChapterUrlList);
                                                                mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
                                                                mChapter.setTotalPages(mChapterUrlList.size());
                                                                mLoadingStatus = MangaEnums.eLoadingStatus.COMPLETE;
                                                                MangaLogger.logInfo(TAG, lMethod, "Completed image url retrieval");
                                                                updateReaderToolbar();
                                                                mChapterReaderMapper.setCurrentChapterPage(mChapter.getCurrentPage());

                                                                mImageSubFlag = true;

                                                            }

                                                            @Override
                                                            public void onError(Throwable aThrowable)
                                                            {
                                                                mLoadingStatus = MangaEnums.eLoadingStatus.ERROR;
                                                                MangaLogger.logError(TAG, lMethod, aThrowable.getMessage());
                                                                updateReaderToolbar();
                                                                Toast.makeText(mChapterReaderMapper
                                                                                       .getContext(), "Failed, please try refreshing.", Toast.LENGTH_SHORT)
                                                                     .show();
                                                            }

                                                            @Override
                                                            public void onNext(String imageUrl)
                                                            {
                                                                if (imageUrl != null)
                                                                {
                                                                    mLoadingStatus = MangaEnums.eLoadingStatus.LOADING;
                                                                    mChapterUrlList.add(imageUrl);
                                                                    updateReaderToolbar();
                                                                }
                                                            }
                                                        });
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * This function toggles the header and footers of the reader.
     */
    @Override
    public void toggleToolbar()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function increments the current chapter.
     */
    @Override
    public void setToNextChapter()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mChapterReaderMapper.incrementChapter();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function decrements the current chapter.
     */
    @Override
    public void setToPreviousChapter()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mChapterReaderMapper.decrementChapter();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function updates the drag offset.
     * This is used to go to the next or previous chapter.
     *
     * @param aOffset
     * @param aPosition
     */
    @Override
    public void updateOffsetCounter(int aOffset, int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (aPosition == 0 || aPosition == mChapterUrlList.size() - 1)
            {
                mPageOffsetCount++;
                mIsForwardChapter = aPosition != 0;
            }
            else mPageOffsetCount = 0;

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * This function updates the state of the drag.
     *
     * @param aState
     */
    @Override
    public void updateState(int aState)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mPageOffsetCount > 40 && aState == 0)
            {
                if (mIsForwardChapter) setToNextChapter();
                else setToPreviousChapter();
            }
            mPageOffsetCount = 0;

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * This function updates the header.
     */
    @Override
    public void updateReaderToolbar()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        try
        {
            if (mActiveChapter)
            {
                switch (mLoadingStatus)
                {
                    case COMPLETE:
                        mChapterReaderMapper
                                .updateToolbar(mChapter.getMangaTitle(), mChapter.getChapterTitle(), mChapterUrlList.size(), mPosition);
                        break;
                    case LOADING:
                        mChapterReaderMapper
                                .updateToolbar(mChapter.getMangaTitle(), "Pages loaded: " + mChapterUrlList.size(), 1, mPosition);
                        break;
                    case ERROR:
                        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), "Failed to load chapter, refresh", 1, mPosition);
                        break;
                    case REFRESH:
                        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), "Starting refresh..", 1, mPosition);
                }
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function sets the current page of the chapter.
     *
     * @param aPosition
     */
    @Override
    public void updateCurrentPage(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mPosition = aPosition;
            mChapterReaderMapper.updateCurrentPage(aPosition + 1); //update page by 1
            mChapter.setCurrentPage(aPosition);
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function sets the active chapter.
     */
    @Override
    public void updateActiveChapter()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mActiveChapter = true;
            updateReaderToolbar();

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * This function updates the current chapters view status.
     */
    @Override
    public void updateChapterViewStatus()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            Chapter viewedChapter = MangaDB.getInstance().getChapter(mChapter.getChapterUrl());

            if (mChapterParentFollowing && viewedChapter == null)
            {
                MangaDB.getInstance().addChapter(mChapter);
            }

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * This function performs the chapter refresh.
     * @param aPosition
     */
    @Override
    public void onRefresh(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mImageSubFlag = false; //resets flag to allow chapter to refresh

            cleanupSubscribers();
            mChapterUrlList = new ArrayList<>();
            updateImageUrlList(mChapterUrlList);
            mLoadingStatus = MangaEnums.eLoadingStatus.REFRESH;
            getImageUrls();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * This function pre loads the images of the chapter to the cache for quick loading.
     */
    private void preLoadImagesToCache()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mLazyLoading)
            {
                if (mLoadImageUrlSubscription != null)
                {
                    mLoadImageUrlSubscription.unsubscribe();
                    mLoadImageUrlSubscription = null;
                }

                if (mChapterUrlList != null)
                {
                    mLoadImageUrlSubscription = new SourceFactory().getSource()
                                                                   .cacheFromImagesOfSize(mChapterUrlList)
                                                                   .subscribe(new Observer<Drawable>()
                                                                   {
                                                                       @Override
                                                                       public void onCompleted()
                                                                       {
                                                                           mLoadImageUrlSubscription.unsubscribe();
                                                                           mLoadImageUrlSubscription = null;
                                                                       }

                                                                       @Override
                                                                       public void onError(Throwable aThrowable)
                                                                       {
                                                                           MangaLogger.logError(TAG, lMethod, aThrowable.getMessage());
                                                                       }

                                                                       @Override
                                                                       public void onNext(Drawable glideDrawable)
                                                                       {
                                                                       }
                                                                   });
                }
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * This function cleans up all used subscribers.
     */
    private void cleanupSubscribers()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
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
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * This function updates the image url list.
     *
     * @param aUrlList
     */
    private void updateImageUrlList(List<String> aUrlList)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }
}
