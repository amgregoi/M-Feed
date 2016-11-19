package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Presenters.FollowedPresenter;

import butterknife.ButterKnife;

public class FollowedFragment extends MainFragmentBase
{
    public final static String TAG = FollowedFragment.class.getSimpleName();

    /***
     * TODO..
     *
     * @return
     */
    public static Fragment getnewInstance()
    {
        Fragment lDialog = new FollowedFragment();
        return lDialog;
    }

    /***
     * TODO..
     *
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState)
    {
        View lView = aInflater.inflate(R.layout.main_tab_relative, aContainer, false);
        ButterKnife.bind(this, lView);

        mFragmentPresenter = new FollowedPresenter(this);
        return lView;
    }

    /***
     * TODO..
     */
    @Override
    public void startRefresh()
    {
        //do nothing
    }

    /***
     * TODO..
     */
    @Override
    public void stopRefresh()
    {
        //do nothing
    }

    /***
     * TODO..
     */
    @Override
    public void setupSwipeRefresh()
    {
        //do nothing
    }

    /***
     * TOOD..
     */
    @Override
    public void removeFilters()
    {

    }
}