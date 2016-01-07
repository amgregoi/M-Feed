package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterListAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterListMapper;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.ChapterReaderFragment;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.ChapterOrderEvent;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;

public class ChapterListPresenterImpl implements ChapterListPresenter {

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
        observableChapterList = WebSource.getChapterListObservable(mManga.getMangaURL());
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
        ((Fragment) mChapterListMapper).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void updateChapterList(List<Chapter> chapters) {
        //TODO
        //http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
        //expandable listview for sites with multiple release versions
        if (chapters != null && mChapterListMapper.getContext() != null) {
            chapterList = new ArrayList<>(chapters);
            mAdapter = new ChapterListAdapter(mChapterListMapper.getContext(), R.layout.chapter_list_item, chapterList);

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
    public void onDestroyView() {
        ButterKnife.unbind(mChapterListMapper);
    }

    @Subscribe public void onChapterOrderChange(ChapterOrderEvent event) {
        if (chapterList != null) {
            mChapterOrderDescending = !mChapterOrderDescending;
            Collections.reverse(chapterList);
            mAdapter = new ChapterListAdapter(mChapterListMapper.getContext(), R.layout.chapter_list_item, chapterList);
            mChapterListMapper.registerAdapter(mAdapter);
        }
    }

}