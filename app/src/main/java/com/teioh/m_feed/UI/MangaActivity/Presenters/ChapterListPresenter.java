package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;

public interface ChapterListPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void getChapterList();

    void onChapterClicked(Chapter chapter);

    void onPause();

    void onResume();

    void onDestroyView();
}
