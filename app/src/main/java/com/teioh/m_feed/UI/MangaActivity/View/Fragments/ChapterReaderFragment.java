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

public class ChapterReaderFragment extends Fragment implements ChapterReaderMapper {


    @Bind(R.id.pager) ViewPager viewPager;

    private ChapterReaderPresenter mChapterReaderPresenter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_reader_fragment, container, false);

        mChapterReaderPresenter = new ChapterReaderPresenterImpl(this, getArguments());
        mChapterReaderPresenter.getImageUrls();
        return v;
    }

    @Override
    public void registerAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);

    }
}
