package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface MainPresenter extends LifeCycleMap {

    void onSignIn();

    void updateQueryChange(String newTest);

    void onDrawerItemChosen(int position);

    void onSourceItemChosen(int position);

    void onFilterSelected(int filter);

    void onMALSignIn();

    void removeSettingsFragment();

    void onGenreFilterSelected(Intent intent);

    void onClearGenreFilter();

    boolean genreFilterActive();

    void getRecentManga();

    void setRecentManga(long id);
}
