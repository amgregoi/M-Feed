package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterListPresenterImpl;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterPresenterImpl implements ChapterPresenter {
    public static final String TAG = ChapterPresenterImpl.class.getSimpleName();
    private static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":CURRENT";
    private static final String CHAPTER_LIST_PARCELABLE_KEY = TAG + ":CHAPTER";
    private static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":POSITION";

    private ChapterReaderMapper mChapterReaderMapper;
    private ImagePageAdapter mChapterPageAdapter;

    private ArrayList<String> mCurUrlList;
    private ArrayList<Chapter> mChapterList;
    private int mPosition, mCurChapterPageCount;
    private boolean mToolbarShowing;
    private Chapter mChapter;

    private Observable<List<String>> mNextChapterObservable, mPrevChapterObservable;

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
        if (mCurUrlList == null) this.getImageUrls();
        else updateImageUrlList(mCurUrlList);

        mChapterReaderMapper.setupOnSingleTapListener();
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mCurUrlList != null) {
            bundle.putStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY, mCurUrlList);
        }
        if (mChapterList != null) {
            bundle.putParcelableArrayList(CHAPTER_LIST_PARCELABLE_KEY, mChapterList);
        }
        bundle.putInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY, mPosition);
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(CURRENT_URL_LIST_PARCELABLE_KEY)) {
            mCurUrlList = bundle.getStringArrayList(CURRENT_URL_LIST_PARCELABLE_KEY);
        }
        if (bundle.containsKey(CHAPTER_LIST_PARCELABLE_KEY)) {
            mChapterList = bundle.getParcelableArrayList(CHAPTER_LIST_PARCELABLE_KEY);
        }
        mPosition = bundle.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
    }

    @Override
    public void getImageUrls() {
//        Observable<List<String>> observableImageUrlList = WebSource.getChapterImageListObservable(mChapterList.get(mPosition).getChapterUrl());
        Observable<List<String>> observableImageUrlList = WebSource.getChapterImageListObservable(mChapter.getChapterUrl());
        observableImageUrlList.subscribe(urlList -> updateImageUrlList(urlList));
    }

    @Override
    public void onPause() {
        if (mNextChapterObservable != null) {
            mNextChapterObservable.unsubscribeOn(Schedulers.io());
            mNextChapterObservable = null;
        }
        if (mPrevChapterObservable != null) {
            mPrevChapterObservable.unsubscribeOn(Schedulers.io());
            mPrevChapterObservable = null;
        }
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

    private void updateImageUrlList(List<String> urlList) {
        if (mChapterReaderMapper.getContext() != null) {
            mCurUrlList = new ArrayList<>(urlList);
            mCurChapterPageCount = mCurUrlList.size();
            mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mCurUrlList);
            mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
            mChapterReaderMapper.setupToolbar(mChapter.toString(), mCurUrlList.size());
        }
    }
}
