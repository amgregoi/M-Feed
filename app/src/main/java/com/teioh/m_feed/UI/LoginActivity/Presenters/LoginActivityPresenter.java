package com.teioh.m_feed.UI.LoginActivity.Presenters;

/**
 * Created by amgregoi on 1/5/16.
 */
public interface LoginActivityPresenter {

    void initialize();

    void onResume();

    void onPause();

    void onDestroy();

    void onBackPressed();

}
