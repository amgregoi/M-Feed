package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MangaActivity.MangaPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.ChapterMangaFragment;
import com.teioh.m_feed.UI.ReaderActivity.ChapterNovelFragment;
import com.teioh.m_feed.UI.ReaderActivity.CurrentSelection;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.UI.ReaderActivity.ReaderActivity;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;

import java.util.ArrayList;

public class ReaderPresenter implements IReader.ReaderActivityPresenter
{
    public final static String TAG = ReaderPresenter.class.getSimpleName();
//    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String CHAPTER_POSITION = TAG + ":POSITION";
    public final static String PARENT_URL = TAG + ":PARENT_URL";

    private IReader.ReaderActivityView mReaderMap;
    private ChapterPageAdapter mChapterPagerAdapter;
    private ArrayList<Chapter> mChapterList;
    private int mChapterPosition;

    private Manga mParentManga;

    /***
     * This is the constructor for the reader presenter.
     *
     * @param aMap
     */
    public ReaderPresenter(IReader.ReaderActivityView aMap)
    {
        mReaderMap = aMap;
    }

    /***
     * This function initializes the reader presenter.
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle)
    {
        try
        {
            if (mChapterList == null)
            {
                mChapterList = new ArrayList<>(CurrentSelection.getChapters());
                mChapterPosition = aBundle.getInt(MangaPresenter.LIST_POSITION_KEY);
            }

            String lParentUrl = aBundle.getString(PARENT_URL);
            mParentManga = MangaDB.getInstance().getManga(lParentUrl);
            mChapterPagerAdapter = new ChapterPageAdapter(((ReaderActivity) mReaderMap)
                                                                  .getSupportFragmentManager(), mChapterList, mParentManga.getFollowing());
            mReaderMap.registerAdapter(mChapterPagerAdapter);
            mReaderMap.setCurrentChapter(mChapterPosition);
            mReaderMap.setupToolbar();
            mReaderMap.setScreenOrientation(SharedPrefs.getChapterScreenOrientation());

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
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
            aSave.putInt(CHAPTER_POSITION, mChapterPosition);
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
            if (aRestore.containsKey(CHAPTER_POSITION)) mChapterPosition = aRestore.getInt(CHAPTER_POSITION);
            mChapterList = new ArrayList<>(CurrentSelection.getChapters());
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
        //do nothing
    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    public void onResume()
    {
        //do nothing
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    public void onDestroy()
    {
        try
        {
            mReaderMap = null;
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function updates the activity toolbar.
     *
     * @param aPosition
     */
    @Override
    public void updateToolbar(int aPosition)
    {
        try
        {
            IReader.ReaderFragmentBaseView lTempFragment;
            if ((lTempFragment = ((IReader.ReaderFragmentBaseView) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.updateToolbar();
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function increments the current chapter.
     *
     * @param aPosition
     */
    @Override
    public void incrementChapterPage(int aPosition)
    {
        try
        {
            ChapterMangaFragment lTempFragment;
            if ((lTempFragment = ((ChapterMangaFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.incrementChapterPage();
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function decrements the current page.
     *
     * @param aPosition
     */
    @Override
    public void decrementChapterPage(int aPosition)
    {
        try
        {
            ChapterMangaFragment lTempFragment;
            if ((lTempFragment = ((ChapterMangaFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.decrementChapterPage();
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    @Override
    public void alterNovelTextSize(int aValue, int aPosition)
    {
        try
        {
            ChapterNovelFragment lTempFragment;
            if ((lTempFragment = ((ChapterNovelFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.alterNovelTextSize(aValue);
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function updates the chapter view status.
     *
     * @param aPosition
     */
    @Override
    public void updateChapterViewStatus(int aPosition)
    {
        try
        {
            IReader.ReaderFragmentBaseView lTempFragment;
            if ((lTempFragment = ((IReader.ReaderFragmentBaseView) (mChapterPagerAdapter.getItem(aPosition)))) != null)
            {
                lTempFragment.updateChapterViewStatus();
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function performs the chapter refresh.
     *
     * @param aPosition
     */
    @Override
    public void onRefreshButton(int aPosition)
    {
        try
        {
            IReader.ReaderFragmentBaseView lChapterFragment;
            if ((lChapterFragment = ((IReader.ReaderFragmentBaseView) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lChapterFragment.onRefresh();
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function toggles the reader orientation.
     */
    @Override
    public void toggleOrientation()
    {
        try
        {
            boolean lCurrentValue = SharedPrefs.getChapterScreenOrientation();
            SharedPrefs.setChapterScreenOrientation(!lCurrentValue);
            mReaderMap.setScreenOrientation(!lCurrentValue);

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function toggles the vertical scrolling setting.
     *
     * @param aPosition
     */
    @Override
    public void toggleVerticalScrollSettings(int aPosition)
    {
        try
        {
            boolean lCurrentValue = SharedPrefs.getChapterScrollVertical();
            SharedPrefs.setChapterScrollVertical(!lCurrentValue);

            ChapterMangaFragment lTempFragment;
            if ((lTempFragment = ((ChapterMangaFragment) mChapterPagerAdapter.getItem(aPosition))) != null)
            {
                lTempFragment.toggleVerticalScrollSettings();
            }
            if ((lTempFragment = ((ChapterMangaFragment) mChapterPagerAdapter.getItem(aPosition + 1))) != null)
            {
                lTempFragment.toggleVerticalScrollSettings();
            }
            if ((lTempFragment = ((ChapterMangaFragment) mChapterPagerAdapter.getItem(aPosition - 1))) != null)
            {
                lTempFragment.toggleVerticalScrollSettings();
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function updates the recent chapter.
     * @param aPosition
     */
    @Override
    public void updateRecentChapter(int aPosition)
    {
        try
        {
            if (aPosition < mChapterList.size() && aPosition > 0)
            {
                mParentManga.setRecentChapter(mChapterList.get(aPosition).getChapterUrl());
                MangaDB.getInstance().updateManga(mParentManga);
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }
}
