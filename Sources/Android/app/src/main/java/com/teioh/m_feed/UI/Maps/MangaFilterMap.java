package com.teioh.m_feed.UI.Maps;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;

import java.util.ArrayList;

public interface MangaFilterMap
{
    void updateSource();

    void onFilterSelected(MangaEnums.eFilterStatus aFilter);

    boolean onGenreFilterSelected(ArrayList<Manga> aMangaList);

    boolean onClearGenreFilter();
}
