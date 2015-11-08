package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;

/**
 * Created by Asus1 on 11/6/2015.
 */
public interface RecentPresenter {
    void initialize();
    void updateGridView();
    void onItemClick(Manga item);
    void onQueryTextChange(String newText);
    void ButterKnifeUnbind();
    void BusProviderRegister();
    void BusProviderUnregister();
    void setAdapter();
    void onMangaAdd(Manga manga);
    void onMangaRemoved(RemoveFromLibrary rm);
}
