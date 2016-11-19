package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.teioh.m_feed.BuildConfig;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.Utils.MFDBHelper;
import com.teioh.m_feed.Utils.MangaLogger;
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
    private MangaEnums.eLoadingStatus mLoadingStatus;


    /***
     * TODO..
     *
     * @param aMap
     * @param aBundle
     */
    public ChapterPresenter(IReader.FragmentView aMap, Bundle aBundle)
    {
        mChapterReaderMapper = aMap;
        mPosition = aBundle.getInt(ChapterPageAdapter.POSITION_KEY);
        mChapter = aBundle.getParcelable(Chapter.TAG + ":" + mPosition);
        mActiveChapter = mChapterReaderMapper.checkActiveChapter(mPosition);
        mIsToolbarShowing = true;
        mLoadingStatus = MangaEnums.eLoadingStatus.LOADING;
    }

    /***
     * TODO..
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

            if (mChapterUrlList == null) this.getImageUrls();
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
     * TODO..
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

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * TODO..
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
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * TODO..
     */
    @Override
    public void getImageUrls()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mChapterUrlList = new ArrayList<>();
            updateReaderToolbar();

            mImageListSubscription = new SourceFactory().getSource().getChapterImageListObservable(new RequestWrapper(mChapter)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>()
            {
                /***
                 * TODO..
                 */
                @Override
                public void onCompleted()
                {
                    preLoadImagesToCache();
                    mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mChapterUrlList);
                    mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
                    mChapter.setTotalPages(mChapterUrlList.size());
                    mLoadingStatus = MangaEnums.eLoadingStatus.COMPLETE;
                    MangaLogger.logInfo(TAG, lMethod, "Completed image url retrieval");
                    updateReaderToolbar();

                }

                /***
                 * TODO..
                 * @param aThrowable
                 */
                @Override
                public void onError(Throwable aThrowable)
                {
                    mLoadingStatus = MangaEnums.eLoadingStatus.ERROR;
                    MangaLogger.logError(TAG, lMethod, aThrowable.getMessage());
                    updateReaderToolbar();
                    Toast.makeText(mChapterReaderMapper.getContext(), "Failed, please try refreshing.", Toast.LENGTH_SHORT).show();
                }

                /***
                 * TODO..
                 * @param imageUrl
                 */
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
     * TODO..
     */
    private void preLoadImagesToCache()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
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
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * TODO..
     */
    @Override
    public void onPause()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            Glide.get(mChapterReaderMapper.getContext()).clearMemory();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO..
     */
    @Override
    public void onResume()
    {

    }

    /***
     * TODO..
     */
    @Override
    public void onDestroy()
    {
        cleanupSubscribers();
        mChapterReaderMapper = null;
    }

    /***
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO..
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
     * TODO..
     */
    @Override
    public void updateChapterViewStatus()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            Chapter viewedChapter = cupboard().withDatabase(MFDBHelper.getInstance().getReadableDatabase()).query(Chapter.class).withSelection("mangaTitle = ? AND chapterNumber = ?", mChapter.getMangaTitle(), Integer.toString(mChapter.getChapterNumber())).get();
            if (viewedChapter == null) cupboard().withDatabase(MFDBHelper.getInstance().getWritableDatabase()).put(mChapter);

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    @Override
    public void onRefresh(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            cleanupSubscribers();

            if (mChapterPageAdapter != null && !mChapterUrlList.isEmpty() && mChapterUrlList != null)
                mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
            else getImageUrls();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO..
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
     * TODO..
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
