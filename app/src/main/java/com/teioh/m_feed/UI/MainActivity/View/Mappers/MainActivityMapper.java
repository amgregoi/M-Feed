package com.teioh.m_feed.UI.MainActivity.View.Mappers;


import android.support.v7.app.ActionBarDrawerToggle;

import com.teioh.m_feed.UI.Maps.DrawerLayoutMap;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;

import java.util.List;
import java.util.Map;

public interface MainActivityMapper extends BaseContextMap, Listeners.MALDialogListener, SearchViewListenerMap, Listeners.MainFragmentListener, DrawerLayoutMap {

    void registerAdapter(ViewPagerAdapterMain adapter);

    void setupToolbar();

    void setupTabLayout();

    void setupSearchView();

    void setupSourceFilterMenu();

    void setActivityTitle(String title);

    void setPageAdapterItem(int position);

    void setDefaultFilterImage();

    void toggleToolbarElements();

}
