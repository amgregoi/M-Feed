package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;

import java.util.ArrayList;

public interface HomePresenter extends LifeCycleMap {

    void updateMangaList();

    void onQueryTextChange(String aQueryText);

    void updateSource();

    void onFilterSelected(int aFilter);

    void onGenreFilterSelected(ArrayList<Manga> aMangaList);

    void onClearGenreFilter();

    void updateSelection(Manga aManga);

}
