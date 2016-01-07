package com.teioh.m_feed.UI.LoginActivity.Presenters.View;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.LoginActivity.Presenters.Presenters.LoginActivityPresenter;
import com.teioh.m_feed.UI.LoginActivity.Presenters.Presenters.LoginActivityPresenterImpl;
import com.teioh.m_feed.UI.LoginActivity.Presenters.Presenters.Mappers.LoginActivityMap;

public class LoginActivity extends AppCompatActivity implements LoginActivityMap{

    private LoginActivityPresenter mLoginPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mLoginPresenter =  new LoginActivityPresenterImpl(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLoginPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLoginPresenter.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        Fragment myFragment = getSupportFragmentManager().findFragmentByTag("LoginFragment");
        if (myFragment != null && myFragment.isVisible()) {
            finish();
        }else {
            super.onBackPressed();
        }
    }
}
