package com.teioh.m_feed.UI.LoginActivity.Presenters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.teioh.m_feed.UI.LoginActivity.View.Mappers.LoginActivityMapper;
import com.teioh.m_feed.UI.LoginActivity.View.Fragments.LoginFragment;


public class LoginActivityPresenterImpl implements LoginActivityPresenter {
    public final static String TAG = LoginActivityPresenterImpl.class.getSimpleName();

    private LoginActivityMapper mLoginMap;

    public LoginActivityPresenterImpl(LoginActivityMapper map) {
        mLoginMap = map;
    }

    @Override
    public void init() {
    }

    @Override
    public void onResume() {
        Fragment fragment = new LoginFragment();
        ((FragmentActivity) mLoginMap.getContext()).getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, fragment, LoginFragment.TAG).addToBackStack(LoginFragment.TAG).commit();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }
}
