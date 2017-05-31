package com.teioh.m_feed.UI.MangaActivity;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.Maps.BaseAdapterMap;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.EmptyLayoutMap;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;
import com.teioh.m_feed.UI.Maps.SwipeRefreshMap;

/**
 * Created by amgregoi on 8/21/16.
 */
public interface IManga
{
    /***
     * TODO..
     */
    interface ActivityView extends BaseContextMap, BaseAdapterMap, SwipeRefreshMap, EmptyLayoutMap
    {

        void setActivityTitle(String title);

        void setupToolBar();

        void setMangaViews(com.teioh.m_feed.Models.Manga manga);

        void setupHeaderButtons();

        void initializeHeaderViews();

        void showFailedToLoad();

    }

    /***
     * TODO..
     */
    interface ActivityPresenter extends LifeCycleMap
    {

        boolean chapterOrderButtonClick();

        boolean onFollowButtonClick(int aValue);

        boolean onUnfollowButtonClick();

        boolean onChapterClicked(Chapter aChapter);

        String getImageUrl();

        boolean onContinueReadingButtonClick();

        boolean clearCachedChapters();
    }


}
