package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

public interface ChapterReaderPresenter {

    Bundle onSaveState(Bundle bundle);

    void onRestoreState(Bundle bundle);

    void init();

    void getImageUrls();

    void onPause();

    void onDestroyView();

    void toggleToolbar();

    void updateOffsetCounter(int offset, int position);

    void updateState(int state);

    void setToNextChapter();

    void setToPreviousChapter();



}
