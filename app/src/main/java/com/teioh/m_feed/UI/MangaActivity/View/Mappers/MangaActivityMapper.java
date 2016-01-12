package com.teioh.m_feed.UI.MangaActivity.View.Mappers;

import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.UI.Maps.BaseContextMap;

public interface MangaActivityMapper extends BaseContextMap {

    void registerAdapter(ViewPagerAdapterManga adapter);

    void setActivityTitle(String title);

    void setupSlidingTabLayout();

    void setupToolBar();
}
