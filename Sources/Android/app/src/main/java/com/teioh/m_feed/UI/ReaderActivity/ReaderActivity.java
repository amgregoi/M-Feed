package com.teioh.m_feed.UI.ReaderActivity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.MangaPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ReaderPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.NoScrollViewPager;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ReaderActivity extends AppCompatActivity implements IReader.ReaderActivityView
{
    public final static String TAG = ReaderActivity.class.getSimpleName();

    @BindView(R.id.no_scroll_pager) NoScrollViewPager mViewPager;
    @BindView(R.id.chapter_header) Toolbar mToolbarHeader;
    @BindView(R.id.chapter_header_2) Toolbar mToolbarHeader2;
    @BindView(R.id.vertical_scroll_toggle) ImageButton mVerticalScrollButton;
    @BindView(R.id.chapter_footer) Toolbar mToolbarFooter;
    @BindView(R.id.chapterTitle) TextView mChapterTitle;
    @BindView(R.id.mangaTitle) TextView mMangaTitle;
    @BindView(R.id.currentPageNumber) TextView mCurrentPage;
    @BindView(R.id.endPageNumber) TextView mEndPage;
    @BindView(R.id.pageNumDivider) TextView mDivider;

    @BindView(R.id.backPageButton) ImageButton mBackPageButton;
    @BindView(R.id.forwardPageButton) ImageButton mForwardPageButton;

    @BindView(R.id.seekBar) SeekBar mTextSizeSeekBar;


    private IReader.ReaderActivityPresenter mReaderPresenter;
    private ToolbarTimerService mToolBarService;
    private boolean mToolbarsHidden = true;

    private ServiceConnection mConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service)
        {
            // We've bound to ToolbarTimerService, cast the IBinder and get ToolbarTimerService instance
            ToolbarTimerService.LocalBinder binder = (ToolbarTimerService.LocalBinder) service;
            mToolBarService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName aComponent)
        {
            MangaLogger.logInfo(TAG, aComponent.flattenToShortString() + " service disconnected.");
        }
    };

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

        mToolBarService = new ToolbarTimerService();
        mToolBarService.setToolbarListener(this);


        Intent intent = new Intent(this, ToolbarTimerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mReaderPresenter.onDestroy();
        unbindService(mConnection);
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

            if (SourceFactory.getInstance().getSource().getSourceType() == MangaEnums.eSourceType.NOVEL)
            {
                mViewPager.setPagingEnabled(true);
            }
            else
            {
                mViewPager.setPagingEnabled(false);
            }
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

        if (SourceFactory.getInstance().getSource().getSourceType() == MangaEnums.eSourceType.NOVEL)
        {
            mVerticalScrollButton.setVisibility(View.GONE);
            mForwardPageButton.setVisibility(View.GONE);
            mBackPageButton.setVisibility(View.GONE);

            mDivider.setVisibility(View.GONE);
            mEndPage.setVisibility(View.GONE);
            mCurrentPage.setVisibility(View.GONE);

            mTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    mReaderPresenter.alterNovelTextSize(progress, mViewPager.getCurrentItem());
                    startToolbarTimer();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                    // Do nothing
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                    // Do nothing
                }
            });

            mTextSizeSeekBar.setProgress(SharedPrefs.getNovelTextSize());
        }
        else
        {
            mTextSizeSeekBar.setVisibility(View.GONE);
        }


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
        mToolBarService.startToolBarTimer();
    }

    /***
     * This function hides the header and footer toolbars.
     *
     */
    @Override
    public void hideToolbar()
    {
        try
        {
            if (!mToolbarsHidden)
            {
                hideSystemUi();

                mToolbarHeader.animate()
                              .translationY(-mToolbarHeader.getHeight())
                              .setInterpolator(new AccelerateInterpolator())
                              .setStartDelay(10)
                              .start();

                mToolbarHeader2.animate()
                               .translationY(-mToolbarHeader2.getHeight() - mToolbarHeader.getHeight())
                               .setInterpolator(new AccelerateInterpolator())
                               .setStartDelay(10)
                               .start();

                mToolbarFooter.animate()
                              .translationY(mToolbarFooter.getHeight())
                              .setInterpolator(new DecelerateInterpolator())
                              .setStartDelay(20)
                              .start();

                mToolbarsHidden = true;
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.toString());
        }
    }

    @Override
    public void hideSystemUi()
    {
        int lHiddenSystemView = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (getWindow().getDecorView().getSystemUiVisibility() != lHiddenSystemView)
        {
            getWindow().getDecorView()
                       .setSystemUiVisibility(lHiddenSystemView);
        }
    }

    /***
     * This function shows the header and footer tool bars.
     */
    private void showToolbar()
    {
        try
        {
            if (mToolbarsHidden)
            {
                getWindow().getDecorView().setSystemUiVisibility(0);
                mToolbarHeader.animate()
                              .translationY(mToolbarHeader.getScrollY())
                              .setInterpolator(new DecelerateInterpolator(.8f))
                              .setStartDelay(10)
                              .start();

                mToolbarHeader2.animate()
                               .translationY(mToolbarHeader2.getScrollY() + mToolbarHeader.getScrollY())
                               .setInterpolator(new DecelerateInterpolator(.8f))
                               .setStartDelay(10)
                               .start();

                mToolbarFooter.animate()
                              .translationY(-mToolbarFooter.getScrollY()).setInterpolator(new AccelerateInterpolator(.8f))
                              .setStartDelay(10)
                              .start();

                mToolbarsHidden = false;
                startToolbarTimer();
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.toString());
        }
    }

    /***
     * This function toggles the toolbar visibility specified by the users confirmed single taps.
     */
    @Override
    public void toggleToolbar()
    {
        if (!mToolbarsHidden)
        {
            hideToolbar();
        }
        else
        {
            showToolbar();
        }
    }

    /***
     * This function starts the timer of the ToolbarTimerService to hide the tool bars.
     */
    public void startToolbarTimer()
    {
        mToolBarService.startToolBarTimer();
    }

    /***
     * This function updates the header toolbar.
     *
     * @param aTitle
     * @param aChapterTitle
     * @param aSize
     * @param aCurrentPage
     */
    @Override
    public void updateToolbar(String aTitle, String aChapterTitle, int aSize, int aCurrentPage, int aChapterPosition)
    {
        if (mViewPager.getCurrentItem() == aChapterPosition)
        {
            mMangaTitle.setText(aTitle);
            mChapterTitle.setText(aChapterTitle);
            mEndPage.setText(String.valueOf(aSize));
            mCurrentPage.setText(String.valueOf(aCurrentPage));
            mReaderPresenter.updateChapterViewStatus(mViewPager.getCurrentItem());
        }
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
        mReaderPresenter.updateRecentChapter(aPosition);
        showToolbar();
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

    /***
     * This function handles skip to previous chapter.
     */
    @OnClick(R.id.skipPreviousButton)
    public void onSkipPreviousClick()
    {
        mViewPager.decrememntCurrentItem();
        mReaderPresenter.updateRecentChapter(mViewPager.getCurrentItem());
        startToolbarTimer();
    }

    /***
     * This function handles go back one chapter page.
     */
    @OnClick(R.id.backPageButton)
    public void onBackPageClick()
    {
        mReaderPresenter.decrementChapterPage(mViewPager.getCurrentItem());
        startToolbarTimer();
    }

    /***
     * This function handles refresh chapter button.
     */
    @OnClick(R.id.refresh_button)
    public void onRefreshClicked()
    {
        mReaderPresenter.onRefreshButton(mViewPager.getCurrentItem());
        Toast.makeText(this, "Refreshing chapter", Toast.LENGTH_SHORT).show();
        mToolBarService.stopTimer();
    }

    /***
     * This function handles go forward one chapter page.
     */
    @OnClick(R.id.forwardPageButton)
    public void onForwardPageClick()
    {
        mReaderPresenter.incrementChapterPage(mViewPager.getCurrentItem());
        startToolbarTimer();
    }

    /***
     * This function handles skip to next chapter.
     */
    @OnClick(R.id.skipForwardButton)
    public void onSkipForwardClick()
    {
        mViewPager.incrementCurrentItem();
        mReaderPresenter.updateRecentChapter(mViewPager.getCurrentItem());
        startToolbarTimer();
    }

    /***
     * This function handles screen orient toggle.
     */
    @OnClick(R.id.screen_orient_button)
    public void onScreenOrientClick()
    {
        mReaderPresenter.toggleOrientation();
        startToolbarTimer();
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
        startToolbarTimer();

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
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        mReaderPresenter.onPause();
    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        mReaderPresenter.onResume();
        showToolbar();
    }


}
