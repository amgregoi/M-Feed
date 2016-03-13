package com.teioh.m_feed.UI.MainActivity.View.Mappers;

import com.teioh.m_feed.UI.Maps.BaseAdapterMap;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;
import com.teioh.m_feed.UI.Maps.SwipeRefreshMap;

public interface RecentFragmentMapper extends BaseAdapterMap, SwipeRefreshMap, SearchViewListenerMap, BaseContextMap {
    void updateSource();
}
