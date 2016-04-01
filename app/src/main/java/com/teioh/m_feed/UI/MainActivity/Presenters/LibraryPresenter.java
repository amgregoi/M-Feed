package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Manga;

import java.util.ArrayList;

public interface LibraryPresenter {
    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void updateLibraryMangaList();

    void onItemClick(Manga manga);

    void onQueryTextChange(String newText);

    void onDestroyView();

    void onResume();

    void onPause();

    void updateSource();

    void onFilterSelected(int filter);

    void onGenreFilterSelected(ArrayList<String> keep, ArrayList<String> remove);

    void onClearGenreFilter();


    void updateSelection(Manga manga);


}
