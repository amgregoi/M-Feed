package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaActivityMap;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.ChapterOrderEvent;

import butterknife.ButterKnife;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaPresenterImpl implements MangaPresenter {
    public static final String TAG = MangaPresenterImpl.class.getSimpleName();
    public static final String MANGA_KEY = TAG + ":MANGA";

    private ViewPagerAdapterManga mViewPagerAdapterManga;
    private CharSequence Titles[] = {"Info", "Chapters"};
    private int numbtabs = 2;
    private Manga mManga;

    private MangaActivityMap mMangaMapper;

    public MangaPresenterImpl(MangaActivityMap map) {
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
            Log.e("RAWR", title);
            mManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                    .query(Manga.class).withSelection("mTitle = ?", title).get();
        }

        mViewPagerAdapterManga = new ViewPagerAdapterManga(((FragmentActivity) mMangaMapper.getContext()).getSupportFragmentManager(), Titles, numbtabs, mManga.getTitle());
        mMangaMapper.setActivityTitle(mManga.getTitle());
        mMangaMapper.registerAdapter(mViewPagerAdapterManga);
        mMangaMapper.setupSlidingTabLayout();
        mMangaMapper.setupToolBar();
    }

    @Override public void onResume() {
        BusProvider.getInstance().register(this);
    }

    @Override public void onPause() {
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroy() {
        ButterKnife.unbind(mMangaMapper);
    }

    @Override public void chapterOrderButtonClick() {
        BusProvider.getInstance().post(new ChapterOrderEvent());
    }
}
