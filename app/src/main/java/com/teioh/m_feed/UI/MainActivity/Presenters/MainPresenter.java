package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public interface MainPresenter {

    void initialize();

    void setupDrawerLayoutListener(Toolbar mToolBar, DrawerLayout mDrawerLayout);

    void onLogout();

    void onResume();

    void onPause();

    void updateQueryChange(String newTest);

    void onDestroy();

    void onPostCreate();

    void onConfigurationChanged(Configuration newConfig);

    boolean onOptionsSelected(MenuItem item);

    void parseLogin();

    void onSourceChosen(String source);

}
