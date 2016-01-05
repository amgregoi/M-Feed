package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.Models.Manga;

public interface FollowLibraryPresenter {
    void initializeView();

    void updateGridView();

    void onItemClick(Manga item);

    void onQueryTextChange(String newText);

    void onDestroyView();

    void onResume();

    void onPause();

    void setAdapter();
}
