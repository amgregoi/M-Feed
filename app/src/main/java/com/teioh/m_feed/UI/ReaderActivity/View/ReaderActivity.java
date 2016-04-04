package com.teioh.m_feed.UI.ReaderActivity.View;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenterImpl;
import com.teioh.m_feed.UI.ReaderActivity.View.Mappers.ReaderActivityMapper;
import com.teioh.m_feed.UI.ReaderActivity.View.Widgets.NoScrollViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ReaderActivity extends AppCompatActivity implements ReaderActivityMapper {

    @Bind(R.id.pager) NoScrollViewPager mViewPager;
    @Bind(R.id.chapter_header) Toolbar mToolbarHeader;
    @Bind(R.id.chapter_header_2) Toolbar mToolbarHeader2;
    @Bind(R.id.chapter_footer) Toolbar mToolbarFooter;
    @Bind(R.id.chapterTitle) TextView mChapterTitle;
    @Bind(R.id.mangaTitle) TextView mMangaTitle;
    @Bind(R.id.currentPageNumber) TextView mCurrentPage;
    @Bind(R.id.endPageNumber) TextView mEndPage;


    private ReaderPresenter mReaderPresenter;

    public static Intent getNewInstance(Context context) {
        Intent intent = new Intent(context, ReaderActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);
        ButterKnife.bind(this);

        mReaderPresenter = new ReaderPresenterImpl(this);

        if (savedInstanceState != null) {
            mReaderPresenter.onRestoreState(savedInstanceState);
        }
        mReaderPresenter.init(getIntent().getExtras());
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
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mToolbarHeader.animate().translationY(-mToolbarHeader.getHeight()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();
        mToolbarHeader2.animate().translationY(-mToolbarHeader2.getHeight()-mToolbarHeader.getHeight()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();
        mToolbarFooter.animate().translationY(mToolbarFooter.getHeight()).setInterpolator(new DecelerateInterpolator()).setStartDelay(20).start();
    }

    @Override
    public void showToolbar() {
        getWindow().getDecorView().setSystemUiVisibility(0);
        mToolbarHeader.animate().translationY(mToolbarHeader.getScrollY()).setInterpolator(new DecelerateInterpolator()).setStartDelay(10).start();
        mToolbarHeader2.animate().translationY(mToolbarHeader2.getScrollY()+mToolbarHeader.getScrollY()).setInterpolator(new DecelerateInterpolator()).setStartDelay(10).start();
        mToolbarFooter.animate().translationY(-mToolbarFooter.getScrollY()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();

    }

    @Override
    public void updateToolbar(String title, int size, int page) {
        if (page == mViewPager.getCurrentItem()) {
            mChapterTitle.setText(title);
            mMangaTitle.setText(title); //TODO update with manga title
            mEndPage.setText(String.valueOf(size));
            mReaderPresenter.updateChapterViewStatus(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void setupToolbar() {
        setSupportActionBar(mToolbarHeader);
        mToolbarHeader.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbarHeader.setNavigationOnClickListener(v -> onBackPressed());
        mToolbarHeader.setPadding(0, getStatusBarHeight(), 0, 0);
        mToolbarFooter.setPadding(0,0,0,getNavBarHeight());
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public int getNavBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        return result;
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
//    @OnClick(R.id.refreshButton)
//    public void onRefreshClicked() {
//        mReaderPresenter.onRefreshButton(mViewPager.getCurrentItem());
//    }

    @OnClick(R.id.forwardPageButton)
    public void onForwardPageClick() {
        mReaderPresenter.incrementChapterPage(mViewPager.getCurrentItem());
    }

    @OnClick(R.id.skipForwardButton)
    public void onSkipForwardClick() {
        mViewPager.incrementCurrentItem();
    }


}
