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


    @Bind( R.id.pager )
    GestureViewPager mViewPager;
    private IReader.FragmentPresenter mChapterPresenter;
    private Listeners.ReaderListener listener;


    /***
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
     *
     * @param aPosition
     */
    @Override
    public void onPageSelected(int aPosition)
    {
        mChapterPresenter.updateCurrentPage(aPosition);
    }

    /***
     * TODO..
     *
     * @param aState
     */
    @Override
    public void onPageScrollStateChanged(int aState)
    {
        mChapterPresenter.updateState(aState);
    }

    /***
     * TODO..
     */
    @Override
    public void onSingleTap()
    {
        mChapterPresenter.toggleToolbar();
    }

    /***
     * TODO..
     */
    @Override
    public void setupOnSingleTapListener()
    {
        mViewPager.setOnSingleTapListener(this);
    }

    /***
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
     */
    @Override
    public void incrementChapter()
    {
        listener.incrementChapter();
    }

    /***
     * TODO..
     */
    @Override
    public void decrementChapter()
    {
        listener.decrementChapter();
    }

    /***
     * TODO..
     *
     * @param aDelay
     */
    @Override
    public void hideToolbar(long aDelay)
    {
        listener.hideToolbar(aDelay);
    }

    /***
     * TODO..
     */
    @Override
    public void showToolbar()
    {
        listener.showToolbar();
    }

    /***
     * TODO..
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
     * TODO..
     *
     * @param aPosition
     */
    @Override
    public void updateCurrentPage(int aPosition)
    {
        listener.updateCurrentPage(aPosition);
    }

    /***
     * TODO..
     */
    @Override
    public void onRefresh()
    {
        mChapterPresenter.onRefresh(mViewPager.getCurrentItem());

    }

    /***
     * TODO..
     */
    @Override
    public void failedLoadChapter()
    {
        listener.onBackPressed();
    }

    /***
     * TODO..
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
     * TODO..
     *
     * @param aPosition
     */
    @Override
    public void setCurrentChapterPage(int aPosition)
    {
        mViewPager.setCurrentItem(aPosition);
    }

    /***
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
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
     * TODO..
     */
    @Override
    public void onStart()
    {
        mChapterPresenter.init(getArguments());
        super.onStart();
    }

    /***
     * TODO..
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mChapterPresenter.onResume();
    }

    /***
     * TODO..
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
     * TODO..
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mChapterPresenter.onPause();
    }

    /***
     * TODO..
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(getContext()).clearMemory();
    }

    /***
     * TODO..
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mChapterPresenter.onDestroy();
    }
}
