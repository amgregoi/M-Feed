package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

public interface ChapterPresenter {

    void onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void getImageUrls();

    void onPause();

    void onResume();

    void onDestroyView();

    void toggleToolbar();

    void setToNextChapter();

    void setToPreviousChapter();

    void updateOffsetCounter(int offset, int position);

    void updateState(int state);

    void updateToolbar();

    void updateCurrentPage(int position);

    void updateChapterViewStatus();
}
