package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.UI.MangaActivity.View.Mappers.MangaActivityMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.ChapterOrderEvent;
import com.teioh.m_feed.WebSources.WebSource;

import butterknife.ButterKnife;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaPresenterImpl implements MangaPresenter {
    public static final String TAG = MangaPresenterImpl.class.getSimpleName();
    public static final String MANGA_KEY = TAG + ":MANGA";

    private CharSequence Titles[] = {"Info", "Chapters"};
    private Manga mManga;

    private MangaActivityMapper mMangaMapper;


    public MangaPresenterImpl(MangaActivityMapper map) {
        mMangaMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if(mManga != null){
            bundle.putParcelable(MANGA_KEY, mManga);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if(bundle.containsKey(MANGA_KEY)){
            mManga = bundle.getParcelable(MANGA_KEY);
        }
    }

    @Override public void init(Intent intent) {
        if(mManga == null) {
            String title = intent.getStringExtra(Manga.TAG);
            mManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).query(Manga.class)
                    .withSelection("mTitle = ? AND mSource = ?", title, WebSource.getCurrentSource()).get();
        }

        ViewPagerAdapterManga mViewPagerAdapterManga = new ViewPagerAdapterManga(((MangaActivity) mMangaMapper.getContext()).getSupportFragmentManager(), Titles, 2, mManga.getTitle());
        mMangaMapper.setActivityTitle(mManga.getTitle());
        mMangaMapper.registerAdapter(mViewPagerAdapterManga);
        mMangaMapper.setupSlidingTabLayout();
        mMangaMapper.setupToolBar();
    }

    @Override public void onResume() {
//        BusProvider.getInstance().register(this);
    }

    @Override public void onPause() {
//        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroy() {
        ButterKnife.unbind(mMangaMapper);
    }

    @Override public void chapterOrderButtonClick() {
//        BusProvider.getInstance().post(new ChapterOrderEvent());
    }
}
