package com.teioh.m_feed.UI.Maps;

import android.support.v7.widget.RecyclerView;

import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;

public interface MoPubAdapterMap extends Listeners.MainFragmentListener{
    void registerAdapter(MoPubRecyclerAdapter mAdapter, RecyclerView.LayoutManager layout, boolean needItemDecoration);
    void updateSelection(Manga manga);
}
