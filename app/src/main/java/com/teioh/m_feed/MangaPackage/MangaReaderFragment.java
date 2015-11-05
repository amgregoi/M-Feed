package com.teioh.m_feed.MangaPackage;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.R;
import com.teioh.m_feed.WebSources.MangaJoy;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

public class MangaReaderFragment extends Fragment{


    @Bind(R.id.pager) ViewPager viewPager;
    private ChapterPageAdapter chapterAdapter;
    private Chapter chapter;
    Observable<List<String>> observableImageUrlList;
    ArrayList<String> urls;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_reader_fragment, container, false);
        ButterKnife.bind(this, v);

        chapter = getArguments().getParcelable("Chapter");
        observableImageUrlList = MangaJoy.getChapterImageListObservable(chapter.getChapterUrl());
        observableImageUrlList.subscribe(urlList -> updateView(urlList));

        return v;
    }

    private void updateView(List<String> urls){
        this.urls = new ArrayList<>(urls);
        chapterAdapter = new ChapterPageAdapter(getContext(), urls);
        viewPager.setAdapter(chapterAdapter);
    }



}
