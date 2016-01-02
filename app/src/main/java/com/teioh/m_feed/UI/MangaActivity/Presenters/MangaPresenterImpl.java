package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaActivityMap;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.SlidingTabLayout;

import butterknife.ButterKnife;

/**
 * Created by amgregoi on 1/2/16.
 */
public class MangaPresenterImpl implements MangaPresenter {
    private ViewPagerAdapterManga mViewPagerAdapterManga;
    private CharSequence Titles[] = {"Info", "Chapters"};
    private int Numbtabs = 2;
    private ActionBarDrawerToggle mDrawerToggle;

    private MangaActivityMap mMangaMapper;

    public MangaPresenterImpl(MangaActivityMap map){
        mMangaMapper = map;
    }

    @Override public void initialize(Manga item) {
        mViewPagerAdapterManga = new ViewPagerAdapterManga(((FragmentActivity) mMangaMapper.getContext()).getSupportFragmentManager(), Titles, Numbtabs, item);
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
}
