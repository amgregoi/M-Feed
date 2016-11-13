package com.teioh.m_feed.UI.ReaderActivity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.NoScrollViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ReaderActivity extends AppCompatActivity implements IReader.ActivityView {

    @Bind(R.id.pager) NoScrollViewPager mViewPager;
    @Bind(R.id.chapter_header) Toolbar mToolbarHeader;
    @Bind(R.id.chapter_header_2) Toolbar mToolbarHeader2;
    @Bind(R.id.refresh_button) ImageButton mRefreshButton;
    @Bind(R.id.chapter_footer) Toolbar mToolbarFooter;
    @Bind(R.id.chapterTitle) TextView mChapterTitle;
    @Bind(R.id.mangaTitle) TextView mMangaTitle;
    @Bind(R.id.currentPageNumber) TextView mCurrentPage;
    @Bind(R.id.endPageNumber) TextView mEndPage;


    private IReader.ActivityPresenter mReaderPresenter;

    public static Intent getNewInstance(Context aContext) {
        Intent intent = new Intent(aContext, ReaderActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.reader_activity);
        ButterKnife.bind(this);

        mReaderPresenter = new ReaderPresenter(this);

        if (aSavedInstanceState != null) {
            mReaderPresenter.onRestoreState(aSavedInstanceState);
        }
        mReaderPresenter.init(getIntent().getExtras());
    }

    @Override
    protected void onSaveInstanceState(Bundle aSave) {
        super.onSaveInstanceState(aSave);
        if (mReaderPresenter != null) mReaderPresenter.onSaveState(aSave);
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
    public void registerAdapter(PagerAdapter aAdapter) {
        if (aAdapter != null) {
            mViewPager.setAdapter(aAdapter);
            mViewPager.addOnPageChangeListener(this);
            mViewPager.setOffscreenPageLimit(0);
        }
    }

    @Override
    public void setCurrentChapter(int aPosition) {
        mViewPager.setCurrentItem(aPosition);
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
    public void hideToolbar(long aDelay) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        mToolbarHeader.animate().translationY(-mToolbarHeader.getHeight()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();
        mToolbarHeader2.animate().translationY(-mToolbarHeader2.getHeight() - mToolbarHeader.getHeight()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();
        mToolbarFooter.animate().translationY(mToolbarFooter.getHeight()).setInterpolator(new DecelerateInterpolator()).setStartDelay(20).start();
    }

    @Override
    public void showToolbar() {
        getWindow().getDecorView().setSystemUiVisibility(0);
        mToolbarHeader.animate().translationY(mToolbarHeader.getScrollY()).setInterpolator(new DecelerateInterpolator()).setStartDelay(10).start();
        mToolbarHeader2.animate().translationY(mToolbarHeader2.getScrollY() + mToolbarHeader.getScrollY()).setInterpolator(new DecelerateInterpolator()).setStartDelay(10).start();
        mToolbarFooter.animate().translationY(-mToolbarFooter.getScrollY()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();

    }

    @Override
    public void updateToolbar(String aTitle, String aChapterTitle, int aSize, int aChapter) {
        mMangaTitle.setText(aTitle);
        mChapterTitle.setText(aChapterTitle);
        mEndPage.setText(String.valueOf(aSize));
        mReaderPresenter.updateChapterViewStatus(mViewPager.getCurrentItem());
    }

    @Override
    public void setupToolbar() {
        setSupportActionBar(mToolbarHeader);
        mToolbarHeader.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbarHeader.setNavigationOnClickListener(v -> onBackPressed());
        mToolbarHeader.setPadding(0, getStatusBarHeight(), 0, 0);
        mToolbarFooter.setPadding(0, 0, 0, getNavBarHeight());
    }

    // A method to find height of the status bar
    private int getStatusBarHeight() {
        int lResult = 0;
        int lResourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (lResourceId > 0) {
            lResult = getResources().getDimensionPixelSize(lResourceId);
        }

        return lResult;
    }

    private int getNavBarHeight() {
        int lResult = 0;
        int lResourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (lResourceId > 0) {
            lResult = getResources().getDimensionPixelSize(lResourceId);
        }

        return lResult;
    }

    @Override
    public void onPageScrolled(int aPosition, float aPositionOffset, int aPositionOffsetPixels) {
        //Do Nothing
    }

    @Override
    public void onPageSelected(int aPosition) {
        mReaderPresenter.updateToolbar(aPosition);
    }

    @Override
    public void onPageScrollStateChanged(int aState) {
        //Do Nothing
    }

    @Override
    public void updateCurrentPage(int aPosition) {
        mCurrentPage.setText(String.valueOf(aPosition));
    }

    @Override
    public boolean checkActiveChapter(int aChapter) {
        if (mViewPager != null && aChapter == mViewPager.getCurrentItem()) return true;
        return false;
    }

    @OnClick(R.id.skipPreviousButton)
    public void onSkipPreviousClick() {
        mViewPager.decrememntCurrentItem();
    }

    @OnClick(R.id.backPageButton)
    public void onBackPageClick() {
        mReaderPresenter.decrementChapterPage(mViewPager.getCurrentItem());
    }

    //    refresh button
    @OnClick(R.id.refresh_button)
    public void onRefreshClicked() {
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

    @OnClick(R.id.screen_orient_button)
    public void onScreenOrientClick() {
        mReaderPresenter.toggleOrientation();
        setScreenOrientation(mReaderPresenter.getOrientation());
    }

    @OnClick(R.id.vertical_scroll_toggle)
    public void onVerticalScrollToggle(){
        mReaderPresenter.toggleVerticalScrollSettings(mViewPager.getCurrentItem());
    }

    public void setScreenOrientation(boolean isLandscape){
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onTrimMemory(int aLevel) {
        super.onTrimMemory(aLevel);
        Glide.get(this).trimMemory(aLevel);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }


}
