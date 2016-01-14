package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.View.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.UI.ReaderActivity.View.ReaderActivity;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterPresenterImpl implements ChapterPresenter {
    public static final String TAG = ChapterPresenterImpl.class.getSimpleName();
    private static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":CURRENT";
    private static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":POSITION";

    private ChapterReaderMapper mChapterReaderMapper;
    private ImagePageAdapter mChapterPageAdapter;

    private ArrayList<String> mChapterUrlList;
    private int mPosition, mPageOffsetCount, mChapterListSize;
    private boolean mToolbarShowing, mIsNext;
    private Chapter mChapter;

    private ReaderActivity mBaseActivity;

    public ChapterPresenterImpl(ChapterReaderMapper map, Bundle bundle) {
        mChapterReaderMapper = map;

        mPosition = bundle.getInt(ChapterPageAdapter.POSITION_KEY);
        mChapter = bundle.getParcelable(Chapter.TAG + ":" + mPosition);
        mToolbarShowing = true;

        mBaseActivity = ((ReaderActivity) ((Fragment) mChapterReaderMapper).getActivity());

    }

    @Override
    public void init() {
        if (mChapterUrlList == null) this.getImageUrls();
        else updateImageUrlList(mChapterUrlList);

        mPageOffsetCount = 0;
        mChapterReaderMapper.setupOnSingleTapListener();
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mChapterUrlList != null) {
            bundle.putStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY, mChapterUrlList);
        }
        bundle.putInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY, mPosition);
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(CURRENT_URL_LIST_PARCELABLE_KEY)) {
            mChapterUrlList = bundle.getStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY);
        }

        mPosition = bundle.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
    }

    @Override
    public void getImageUrls() {
        Observable<List<String>> observableImageUrlList = WebSource.getChapterImageListObservable(mChapter.getChapterUrl());
        observableImageUrlList.subscribe(urlList -> updateImageUrlList(urlList));
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        mBaseActivity = null;
    }

    @Override
    public void toggleToolbar() {
        if (mToolbarShowing) {
            mToolbarShowing = false;
            mBaseActivity.hideToolbar(0);
        } else {
            mToolbarShowing = true;
            mBaseActivity.showToolbar();
        }
    }

    @Override
    public void setToNextChapter() {
//        BusProvider.getInstance().post(new ChangeChapter(true));
        mBaseActivity.incrementChapter();
    }

    @Override
    public void setToPreviousChapter() {
//        BusProvider.getInstance().post(new ChangeChapter(false));
        mBaseActivity.decrementChapter();

    }

    @Override
    public void updateOffsetCounter(int offset, int position) {
        if (position == 0 || position == mChapterListSize - 1) {
            if (offset == 0) {
                mPageOffsetCount++;
            } else mPageOffsetCount = 0;

            if (position == 0) mIsNext = false;
            else mIsNext = true;
        } else mPageOffsetCount = 0;
    }

    @Override
    public void updateState(int state) {
        if (mPageOffsetCount > 50 && state == 0) {
            if (mIsNext) setToNextChapter();
            else setToPreviousChapter();
        }
        mPageOffsetCount = 0;
    }

    @Override
    public void updateToolbar() {
        mBaseActivity.setupToolbar(mChapter.toString(), mChapterListSize, mPosition);
    }

    @Override
    public void updateCurrentPage(int position) {
        mBaseActivity.updateCurrentPage(position);
    }

    @Override
    public void updateChapterViewStatus(){
        Chapter viewedChapter = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                .query(Chapter.class)
                .withSelection("mTitle = ? AND cNumber = ?", mChapter.getMangaTitle(), Integer.toString(mChapter.getChapterNumber()))
                .get();

        if (viewedChapter == null)
            cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(mChapter);
    }

    private void updateImageUrlList(List<String> urlList) {
        if (mChapterReaderMapper.getContext() != null) {
            mChapterUrlList = new ArrayList<>(urlList);
            mChapterListSize = mChapterUrlList.size();
            mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mChapterUrlList);
            mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
            updateToolbar();
        }
    }
}
