package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterListPresenterImpl;
import com.teioh.m_feed.UI.ReaderActivity.Adapters.ChapterPageAdapter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.Mappers.ReaderActivityMap;
import com.teioh.m_feed.UI.ReaderActivity.View.ReaderActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ReaderPresenterImpl implements ReaderPresenter {
    public final static String TAG = ReaderPresenterImpl.class.getSimpleName();
    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String CHAPTER_POSITION = TAG + ":POSITION";


    private ReaderActivityMap mReaderMap;
    ChapterPageAdapter mChapterPagerAdapter;
    ArrayList<Chapter> mChapterList;
    int mChapterPosition;

    public ReaderPresenterImpl(ReaderActivityMap map) {
        mReaderMap = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if(mChapterList != null) bundle.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
        bundle.putInt(CHAPTER_POSITION, mChapterPosition);
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if(bundle.containsKey(CHAPTER_LIST_KEY)) mChapterList = new ArrayList<>(bundle.getParcelableArrayList(CHAPTER_LIST_KEY));
        if(bundle.containsKey(CHAPTER_POSITION)) mChapterPosition = bundle.getInt(CHAPTER_POSITION);
    }

    @Override
    public void init(Intent intent) {
        if(mChapterList == null) {
            mChapterList = new ArrayList<>(intent.getExtras().getParcelableArrayList(ChapterListPresenterImpl.CHAPTER_LIST_KEY));
            mChapterPosition = intent.getExtras().getInt(ChapterListPresenterImpl.LIST_POSITION_KEY);
        }

        mChapterPagerAdapter = new ChapterPageAdapter(((ReaderActivity) mReaderMap).getSupportFragmentManager(), mChapterList);
        mReaderMap.registerAdapter(mChapterPagerAdapter);
        mReaderMap.setCurrentChapter(mChapterPosition);
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
}
