package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;

import java.util.List;

public interface AllLibraryPresenter {
    void initializeView();

    void updateGridView();

    void onItemClick(Manga item);

    void onQueryTextChange(String newText);

    void onDestroyView();

    void onResume();

    void onPause();

    void setAdapter();

    void onMangaRemoved(RemoveFromLibrary rm);

    void udpateChapterList(List<Manga> mList);
}
