package com.teioh.m_feed.UI.MainActivity.View.Mappers;


import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.DrawerLayoutMap;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;

public interface MainActivityMapper extends BaseContextMap, Listeners.MALDialogListener, SearchViewListenerMap, Listeners.MainFragmentListener, DrawerLayoutMap {

    void registerAdapter(ViewPagerAdapterMain aAdapter);

    void setupToolbar();

    void setupTabLayout();

    void setupSearchView();

    void setupSourceFilterMenu();

    void setActivityTitle(String aTitle);

    void setPageAdapterItem(int aPosition);

    void setDefaultFilterImage();

    void toggleToolbarElements();

}
