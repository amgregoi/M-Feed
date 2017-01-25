package com.teioh.m_feed.UI.Maps;

import java.util.List;
import java.util.Map;

public interface DrawerLayoutMap
{
    void setDrawerLayoutListener();

    void closeDrawer();

    void openDrawer();

    void setupDrawerLayout(List<String> aDrawerItems, Map<String, List<String>> aSourceCollections);

}
