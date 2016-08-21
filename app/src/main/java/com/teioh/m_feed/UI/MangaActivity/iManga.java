package com.teioh.m_feed.UI.MangaActivity;

import com.teioh.m_feed.MAL_Models.MALMangaList;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.Maps.BaseAdapterMap;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.EmptyLayoutMap;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;
import com.teioh.m_feed.UI.Maps.SwipeRefreshMap;

/**
 * Created by amgregoi on 8/21/16.
 */
public interface IManga {
    interface ActivityView extends BaseContextMap, BaseAdapterMap, SwipeRefreshMap, EmptyLayoutMap {

        void setActivityTitle(String title);

        void setupToolBar();

        void setMangaViews(com.teioh.m_feed.Models.Manga manga);

        void setupHeaderButtons();

        void changeFollowButton(boolean following);

        void initializeHeaderViews();

        void onMALSyncClicked(MALMangaList list);

    }

    interface ActivityModel {

    }

    interface ActivityPresenter extends LifeCycleMap{

        void chapterOrderButtonClick();

        void onFollwButtonClick(int aValue);

        void onUnfollowButtonClick();

        void onChapterClicked(Chapter aChapter);

        void onMALSyncClicked();

        String getImageUrl();
    }


}
