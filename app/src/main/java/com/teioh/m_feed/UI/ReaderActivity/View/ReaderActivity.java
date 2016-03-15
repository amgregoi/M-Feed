package com.teioh.m_feed.UI.ReaderActivity.View;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.View.Mappers.ReaderActivityMapper;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenterImpl;
import com.teioh.m_feed.UI.ReaderActivity.View.Widgets.NoScrollViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ReaderActivity extends AppCompatActivity implements ReaderActivityMapper {

    @Bind(R.id.pager) NoScrollViewPager mViewPager;
    @Bind(R.id.chapter_header) Toolbar mToolbarHeader;
    @Bind(R.id.chapter_footer) Toolbar mToolbarFooter;
    @Bind(R.id.chapterTitle) TextView mChapterTitle;
    @Bind(R.id.currentPageNumber) TextView mCurrentPage;
    @Bind(R.id.endPageNumber) TextView mEndPage;


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
        if (mReaderPresenter != null) mReaderPresenter.onSaveState(outState);
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
            mViewPager.addOnPageChangeListener(this);
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

    @Override
    public void hideToolbar(long delay) {
        mToolbarHeader.animate().translationY(-mToolbarHeader.getHeight()).setInterpolator(new AccelerateInterpolator()).setStartDelay(delay).start();
        mToolbarFooter.animate().translationY(mToolbarFooter.getHeight()).setInterpolator(new DecelerateInterpolator()).setStartDelay(delay).start();
    }

    @Override
    public void showToolbar() {
        mToolbarHeader.animate().translationY(mToolbarHeader.getScrollY()).setInterpolator(new DecelerateInterpolator()).setStartDelay(10).start();
        mToolbarFooter.animate().translationY(-mToolbarFooter.getScrollY()).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void updateToolbar(String title, int size, int page) {
        if (page == mViewPager.getCurrentItem()) {
            mChapterTitle.setText(title);
            mEndPage.setText(String.valueOf(size));
            //update view status at the start of each chapter
            mReaderPresenter.updateChapterViewStatus(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void setupToolbar() {
        setSupportActionBar(mToolbarHeader);
        mToolbarHeader.setNavigationIcon(getDrawable(R.drawable.ic_back));
        mToolbarHeader.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mReaderPresenter.updateToolbar(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void updateCurrentPage(int position) {
        mCurrentPage.setText(String.valueOf(position));
    }

    @OnClick(R.id.skipPreviousButton)
    public void onSkipPreviousClick() {
        mViewPager.decrememntCurrentItem();
    }

    @OnClick(R.id.backPageButton)
    public void onBackPageClick() {
        mReaderPresenter.decrementChapterPage(mViewPager.getCurrentItem());
    }

    //refresh button
    @OnClick(R.id.refreshButton)
    public void onRefreshClicked(){
        mReaderPresenter.onRefreshButton(mViewPager.getCurrentItem());
    }

    @OnClick(R.id.forwardPageButton)
    public void onForwardPageClick() {
        mReaderPresenter.incrementChapterPage(mViewPager.getCurrentItem());
    }

    @OnClick(R.id.skipForwardButton)
    public void onSkipForwardClick() {
        mViewPager.incrementCurrentItem();
    }


}
