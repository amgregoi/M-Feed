package com.teioh.m_feed.UI.ReaderActivity.View.Mappers;


import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.PageAdapterMap;

public interface ReaderActivityMapper extends BaseContextMap, PageAdapterMap{

    void setCurrentChapter(int position);

    void incrementChapter();

    void decrementChapter();

}
