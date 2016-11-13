package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.MangaActivity.MangaPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.ChapterFragment;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.UI.ReaderActivity.ReaderActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ReaderPresenter implements IReader.ActivityPresenter {
    public final static String TAG = ReaderPresenter.class.getSimpleName();
    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String CHAPTER_POSITION = TAG + ":POSITION";
    public final static String SCREEN_ORIENTATION = TAG + ":SCREEN";


    private IReader.ActivityView mReaderMap;
    private ChapterPageAdapter mChapterPagerAdapter;
    private ArrayList<Chapter> mChapterList;
    private int mChapterPosition;

    private boolean mLandscapeOrientationLocked = false;

    public ReaderPresenter(IReader.ActivityView aMap) {
        mReaderMap = aMap;
    }

    @Override
    public void onSaveState(Bundle aSave) {
        if (mChapterList != null) aSave.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
        aSave.putInt(CHAPTER_POSITION, mChapterPosition);
        aSave.putBoolean(SCREEN_ORIENTATION, mLandscapeOrientationLocked);
    }

    @Override
    public void onRestoreState(Bundle aRestore) {
        if (aRestore.containsKey(CHAPTER_LIST_KEY))
            mChapterList = new ArrayList<>(aRestore.getParcelableArrayList(CHAPTER_LIST_KEY));
        if (aRestore.containsKey(CHAPTER_POSITION))
            mChapterPosition = aRestore.getInt(CHAPTER_POSITION);
        if(aRestore.containsKey(SCREEN_ORIENTATION))
            mLandscapeOrientationLocked = aRestore.getBoolean(SCREEN_ORIENTATION);
    }

    @Override
    public void init(Bundle aBundle) {
        if (mChapterList == null) {
            mChapterList = new ArrayList<>(aBundle.getParcelableArrayList(MangaPresenter.CHAPTER_LIST_KEY));
            mChapterPosition = aBundle.getInt(MangaPresenter.LIST_POSITION_KEY);
        }

        mChapterPagerAdapter = new ChapterPageAdapter(((ReaderActivity) mReaderMap).getSupportFragmentManager(), mChapterList);
        mReaderMap.registerAdapter(mChapterPagerAdapter);
        mReaderMap.setCurrentChapter(mChapterPosition);
        mReaderMap.setupToolbar();
        mReaderMap.setScreenOrientation(mLandscapeOrientationLocked);
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

    @Override
    public void toggleVerticalScrollSettings(int aPosition){
        ((ChapterFragment) mChapterPagerAdapter.getItem(aPosition)).toggleVerticalScrollSettings();
    }

    @Override
    public void toggleOrientation(){
        mLandscapeOrientationLocked = !mLandscapeOrientationLocked;
    }

    @Override
    public boolean getOrientation(){
        return mLandscapeOrientationLocked;
    }
}
