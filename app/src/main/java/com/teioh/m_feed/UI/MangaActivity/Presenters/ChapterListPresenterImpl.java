package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterListAdapter;
import com.teioh.m_feed.UI.MangaActivity.View.Mappers.ChapterListMapper;
import com.teioh.m_feed.UI.ReaderActivity.View.ReaderActivity;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.ChapterOrderEvent;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ChapterListPresenterImpl implements ChapterListPresenter {
    public final static String TAG = ChapterListPresenterImpl.class.getSimpleName();
    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String MANGA_KEY = TAG + ":MANGA";
    public final static String ORDER_DESCENDING_KEY = TAG + ":DESCENDING";
    public final static String LIST_POSITION_KEY = TAG + ":POSITION";

    private Observable<List<Chapter>> mObservableChapterList;
    private ArrayList<Chapter> mChapterList;
    private ChapterListAdapter mAdapter;
    private Manga mManga;
    private boolean mChapterOrderDescending;

    private ChapterListMapper mChapterListMapper;

    public ChapterListPresenterImpl(ChapterListMapper map, Bundle b) {
        String title = b.getString(Manga.TAG);
        mManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                .query(Manga.class).withSelection("mTitle = ? AND mSource = ?", title, WebSource.getCurrentSource()).get();

        mChapterListMapper = map;
        mChapterOrderDescending = true;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mChapterList != null) bundle.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
        if(mManga != null) bundle.putParcelable(MANGA_KEY, mManga);
        bundle.putBoolean(ORDER_DESCENDING_KEY, mChapterOrderDescending);
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(CHAPTER_LIST_KEY))
            mChapterList = new ArrayList<>(bundle.getParcelableArrayList(CHAPTER_LIST_KEY));

        if(bundle.containsKey(MANGA_KEY))
            mManga = bundle.getParcelable(MANGA_KEY);

        if(bundle.containsKey(ORDER_DESCENDING_KEY))
            mChapterOrderDescending = bundle.getBoolean(ORDER_DESCENDING_KEY);
    }

    @Override
    public void init() {
        if (mChapterList == null) {
            mChapterListMapper.setupSwipeRefresh();
            this.getChapterList();
        } else {
            this.updateChapterList(mChapterList);
        }
    }

    @Override
    public void getChapterList() {
        mObservableChapterList = WebSource.getChapterListObservable(mManga.getMangaURL());
        mObservableChapterList.subscribe(chapters -> updateChapterList(chapters));
    }

    @Override
    public void onChapterClicked(Chapter chapter) {
        ArrayList<Chapter> newChapterList = new ArrayList<>(mChapterList);

        if(mChapterOrderDescending) Collections.reverse(newChapterList);

        int position = newChapterList.indexOf(chapter);
        Intent intent = new Intent(mChapterListMapper.getContext(), ReaderActivity.class);
        intent.putParcelableArrayListExtra(CHAPTER_LIST_KEY, newChapterList);
        intent.putExtra(LIST_POSITION_KEY, position);
        mChapterListMapper.getContext().startActivity(intent);
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);

        if(mObservableChapterList != null) {
            mObservableChapterList.unsubscribeOn(Schedulers.io());
            mObservableChapterList = null;
        }
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mChapterListMapper);
    }

    @Subscribe
    public void onChapterOrderChange(ChapterOrderEvent event) {
        if (mChapterList != null) {
            mChapterOrderDescending = !mChapterOrderDescending;
            Collections.reverse(mChapterList);
            mAdapter = new ChapterListAdapter(mChapterListMapper.getContext(), R.layout.chapter_list_item, mChapterList);
            mChapterListMapper.registerAdapter(mAdapter);
        }
    }

    private void updateChapterList(List<Chapter> chapters) {
        if (chapters != null && mChapterListMapper.getContext() != null) {
            mChapterList = new ArrayList<>(chapters);
            mAdapter = new ChapterListAdapter(mChapterListMapper.getContext(), R.layout.chapter_list_item, mChapterList);
            mChapterListMapper.registerAdapter(mAdapter);
            mChapterListMapper.stopRefresh();
        }
    }

}
