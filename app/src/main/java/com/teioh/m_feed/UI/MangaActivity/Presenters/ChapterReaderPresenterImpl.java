package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;
import android.util.Log;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterReaderPresenterImpl implements ChapterReaderPresenter {

    public static final String TAG = ChapterReaderPresenter.class.getSimpleName();

    private static final String NEXT_URL_LIST_PARCELABLE_KEY = TAG + ":" + "NEXT";
    private static final String PREV_URL_LIST_PARCELABLE_KEY = TAG + ":" + "PREV";
    private static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":" + "CURRENT";
    private static final String CHAPTER_LIST_PARCELABLE_KEY = TAG + ":" + "CHAPTER";
    private static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":" + "POSITION";
    private static final String DESCENDING_PARCELABLE_KEY = TAG + ":" + "DESCENDING";


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
    }

    @Override
    public void initialize() {
        if (curUrlList == null) this.getImageUrls();
        else updateView(curUrlList);

        if (prevUrlList == null) this.getPrevList();
        if (nextUrlList == null) this.getNextList();
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (nextUrlList != null) {
            bundle.putStringArrayList(NEXT_URL_LIST_PARCELABLE_KEY, nextUrlList);
        }
        if (prevUrlList != null) {
            bundle.putStringArrayList(PREV_URL_LIST_PARCELABLE_KEY, prevUrlList);
        }
        if (curUrlList != null) {
            bundle.putStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY, curUrlList);
        }
        if (mChapterList != null) {
            bundle.putParcelableArrayList(CHAPTER_LIST_PARCELABLE_KEY, mChapterList);
        }

        bundle.putInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY, mPosition);
        bundle.putBoolean(DESCENDING_PARCELABLE_KEY, mChapterOrderDescending);
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(NEXT_URL_LIST_PARCELABLE_KEY)) {
            nextUrlList = bundle.getStringArrayList(NEXT_URL_LIST_PARCELABLE_KEY);
        }
        if (bundle.containsKey(PREV_URL_LIST_PARCELABLE_KEY)) {
            prevUrlList = bundle.getStringArrayList(PREV_URL_LIST_PARCELABLE_KEY);
        }
        if (bundle.containsKey(CURRENT_URL_LIST_PARCELABLE_KEY)) {
            curUrlList = bundle.getStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY);
        }
        if (bundle.containsKey(CHAPTER_LIST_PARCELABLE_KEY)) {
            mChapterList = bundle.getParcelableArrayList(CHAPTER_LIST_PARCELABLE_KEY);
        }
        mPosition = bundle.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
        mChapterOrderDescending = bundle.getBoolean(DESCENDING_PARCELABLE_KEY);
    }

    @Override
    public void getImageUrls() {
        Observable<List<String>> observableImageUrlList = WebSource.getChapterImageListObservable(mChapterList.get(mPosition).getChapterUrl());
        observableImageUrlList.subscribe(urlList -> updateView(urlList));
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
            if (offset == 0) {
                pageOffsetCount++;
                Log.e("RAWR", "~ " + pageOffsetCount);
            } else pageOffsetCount = 0;

            if (position == 0) pageDirection = 0;
            else pageDirection = 1;
        } else pageOffsetCount = 0;
    }

    @Override
    public void updateState(int state) {
        if (pageOffsetCount > 50 && state == 0) {
            Log.e("RAWR", "OffsetCount at transition " + pageOffsetCount);
            if (mChapterOrderDescending) {
                if (pageDirection == 0 && mPosition < mChapterList.size() - 1 && prevUrlList != null) {  //backward (previous)
                    mPosition++;
                    nextUrlList = new ArrayList<>(curUrlList);
                    updateView(prevUrlList);
                    getPrevList();

                } else if (pageDirection == 1 && mPosition > 0 && nextUrlList != null) { //forward (next)
                    mPosition--;
                    prevUrlList = new ArrayList<>(curUrlList);
                    updateView(nextUrlList);
                    getNextList();
                }
            } else {
                if (pageDirection == 0 && mPosition > 0 && prevUrlList != null) {  //backward (previous)
                    mPosition--;
                    nextUrlList = new ArrayList<>(curUrlList);
                    updateView(prevUrlList);
                    getPrevList();

                } else if (pageDirection == 1 && mPosition < mChapterList.size() - 1 && nextUrlList != null) { //forward (next)
                    mPosition++;
                    prevUrlList = new ArrayList<>(curUrlList);
                    updateView(nextUrlList);
                    getNextList();
                }
            }
        }
        pageOffsetCount = 0;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mChapterReaderMapper);
    }

    private void getNextList() {
        nextUrlList = null;
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
        prevUrlList = null;
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
