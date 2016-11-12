package com.teioh.m_feed.UI.ReaderActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.GestureViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChapterFragment extends Fragment implements IReader.FragmentView {
    public final static String TAG = ChapterFragment.class.getSimpleName();


    @Bind(R.id.pager) GestureViewPager mViewPager;
    private IReader.FragmentPresenter mChapterPresenter;
    private Listeners.ReaderListener listener;


    public static Fragment getNewInstance() {
        Fragment lFragment = new ChapterFragment();
        return lFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        mChapterPresenter = new ChapterPresenter(this, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedInstanceState) {
        View lView = aInflater.inflate(R.layout.reader_fragment_item, aContainer, false);
        ButterKnife.bind(this, lView);

        return lView;
    }

    @Override
    public void onStart() {
        mChapterPresenter.init(getArguments());
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle aSave) {
        super.onSaveInstanceState(aSave);
        if (mChapterPresenter != null) {
            mChapterPresenter.onSaveState(aSave);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mChapterPresenter.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle aSavedInstanceState) {
        super.onActivityCreated(aSavedInstanceState);
        if (aSavedInstanceState != null) {
            mChapterPresenter.onRestoreState(aSavedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mChapterPresenter.onDestroy();
    }

    @Override
    public void registerAdapter(PagerAdapter aAdapter) {
        if (aAdapter != null && getContext() != null) {
            mViewPager.setAdapter(aAdapter);
            mViewPager.clearOnPageChangeListeners();
            mViewPager.addOnPageChangeListener(this);
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.setPageMargin(128);
        }
    }

    @Override
    public void setCurrentChapterPage(int aPosition){
        mViewPager.setCurrentItem(aPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        mChapterPresenter.onPause();
    }

    @Override
    public void onPageScrolled(int aPosition, float aPositionOffset, int aPositionOffsetPixels) {
        mChapterPresenter.updateOffsetCounter(aPositionOffsetPixels, mViewPager.getCurrentItem());
    }

    @Override
    public void onPageSelected(int aPosition) {
        mChapterPresenter.updateCurrentPage(aPosition);
    }

    @Override
    public void onPageScrollStateChanged(int aState) {
        mChapterPresenter.updateState(aState);
    }

    @Override
    public void onSingleTap() {
        mChapterPresenter.toggleToolbar();
    }

    @Override
    public void setupOnSingleTapListener() {
        mViewPager.setOnSingleTapListener(this);
    }

    @Override
    public void updateToolbar() {
        if (mChapterPresenter != null) {
            mChapterPresenter.updateActiveChapter();
            mChapterPresenter.updateCurrentPage(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void incrementChapterPage() {
        if (getContext() != null) {
            mViewPager.incrementCurrentItem();
        }
    }

    @Override
    public void decrementChapterPage() {
        if (getContext() != null) {
            mViewPager.decrememntCurrentItem();
        }
    }

    @Override
    public void updateChapterViewStatus() {
        if (mChapterPresenter != null) {
            mChapterPresenter.updateChapterViewStatus();
        }
    }

    @Override
    public void onAttach(Context aContext) {
        super.onAttach(aContext);
        if (aContext instanceof Listeners.ReaderListener)
            listener = (Listeners.ReaderListener) aContext;
        else
            throw new ClassCastException(aContext.toString() + " must implement Listeners.ReaderListener");

    }

    @Override
    public void incrementChapter() {
        listener.incrementChapter();
    }

    @Override
    public boolean checkActiveChapter(int aChapter){
        return listener.checkActiveChapter(aChapter);
    }

    @Override
    public void decrementChapter() {
        listener.decrementChapter();
    }

    @Override
    public void hideToolbar(long aDelay) {
        listener.hideToolbar(aDelay);
    }

    @Override
    public void showToolbar() {
        listener.showToolbar();
    }

    @Override
    public void updateToolbar(String aMangaTitle, String aChapterTitle, int aSize, int aPage) {
        listener.updateToolbar(aMangaTitle, aChapterTitle, aSize, aPage);
    }

    @Override
    public void updateCurrentPage(int aPosition) {
        listener.updateCurrentPage(aPosition);
    }

    @Override
    public void onRefresh() {
        mChapterPresenter.onRefresh(mViewPager.getCurrentItem());
    }

    @Override
    public void failedLoadChapter() {
        listener.onBackPressed();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getContext()).clearMemory();
    }
}
