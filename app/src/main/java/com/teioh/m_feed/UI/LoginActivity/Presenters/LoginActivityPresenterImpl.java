package com.teioh.m_feed.UI.LoginActivity.Presenters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.teioh.m_feed.UI.LoginActivity.Presenters.Mappers.LoginActivityMap;
import com.teioh.m_feed.UI.LoginActivity.View.LoginFragment;


public class LoginActivityPresenterImpl implements LoginActivityPresenter {
    public final static String TAG = LoginActivityPresenterImpl.class.getSimpleName();

    private LoginActivityMap mLoginMap;

    public LoginActivityPresenterImpl(LoginActivityMap map) {
        mLoginMap = map;
    }

    @Override
    public void init() {
    }

    @Override
    public void onResume() {
        Fragment fragment = new LoginFragment();
        ((FragmentActivity) mLoginMap.getContext()).getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, fragment, LoginFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }
}
