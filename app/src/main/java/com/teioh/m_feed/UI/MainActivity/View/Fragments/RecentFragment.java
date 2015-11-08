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
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.RecentFragmentMap;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenterImpl;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class RecentFragment extends Fragment implements RecentFragmentMap {

    @Bind(R.id.recent_list_view) GridView mListView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeContainer;

    private RecentPresenter mRecentPresenterManga;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
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
        mRecentPresenterManga.ButterKnifeUnbind();

    }

    @Override public void onResume() {
        super.onResume();
        mRecentPresenterManga.BusProviderRegister();
        mRecentPresenterManga.updateGridView();
    }

    @Override public void onPause() {
        super.onPause();
        mRecentPresenterManga.BusProviderUnregister();
    }

    @Subscribe public void onMangaAdded(Manga manga) {
        mRecentPresenterManga.onMangaAdd(manga);
    }

    @Subscribe public void activityQueryChange(QueryChange q){
        onQueryTextChange(q.getQuery());
    }

    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        mRecentPresenterManga.onMangaRemoved(rm);
    }

    @Override public void registerAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mListView.setAdapter(adapter);
            mListView.setTextFilterEnabled(true);
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

    @Override
    public void setupSwipeRefresh() {
        swipeContainer.setOnRefreshListener(() -> mRecentPresenterManga.updateGridView());

    }
}
