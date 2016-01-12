package com.teioh.m_feed.UI.ReaderActivity.Presenters.Mappers;


import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.PageAdapterMap;

public interface ReaderActivityMap extends BaseContextMap, PageAdapterMap{

    void setCurrentChapter(int position);

}
