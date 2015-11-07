package com.teioh.m_feed.MainPackage.Presenters;

import com.teioh.m_feed.Models.Manga;

public interface FollowPresenter {
    void initializeView();
    void updateGridView();
    void onItemClick(Manga item);
    void onQueryTextChange(String newText);
    void ButterKnifeUnbind();
    void BusProviderRegister();
    void BusProviderUnregister();
    void setAdapter();
}
