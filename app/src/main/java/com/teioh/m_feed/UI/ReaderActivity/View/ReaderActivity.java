package com.teioh.m_feed.UI.ReaderActivity.View;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.View.Mappers.ReaderActivityMapper;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenterImpl;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ReaderActivity extends AppCompatActivity implements ReaderActivityMapper {

    @Bind(R.id.pager) ViewPager mViewPager;

    private ReaderPresenter mReaderPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);
        ButterKnife.bind(this);
        mReaderPresenter = new ReaderPresenterImpl(this);

        if (savedInstanceState != null) {
            mReaderPresenter.onRestoreState(savedInstanceState);
        }

        mReaderPresenter.init(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mReaderPresenter != null) mReaderPresenter.onSaveState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReaderPresenter.onResume();
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
        if (adapter != null) {
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(0);
        }
    }

    @Override
    public void setCurrentChapter(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void incrementChapter() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    @Override
    public void decrementChapter() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }



}
