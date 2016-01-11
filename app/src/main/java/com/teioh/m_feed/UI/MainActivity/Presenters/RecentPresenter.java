package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;

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
}
