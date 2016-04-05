package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.View.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterPresenterImpl implements ChapterPresenter {
    public static final String TAG = ChapterPresenterImpl.class.getSimpleName();
    public static final String CURRENT_URL_LIST_PARCELABLE_KEY = TAG + ":CURRENT";
    public static final String CHAPTER_POSITION_LIST_PARCELABLE_KEY = TAG + ":POSITION";

    private ChapterReaderMapper mChapterReaderMapper;

    private ArrayList<String> mChapterUrlList;
    private int mPosition, mPageOffsetCount, mChapterListSize;
    private boolean mToolbarShowing, mIsNext;
    private Chapter mChapter;
    private Subscription mImageListSubscription;
    private ImagePageAdapter mChapterPageAdapter;


    public ChapterPresenterImpl(ChapterReaderMapper map, Bundle bundle) {
        mChapterReaderMapper = map;

        mPosition = bundle.getInt(ChapterPageAdapter.POSITION_KEY);
        mChapter = bundle.getParcelable(Chapter.TAG + ":" + mPosition);
        mToolbarShowing = true;
    }

    @Override
    public void init(Bundle bundle) {
        if (mChapterUrlList == null) this.getImageUrls();
        else updateImageUrlList(mChapterUrlList);

        mPageOffsetCount = 0;
        mChapterReaderMapper.setupOnSingleTapListener();
        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), mChapter.getChapterTitle(), 1, 1);
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
        mImageListSubscription = WebSource.getChapterImageListObservable(mChapter.getChapterUrl())
                .doOnError(throwable -> Log.e(TAG, throwable.getMessage()))
                .subscribe(urlList -> updateImageUrlList(urlList));
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        if (mImageListSubscription != null) {
            mImageListSubscription.unsubscribe();
            mImageListSubscription = null;
        }
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
        mChapterReaderMapper.incrementChapter();
    }

    @Override
    public void setToPreviousChapter() {
        mChapterReaderMapper.decrementChapter();
    }

    @Override
    public void updateOffsetCounter(int offset, int position) {
        if (position == 0 || position == mChapterListSize - 1) {
            mPageOffsetCount++;
            mIsNext = position != 0;
        } else mPageOffsetCount = 0;
    }

    @Override
    public void updateState(int state) {
        if (mPageOffsetCount > 40 && state == 0) {
            if (mIsNext) setToNextChapter();
            else setToPreviousChapter();
        }
        mPageOffsetCount = 0;
    }

    @Override
    public void updateToolbar() {
        mChapterReaderMapper.updateToolbar(mChapter.getMangaTitle(), mChapter.getChapterTitle(), mChapterListSize, mPosition);
    }

    @Override
    public void updateCurrentPage(int position) {
        mChapterReaderMapper.updateCurrentPage(position);
    }

    @Override
    public void updateChapterViewStatus() {
        Chapter viewedChapter = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                .query(Chapter.class)
                .withSelection("mTitle = ? AND cNumber = ?", mChapter.getMangaTitle(), Integer.toString(mChapter.getChapterNumber()))
                .get();

        if (viewedChapter == null)
            cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(mChapter);
    }

    @Override
    public void onRefresh(int position) {
        mChapterPageAdapter.refreshView(position);
    }

    private void updateImageUrlList(List<String> urlList) {
        if (mChapterReaderMapper.getContext() != null) {
            if(urlList == null){
                Toast.makeText(mChapterReaderMapper.getContext(), "Failed to find chapter :'(", Toast.LENGTH_SHORT).show();
                mChapterReaderMapper.failedLoadChapter();
            }else {
                mChapterUrlList = new ArrayList<>(urlList);
                mChapterListSize = mChapterUrlList.size();
                mChapterPageAdapter = new ImagePageAdapter(mChapterReaderMapper.getContext(), mChapterUrlList);
                mChapterReaderMapper.registerAdapter(mChapterPageAdapter);
                updateToolbar();
            }
        }
    }
}
