package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Presenters.FollowedPresenterImpl;

import butterknife.ButterKnife;

public class FollowedFragment extends MainFragmentBase {
    public final static String TAG = FollowedFragment.class.getSimpleName();

    public static Fragment getnewInstance(){
        Fragment lDialog = new FollowedFragment();
        return lDialog;
    }

    @Override
    public View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState) {
        View lView = aInflater.inflate(R.layout.main_tab2_followed_fragment, aContainer, false);
        ButterKnife.bind(this, lView);

        mFragmentPresenter = new FollowedPresenterImpl(this);
        return lView;
    }

    @Override
    public void startRefresh() {
        //do nothing
    }

    @Override
    public void stopRefresh() {
        //do nothing
    }

    @Override
    public void setupSwipeRefresh() {
        //do nothing
    }
}