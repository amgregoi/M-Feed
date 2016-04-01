package com.teioh.m_feed.UI.MainActivity.View.Mappers;

import android.support.v7.widget.RecyclerView;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecyclerSearchAdapater;
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

    void registerAdapter(RecyclerSearchAdapater mAdapter, RecyclerView.LayoutManager layout);

    void updateSelection(Manga manga);

    void updateRecentSelection(Long id);
    void refreshRecentSelection();

}
