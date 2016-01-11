package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.os.Bundle;

public interface FollowedPresenter {
    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void updateFollowedMangaList();

    void onItemClick(String mTitle);

    void onQueryTextChange(String newText);

    void onDestroyView();

    void onResume();

    void onPause();

    void setAdapter();
}
