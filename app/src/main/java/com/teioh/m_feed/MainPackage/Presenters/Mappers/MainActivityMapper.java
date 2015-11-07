package com.teioh.m_feed.MainPackage.Presenters.Mappers;


import com.teioh.m_feed.MainPackage.Adapters.ViewPagerAdapterMain;

public interface MainActivityMapper {
    void registerAdapter(ViewPagerAdapterMain adapter);

    void setupSearchview();

    void setupTabLayout();

}
