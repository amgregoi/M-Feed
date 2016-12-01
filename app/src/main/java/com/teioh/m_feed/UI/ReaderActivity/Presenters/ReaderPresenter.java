package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.MangaActivity.MangaPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.ChapterFragment;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.UI.ReaderActivity.ReaderActivity;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;

import java.util.ArrayList;

public class ReaderPresenter implements IReader.ActivityPresenter
{
    public final static String TAG = ReaderPresenter.class.getSimpleName();
    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String CHAPTER_POSITION = TAG + ":POSITION";
    public final static String SCREEN_ORIENTATION = TAG + ":SCREEN";

    private IReader.ActivityView mReaderMap;
    private ChapterPageAdapter mChapterPagerAdapter;
    private ArrayList<Chapter> mChapterList;
    private int mChapterPosition;

    /***
     * TODO..
     *
     * @param aMap
     */
    public ReaderPresenter(IReader.ActivityView aMap)
    {
        mReaderMap = aMap;
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
            if (mChapterList != null) aSave.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
            aSave.putInt(CHAPTER_POSITION, mChapterPosition);
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
            if (aRestore.containsKey(CHAPTER_LIST_KEY)) mChapterList = new ArrayList<>(aRestore.getParcelableArrayList(CHAPTER_LIST_KEY));
            if (aRestore.containsKey(CHAPTER_POSITION)) mChapterPosition = aRestore.getInt(CHAPTER_POSITION);

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
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
            if (mChapterList == null)
            {
                mChapterList = new ArrayList<>(aBundle.getParcelableArrayList(MangaPresenter.CHAPTER_LIST_KEY));
                mChapterPosition = aBundle.getInt(MangaPresenter.LIST_POSITION_KEY);
            }

            mChapterPagerAdapter = new ChapterPageAdapter(((ReaderActivity) mReaderMap).getSupportFragmentManager(), mChapterList);
            mReaderMap.registerAdapter(mChapterPagerAdapter);
            mReaderMap.setCurrentChapter(mChapterPosition);
            mReaderMap.setupToolbar();
            mReaderMap.setScreenOrientation(SharedPrefs.getChapterScreenOrientation());

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
    public void onResume()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {

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
    public void onDestroy()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mReaderMap = null;

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * TODO..
     *
     * @param aPosition
     */
    @Override
    public void updateToolbar(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ChapterFragment lTempFragment;
            if ((lTempFragment = ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.updateToolbar();
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
     * @param aPosition
     */
    @Override
    public void incrementChapterPage(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ChapterFragment lTempFragment;
            if ((lTempFragment = ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.incrementChapterPage();
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
     * @param aPosition
     */
    @Override
    public void decrementChapterPage(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ChapterFragment lTempFragment;
            if ((lTempFragment = ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.decrementChapterPage();
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
     * @param aPosition
     */
    @Override
    public void updateChapterViewStatus(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ChapterFragment lTempFragment;
            if ((lTempFragment = ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.updateChapterViewStatus();
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
     * @param aPosition
     */
    @Override
    public void onRefreshButton(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ChapterFragment lTempFragment;
            if ((lTempFragment = ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.onRefresh();
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
     * @param aPosition
     */
    @Override
    public void toggleVerticalScrollSettings(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            boolean lCurrentValue = SharedPrefs.getChapterScrollVertical();
            SharedPrefs.setChapterScrollVertical(!lCurrentValue);

            ChapterFragment lTempFragment;
            if ((lTempFragment = ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.toggleVerticalScrollSettings();
            }
            if ((lTempFragment = ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition + 1))) != null)
            {
                lTempFragment.toggleVerticalScrollSettings();
            }
            if ((lTempFragment = ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition - 1))) != null)
            {
                lTempFragment.toggleVerticalScrollSettings();
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
    public void toggleOrientation()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            boolean lCurrentValue = SharedPrefs.getChapterScreenOrientation();
            SharedPrefs.setChapterScreenOrientation(!lCurrentValue);
            mReaderMap.setScreenOrientation(!lCurrentValue);

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }
}
