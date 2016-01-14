package com.teioh.m_feed.UI.ReaderActivity.View.Mappers;


import android.support.v4.view.ViewPager;

import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.PageAdapterMap;

public interface ReaderActivityMapper extends BaseContextMap, PageAdapterMap{

    void setCurrentChapter(int position);

    void incrementChapter();

    void decrementChapter();

    void hideToolbar(long delay);

    void showToolbar();

    void updateToolbar(String title, int size, int page);

    void setupToolbar();

    void updateCurrentPage(int position);
}
