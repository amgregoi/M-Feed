package com.teioh.m_feed.UI.ReaderActivity.View.Mappers;

import android.support.v4.view.ViewPager;

import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.PageAdapterMap;
import com.teioh.m_feed.UI.ReaderActivity.View.Widgets.GestureViewPager;

public interface ChapterReaderMapper extends BaseContextMap, PageAdapterMap, ViewPager.OnPageChangeListener, GestureViewPager.OnSingleTapListener{
    void setupOnSingleTapListener();

//    void setupToolbar(String title, int size);
//
    void updateToolbar();

    void incrementChapterPage();

    void decrementChapterPage();

    void updateChapterViewStatus();

//    void incrementCurrentPage(int page);

//    void hideToolbar(long delay);
//
//    void showToolbar();

}
