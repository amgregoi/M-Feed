package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterReaderPresenterImpl implements ChapterReaderPresenter {

    private ChapterReaderMapper mChapterReaderMapper;
    private ChapterPageAdapter mChapterAdapter;

    private ArrayList<String> nextUrlList, curUrlList, prevUrlList;
    private ArrayList<Chapter> mChapterList;
    private int mPosition, curChapterPageCount, pageOffsetCount, pageDirection;
    private boolean mChapterOrderDescending;

    private Observable<List<String>> nextObservable, prevObservable;

    public ChapterReaderPresenterImpl(ChapterReaderMapper map, Bundle b) {
        mChapterReaderMapper = map;
        mChapterList = b.getParcelableArrayList("Chapters");
        mPosition = b.getInt("Position");
        mChapterOrderDescending = b.getBoolean("Order");
        this.getImageUrls();
    }

    @Override
    public void getImageUrls() {
        Observable<List<String>> observableImageUrlList = WebSource.getChapterImageListObservable(mChapterList.get(mPosition).getChapterUrl());
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
            mChapterAdapter = new ChapterPageAdapter(mChapterReaderMapper.getContext(), curUrlList);
            mChapterReaderMapper.registerAdapter(mChapterAdapter);
        }
    }

    @Override
    public void updateOffsetCounter(int offset, int position) {
        if (position == 0 || position == curChapterPageCount - 1) {
            if (offset == 0) pageOffsetCount++;
            else pageOffsetCount = 0;

            if (position == 0) pageDirection = 0;
            else pageDirection = 1;
        }
    }

    @Override
    public void updateState(int state) {
        if (pageOffsetCount > 50 && state == 0) {
            if (mChapterOrderDescending) {
                if (pageDirection == 0 && mPosition < mChapterList.size() - 1) {  //backward (previous)
                    mPosition++;
                    nextUrlList = new ArrayList<>(curUrlList);
                    updateView(prevUrlList);
                    getPrevList();

                } else if (pageDirection == 1 && mPosition > 0) { //forward (next)
                    mPosition--;
                    prevUrlList = new ArrayList<>(curUrlList);
                    updateView(nextUrlList);
                    getNextList();
                }
            } else {
                if (pageDirection == 0 && mPosition > 0) {  //backward (previous)
                    mPosition--;
                    nextUrlList = new ArrayList<>(curUrlList);
                    updateView(prevUrlList);
                    getPrevList();

                } else if (pageDirection == 1 && mPosition < mChapterList.size() - 1) { //forward (next)
                    mPosition++;
                    prevUrlList = new ArrayList<>(curUrlList);
                    updateView(nextUrlList);
                    getNextList();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mChapterReaderMapper);
    }

    private void getNextList() {
        if (mChapterOrderDescending) {
            if (mPosition > 0) {
                if (nextObservable != null) {
                    nextObservable.unsubscribeOn(Schedulers.io());
                }
                nextObservable = WebSource.getChapterImageListObservable(mChapterList.get(mPosition - 1).getChapterUrl());
                nextObservable.subscribe(urlList -> setNextList(urlList));
            }
        } else {
            if (mPosition < mChapterList.size() - 1) {
                if (nextObservable != null) {
                    nextObservable.unsubscribeOn(Schedulers.io());
                }
                nextObservable = WebSource.getChapterImageListObservable(mChapterList.get(mPosition + 1).getChapterUrl());
                nextObservable.subscribe(urlList -> setNextList(urlList));
            }
        }
    }

    private void setNextList(List<String> urlList) {
        nextUrlList = new ArrayList<>(urlList);
        nextObservable = null;
    }

    private void getPrevList() {
        if (mChapterOrderDescending) {
            if (mPosition < mChapterList.size() - 1) {
                if (prevObservable != null) {
                    prevObservable.unsubscribeOn(Schedulers.io());
                }
                prevObservable = WebSource.getChapterImageListObservable(mChapterList.get(mPosition + 1).getChapterUrl());
                prevObservable.subscribe(urlList -> setPrevList(urlList));
            }
        } else {
            if (mPosition > 0) {
                if (prevObservable != null) {
                    prevObservable.unsubscribeOn(Schedulers.io());
                }
                prevObservable = WebSource.getChapterImageListObservable(mChapterList.get(mPosition - 1).getChapterUrl());
                prevObservable.subscribe(urlList -> setPrevList(urlList));
            }
        }
    }

    private void setPrevList(List<String> urlList) {
        prevUrlList = new ArrayList<>(urlList);
        prevObservable = null;
    }

}
