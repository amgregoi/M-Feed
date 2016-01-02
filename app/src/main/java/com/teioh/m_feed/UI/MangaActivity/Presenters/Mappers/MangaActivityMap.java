package com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers;


import android.support.v7.app.ActionBarDrawerToggle;

import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.UI.Maps.BaseContextMap;

public interface MangaActivityMap extends BaseContextMap {

    void registerAdapter(ViewPagerAdapterManga adapter);

    void setActivityTitle(String title);

    void setupSlidingTabLayout();

    void setupToolBar();
}
