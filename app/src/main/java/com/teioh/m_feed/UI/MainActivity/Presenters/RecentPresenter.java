package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Manga;

import java.util.ArrayList;

public interface RecentPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void updateRecentMangaList();

    void onItemClick(String mTitle);

    void onQueryTextChange(String newText);

    void onDestroyView();

    void onResume();

    void onPause();

    void setAdapter();

    void updateSource();

    void onFilterSelected(int filter);

    void onGenreFilterSelected(ArrayList<String> keep, ArrayList<Manga> remove);

    void onClearGenreFilter();
}
