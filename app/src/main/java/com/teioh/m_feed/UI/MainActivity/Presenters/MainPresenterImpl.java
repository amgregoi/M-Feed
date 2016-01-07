package com.teioh.m_feed.UI.MainActivity.Presenters;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.LoginActivity.Presenters.View.LoginActivity;
import com.teioh.m_feed.UI.MainActivity.Adapters.SourceListAdapter;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.MainActivityMap;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.Utils.OttoBus.UpdateSource;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class MainPresenterImpl implements MainPresenter {

    private ViewPagerAdapterMain mViewPagerAdapterMain;
    private SourceListAdapter mSourceListAdapater;
    private CharSequence Titles[] = {"Recent", "Library", "All"};
    private ArrayList<String> mSourceList;
    private int Numbtabs = 3;
    private ActionBarDrawerToggle mDrawerToggle;

    private MainActivityMap mMainMapper;

    public MainPresenterImpl(MainActivityMap main) {
        mMainMapper = main;
    }

    @Override
    public void initialize() {
        mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mMainMapper.getContext()).getSupportFragmentManager(), Titles, Numbtabs);
        mSourceListAdapater = new SourceListAdapter(mMainMapper.getContext(), R.layout.source_list_item);
        mSourceList = new ArrayList<>(WebSource.getSourceList());
        mMainMapper.setupTabLayout();
        mMainMapper.registerAdapter(mViewPagerAdapterMain, mSourceListAdapater);
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
                initialize();
            }
        }
    }

    public void onSourceChosen(String source){
        WebSource.setwCurrentSource(source);
        mSourceListAdapater.notifyDataSetChanged();
        BusProvider.getInstance().post(new UpdateSource());

//        Log.e("RAWR", adapter.getItemAtPosition(pos).toString());
        //update icons
        //event bus update the three fragments
    }
}
