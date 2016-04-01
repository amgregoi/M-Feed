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

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.RecentFragmentMapper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class RecentFragment extends Fragment implements RecentFragmentMapper {
    public final static String TAG =RecentFragment.class.getSimpleName();

    @Bind(R.id.recent_list_view) GridView mGridView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeContainer;

    private RecentPresenter mRecentPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1_recent_fragment, container, false);
        ButterKnife.bind(this, v);

        mRecentPresenter = new RecentPresenterImpl(this);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mRecentPresenter.onSaveState(outState);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mRecentPresenter.onRestoreState(savedInstanceState);
        }

        mRecentPresenter.init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecentPresenter.onDestroyView();

    }

    @Override
    public void onResume() {
        super.onResume();
        mRecentPresenter.onResume();
        mGridView.refreshDrawableState();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRecentPresenter.onPause();
    }

    @Override
    public void registerAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mGridView.setAdapter(adapter);
            mGridView.setTextFilterEnabled(true);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mRecentPresenter.onQueryTextChange(newText);
        return false;
    }

    @Override
    public void startRefresh() {
        mGridView.setVisibility(View.GONE);
        swipeContainer.post(() -> swipeContainer.setRefreshing(true));
    }

    @Override
    public void stopRefresh() {
        swipeContainer.setRefreshing(false);
        mGridView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setupSwipeRefresh() {
        swipeContainer.post(() -> swipeContainer.setRefreshing(true));
        swipeContainer.setOnRefreshListener(() -> mRecentPresenter.updateRecentMangaList());
    }

    @Override
    public void updateSource() {
        mRecentPresenter.updateSource();
    }

    @Override
    public void onFilterSelected(int filter) {
        mRecentPresenter.onFilterSelected(filter);
    }

    @Override
    public void onGenreFilterSelected(ArrayList<String> keep, ArrayList<Manga> remove) {
        swipeContainer.setEnabled(false);
        mRecentPresenter.onGenreFilterSelected(keep, remove);
    }

    @Override
    public void onClearGenreFilter() {
        swipeContainer.setEnabled(true);
        mRecentPresenter.onClearGenreFilter();
    }

    @OnItemClick(R.id.recent_list_view)
    void onItemClick(AdapterView<?> adapter, View view, int pos) {
        Manga item = (Manga) adapter.getItemAtPosition(pos);
        mRecentPresenter.onItemClick(item.getTitle());
    }

}
