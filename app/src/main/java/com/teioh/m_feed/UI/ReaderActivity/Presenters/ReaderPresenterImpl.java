package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
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
    public void onSaveState(Bundle bundle) {
        if (mChapterList != null) bundle.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
        bundle.putInt(CHAPTER_POSITION, mChapterPosition);
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(CHAPTER_LIST_KEY))
            mChapterList = new ArrayList<>(bundle.getParcelableArrayList(CHAPTER_LIST_KEY));
        if (bundle.containsKey(CHAPTER_POSITION))
            mChapterPosition = bundle.getInt(CHAPTER_POSITION);
    }

    @Override
    public void init(Intent intent) {
        if (mChapterList == null) {
            mChapterList = new ArrayList<>(intent.getExtras().getParcelableArrayList(MangaPresenterImpl.CHAPTER_LIST_KEY));
            mChapterPosition = intent.getExtras().getInt(MangaPresenterImpl.LIST_POSITION_KEY);
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
        ButterKnife.unbind(this);
    }

    @Override
    public void updateToolbar(int position) {
        ((ChapterFragment) mChapterPagerAdapter.getItem(position)).updateToolbar();
    }

    @Override
    public void incrementChapterPage(int position) {
        ((ChapterFragment) mChapterPagerAdapter.getItem(position)).incrementChapterPage();
    }

    @Override
    public void decrementChapterPage(int position) {
        ((ChapterFragment) mChapterPagerAdapter.getItem(position)).decrementChapterPage();
    }

    @Override
    public void updateChapterViewStatus(int position){
        ((ChapterFragment) mChapterPagerAdapter.getItem(position)).updateChapterViewStatus();
    }

    //TODO possibly remove
//    @Subscribe
//    public void onChangeChapter(ChangeChapter newChapter) {
//        if (newChapter.getIsNext()) {
//            mReaderMap.setCurrentChapter(mChapterPosition + 1);
//        } else {
//            mReaderMap.setCurrentChapter(mChapterPosition - 1);
//        }
//    }
}
