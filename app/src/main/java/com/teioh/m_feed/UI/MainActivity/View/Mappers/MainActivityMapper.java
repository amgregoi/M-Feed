package com.teioh.m_feed.UI.MainActivity.View.Mappers;


import android.support.v7.app.ActionBarDrawerToggle;

import com.commonsware.cwac.merge.MergeAdapter;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;

public interface MainActivityMapper extends BaseContextMap, SearchViewListenerMap {

    void registerAdapter(ViewPagerAdapterMain adapter, MergeAdapter sourceAdapter);

    void setupSearchview();

    void setupTabLayout();

    void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle);

    void onDrawerOpen();

    void onDrawerClose();

    void closeDrawer();

    void setupToolbar();

    void changeSourceTitle(String source);

}
