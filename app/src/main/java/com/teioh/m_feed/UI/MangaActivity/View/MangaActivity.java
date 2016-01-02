package com.teioh.m_feed.UI.MangaActivity.View;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LoginFragment;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaActivityMap;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.SlidingTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MangaActivity extends AppCompatActivity implements MangaActivityMap {


    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.tabs) SlidingTabLayout tabs;
    @Bind(R.id.mainToolBar) Toolbar mToolBar;
    @Bind(R.id.search_view_1) SearchView mSearchView;

//    SearchView mSearchView;

    private MangaPresenter mMangaPresenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        ButterKnife.bind(this);

        mMangaPresenter = new MangaPresenterImpl(this);
        mMangaPresenter.initialize(getIntent().getParcelableExtra("Manga"));
        mSearchView.setVisibility(View.GONE);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume() {
        super.onResume();
        mMangaPresenter.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        mMangaPresenter.onPause();
    }

    @Override protected void onDestroy(){
        super.onDestroy();
        mMangaPresenter.onDestroy();
    }

    @Override public void registerAdapter(ViewPagerAdapterManga adapter) {
        if(adapter != null) {
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(2);
        }
    }

    @Override public void setActivityTitle(String title) {
        mActivityTitle.setText(title);
    }

    @Override public void setupSlidingTabLayout() {
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(position -> getResources().getColor(R.color.tabsScrollColor));
        tabs.setViewPager(mViewPager);
    }

    @Override public void setupToolBar() {
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_back);
        //TODO
        //replace overflow icon with double arrows to switch order of chapters (ascending/descending)
        mToolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override public Context getContext() {
        return this;
    }
}
