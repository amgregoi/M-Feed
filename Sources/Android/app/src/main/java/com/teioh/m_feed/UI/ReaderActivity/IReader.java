package com.teioh.m_feed.UI.ReaderActivity;

import android.support.v4.view.ViewPager;

import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.Maps.PageAdapterMap;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.GestureViewPager;

/**
 * Created by amgregoi on 8/21/16.
 */
public interface IReader
{
    /***
     * TODO..
     */
    interface ActivityView extends BaseContextMap, PageAdapterMap, ViewPager.OnPageChangeListener, Listeners.ReaderListener
    {
        void setCurrentChapter(int aPosition);

        void setupToolbar();

        void setScreenOrientation(boolean aLandscape);
    }

    /***
     * TODO..
     */
    interface ActivityPresenter extends LifeCycleMap
    {

        void updateToolbar(int aPosition);

        void incrementChapterPage(int aPosition);

        void decrementChapterPage(int aPosition);

        void updateChapterViewStatus(int aPosition);

        void onRefreshButton(int aPosition);

        void toggleOrientation();

        void toggleVerticalScrollSettings(int aPosition);

        void updateRecentChapter(int aPosition);
    }

    /***
     * TODO..
     */
    interface FragmentView extends BaseContextMap, PageAdapterMap, ViewPager.OnPageChangeListener, GestureViewPager.OnSingleTapListener
    {

        void setupOnSingleTapListener();

        void updateToolbar();

        void incrementChapterPage();

        void decrementChapterPage();

        void updateChapterViewStatus();

        void incrementChapter();

        void decrementChapter();

        void hideToolbar(long aDelay);

        void showToolbar();

        void updateToolbar(String aMangaTitle, String aChapterTitle, int aSize, int aPage);

        void updateCurrentPage(int aPosition);

        void onRefresh();

        void failedLoadChapter();

        boolean checkActiveChapter(int aChapter);

        void setCurrentChapterPage(int aPosition);

        void setChapterPage(int aPage);

        void toggleVerticalScrollSettings();

    }

    /***
     * TODO..
     */
    interface FragmentPresenter extends LifeCycleMap
    {

        void getImageUrls();

        void toggleToolbar();

        void setToNextChapter();

        void setToPreviousChapter();

        void updateOffsetCounter(int aOffset, int aPosition);

        void updateState(int aState);

        void updateReaderToolbar();

        void updateCurrentPage(int aPosition);

        void updateActiveChapter();

        void updateChapterViewStatus();

        void onRefresh(int aPosition);

    }
}
