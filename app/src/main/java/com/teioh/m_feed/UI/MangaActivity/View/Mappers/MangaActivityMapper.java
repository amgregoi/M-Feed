package com.teioh.m_feed.UI.MangaActivity.View.Mappers;

import com.teioh.m_feed.MAL_Models.MALMangaList;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.Maps.BaseAdapterMap;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.EmptyLayoutMap;
import com.teioh.m_feed.UI.Maps.SwipeRefreshMap;

public interface MangaActivityMapper extends BaseContextMap, BaseAdapterMap, SwipeRefreshMap, EmptyLayoutMap {

    void setActivityTitle(String title);

    void setupToolBar();

    void setMangaViews(Manga manga);

    void setupHeaderButtons();

    void changeFollowButton(boolean following);

    void initializeHeaderViews();

    void onMALSyncClicked(MALMangaList list);

}
