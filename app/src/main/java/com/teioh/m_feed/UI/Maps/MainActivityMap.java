package com.teioh.m_feed.UI.Maps;


import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;

public interface MainActivityMap {
    void registerAdapter(ViewPagerAdapterMain adapter);

    void setupSearchview();

    void setupTabLayout();

}
