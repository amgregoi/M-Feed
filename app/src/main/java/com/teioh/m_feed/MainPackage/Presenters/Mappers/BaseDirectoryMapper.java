package com.teioh.m_feed.MainPackage.Presenters.Mappers;

import android.widget.SearchView;

import com.teioh.m_feed.MainPackage.Adapters.SearchableAdapter;

public interface BaseDirectoryMapper extends SearchView.OnQueryTextListener {

    public void registerAdapter(SearchableAdapter adapter);
    public void initializeSearch();
}
