package com.teioh.m_feed.UI.MangaActivity.Presenters;

import com.teioh.m_feed.Models.Manga;

public interface MangaPresenter {

    void initialize(Manga item);

    void onResume();

    void onPause();

    void onDestroy();
}
