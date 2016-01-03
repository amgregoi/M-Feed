package com.teioh.m_feed.UI.MainActivity.Presenters;


import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.MainActivityMap;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LoginFragment;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.R;

import butterknife.ButterKnife;

public class MainPresenterImpl implements MainPresenter {

    private ViewPagerAdapterMain mViewPagerAdapterMain;
    private CharSequence Titles[] = {"Recent", "Library", "All"};
    private int Numbtabs = 3;
    private ActionBarDrawerToggle mDrawerToggle;

    private MainActivityMap mMainMapper;

    public MainPresenterImpl(MainActivityMap main) {
        mMainMapper = main;
    }

    @Override
    public void initialize() {
        this.parseLogin();
        mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mMainMapper.getContext()).getSupportFragmentManager(), Titles, Numbtabs);
        mMainMapper.setupTabLayout();
        mMainMapper.registerAdapter(mViewPagerAdapterMain);
        mMainMapper.setupSearchview();
        mMainMapper.setupToolbar();
    }

    @Override
    public void parseLogin() {
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Fragment fragment = new LoginFragment();
            ((FragmentActivity) mMainMapper.getContext()).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();
        } else {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser == null) {
                Fragment fragment = new LoginFragment();
                ((FragmentActivity) mMainMapper).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();
            }
        }
    }

    @Override public void setupDrawerLayoutListener(Toolbar mToolBar, DrawerLayout mDrawerLayout){
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
        ParseUser.getCurrentUser().logOut();
        Fragment fragment = new LoginFragment();
        ((FragmentActivity) mMainMapper.getContext()).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();

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

    @Override public void onPostCreate() {
        mDrawerToggle.syncState();
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override public boolean onOptionsSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }
}
