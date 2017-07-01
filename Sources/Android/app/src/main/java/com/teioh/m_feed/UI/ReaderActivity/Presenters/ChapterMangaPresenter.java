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

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChapterMangaPresenter implements IReader.MangaFragmentPresenter
{
    public static final String TAG = ChapterMangaPresenter.class.getSimpleName();
    public static final String CHAPTER_PARENT_FOLLOWING = TAG + ":CHAPTER_PARENT_FOLLOWING";
    public static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":POSITION";
    public static final String CHAPTER = TAG + ":CHAPTER";
    private static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":CURRENT";
    private static final String LOADING_STATUS = TAG + ":LOADING";
    private static final String IMAGE_SUB_FLAG = TAG + ":IMAGE_SUB_FLAG";

    private boolean mLazyLoading;
    private boolean mImageSubFlag = false;

    private Subscription mImageListSubscription, mLoadImageUrlSubscription;
    private ImagePageAdapter mChapterPageAdapter;

    private boolean mIsToolbarShowing, mChapterParentFollowing;
    private int mPosition;
    private ArrayList<String> mContentUrlList;
    private Chapter mChapter;
    private MangaEnums.eLoadingStatus mLoadingStatus;

    private IReader.MangaFragmentView mChapterReaderMapper;

    /***
     * This is the constructor for the Chapter Presenter.
     *
     * @param aMap
     * @param aBundle
     */
    public ChapterMangaPresenter(IReader.MangaFragmentView aMap, Bundle aBundle)
    {
        mChapterReaderMapper = aMap;
        mPosition = aBundle.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
        mChapter = aBundle.getParcelable(Chapter.TAG + ":" + mPosition);
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
        try
        {
            mLazyLoading = true;

            if (!mImageSubFlag) this.getImageUrls();
            else refreshContent();

            mChapterReaderMapper.setUserGestureListener();
            mChapterReaderMapper.setCurrentChapterPage(0, mPosition);
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
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
        try
        {
            if (mContentUrlList != null)
            {
                aSave.putStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY, mContentUrlList);
            }
            if (mChapter != null)
            {
                aSave.putParcelable(CHAPTER, mChapter);
            }
            aSave.putInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY, mPosition);
            aSave.putInt(LOADING_STATUS, MangaEnums.eLoadingStatus.getLoadingStatus(mLoadingStatus));
            aSave.putBoolean(IMAGE_SUB_FLAG, mImageSubFlag);

            mImageListSubscription.unsubscribe();

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
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
        try
        {
            if (aRestore.containsKey(CURRENT_URL_LIST_PARCELABLE_KEY))
            {
                mContentUrlList = aRestore.getStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY);
            }
            if (aRestore.containsKey(CHAPTER))
            {
                mChapter = aRestore.getParcelable(CHAPTER);
            }
            if (aRestore.containsKey(CHAPTER_POSITION_LIST_PARCELABLE_KEY))
            {
                mPosition = aRestore.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
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
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    public void onPause()
    {
        try
        {
            MangaDB.getInstance().updateChapter(mChapter);
            Glide.get(mChapterReaderMapper.getContext()).clearMemory();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
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
        try
        {
            mChapter.setCurrentPage(aPosition);
            mChapterReaderMapper.updateCurrentPage(aPosition + 1); //update page by 1
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function cleans up all used subscribers.
     */
    private void cleanupSubscribers()
    {
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
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function updates the header.
     */
    @Override
    public void updateReaderToolbar()
    {
        try
        {
            switch (mLoadingStatus)
            {
                case COMPLETE:
                    mChapterReaderMapper
                            .updateToolbar(mChapter.getMangaTitle(), mChapter.getChapterTitle(), mContentUrlList.size(), mChapter
                                    .getCurrentPage() + 1, mPosition);
                    break;
                case LOADING:
                    mChapterReaderMapper
                            .updateToolbar(mChapter.getMangaTitle(), "Pages loaded: " + mContentUrlList.size(), 1, 1, mPosition);
                    break;
                case ERROR:
                    mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), "Failed to load chapter, refresh", 1, 1, mPosition);
                    break;
                case REFRESH:
                    mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), "Starting refresh..", 1, 1, mPosition);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    public void onResume()
    {
        //do nothing for now..
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
     * This function toggles the header and footers of the reader.
     */
    @Override
    public void toggleToolbar()
    {
        try
        {
            mChapterReaderMapper.toggleToolbar();

        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function sets the active chapter.
     */
    @Override
    public void updateActiveChapter()
    {
        try
        {
            updateReaderToolbar();

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function performs the chapter refresh.
     * @param aPosition
     */
    @Override
    public void onRefresh(int aPosition)
    {
        try
        {
            cleanupSubscribers();
            refreshContent();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function updates the current chapters status to viewed.
     */
    @Override
    public void updateChapterViewStatus()
    {
        try
        {
            Chapter lViewedChapter = MangaDB.getInstance().getChapter(mChapter.getChapterUrl());

            if (mChapterParentFollowing && lViewedChapter == null)
            {
                MangaDB.getInstance().addChapter(mChapter);
            }

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function updates the image url list.
     *
     */
    private void refreshContent()
    {
        try
        {
            if (mChapterReaderMapper != null && mChapterReaderMapper.getContext() != null)
            {
                mImageSubFlag = false; //resets flag to allow chapter to refresh
                mLoadingStatus = MangaEnums.eLoadingStatus.REFRESH;

                updateReaderToolbar();
                mContentUrlList = new ArrayList<>();
                mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mContentUrlList);
                mChapterReaderMapper.registerAdapter(mChapterPageAdapter, MangaEnums.eSourceType.MANGA);
                updateCurrentPage(mChapter.getCurrentPage());
                getImageUrls();

            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function retrieves the image urls of the current chapter.
     */
    private void getImageUrls()
    {
        try
        {
            if (mContentUrlList == null) mContentUrlList = new ArrayList<>();
            updateReaderToolbar();

            mImageListSubscription = SourceFactory.getInstance().getSource().getChapterImageListObservable(new RequestWrapper(mChapter))
                                                  .cache()
                                                  .subscribeOn(Schedulers.io())
                                                  .observeOn(AndroidSchedulers.mainThread())
                                                  .subscribe(new Observer<String>()
                                                  {

                                                      @Override
                                                      public void onCompleted()
                                                      {
                                                          preLoadImagesToCache();
                                                          mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper
                                                                                                             .getContext(), mContentUrlList);
                                                          mChapterReaderMapper
                                                                  .registerAdapter(mChapterPageAdapter, MangaEnums.eSourceType.MANGA);
                                                          mChapter.setTotalPages(mContentUrlList.size());
                                                          mLoadingStatus = MangaEnums.eLoadingStatus.COMPLETE;
                                                          MangaLogger.logInfo(TAG, "Completed image url retrieval");
                                                          updateReaderToolbar();
                                                          mChapterReaderMapper
                                                                  .setCurrentChapterPage(mChapter.getCurrentPage(), mPosition);

                                                          mImageSubFlag = true;

                                                          mChapterReaderMapper.startToolbarTimer();

                                                      }

                                                      @Override
                                                      public void onError(Throwable aThrowable)
                                                      {
                                                          mLoadingStatus = MangaEnums.eLoadingStatus.ERROR;
                                                          MangaLogger.logError(TAG, aThrowable.getMessage());
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
                                                              if (!mContentUrlList.contains(imageUrl))
                                                                  mContentUrlList.add(imageUrl);
                                                              updateReaderToolbar();
                                                          }
                                                      }
                                                  });
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function pre loads the images of the chapter to the cache for quick loading.
     */
    private void preLoadImagesToCache()
    {
        try
        {
            if (mLazyLoading)
            {
                if (mLoadImageUrlSubscription != null)
                {
                    mLoadImageUrlSubscription.unsubscribe();
                    mLoadImageUrlSubscription = null;
                }

                if (mContentUrlList != null)
                {
                    mLoadImageUrlSubscription = SourceFactory.getInstance().getSource()
                                                             .cacheFromImagesOfSize(mContentUrlList)
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
                                                                     MangaLogger.logError(TAG, aThrowable.getMessage());
                                                                 }

                                                                 @Override
                                                                 public void onNext(Drawable glideDrawable)
                                                                 {
                                                                     //do nothing
                                                                 }
                                                             });
                }
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }
}
