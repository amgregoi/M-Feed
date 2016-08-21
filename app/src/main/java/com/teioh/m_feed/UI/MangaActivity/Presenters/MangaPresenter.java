package com.teioh.m_feed.UI.MangaActivity.Presenters;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface MangaPresenter extends LifeCycleMap {

    void chapterOrderButtonClick();

    void onFollwButtonClick(int aValue);

    void onUnfollowButtonClick();

    void onChapterClicked(Chapter aChapter);

    void onMALSyncClicked();

    String getImageUrl();
}
