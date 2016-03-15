package com.teioh.m_feed.UI.MainActivity.View;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.ExpandableListAdapter;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.Presenters.MainPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.MainPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.MainActivityMapper;
import com.teioh.m_feed.Utils.SlidingTabLayout;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityMapper {
    public final static String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.search_view) SearchView mSearchView;
    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.tabs) SlidingTabLayout tabs;
    @Bind(R.id.tool_bar) Toolbar mToolBar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.drawerLayoutListView) ExpandableListView mDrawerList;

    private MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenterImpl(this);

        if (savedInstanceState != null) {
            mMainPresenter.onRestoreState(savedInstanceState);
        }

        mMainPresenter.init();
        mMainPresenter.setupDrawerLayoutListener(mToolBar, mDrawerLayout);

        //start service in new thread, substantial slow down on main thread
        //startService(new Intent(this, RecentUpdateService.class));
    }

    @Override
    public void setupDrawerLayout(List<String> mDrawerItems, Map<String, List<String>> mSourceCollections) {
        final ExpandableListAdapter adapter = new ExpandableListAdapter(this, mDrawerItems, mSourceCollections);

        View header = LayoutInflater.from(getContext()).inflate(R.layout.drawer_header, null);
        mDrawerList.addHeaderView(header);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            mMainPresenter.onDrawerItemChosen(groupPosition);
            return false;
        });
        mDrawerList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            mMainPresenter.onSourceItemChosen(childPosition);
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMainPresenter.onSavedState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mMainPresenter.onPostCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mMainPresenter.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            return true;
        } else if (id == R.id.refresh) {
            return true;
        } else if (id == R.id.views) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mMainPresenter.updateQueryChange(newText);
        return false;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void registerAdapter(ViewPagerAdapterMain adapter) {
        if (adapter != null) {
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(3);
            tabs.setViewPager(mViewPager);
        }
    }

    @Override
    public void setupSearchview() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextFocusChangeListener((view, queryTextFocused) -> {
            if (!queryTextFocused) {
                mActivityTitle.setVisibility(View.VISIBLE);
                mSearchView.setIconified(true);
                mSearchView.setQuery("", true);
            } else {
                mActivityTitle.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setupTabLayout() {
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setCustomTabColorizer(position -> getResources().getColor(R.color.tabsScrollColor));
    }

    @Override
    public void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle) {
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onDrawerOpen() {
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void onDrawerClose() {
        invalidateOptionsMenu();  // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void setupToolbar() {
        setSupportActionBar(mToolBar);
        mActivityTitle.setText(WebSource.getCurrentSource());
    }

    @Override
    public void changeSourceTitle(String source) {
        mActivityTitle.setText(source);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.closeDrawer(mDrawerList);
        }else {
            super.onBackPressed();
        }
    }
}
