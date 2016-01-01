package com.teioh.m_feed.UI.MangaActivity.Presenters;

import java.util.List;

/**
 * Created by Asus1 on 11/7/2015.
 */
public interface ChapterReaderPresenter {

    void getImageUrls();

    void updateView(List<String> urlList);

    void butterKnifeUnbind();

    void updateOffset(int offset, int position);

    void updateState(int state);

}
