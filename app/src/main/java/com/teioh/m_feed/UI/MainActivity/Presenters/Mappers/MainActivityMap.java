package com.teioh.m_feed.UI.MainActivity.Presenters.Mappers;


import android.support.v7.app.ActionBarDrawerToggle;

import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;

public interface MainActivityMap extends BaseContextMap, SearchViewListenerMap {

    void registerAdapter(ViewPagerAdapterMain adapter);

    void setupSearchview();

    void setupTabLayout();

    void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle);

    void onDrawerOpen();

    void onDrawerClose();

}
