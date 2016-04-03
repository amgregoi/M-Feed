package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;

import java.util.ArrayList;

public interface HomePresenter extends LifeCycleMap {

    void updateMangaList();

    void onQueryTextChange(String newText);

    void updateSource();

    void onFilterSelected(int filter);

    void onGenreFilterSelected(ArrayList<Manga> list);

    void onClearGenreFilter();

    void updateSelection(Manga manga);

}
