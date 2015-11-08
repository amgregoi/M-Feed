package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.WebSources.MangaJoy;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class ChapterReaderPresenterImpl implements  ChapterReaderPresenter{

    private Chapter mChapter;
    private ChapterReaderMapper mChapterReaderMapper;

    public ChapterReaderPresenterImpl(ChapterReaderMapper map, Bundle b){
        mChapterReaderMapper = map;
        mChapter = b.getParcelable("Chapter");
    }

    @Override
    public void getImageUrls() {
        Observable<List<String>> observableImageUrlList = MangaJoy.getChapterImageListObservable(mChapter.getChapterUrl());
        observableImageUrlList.subscribe(urlList -> updateView(urlList));
    }

    @Override
    public void updateView(List<String> urlList) {
        ArrayList<String> urls = new ArrayList<>(urlList);
        ChapterPageAdapter chapterAdapter = new ChapterPageAdapter(mChapterReaderMapper.getContext(), urls);
        mChapterReaderMapper.registerAdapter(chapterAdapter);
    }
}
