package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.teioh.m_feed.UI.MangaActivity.View.Widgets.GestureViewPager;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterReaderPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterReaderPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterReaderMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChapterReaderFragment extends Fragment implements ChapterReaderMapper {
    public final static String TAG = ChapterReaderFragment.class.getSimpleName();

    @Bind(R.id.pager) GestureViewPager mViewPager;
    @Bind(R.id.chapterTitle) TextView mChapterTitle;
    @Bind(R.id.currentPageNumber) TextView mCurrentPage;
    @Bind(R.id.endPageNumber) TextView mEndPage;
    @Bind(R.id.chapter_header) Toolbar mToolbarHeader;
    @Bind(R.id.chapter_footer) Toolbar mToolbarFooter;

    private ChapterReaderPresenter mChapterReaderPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chapter_reader_fragment, container, false);
        ButterKnife.bind(this, v);
        mChapterReaderPresenter = new ChapterReaderPresenterImpl(this, getArguments());
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mChapterReaderPresenter.onSaveState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mChapterReaderPresenter.onRestoreState(savedInstanceState);
        }

        mChapterReaderPresenter.init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mChapterReaderPresenter.onDestroyView();
    }

    @Override
    public void registerAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(5);
    }

    @Override
    public void onPause() {
        super.onPause();
        mChapterReaderPresenter.onPause();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mChapterReaderPresenter.updateOffsetCounter(positionOffsetPixels, position);
    }

    @Override
    public void onPageSelected(int position) {
        incrementCurrentPage(position + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mChapterReaderPresenter.updateState(state);
    }

    @Override
    public void onSingleTap() {
        mChapterReaderPresenter.toggleToolbar();
    }

    @Override
    public void hideToolbar(long delay){
        mToolbarHeader.animate().translationY(-mToolbarHeader.getHeight()).setInterpolator(new AccelerateInterpolator()).setStartDelay(delay).start();
        mToolbarFooter.animate().translationY(mToolbarFooter.getHeight()).setInterpolator(new DecelerateInterpolator()).setStartDelay(delay).start();
    }

    @Override
    public void showToolbar(){
        mToolbarHeader.animate().translationY(mToolbarHeader.getScrollY()).setInterpolator(new DecelerateInterpolator()).setStartDelay(10).start();
        mToolbarFooter.animate().translationY(-mToolbarFooter.getScrollY()).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void setupToolbar(String title, int size){
        mChapterTitle.setText(title);
        mCurrentPage.setText("1");
        mEndPage.setText(String.valueOf(size));

    }

    @Override
    public void updateToolbarTitle(String title) {
        mChapterTitle.setText(title);
    }

    @Override
    public void incrementCurrentPage(int page){
        mCurrentPage.setText(Integer.toString(page));
    }

    @Override
    public void setupOnSingleTapListener() {
        mViewPager.setOnSingleTapListener(this);
    }

    @OnClick(R.id.skipPreviousButton)
    public void onSkipPreviousClick(){
        mChapterReaderPresenter.setToPreviousChapter();
    }

    @OnClick(R.id.backPageButton)
    public void onBackPageClick(){
        mViewPager.decrememntCurrentItem();
    }

    @OnClick(R.id.skipForwardButton)
    public void onSkipForwardClick(){
        mChapterReaderPresenter.setToNextChapter();
    }

    @OnClick(R.id.forwardPageButton)
    public void onForwardPageClick(){
        mViewPager.incrementCurrentItem();
    }
}
