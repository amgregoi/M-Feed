package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.os.Bundle;

public interface RecentPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void updateRecentMangaList();

    void onItemClick(String mTitle);

    void onQueryTextChange(String newText);

    void onDestroyView();

    void onResume();

    void onPause();

    void setAdapter();

    void updateSource();

    void onFilterSelected(int filter);
}
