package com.teioh.m_feed.UI.MainActivity.View.Mappers;

import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.MangaFilterMap;
import com.teioh.m_feed.UI.Maps.MoPubAdapterMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;
import com.teioh.m_feed.UI.Maps.SwipeRefreshMap;

public interface ViewPresenterMapper extends MoPubAdapterMap, SwipeRefreshMap, SearchViewListenerMap, BaseContextMap, MangaFilterMap {
    //RecyclerAdapterMap
}
