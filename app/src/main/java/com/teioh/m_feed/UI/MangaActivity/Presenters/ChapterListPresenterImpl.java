package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterListAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterListMapper;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.ChapterReaderFragment;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.ChapterOrderEvent;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.WebSources.MangaJoy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import nl.qbusict.cupboard.QueryResultIterable;
import rx.Observable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterListPresenterImpl implements  ChapterListPresenter{

    private Observable<List<Chapter>> observableChapterList;
    private ArrayList<Chapter> chapterList;
    private ChapterListAdapter mAdapter;
    private Manga mManga;
    private boolean mChapterOrderDescending;

    private ChapterListMapper mChapterListMapper;

    public ChapterListPresenterImpl(ChapterListMapper map, Bundle b) {
        mManga = b.getParcelable("Manga");
        mChapterListMapper = map;
        mChapterOrderDescending = true;
    }

    @Override
    public void getChapterList() {
        mChapterListMapper.setupSwipeRefresh();
        observableChapterList = MangaJoy.getChapterListObservable(mManga.getMangaURL());
        observableChapterList.subscribe(chapters -> updateChapterList(chapters));
    }

    @Override
    public void onChapterClicked(int position) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("Chapters", chapterList);
        b.putInt("Position", position);
        b.putBoolean("Order", mChapterOrderDescending);
        Fragment fragment = new ChapterReaderFragment();
        fragment.setArguments(b);
        ((Fragment)mChapterListMapper).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void updateChapterList(List<Chapter> chapters) {
        if (chapters != null && mChapterListMapper.getContext() != null) {
            chapterList = new ArrayList<>(chapters);
            mAdapter = new ChapterListAdapter(mChapterListMapper.getContext(), R.layout.chapter_list_item, chapterList);;
            mChapterListMapper.registerAdapter(mAdapter);
            mChapterListMapper.stopRefresh();
        }
    }

    @Override public void onPause() {
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onResume() {
        BusProvider.getInstance().register(this);
    }

    @Override
    public void butterKnifeUnbind() {
        ButterKnife.unbind(mChapterListMapper);
    }

    @Subscribe public void onChapterOrderChange(ChapterOrderEvent event) {
        if(chapterList != null) {
            mChapterOrderDescending = !mChapterOrderDescending;
            Collections.reverse(chapterList);
            mAdapter = new ChapterListAdapter(mChapterListMapper.getContext(), R.layout.chapter_list_item, chapterList);
            mChapterListMapper.registerAdapter(mAdapter);
        }
    }

    }
