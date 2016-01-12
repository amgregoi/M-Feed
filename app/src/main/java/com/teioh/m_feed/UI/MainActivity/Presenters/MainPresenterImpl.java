package com.teioh.m_feed.UI.MainActivity.Presenters;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.commonsware.cwac.merge.MergeAdapter;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.LoginActivity.View.LoginActivity;
import com.teioh.m_feed.UI.MainActivity.Adapters.SourceListAdapter;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.MainActivityMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.Utils.OttoBus.UpdateSource;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;

public class MainPresenterImpl implements MainPresenter {
    public final static String TAG = MainPresenterImpl.class.getSimpleName();


    private ViewPagerAdapterMain mViewPagerAdapterMain;
    private SourceListAdapter mSourceListAdapater, mGeneralListAdapter;
    private MergeAdapter mDrawerAdapter;

    private final CharSequence mTabTitles[] = {"Recent", "Followed", "Library"};
    private final String mGeneralListContent[] = {"Logout", "Advanced Search"};
    private ArrayList<String> mSourceList, mGeneralList;
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

        //init arrays
        mSourceList = new ArrayList<>(WebSource.getSourceList());
        mGeneralList = new ArrayList<>(Arrays.asList(mGeneralListContent));

        //init adapters
        mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mMainMapper.getContext()).getSupportFragmentManager(), mTabTitles, 3);
        mSourceListAdapater = new SourceListAdapter(mMainMapper.getContext(), R.layout.source_list_item, mSourceList);
        mGeneralListAdapter = new SourceListAdapter(mMainMapper.getContext(), R.layout.source_list_item, mGeneralList);

        //init views
        View header = ((FragmentActivity) mMainMapper.getContext()).getLayoutInflater().inflate(R.layout.drawer_header, null);
        View general = ((FragmentActivity) mMainMapper.getContext()).getLayoutInflater().inflate(R.layout.drawer_general_header, null);
        View source = ((FragmentActivity) mMainMapper.getContext()).getLayoutInflater().inflate(R.layout.drawer_source_header, null);

        //setup drawer adapter
        mDrawerAdapter = new MergeAdapter();
        mDrawerAdapter.addView(header);
        mDrawerAdapter.addView(general);
        mDrawerAdapter.addAdapter(mGeneralListAdapter);
        mDrawerAdapter.addView(source);
        mDrawerAdapter.addAdapter(mSourceListAdapater);

        //init layout
        mMainMapper.setupTabLayout();
        mMainMapper.registerAdapter(mViewPagerAdapterMain, mDrawerAdapter);
        mMainMapper.setupSearchview();
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

        mMainMapper.setDrawerLayoutListener(mDrawerToggle);
    }

    @Override
    public void onLogout() {
        ParseUser.logOutInBackground();
        Intent intent = new Intent(mMainMapper.getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mMainMapper.getContext().startActivity(intent);

    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(mMainMapper);
        mMainMapper.closeDrawer();
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(mMainMapper);
    }

    @Override
    public void updateQueryChange(String newTest) {
        BusProvider.getInstance().post(new QueryChange(newTest));
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
    public boolean onOptionsSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public void parseLogin() {
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Intent intent = new Intent(mMainMapper.getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mMainMapper.getContext().startActivity(intent);

        } else {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser == null) {
                Intent intent = new Intent(mMainMapper.getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mMainMapper.getContext().startActivity(intent);

            } else {
                init();
            }
        }
    }

    @Override
    public void onSourceChosen(String source) {

        switch (source) {
            case ("Logout"):
                onLogout();
                return;
            case ("Advanced Search"):
                return;
            default:
                if (!source.equals(WebSource.getSourceKey())) {
                    WebSource.setwCurrentSource(source);
                    BusProvider.getInstance().post(new UpdateSource());
                }
        }
    }
}
