package com.teioh.m_feed.UI.ReaderActivity.View.Mappers;


import android.support.v4.view.ViewPager;

import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.Maps.PageAdapterMap;
import com.teioh.m_feed.UI.ReaderActivity.View.Fragments.ChapterFragment;

public interface ReaderActivityMapper extends BaseContextMap, PageAdapterMap, ViewPager.OnPageChangeListener, Listeners.ReaderListener {

    void setCurrentChapter(int position);

    void setupToolbar();

}
