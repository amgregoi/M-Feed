package com.teioh.m_feed.UI.MainActivity.View.Mappers;


import android.support.v7.app.ActionBarDrawerToggle;

import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;

import java.util.List;
import java.util.Map;

public interface MainActivityMapper extends BaseContextMap, SearchViewListenerMap {

    void registerAdapter(ViewPagerAdapterMain adapter);

    void setupSearchView();

    void setupTabLayout();

    void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle);

    void onDrawerOpen();

    void onDrawerClose();

    void closeDrawer();

    void setupToolbar();

    void changeSourceTitle(String source);

    void setupDrawerLayout(List<String> mDrawerItems, Map<String, List<String>> mSourceCollections);

    }
