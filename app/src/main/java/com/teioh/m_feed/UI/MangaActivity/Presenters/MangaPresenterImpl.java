package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaActivityMap;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.ChapterOrderEvent;

import butterknife.ButterKnife;

/**
 * Created by amgregoi on 1/2/16.
 */
public class MangaPresenterImpl implements MangaPresenter {
    private ViewPagerAdapterManga mViewPagerAdapterManga;
    private CharSequence Titles[] = {"Info", "Chapters"};
    private int numbtabs = 2;

    private MangaActivityMap mMangaMapper;

    public MangaPresenterImpl(MangaActivityMap map) {
        mMangaMapper = map;
    }

    @Override public void initialize(Manga item) {
        mViewPagerAdapterManga = new ViewPagerAdapterManga(((FragmentActivity) mMangaMapper.getContext()).getSupportFragmentManager(), Titles, numbtabs, item);
        mMangaMapper.setActivityTitle(item.getTitle());
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
