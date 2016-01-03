package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;

public interface RecentPresenter {
    void initialize();

    void updateGridView();

    void onItemClick(Manga item);

    void onQueryTextChange(String newText);

    void onDestroyView();

    void onResume();

    void onPause();

    void setAdapter();

    void onMangaAdd(Manga manga);

    void onMangaRemoved(RemoveFromLibrary rm);
}
