package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;

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

    public ChapterPresenterImpl(ChapterReaderMapper map, Bundle b) {
        mChapterReaderMapper = map;

        mPosition = b.getInt(ChapterPageAdapter.POSITION_KEY);
        mChapter = b.getParcelable(Chapter.TAG + ":" + mPosition);
        mToolbarShowing = true;
        mChapterReaderMapper.updateToolbarTitle(mChapter.toString());

        Chapter viewedChapter = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                .query(Chapter.class)
                .withSelection("mTitle = ? AND cNumber = ?", mChapter.getMangaTitle(), Integer.toString(mChapter.getChapterNumber()))
                .get();

        if (viewedChapter == null)
            cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(mChapter);

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
    public void onResume(){
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public void toggleToolbar() {
        if (mToolbarShowing) {
            mToolbarShowing = false;
            mChapterReaderMapper.hideToolbar(0);
        } else {
            mToolbarShowing = true;
            mChapterReaderMapper.showToolbar();
        }
    }

    @Override
    public void setToNextChapter() {
//        BusProvider.getInstance().post(new ChangeChapter(true));
        ((ReaderActivity)((Fragment) mChapterReaderMapper).getActivity()).incrementChapter();
    }

    @Override
    public void setToPreviousChapter() {
//        BusProvider.getInstance().post(new ChangeChapter(false));
        ((ReaderActivity)((Fragment) mChapterReaderMapper).getActivity()).decrementChapter();

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

    private void updateImageUrlList(List<String> urlList) {
        if (mChapterReaderMapper.getContext() != null) {
            mChapterUrlList = new ArrayList<>(urlList);
            mChapterListSize = mChapterUrlList.size();
            mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mChapterUrlList);
            mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
            mChapterReaderMapper.setupToolbar(mChapter.toString(), mChapterListSize);
        }
    }
}
