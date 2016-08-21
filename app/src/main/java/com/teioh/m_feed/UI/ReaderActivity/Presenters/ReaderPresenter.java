package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;


public interface ReaderPresenter extends LifeCycleMap {

    void updateToolbar(int aPosition);

    void incrementChapterPage(int aPosition);

    void decrementChapterPage(int aPosition);

    void updateChapterViewStatus(int aPosition);

    void onRefreshButton(int aPosition);
}
