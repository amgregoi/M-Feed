package com.teioh.m_feed.UI.ReaderActivity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.teioh.m_feed.MangaEnums;
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
    interface ReaderActivityView extends BaseContextMap, PageAdapterMap, ViewPager.OnPageChangeListener, Listeners.ReaderListener
    {
        void setCurrentChapter(int aPosition);

        void setupToolbar();

        void setScreenOrientation(boolean aLandscape);

        void hideToolbar();
    }

    /***
     * TODO..
     */
    interface ReaderActivityPresenter extends LifeCycleMap
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
    interface MangaFragmentView extends BaseContextMap, ViewPager.OnPageChangeListener, GestureViewPager.UserGestureListener
    {

        void setUserGestureListener();

        void updateToolbar();

        void incrementChapterPage();

        void decrementChapterPage();

        void updateChapterViewStatus();

        void incrementChapter();

        void decrementChapter();

        void toggleToolbar();

        void startToolbarTimer();

        void updateToolbar(String aMangaTitle, String aChapterTitle, int aSize, int aPage, int aChapterPosition);

        void updateCurrentPage(int aPosition);

        void onRefresh();

        void setCurrentChapterPage(int aPosition, int aChapterPosition);

        void toggleVerticalScrollSettings();

        void registerAdapter(PagerAdapter aAdapter, MangaEnums.eSourceType aType);

    }

    /***
     * TODO..
     */
    interface MangaFragmentPresenter extends LifeCycleMap
    {
        void toggleToolbar();

        void updateReaderToolbar();

        void updateCurrentPage(int aPosition);

        void updateActiveChapter();

        void updateChapterViewStatus();

        void onRefresh(int aPosition);
    }

    interface NovelFragmentView extends BaseContextMap, GestureViewPager.UserGestureListener
    {
        void setUserGestureListener();

        void updateChapterViewStatus();

        void incrementChapter();

        void decrementChapter();

        void toggleToolbar();

        void startToolbarTimer();

        void updateToolbar(String aMangaTitle, String aChapterTitle, int aSize, int aPage, int aChapterPosition);

        void updateCurrentPage(int aPosition);

        void onRefresh();

        void setContentText(String aText);

    }

    interface NovelFragmentPresenter extends LifeCycleMap
    {
        void toggleToolbar();

        void updateReaderToolbar();

        void updateChapterViewStatus();

        void onRefresh(int aPosition);

    }
}
