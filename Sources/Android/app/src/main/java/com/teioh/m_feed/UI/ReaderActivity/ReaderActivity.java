package com.teioh.m_feed.UI.ReaderActivity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.MangaPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.NoScrollViewPager;
import com.teioh.m_feed.Utils.SharedPrefs;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ReaderActivity extends AppCompatActivity implements IReader.ActivityView
{

    @Bind(R.id.no_scroll_pager) NoScrollViewPager mViewPager;
    @Bind(R.id.chapter_header) Toolbar mToolbarHeader;
    @Bind(R.id.chapter_header_2) Toolbar mToolbarHeader2;
    @Bind(R.id.vertical_scroll_toggle) ImageButton mVerticalScrollButton;
    @Bind(R.id.chapter_footer) Toolbar mToolbarFooter;
    @Bind(R.id.chapterTitle) TextView mChapterTitle;
    @Bind(R.id.mangaTitle) TextView mMangaTitle;
    @Bind(R.id.currentPageNumber) TextView mCurrentPage;
    @Bind(R.id.endPageNumber) TextView mEndPage;


    private IReader.ActivityPresenter mReaderPresenter;

    /***
     * This function creates and returns a new intent for the activity.
     *
     * @param aContext
     * @return
     */
    public static Intent getNewInstance(Context aContext, ArrayList<Chapter> aChapterList, int aPosition, String aParentUrl)
    {
        Intent lIntent = new Intent(aContext, ReaderActivity.class);
        lIntent.putParcelableArrayListExtra(MangaPresenter.CHAPTER_LIST_KEY, aChapterList);
        lIntent.putExtra(MangaPresenter.LIST_POSITION_KEY, aPosition);
        lIntent.putExtra(ReaderPresenter.PARENT_URL, aParentUrl);

        return lIntent;
    }

    /***
     * This function initializes the view for the activity.
     *
     * @param aSavedInstanceState
     */
    @Override
    protected void onCreate(Bundle aSavedInstanceState)
    {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.reader_activity);
        ButterKnife.bind(this);

        mReaderPresenter = new ReaderPresenter(this);

        if (aSavedInstanceState != null)
        {
            mReaderPresenter.onRestoreState(aSavedInstanceState);
        }
        mReaderPresenter.init(getIntent().getExtras());
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aSave
     */
    @Override
    protected void onSaveInstanceState(Bundle aSave)
    {
        super.onSaveInstanceState(aSave);
        if (mReaderPresenter != null) mReaderPresenter.onSaveState(aSave);
    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        mReaderPresenter.onResume();
    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        mReaderPresenter.onPause();
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ButterKnife.unbind(this);
        mReaderPresenter.onDestroy();
    }

    /***
     * This function returns the activity's context.
     *
     * @return
     */
    @Override
    public Context getContext()
    {
        return this;
    }

    /***
     * This function registers the adapter to the viewpager.
     *
     * @param aAdapter
     */
    @Override
    public void registerAdapter(PagerAdapter aAdapter)
    {
        if (aAdapter != null)
        {
            mViewPager.setAdapter(aAdapter);
            mViewPager.addOnPageChangeListener(this);
            mViewPager.setOffscreenPageLimit(0);
        }
    }

    /***
     * This function sets the current viewpager position.
     *
     * @param aPosition
     */
    @Override
    public void setCurrentChapter(int aPosition)
    {
        mViewPager.setCurrentItem(aPosition);
    }

    /***
     * This function increments the viewpager position.
     */
    @Override
    public void incrementChapter()
    {
        mViewPager.incrementCurrentItem();
        mReaderPresenter.updateRecentChapter(mViewPager.getCurrentItem());

    }

    /***
     * This function decrements the viewpager position.
     */
    @Override
    public void decrementChapter()
    {
        mViewPager.decrememntCurrentItem();
        mReaderPresenter.updateRecentChapter(mViewPager.getCurrentItem());
    }

    /***
     * This function hides the header and footer toolbars.
     *
     * @param aDelay
     */
    @Override
    public void hideToolbar(long aDelay)
    {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        mToolbarHeader.animate().translationY(-mToolbarHeader.getHeight()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();
        mToolbarHeader2.animate().translationY(-mToolbarHeader2.getHeight() - mToolbarHeader.getHeight()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();
        mToolbarFooter.animate().translationY(mToolbarFooter.getHeight()).setInterpolator(new DecelerateInterpolator()).setStartDelay(20).start();
    }

    /***
     * This function shows the header and footer tool bars.
     */
    @Override
    public void showToolbar()
    {
        getWindow().getDecorView().setSystemUiVisibility(0);
        mToolbarHeader.animate().translationY(mToolbarHeader.getScrollY()).setInterpolator(new DecelerateInterpolator()).setStartDelay(10).start();
        mToolbarHeader2.animate().translationY(mToolbarHeader2.getScrollY() + mToolbarHeader.getScrollY()).setInterpolator(new DecelerateInterpolator()).setStartDelay(10).start();
        mToolbarFooter.animate().translationY(-mToolbarFooter.getScrollY()).setInterpolator(new AccelerateInterpolator()).setStartDelay(10).start();

    }

    /***
     * This function updates the header toolbar.
     *
     * @param aTitle
     * @param aChapterTitle
     * @param aSize
     * @param aChapter
     */
    @Override
    public void updateToolbar(String aTitle, String aChapterTitle, int aSize, int aChapter)
    {
        mMangaTitle.setText(aTitle);
        mChapterTitle.setText(aChapterTitle);
        mEndPage.setText(String.valueOf(aSize));
        mReaderPresenter.updateChapterViewStatus(mViewPager.getCurrentItem());
    }

    /***
     * This function initializes the header toolbar.
     */
    @Override
    public void setupToolbar()
    {
        setSupportActionBar(mToolbarHeader);
        mToolbarHeader.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbarHeader.setNavigationOnClickListener(v -> onBackPressed());
        mToolbarHeader.setPadding(0, getStatusBarHeight(), 0, 0);
        mToolbarFooter.setPadding(0, 0, 0, getNavBarHeight());
    }

    /***
     * A method to find height of the status bar
     *
     * @return
     */
    //
    private int getStatusBarHeight()
    {
        int lResult = 0;
        int lResourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (lResourceId > 0)
        {
            lResult = getResources().getDimensionPixelSize(lResourceId);
        }

        return lResult;
    }

    /***
     * This function gets the navigation bar height of the device.
     *
     * @return
     */
    private int getNavBarHeight()
    {
        int lResult = 0;
        int lResourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (lResourceId > 0)
        {
            lResult = getResources().getDimensionPixelSize(lResourceId);
        }

        return lResult;
    }

    /***
     * This function handles when a page is scrolled.
     *
     * @param aPosition
     * @param aPositionOffset
     * @param aPositionOffsetPixels
     */
    @Override
    public void onPageScrolled(int aPosition, float aPositionOffset, int aPositionOffsetPixels)
    {
        //Do Nothing
    }

    /***
     * This function handles when a page is selected.
     *
     * @param aPosition
     */
    @Override
    public void onPageSelected(int aPosition)
    {
        toggleVerticalScrollIcon();
        mReaderPresenter.updateToolbar(aPosition);
    }

    /***
     * This function handles when a page scroll state has changed.
     *
     * @param aState
     */
    @Override
    public void onPageScrollStateChanged(int aState)
    {
        //Do Nothing
    }

    /***
     * This function updates the current page counter in the footer toolbar.
     *
     * @param aPosition
     */
    @Override
    public void updateCurrentPage(int aPosition)
    {
        mCurrentPage.setText(String.valueOf(aPosition));
    }

    /***
     * This function verifies if this is the active (visible) chapter.
     *
     * @param aChapter
     * @return
     */
    @Override
    public boolean checkActiveChapter(int aChapter)
    {
        if (mViewPager != null && aChapter == mViewPager.getCurrentItem()) return true;
        return false;
    }

    /***
     * This function handles skip to previous chapter.
     */
    @OnClick(R.id.skipPreviousButton)
    public void onSkipPreviousClick()
    {
        mViewPager.decrememntCurrentItem();
        mReaderPresenter.updateRecentChapter(mViewPager.getCurrentItem());
    }

    /***
     * This function handles go back one chapter page.
     */
    @OnClick(R.id.backPageButton)
    public void onBackPageClick()
    {
        mReaderPresenter.decrementChapterPage(mViewPager.getCurrentItem());
    }

    /***
     * This function handles refresh chapter button.
     */
    @OnClick(R.id.refresh_button)
    public void onRefreshClicked()
    {
        mReaderPresenter.onRefreshButton(mViewPager.getCurrentItem());
        Toast.makeText(this, "Refreshing chapter", Toast.LENGTH_SHORT).show();
    }

    /***
     * This function handles go forward one chapter page.
     */
    @OnClick(R.id.forwardPageButton)
    public void onForwardPageClick()
    {
        mReaderPresenter.incrementChapterPage(mViewPager.getCurrentItem());
    }

    /***
     * This function handles skip to next chapter.
     */
    @OnClick(R.id.skipForwardButton)
    public void onSkipForwardClick()
    {
        mViewPager.incrementCurrentItem();
        mReaderPresenter.updateRecentChapter(mViewPager.getCurrentItem());

    }

    /***
     * This function handles screen orient toggle.
     */
    @OnClick(R.id.screen_orient_button)
    public void onScreenOrientClick()
    {
        mReaderPresenter.toggleOrientation();
        //TODO.. toggle icon
    }

    /***
     * This function handles vertical scroll toggle.
     */
    @OnClick(R.id.vertical_scroll_toggle)
    public void onVerticalScrollToggle()
    {
        mReaderPresenter.toggleVerticalScrollSettings(mViewPager.getCurrentItem());
        toggleVerticalScrollIcon();

    }

    /***
     * This function sets the screen orientation.
     *
     * @param isLandscape
     */
    public void setScreenOrientation(boolean isLandscape)
    {
        if (isLandscape)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /***
     * This function trims Glide cache when low memory
     *
     * @param aLevel
     */
    @Override
    public void onTrimMemory(int aLevel)
    {
        super.onTrimMemory(aLevel);
        Glide.get(this).trimMemory(aLevel);
    }

    /***
     * This function clears Glide cache when low memory.
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    /***
     * This function toggles vertical scroll icon accordingly.
     */
    private void toggleVerticalScrollIcon()
    {
        if (SharedPrefs.getChapterScrollVertical())
        {
            mVerticalScrollButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_swap_vert_white_24dp));
        }
        else
        {
            mVerticalScrollButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_swap_horiz_white_24dp));
        }
    }


}
