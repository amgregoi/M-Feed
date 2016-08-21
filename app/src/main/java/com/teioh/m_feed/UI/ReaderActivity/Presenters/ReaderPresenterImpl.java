package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenterImpl;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.View.Fragments.ChapterFragment;
import com.teioh.m_feed.UI.ReaderActivity.View.Mappers.ReaderActivityMapper;
import com.teioh.m_feed.UI.ReaderActivity.View.ReaderActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ReaderPresenterImpl implements ReaderPresenter {
    public final static String TAG = ReaderPresenterImpl.class.getSimpleName();
    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String CHAPTER_POSITION = TAG + ":POSITION";


    private ReaderActivityMapper mReaderMap;
    private ChapterPageAdapter mChapterPagerAdapter;
    private ArrayList<Chapter> mChapterList;
    private int mChapterPosition;

    public ReaderPresenterImpl(ReaderActivityMapper map) {
        mReaderMap = map;
    }

    @Override
    public void onSaveState(Bundle aSave) {
        if (mChapterList != null) aSave.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
        aSave.putInt(CHAPTER_POSITION, mChapterPosition);
    }

    @Override
    public void onRestoreState(Bundle aRestore) {
        if (aRestore.containsKey(CHAPTER_LIST_KEY))
            mChapterList = new ArrayList<>(aRestore.getParcelableArrayList(CHAPTER_LIST_KEY));
        if (aRestore.containsKey(CHAPTER_POSITION))
            mChapterPosition = aRestore.getInt(CHAPTER_POSITION);
    }

    @Override
    public void init(Bundle aBundle) {
        if (mChapterList == null) {
            mChapterList = new ArrayList<>(aBundle.getParcelableArrayList(MangaPresenterImpl.CHAPTER_LIST_KEY));
            mChapterPosition = aBundle.getInt(MangaPresenterImpl.LIST_POSITION_KEY);
        }

        mChapterPagerAdapter = new ChapterPageAdapter(((ReaderActivity) mReaderMap).getSupportFragmentManager(), mChapterList);
        mReaderMap.registerAdapter(mChapterPagerAdapter);
        mReaderMap.setCurrentChapter(mChapterPosition);
        mReaderMap.setupToolbar();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(mReaderMap.getContext());
        mReaderMap = null;
    }

    @Override
    public void updateToolbar(int aPosition) {
        ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition)).updateToolbar();
    }

    @Override
    public void incrementChapterPage(int aPosition) {
        ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition)).incrementChapterPage();
    }

    @Override
    public void decrementChapterPage(int aPosition) {
        ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition)).decrementChapterPage();
    }

    @Override
    public void updateChapterViewStatus(int aPosition){
        ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition)).updateChapterViewStatus();
    }

    @Override
    public void onRefreshButton(int aPosition) {
        ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition)).onRefresh();
    }


}
