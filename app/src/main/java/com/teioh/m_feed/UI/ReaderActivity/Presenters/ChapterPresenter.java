package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface ChapterPresenter extends LifeCycleMap {

    void getImageUrls();

    void toggleToolbar();

    void setToNextChapter();

    void setToPreviousChapter();

    void updateOffsetCounter(int aOffset, int aPosition);

    void updateState(int aState);

    void updateToolbarComplete();

    void updateCurrentPage(int aPosition);

    void updateChapterViewStatus();

    void onRefresh(int aPosition);


}
