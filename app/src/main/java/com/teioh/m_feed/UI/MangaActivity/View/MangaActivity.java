package com.teioh.m_feed.UI.MangaActivity.View;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.View.Mappers.MangaActivityMapper;
import com.teioh.m_feed.Utils.SlidingTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MangaActivity extends AppCompatActivity implements MangaActivityMapper {

    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.tabs) SlidingTabLayout tabs;
    @Bind(R.id.tool_bar) Toolbar mToolBar;
    @Bind(R.id.search_view) SearchView mSearchView;

    private MangaPresenter mMangaPresenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manga_activity);
        ButterKnife.bind(this);
        mMangaPresenter = new MangaPresenterImpl(this);

        if(savedInstanceState != null){
            mMangaPresenter.onRestoreState(savedInstanceState);
        }

        mMangaPresenter.init(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMangaPresenter.onSaveState(outState);
    }

    @Override protected void onResume() {
        super.onResume();
        mMangaPresenter.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        mMangaPresenter.onPause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mMangaPresenter.onDestroy();
    }

    @Override public void registerAdapter(ViewPagerAdapterManga adapter) {
        if (adapter != null) {
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
        mToolBar.setNavigationOnClickListener(v -> onBackPressed());
        mSearchView.setVisibility(View.GONE);
    }

    @Override public Context getContext() {
        return this;
    }

    @OnClick(R.id.orderButton)
    public void orderButton(View view) {
        mMangaPresenter.chapterOrderButtonClick();
    }
}
