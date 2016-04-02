package com.teioh.m_feed.UI.Maps;

import com.teioh.m_feed.Models.Manga;

import java.util.ArrayList;

public interface MangaFilterMap {
    void updateSource();
    void onFilterSelected(int filter);
    void onGenreFilterSelected(ArrayList<Manga> list);
    void onClearGenreFilter();
}
