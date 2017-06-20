package com.teioh.m_feed.UI.ReaderActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.GestureViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChapterFragment extends Fragment implements IReader.FragmentView
{
    public final static String TAG = ChapterFragment.class.getSimpleName();


    @Bind(R.id.gesture_pager) GestureViewPager mViewPager;
    private IReader.FragmentPresenter mChapterPresenter;
    private Listeners.ReaderListener listener;


    /***
     * This function creates and returns a new instance of the fragment.
     *
     * @return
     */
    public static Fragment getNewInstance(boolean aFollowing, Chapter aChapter, int aPosition)
    {

        Bundle lBundle = new Bundle();
        lBundle.putBoolean(ChapterPresenter.CHAPTER_PARENT_FOLLOWING, aFollowing);
        lBundle.putParcelable(Chapter.TAG + ":" + aPosition, aChapter);
        lBundle.putInt(ChapterPresenter.CHAPTER_POSITION_LIST_PARCELABLE_KEY, aPosition);

        Fragment lFragment = new ChapterFragment();
        lFragment.setArguments(lBundle);
        return lFragment;
    }

    /***
     * This function registers the adapter to the viewpager.
     *
     * @param aAdapter
     */
    @Override
    public void registerAdapter(PagerAdapter aAdapter)
    {
        if (aAdapter != null && getContext() != null)
        {
            mViewPager.setAdapter(aAdapter);
            mViewPager.clearOnPageChangeListeners();
            mViewPager.addOnPageChangeListener(this);
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.setPageMargin(128);
            mViewPager.setScrollerType();
        }
    }

    /***
     * This function updates the scroll offset for going to the next or previous chapter.
     *
     * @param aPosition
     * @param aPositionOffset
     * @param aPositionOffsetPixels
     */
    @Override
    public void onPageScrolled(int aPosition, float aPositionOffset, int aPositionOffsetPixels)
    {
        mChapterPresenter.updateOffsetCounter(aPositionOffsetPixels, mViewPager.getCurrentItem());
    }

    /***
     * This function handles when a page in the viewpager is selected or changed.
     *
     * @param aPosition
     */
    @Override
    public void onPageSelected(int aPosition)
    {
        mChapterPresenter.updateCurrentPage(aPosition);
    }

    /***
     * This function handles scroll state changes in the view pager.
     *
     * @param aState
     */
    @Override
    public void onPageScrollStateChanged(int aState)
    {
        mChapterPresenter.updateState(aState);
    }

    /***
     * This function handles single taps on images in the viewpager.
     */
    @Override
    public void onSingleTap()
    {
        mChapterPresenter.toggleToolbar();
    }

    /***
     * This function initializes the viewpager single tap listener.
     */
    @Override
    public void setupOnSingleTapListener()
    {
        mViewPager.setOnSingleTapListener(this);
    }

    /***
     * This function updates the toolbar.
     */
    @Override
    public void updateToolbar()
    {
        if (mChapterPresenter != null)
        {
            mChapterPresenter.updateActiveChapter();
            mChapterPresenter.updateCurrentPage(mViewPager.getCurrentItem());
        }
    }

    /***
     * This function increments the current page of the viewpager.
     */
    @Override
    public void incrementChapterPage()
    {
        if (getContext() != null)
        {
            mViewPager.incrementCurrentItem();
        }
    }

    /***
     * This function decrements the current page of the view pager.
     */
    @Override
    public void decrementChapterPage()
    {
        if (getContext() != null)
        {
            mViewPager.decrememntCurrentItem();
        }
    }

    /***
     * this function updates the chapter view status.
     */
    @Override
    public void updateChapterViewStatus()
    {
        if (mChapterPresenter != null)
        {
            mChapterPresenter.updateChapterViewStatus();
        }
        if (mViewPager != null)
        {
            mViewPager.setScrollerType();
        }
    }

    /***
     * This function increments the chapter.
     */
    @Override
    public void incrementChapter()
    {
        listener.incrementChapter();
    }

    /***
     * This function decrements the chapter.
     */
    @Override
    public void decrementChapter()
    {
        listener.decrementChapter();
    }

    /***
     * This function hides the header and footer toolbars.
     *
     * @param aDelay
     */
    @Override
    public void hideToolbar(long aDelay)
    {
        listener.hideToolbar(aDelay);
    }

    /***
     * This function shows the header and footer tool bars.
     */
    @Override
    public void showToolbar()
    {
        listener.showToolbar();
    }

    /***
     * This function updates the header toolbar.
     *
     * @param aMangaTitle
     * @param aChapterTitle
     * @param aSize
     * @param aPage
     */
    @Override
    public void updateToolbar(String aMangaTitle, String aChapterTitle, int aSize, int aPage)
    {
        listener.updateToolbar(aMangaTitle, aChapterTitle, aSize, aPage);
    }

    /***
     * This function updates the current page.
     *
     * @param aPosition
     */
    @Override
    public void updateCurrentPage(int aPosition)
    {
        listener.updateCurrentPage(aPosition);
    }

    /***
     * This function handles the viewpager refresh.
     */
    @Override
    public void onRefresh()
    {
        mChapterPresenter.onRefresh(mViewPager.getCurrentItem());

    }

    /***
     * This function closes the activity when it fails to load.
     */
    @Override
    public void failedLoadChapter()
    {
        listener.onBackPressed();
    }

    /***
     * This function verifies this is the active (visible) chapter.
     *
     * @param aChapter
     * @return
     */
    @Override
    public boolean checkActiveChapter(int aChapter)
    {
        return listener.checkActiveChapter(aChapter);
    }

    /***
     * This function sets the current page of the view pager.
     *
     * @param aPosition
     */
    @Override
    public void setCurrentChapterPage(int aPosition)
    {
        mViewPager.setCurrentItem(aPosition);
    }

    /***
     * This function sets the current page of the view pager.
     *
     * @param aPage
     */
    @Override
    public void setChapterPage(int aPage)
    {
        if (getContext() != null)
        {
            mViewPager.setCurrentItem(aPage);
        }
    }

    /***
     * This function toggles the vertical scrolling setting.
     */
    @Override
    public void toggleVerticalScrollSettings()
    {
        if (mViewPager.toggleVerticalScroller())
        {
            Toast.makeText(getActivity(), "Vertical scroll enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "Horizontal scroll enabled", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     * This function is called in the fragment lifecycle.
     *
     * @param aContext
     */
    @Override
    public void onAttach(Context aContext)
    {
        super.onAttach(aContext);
        if (aContext instanceof Listeners.ReaderListener) listener = (Listeners.ReaderListener) aContext;
        else throw new ClassCastException(aContext.toString() + " must implement Listeners.ReaderListener");

    }

    /***
     * This function creates the fragment.
     *
     * @param aSavedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle aSavedInstanceState)
    {
        super.onCreate(aSavedInstanceState);
        mChapterPresenter = new ChapterPresenter(this, getArguments());
    }

    /***
     * This function initializes the view of the fragment.
     *
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedInstanceState)
    {
        View lView = aInflater.inflate(R.layout.reader_fragment_item, aContainer, false);
        ButterKnife.bind(this, lView);

        return lView;
    }

    /***
     * This function is called in the fragment lifecycle.
     *
     * @param aSavedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle aSavedInstanceState)
    {
        super.onActivityCreated(aSavedInstanceState);
        if (aSavedInstanceState != null)
        {
            mChapterPresenter.onRestoreState(aSavedInstanceState);
        }
    }

    /***
     * This function is called in the fragment lifecycle.
     */
    @Override
    public void onStart()
    {
        mChapterPresenter.init(getArguments());
        super.onStart();
    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mChapterPresenter.onResume();
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aSave
     */
    @Override
    public void onSaveInstanceState(Bundle aSave)
    {
        super.onSaveInstanceState(aSave);
        if (mChapterPresenter != null)
        {
            mChapterPresenter.onSaveState(aSave);
        }
    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mChapterPresenter.onPause();
    }

    /***
     * This function trims Glide cache when low memory.
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(getContext()).clearMemory();
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mChapterPresenter.onDestroy();
    }
}
