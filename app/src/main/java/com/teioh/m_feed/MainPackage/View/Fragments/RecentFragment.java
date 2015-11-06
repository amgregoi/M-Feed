package com.teioh.m_feed.MainPackage.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.BaseDirectoryMapper;
import com.teioh.m_feed.MainPackage.Adapters.SearchableAdapter;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.SwipeRefreshMapper;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.MainPackage.Presenters.RecentPresenter;
import com.teioh.m_feed.MainPackage.Presenters.RecentPresenterImpl;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class RecentFragment extends Fragment implements BaseDirectoryMapper, SwipeRefreshMapper {

    @Bind(R.id.recent_list_view) GridView mListView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeContainer;

    private RecentPresenter mRecentPresenter;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        ButterKnife.bind(this, v);

        mRecentPresenter = new RecentPresenterImpl(v, this);
        mRecentPresenter.initializeView();
        return v;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnItemClick(R.id.recent_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        mRecentPresenter.onItemClick(item);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mRecentPresenter.ButterKnifeUnbind();

    }

    @Override public void onResume() {
        super.onResume();
        mRecentPresenter.BusProviderRegister();
        mRecentPresenter.updateGridView();
    }

    @Override public void onPause() {
        super.onPause();
        mRecentPresenter.BusProviderUnregister();
    }

    @Subscribe public void onMangaAdded(Manga manga) {
        mRecentPresenter.onMangaAdd(manga);
    }

    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        mRecentPresenter.onMangaRemoved(rm);
    }

    @Override public void registerAdapter(SearchableAdapter adapter) {
        if(adapter != null)
        {
            mListView.setAdapter(adapter);
            mListView.setTextFilterEnabled(true);
        }
    }

    @Override public void initializeSearch() {

    }

    @Override public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override public void startRefresh() {
        swipeContainer.post(() -> swipeContainer.setRefreshing(true));  // starts activity with loading icon while retrieving list
    }

    @Override public void stopRefresh() {
        swipeContainer.setRefreshing(false);

    }

    @Override public void setupRefreshListener() {
        swipeContainer.setOnRefreshListener(() -> mRecentPresenter.updateGridView());

    }
}
