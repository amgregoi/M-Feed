package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterListAdapter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.ChapterListMapper;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.ChapterReaderFragment;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.WebSources.MangaJoy;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;

public class ChapterListPresenterImpl implements  ChapterListPresenter{

    private Observable<List<Chapter>> observableChapterList;
    private ArrayList<Chapter> chapterList;
    private ChapterListAdapter mAdapter;
    private Manga manga;

    private ChapterListMapper mChapterListMapper;

    public ChapterListPresenterImpl(ChapterListMapper map, Bundle b) {
        manga = b.getParcelable("Manga");
        mChapterListMapper = map;
    }

    @Override
    public void getChapterList() {
        mChapterListMapper.setupSwipeRefresh();
        observableChapterList = MangaJoy.getChapterListObservable(manga.getMangaURL());
        observableChapterList.subscribe(chapters -> updateChapterList(chapters));
    }

    @Override
    public void onChapterClicked(int position) {
        Bundle b = new Bundle();
        b.putParcelable("Chapter", chapterList.get(position));
        Fragment fragment = new ChapterReaderFragment();
        fragment.setArguments(b);
        ((Fragment)mChapterListMapper).getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();
    }

    @Override
    public void updateChapterList(List<Chapter> chapters) {
        try {
            if (chapters != null) {
                chapterList = new ArrayList<>(chapters);
                mAdapter = new ChapterListAdapter(mChapterListMapper.getContext(), R.layout.chapter_list_item, chapterList);;
                mChapterListMapper.registerAdapter(mAdapter);
                mChapterListMapper.stopRefresh();
            }
        }catch(NullPointerException e){
            Log.e("ChapterListFrag", "Moved views to fast \n\t\t\t" + e.toString());
        }
    }

    @Override
    public void butterKnifeUnbind() {
        ButterKnife.unbind(mChapterListMapper);
    }
}
