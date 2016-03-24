package com.teioh.m_feed.UI.MainActivity.Presenters;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.LoginActivity.View.LoginActivity;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.FollowedFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LibraryFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.RecentFragment;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.MainActivityMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.MangaHere;
import com.teioh.m_feed.WebSources.MangaJoy;
import com.teioh.m_feed.WebSources.MangaPark;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class MainPresenterImpl implements MainPresenter {
    public final static String TAG = MainPresenterImpl.class.getSimpleName();

    private final CharSequence mTabTitles[] = {"Recent", "Followed", "Library"};
    private ViewPagerAdapterMain mViewPagerAdapterMain;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivityMapper mMainMapper;


    public MainPresenterImpl(MainActivityMapper main) {
        mMainMapper = main;
    }

    @Override
    public void onSavedState(Bundle bundle) {

    }

    @Override
    public void onRestoreState(Bundle bundle) {

    }

    @Override
    public void init() {
        //creates database if fresh install
        MangaFeedDbHelper.getInstance().createDatabase();
        setupDrawerLayouts();

        mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mMainMapper.getContext()).getSupportFragmentManager(), mTabTitles, 3);

        //init layout
        mMainMapper.setupTabLayout();
        mMainMapper.registerAdapter(mViewPagerAdapterMain);
        mMainMapper.setupSearchView();
        mMainMapper.setupToolbar();

    }

    @Override
    public void setupDrawerLayoutListener(Toolbar mToolBar, DrawerLayout mDrawerLayout) {
        mDrawerToggle = new ActionBarDrawerToggle(((Activity) mMainMapper.getContext()), mDrawerLayout,
                mToolBar, R.string.app_name, R.string.Login) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(R.string.app_name);
                mMainMapper.onDrawerClose();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActionBar().setTitle(R.string.LoginBtn);
                mMainMapper.onDrawerOpen();
            }
        };
    }

    @Override
    public void onLogout() {
        Intent intent = new Intent(mMainMapper.getContext(), LoginActivity.class);
        mMainMapper.getContext().startActivity(intent);
    }

    @Override
    public void onResume() {
        mMainMapper.closeDrawer();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void updateQueryChange(String newTest) {
        if (mViewPagerAdapterMain.hasRegisteredFragments()) {
            ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onQueryTextChange(newTest);
            ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onQueryTextChange(newTest);
            ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onQueryTextChange(newTest);
        }
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(mMainMapper);
    }

    @Override
    public void onPostCreate() {
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDrawerItemChosen(int position) {
        switch (position) {
            case (0):
                //TODO need to do more research in MAL api options
                onLogout();
                return;
            case (2):
                //advanced search fragment
                return;
            case(3):
                //settings fragment
                return;
        }
    }

    @Override
    public void onSourceItemChosen(int position) {
        String source;
        switch (position) {
            case (0):
                source = MangaHere.SourceKey;
                break;
            case(1):
                source = MangaPark.SourceKey;
                break;
            default:
                source = MangaJoy.SourceKey;
                break;
        }

        if (!source.equals(WebSource.getCurrentSource())) {
            WebSource.setwCurrentSource(source);
            Toast.makeText(mMainMapper.getContext(), "Changing source to " + source,  Toast.LENGTH_SHORT).show();
            if (mViewPagerAdapterMain.hasRegisteredFragments()) {
                ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).updateSource();
                ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).updateSource();
                ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).updateSource();
            }
            mMainMapper.changeSourceTitle(source);
        }
    }

    private void setupDrawerLayouts() {
        List<String> mDrawerItems = new ArrayList<>();
        //TODO if sign in credential set, change to Sign out.
        //NOTE: only necessary when MAL fully implemented
        mDrawerItems.add("MAL Sign In");
        mDrawerItems.add("Sources");
        mDrawerItems.add("Search");
        mDrawerItems.add("Settings");


//        String[] sources = {MangaHere.SourceKey, MangaPark.SourceKey, MangaJoy.SourceKey};
        Map<String, List<String>> mSourceCollections = new LinkedHashMap<>();
        for (String item : mDrawerItems) {
            List<String> mDrawerChildren = new ArrayList<>();
            if (item.equals("Sources")) {
                for (String model : WebSource.getSourceList())
                    mDrawerChildren.add(model);
            }
            mSourceCollections.put(item, mDrawerChildren);
        }
        mMainMapper.setupDrawerLayout(mDrawerItems, mSourceCollections);
    }
}
