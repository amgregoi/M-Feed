package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecentFragment extends MainFragmentBase {
    public final static String TAG = RecentFragment.class.getSimpleName();

    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeContainer;

    public static Fragment getnewInstance() {
        Fragment dialog = new RecentFragment();
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState) {
        View lView = aInflater.inflate(R.layout.main_tab1_recent_fragment, aContainer, false);
        ButterKnife.bind(this, lView);

        mGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mFragmentPresenter = new RecentPresenter(this);

        return lView;
    }

    @Override
    public void startRefresh() {
        mGridView.setVisibility(View.GONE);
        mSwipeContainer.post(() -> mSwipeContainer.setRefreshing(true));

    }

    @Override
    public void stopRefresh() {
        mSwipeContainer.setRefreshing(false);
        mGridView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setupSwipeRefresh() {
        mSwipeContainer.post(() -> mSwipeContainer.setRefreshing(true));
        mSwipeContainer.setOnRefreshListener(() -> {
            mGridView.setVisibility(View.GONE);
            mFragmentPresenter.updateMangaList();
            mListener.removeFilters();

        });
    }

    @Override
    public void removeFilters() {

    }
}
