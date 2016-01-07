package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.RecentFragmentMap;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenterImpl;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class RecentFragment extends Fragment implements RecentFragmentMap {

    @Bind(R.id.recent_list_view) GridView mGridView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeContainer;

    private RecentPresenter mRecentPresenterManga;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1_recent_fragment, container, false);
        ButterKnife.bind(this, v);

        mRecentPresenterManga = new RecentPresenterImpl(this);
        mRecentPresenterManga.initialize();
        return v;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @OnItemClick(R.id.recent_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        mRecentPresenterManga.onItemClick(item);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mRecentPresenterManga.onDestroyView();

    }

    @Override public void onResume() {
        super.onResume();
        mRecentPresenterManga.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        mRecentPresenterManga.onPause();
    }

    @Override public void registerAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mGridView.setAdapter(adapter);
            mGridView.setTextFilterEnabled(true);
        }
    }

    @Override public boolean onQueryTextSubmit(String newText) {
        return false;
    }

    @Override public boolean onQueryTextChange(String newText) {
        mRecentPresenterManga.onQueryTextChange(newText);
        return false;
    }

    @Override public void startRefresh() {
        swipeContainer.post(() -> swipeContainer.setRefreshing(true));  // starts activity with loading icon while retrieving list
    }

    @Override public void stopRefresh() {
        swipeContainer.setRefreshing(false);
    }

    @Override public void setupSwipeRefresh() {
        swipeContainer.setOnRefreshListener(() -> mRecentPresenterManga.updateGridView());

    }
}