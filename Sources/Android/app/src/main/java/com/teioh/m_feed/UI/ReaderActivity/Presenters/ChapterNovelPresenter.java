package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterMangaPresenter.CHAPTER_PARENT_FOLLOWING;
import static com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterMangaPresenter.CHAPTER_POSITION_LIST_PARCELABLE_KEY;


public class ChapterNovelPresenter implements IReader.NovelFragmentPresenter
{
    public final static String TAG = ChapterNovelPresenter.class.getSimpleName();
    public static final String CHAPTER = TAG + ":CHAPTER";
    private static final String LOADING_STATUS = TAG + ":LOADING";

    private boolean mChapterParentFollowing;
    private int mPosition;
    private Chapter mChapter;
    private MangaEnums.eLoadingStatus mLoadingStatus;

    private float[] mNovelTextSizes = {13.0f, 14.5f, 16.0f, 17.5f, 19.0f};


    private IReader.NovelFragmentView mChapterReaderMapper;

    /***
     * ChapterNovelPresenter Constructor
     * @param aMap
     */
    public ChapterNovelPresenter(IReader.NovelFragmentView aMap, Bundle aBundle)
    {
        mChapterReaderMapper = aMap;
        mPosition = aBundle.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
        mChapter = aBundle.getParcelable(Chapter.TAG + ":" + mPosition);
        mChapterParentFollowing = aBundle.getBoolean(CHAPTER_PARENT_FOLLOWING, false);
        mLoadingStatus = MangaEnums.eLoadingStatus.LOADING;

    }

    private List<String> mContentList = new ArrayList<>();
    Subscription mImageListSubscription;

    @Override
    public void init(Bundle aBundle)
    {
        mChapterReaderMapper.setUserGestureListener();
        mChapterReaderMapper.alterNovelTextSize(SharedPrefs.getNovelTextSize());

        retrieveChapter();
    }

    private void retrieveChapter()
    {
        mImageListSubscription = SourceFactory.getInstance().getSource().getChapterImageListObservable(new RequestWrapper(mChapter)).cache()
                                              .subscribeOn(Schedulers.io())
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .subscribe(new Observer<String>()
                                              {

                                                  @Override
                                                  public void onCompleted()
                                                  {
                                                      mChapterReaderMapper.setContentText(mContentList.get(0));
                                                      mLoadingStatus = MangaEnums.eLoadingStatus.COMPLETE;
                                                      MangaLogger.logInfo(TAG, "Completed novel content retrieval");
                                                      updateReaderToolbar();
                                                      mChapterReaderMapper.startToolbarTimer();
                                                  }

                                                  @Override
                                                  public void onError(Throwable aThrowable)
                                                  {
                                                      mLoadingStatus = MangaEnums.eLoadingStatus.ERROR;
                                                  }

                                                  @Override
                                                  public void onNext(String aNovelPage)
                                                  {
                                                      mLoadingStatus = MangaEnums.eLoadingStatus.LOADING;
                                                      mContentList.add(aNovelPage);

                                                  }
                                              });

    }


    @Override
    public void onSaveState(Bundle aSave)
    {
        try
        {
            if (mChapter != null)
            {
                aSave.putParcelable(CHAPTER, mChapter);
            }
            aSave.putInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY, mPosition);
            aSave.putInt(LOADING_STATUS, MangaEnums.eLoadingStatus.getLoadingStatus(mLoadingStatus));
            mImageListSubscription.unsubscribe();

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    @Override
    public void onRestoreState(Bundle aRestore)
    {

        try
        {
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
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onDestroy()
    {

    }

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

    @Override
    public void onRefresh(int aPosition)
    {
        mChapterReaderMapper.setContentText("");
        try
        {
            if (mChapterReaderMapper != null && mChapterReaderMapper.getContext() != null)
            {
                mLoadingStatus = MangaEnums.eLoadingStatus.REFRESH;
                updateReaderToolbar();
                retrieveChapter();
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    @Override
    public float getNovelTextSize(int aPosition)
    {
        SharedPrefs.setNovelTextSize(aPosition);
        return mNovelTextSizes[aPosition];
    }

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

    @Override
    public void updateReaderToolbar()
    {
        try
        {
            switch (mLoadingStatus)
            {
                case COMPLETE:
                    mChapterReaderMapper
                            .updateToolbar(mChapter.getMangaTitle(), mChapter.getChapterTitle(), 1, mChapter
                                    .getCurrentPage() + 1, mPosition);
                    break;
                case LOADING:
                    mChapterReaderMapper
                            .updateToolbar(mChapter.getMangaTitle(), "Pages loaded: " + 1, 1, 1, mPosition);
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
}
