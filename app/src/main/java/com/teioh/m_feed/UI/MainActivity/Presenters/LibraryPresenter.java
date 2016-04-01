package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.os.Bundle;

import java.util.ArrayList;

public interface LibraryPresenter {
    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void updateLibraryMangaList();

    void onItemClick(String mTitle);

    void onQueryTextChange(String newText);

    void onDestroyView();

    void onResume();

    void onPause();

    void setAdapter();

    void updateSource();

    void onFilterSelected(int filter);

    void onGenreFilterSelected(ArrayList<String> keep, ArrayList<String> remove);

    void onClearGenreFilter();

}
