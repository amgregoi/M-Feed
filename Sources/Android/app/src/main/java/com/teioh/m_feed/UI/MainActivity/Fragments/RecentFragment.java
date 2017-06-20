package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecentFragment extends MainFragmentBase
{
    public final static String TAG = RecentFragment.class.getSimpleName();

    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeContainer;
    @Bind(R.id.no_internet_image) ImageView mWifiView;

    /***
     * This function creates and returns a new instance of the fragment.
     *
     * @return
     */
    public static Fragment getnewInstance()
    {
        Fragment dialog = new RecentFragment();
        return dialog;
    }

    /***
     * This function initializes the view of the fragment.
     *
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState)
    {
        View lView = aInflater.inflate(R.layout.main_tab_swipe, aContainer, false);
        ButterKnife.bind(this, lView);

        mGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mFragmentPresenter = new RecentPresenter(this);


        return lView;
    }

    /***
     * This function starts the swipe refresh layout refresh animation.
     */
    @Override
    public void startRefresh()
    {
        mWifiView.setVisibility(View.GONE);
        mGridView.setVisibility(View.GONE);
        mGridView.setHideScrollbar(true);
        mSwipeContainer.post(() -> mSwipeContainer.setRefreshing(true));
    }

    /***
     * This function stops the swipe refresh layout refresh animation.
     */
    @Override
    public void stopRefresh()
    {
        mSwipeContainer.setRefreshing(false);
        mWifiView.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
        mGridView.setHideScrollbar(false);
    }

    /***
     * This function initializes the swipe refresh layout.
     */
    @Override
    public void setupSwipeRefresh()
    {
        mSwipeContainer.setEnabled(true);
        mSwipeContainer.setOnRefreshListener(() -> {
            mGridView.setVisibility(View.GONE);
            mFragmentPresenter.updateMangaList();
            mListener.removeFilters();

        });
    }

    /***
     * Not implemented for this class
     */
    @Override
    public boolean removeFilters()
    {
        //left blank
        return true;
    }

    /***
     * This function hides the fragment layouts behind the Wi-Fi view to show there is no Wi-Fi.
     */
    public void showNoWifiView()
    {
        mGridView.setVisibility(View.VISIBLE);
        mWifiView.setVisibility(View.VISIBLE);
        mSwipeContainer.setRefreshing(true);
    }
}
