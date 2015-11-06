package com.teioh.m_feed.MainPackage.Presenters;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;

public interface LibraryPresenter {
    void initializeView();
    void updateGridView();
    void onItemClick(Manga item);
    void onQueryTextChange(String newText);
    void ButterKnifeUnbind();
    void BusProviderRegister();
    void BusProviderUnregister();
    void setAdapter();
    void initializeSearch();

    void onMangaRemoved(RemoveFromLibrary rm);
}
