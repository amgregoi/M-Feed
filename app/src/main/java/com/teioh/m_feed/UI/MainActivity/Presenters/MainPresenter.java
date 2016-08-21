package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface MainPresenter extends LifeCycleMap {

    void updateQueryChange(String aNewTest);

    void onDrawerItemChosen(int aPosition);

    void onSourceItemChosen(int aPosition);

    void onFilterSelected(int aFilter);

    void removeSettingsFragment();

    void onGenreFilterSelected(Intent aIntent);

    void onClearGenreFilter();

    boolean genreFilterActive();

    void getRecentManga();

    void setRecentManga(long aMangaId);

    void onSignOut();

    void onSignIn();

}
