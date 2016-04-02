package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface MangaPresenter extends LifeCycleMap {

    void chapterOrderButtonClick();

    void onFollwButtonClick();

    void onChapterClicked(Chapter chapter);

    void onMALSyncClicked();

    String getImageUrl();
}
