package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

public interface MainPresenter {
    void onSavedState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void setupDrawerLayoutListener(Toolbar mToolBar, DrawerLayout mDrawerLayout);

    void onSignIn();

    void onResume();

    void onPause();

    void updateQueryChange(String newTest);

    void onDestroy();

    void onPostCreate();

    void onConfigurationChanged(Configuration newConfig);

    void onDrawerItemChosen(int position);

    void onSourceItemChosen(int position);

    void onFilterSelected(int filter);

    void onMALSignIn();

    void removeSettingsFragment();

    String onGenreFilterSelected(Intent intent);

    void onClearGenreFilter();

    boolean genreFilterActive();

    void toggleGenreFilterActive();
}
