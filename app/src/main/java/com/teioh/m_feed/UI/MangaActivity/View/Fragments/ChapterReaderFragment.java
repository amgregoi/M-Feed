package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterReaderPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterReaderPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChapterReaderFragment extends Fragment implements ChapterReaderMapper {


    @Bind(R.id.pager) ViewPager viewPager;
    private ChapterReaderPresenter mChapterReaderPresenter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chapter_reader_fragment, container, false);
        ButterKnife.bind(this, v);

        mChapterReaderPresenter = new ChapterReaderPresenterImpl(this, getArguments());
        return v;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mChapterReaderPresenter.onDestroyView();
    }

    @Override
    public void registerAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mChapterReaderPresenter.updateOffsetCounter(positionOffsetPixels, position);
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mChapterReaderPresenter.updateState(state);
    }
}
