package com.teioh.m_feed.UI.ReaderActivity.View;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.Mappers.ReaderActivityMap;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenterImpl;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ReaderActivity extends AppCompatActivity implements ReaderActivityMap{

    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.tool_bar) Toolbar mToolbar;

    private ReaderPresenter mReaderPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        ButterKnife.bind(this);
        mReaderPresenter = new ReaderPresenterImpl(this);

        mToolbar.setVisibility(View.GONE);
        if(savedInstanceState != null){
            mReaderPresenter.onRestoreState(savedInstanceState);
        }

        mReaderPresenter.init(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mReaderPresenter.onSaveState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReaderPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReaderPresenter.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void registerAdapter(PagerAdapter adapter) {
        if(adapter != null){
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(0);
        }
    }


    @Override
    public void setCurrentChapter(int position) {
        mViewPager.setCurrentItem(position);
    }

}
