package com.teioh.m_feed.UI.MainActivity.View.Mappers;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.Maps.BaseAdapterMap;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;
import com.teioh.m_feed.UI.Maps.SwipeRefreshMap;

import java.util.ArrayList;

public interface RecentFragmentMapper extends BaseAdapterMap, SwipeRefreshMap, SearchViewListenerMap, BaseContextMap {
    void updateSource();
    void onFilterSelected(int filter);
    void onGenreFilterSelected(ArrayList<String> keep, ArrayList<Manga> remove);
    void onClearGenreFilter();

}
