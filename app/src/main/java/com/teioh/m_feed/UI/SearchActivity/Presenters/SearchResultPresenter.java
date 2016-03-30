package com.teioh.m_feed.UI.SearchActivity.Presenters;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface SearchResultPresenter extends LifeCycleMap{
    void onItemClick(String title);

}
