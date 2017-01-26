package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Presenters.CatalogPresenter;

import butterknife.ButterKnife;

public class CatalogFragment extends MainFragmentBase
{
    public final static String TAG = CatalogFragment.class.getSimpleName();

    /***
     * TODO..
     *
     * @return
     */
    public static Fragment getnewInstance()
    {
        Fragment dialog = new CatalogFragment();
        return dialog;
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

        mFragmentPresenter = new CatalogPresenter(this);
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
     * TODO..
     */
    @Override
    public void removeFilters()
    {

    }
}