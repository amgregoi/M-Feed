package com.teioh.m_feed.UI.MangaActivity.Presenters;

import com.teioh.m_feed.Models.Chapter;

import java.util.List;

/**
 * Created by Asus1 on 11/7/2015.
 */
public interface  ChapterListPresenter{

    void getChapterList();

    void onChapterClicked(int position);

    void updateChapterList(List<Chapter> chapters);

    void onPause();

    void onResume();

    void butterKnifeUnbind();

}
