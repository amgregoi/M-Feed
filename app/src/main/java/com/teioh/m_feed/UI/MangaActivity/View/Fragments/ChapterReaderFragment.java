package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterReaderPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.ChapterReaderPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterReaderMapper;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChapterReaderFragment extends Fragment implements ChapterReaderMapper{


    @Bind(R.id.pager) ViewPager viewPager;

    private ChapterReaderPresenter mChapterReaderPresenter;
    private boolean notChangedChapter = true;
    private int pageCount;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_reader_fragment, container, false);
        ButterKnife.bind(this, v);
        mChapterReaderPresenter = new ChapterReaderPresenterImpl(this, getArguments());
        mChapterReaderPresenter.getImageUrls();
        viewPager.addOnPageChangeListener(this);
        return v;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mChapterReaderPresenter.butterKnifeUnbind();
    }

    @Override
    public void registerAdapter(PagerAdapter adapter) {
        if(viewPager == null)System.exit(-1);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.e("RAWR", Float.toString(positionOffset));
        //send positionoffset count to 3-5 make sure they're all 0 (for left) ... (for right)
        mChapterReaderPresenter.updateOffset(positionOffsetPixels, position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.e("RAWR", "Scroll State: " + state);
        //send state when settles (0) and we have positionoffset count of 3-5
        mChapterReaderPresenter.updateState(state);
    }
}
