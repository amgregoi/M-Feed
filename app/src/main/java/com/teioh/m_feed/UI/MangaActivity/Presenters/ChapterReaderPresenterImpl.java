package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;
import android.util.Log;

import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.MangaJoy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterReaderPresenterImpl implements  ChapterReaderPresenter{

    private ChapterReaderMapper mChapterReaderMapper;
    private ChapterPageAdapter chapterAdapter;

    private ArrayList<String> nextUrlList,  curUrlList, prevUrlList;
    private ArrayList<Chapter> mChapterList;
    private int mPosition, curChapterPageCount, pageOffsetCount, pageDirection;

    Observable<List<String>> nextObservable, prevObservable;

    public ChapterReaderPresenterImpl(ChapterReaderMapper map, Bundle b){
        mChapterReaderMapper = map;
        mChapterList = b.getParcelableArrayList("Chapters");
        mPosition = b.getInt("Position");
    }

    @Override
    public void getImageUrls() {
        Observable<List<String>> observableImageUrlList = MangaJoy.getChapterImageListObservable(mChapterList.get(mPosition).getChapterUrl());
        observableImageUrlList.subscribe(urlList -> updateView(urlList));
        getPrevList();
        getNextList();
    }

    @Override
    public void updateView(List<String> urlList) {
        if (mChapterReaderMapper.getContext() != null) {
            //TODO send an ottobus message to update chapter viewed in list fragment
            Chapter ch = mChapterList.get(mPosition);
            Chapter viewedChapter = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                    .query(Chapter.class)
                    .withSelection("mTitle = ? AND cNumber = ?", ch.getMangaTitle(), Integer.toString(ch.getChapterNumber()))
                    .get();

            if (viewedChapter == null)
                cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(ch);

            curUrlList = new ArrayList<>(urlList);
            curChapterPageCount = curUrlList.size();
            chapterAdapter = new ChapterPageAdapter(mChapterReaderMapper.getContext(), curUrlList);
            mChapterReaderMapper.registerAdapter(chapterAdapter);
        }
    }

    @Override
    public void updateOffset(int offset, int position){
        if(position == 0 || position == curChapterPageCount-1) {
            if (offset == 0) pageOffsetCount++;
            else pageOffsetCount = 0;

            if(position == 0) pageDirection = 0;
            else pageDirection = 1;
        }
    }

    @Override
    public void  updateState(int state){
        if(pageOffsetCount > 50 && state == 0)
        {
            if(pageDirection == 0) {  //backward (previous)
                mPosition++;
                nextUrlList = new ArrayList<>(curUrlList);
                updateView(prevUrlList);
                getPrevList();

            }
            else if(pageDirection == 1) { //forward (next)
                mPosition--;
                prevUrlList = new ArrayList<>(curUrlList);
                updateView(nextUrlList);
                getNextList();
            }
        }
    }


    private void getNextList(){
        if(nextObservable != null){
            nextObservable.unsubscribeOn(Schedulers.io());
        }
        nextObservable = MangaJoy.getChapterImageListObservable(mChapterList.get(mPosition - 1).getChapterUrl());
        nextObservable.subscribe(urlList -> setNextList(urlList));
    }

    private void setNextList(List<String> urlList){
        nextUrlList = new ArrayList<>(urlList);
        nextObservable = null;
    }

    private void getPrevList(){
        if(mPosition > 0) {
            if(prevObservable != null){
                prevObservable.unsubscribeOn(Schedulers.io());
            }
            prevObservable = MangaJoy.getChapterImageListObservable(mChapterList.get(mPosition + 1).getChapterUrl());
            prevObservable.subscribe(urlList -> setPrevList(urlList));
        }
    }

    private void setPrevList(List<String> urlList){
        prevUrlList = new ArrayList<>(urlList);
        prevObservable = null;
    }

    @Override
    public void butterKnifeUnbind() {
        ButterKnife.unbind(mChapterReaderMapper);
    }
}
