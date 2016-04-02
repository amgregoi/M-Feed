package com.teioh.m_feed.UI.Maps;

import android.support.v7.widget.RecyclerView;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;

public interface RecyclerAdapterMap extends Listeners.MainFragmentListener{
    void registerAdapter(RecycleSearchAdapter mAdapter, RecyclerView.LayoutManager layout, boolean needItemDecoration);
    void updateSelection(Manga manga);
}
