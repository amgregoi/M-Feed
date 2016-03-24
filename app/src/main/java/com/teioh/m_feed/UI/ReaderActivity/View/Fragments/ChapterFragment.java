package com.teioh.m_feed.UI.ReaderActivity.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterPresenter;
import com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterPresenterImpl;
import com.teioh.m_feed.UI.ReaderActivity.View.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.UI.ReaderActivity.View.Widgets.GestureViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChapterFragment extends Fragment implements ChapterReaderMapper {
    public final static String TAG = ChapterFragment.class.getSimpleName();


    @Bind(R.id.pager) GestureViewPager mViewPager;
    private ChapterPresenter mChapterPresenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChapterPresenter = new ChapterPresenterImpl(this, getArguments());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chapter_reader_fragment, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mChapterPresenter != null) {
            mChapterPresenter.onSaveState(outState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mChapterPresenter.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mChapterPresenter.onRestoreState(savedInstanceState);
        }

        mChapterPresenter.init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mChapterPresenter.onDestroyView();
    }

    @Override
    public void registerAdapter(PagerAdapter adapter) {
        if (adapter != null && getContext() != null) {
            mViewPager.setAdapter(adapter);
            mViewPager.clearOnPageChangeListeners();
            mViewPager.addOnPageChangeListener(this);
            mViewPager.setOffscreenPageLimit(5);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mChapterPresenter.onPause();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mChapterPresenter.updateOffsetCounter(positionOffsetPixels, position);
    }

    @Override
    public void onPageSelected(int position) {
        mChapterPresenter.updateCurrentPage(position + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mChapterPresenter.updateState(state);
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
            mChapterPresenter.updateToolbar();
            mChapterPresenter.updateCurrentPage(mViewPager.getCurrentItem() + 1);
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

    private ChapterCommunication listener;

    public interface ChapterCommunication {
        void incrementChapter();

        void decrementChapter();

        void hideToolbar(long delay);

        void showToolbar();

        void updateToolbar(String title, int size, int page);

        void updateCurrentPage(int position);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof ChapterCommunication) listener = (ChapterCommunication) context;
        else throw new ClassCastException(context.toString() + " must implement ChapterPresenterImpl.ChapterCommunication");

    }

    @Override
    public void incrementChapter() {
        listener.incrementChapter();
    }

    @Override
    public void decrementChapter() {
        listener.decrementChapter();
    }

    @Override
    public void hideToolbar(long delay) {
        listener.hideToolbar(delay);
    }

    @Override
    public void showToolbar() {
        listener.showToolbar();
    }

    @Override
    public void updateToolbar(String title, int size, int page) {
        listener.updateToolbar(title, size, page);
    }

    @Override
    public void updateCurrentPage(int position) {
        listener.updateCurrentPage(position);
    }

    @Override
    public void onRefresh() {
        mChapterPresenter.onRefresh(mViewPager.getCurrentItem());
    }


}
