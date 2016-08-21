package com.teioh.m_feed.UI.Maps;

import android.support.v7.widget.RecyclerView;

import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.teioh.m_feed.Models.Manga;

public interface MoPubAdapterMap extends Listeners.MainFragmentListener{
    void registerAdapter(MoPubRecyclerAdapter aAdapter, RecyclerView.LayoutManager aLayout, boolean aNeedsDecoration);
    void updateSelection(Manga aManga);
}
