package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface ChapterPresenter extends LifeCycleMap {

    void getImageUrls();

    void toggleToolbar();

    void setToNextChapter();

    void setToPreviousChapter();

    void updateOffsetCounter(int offset, int position);

    void updateState(int state);

    void updateToolbar();

    void updateCurrentPage(int position);

    void updateChapterViewStatus();

    void onRefresh(int position);

}
