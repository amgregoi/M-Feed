package com.teioh.m_feed.UI.MainActivity.View.Mappers;

import com.teioh.m_feed.UI.Maps.BaseAdapterMap;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;


public interface FollowFragmentMapper extends BaseAdapterMap, SearchViewListenerMap, BaseContextMap {
    void updateSource();

}
