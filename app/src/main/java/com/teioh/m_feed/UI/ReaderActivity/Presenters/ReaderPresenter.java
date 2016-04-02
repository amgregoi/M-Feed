package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;


public interface ReaderPresenter extends LifeCycleMap {

    void updateToolbar(int position);

    void incrementChapterPage(int position);

    void decrementChapterPage(int position);

    void updateChapterViewStatus(int position);

    void onRefreshButton(int position);
}
