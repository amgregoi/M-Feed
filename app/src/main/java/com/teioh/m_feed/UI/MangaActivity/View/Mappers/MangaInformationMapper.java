package com.teioh.m_feed.UI.MangaActivity.View.Mappers;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.EmptyLayoutMap;
import com.teioh.m_feed.UI.Maps.SwipeRefreshMap;

public interface MangaInformationMapper extends BaseContextMap, SwipeRefreshMap, EmptyLayoutMap{

    void setMangaViews(Manga manga);

    void setFollowButtonText(int resourceId, boolean notInit);

    void setupFollowButton();
}
