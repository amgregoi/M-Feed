package com.teioh.m_feed.MainPackage.Presenters.Mappers;

public interface SwipeRefreshMapper {
    void startRefresh();
    void stopRefresh();
    void setupRefreshListener();
}
