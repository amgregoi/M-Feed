package com.teioh.m_feed.MangaPackage.Chapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teioh.m_feed.WebSources.MangaJoy;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;

public class ChapterListFragment extends Fragment {

    @Bind(R.id.mangaChapterList) ListView mChapterListView;
    private Observable<List<Chapter>> observableChapterList;
    private ArrayList<Chapter> chapterList;
    private ChapterListAdapter mAdapter;
    private Manga manga;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_chapters_fragment, container, false);
        ButterKnife.bind(this, v);
        manga = getArguments().getParcelable("Manga");
        observableChapterList = MangaJoy.getChapterListObservable(manga.getMangaURL());
        observableChapterList.subscribe(chapters -> udpateChapterList(chapters));

        return v;
    }

    @OnItemClick(R.id.mangaChapterList) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        Bundle b = new Bundle();
        Chapter chapter = chapterList.get(pos);
        b.putParcelable("Chapter", chapter);
        Fragment fragment = new ChapterReaderFragment();
        fragment.setArguments(b);
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();
    }

    private void udpateChapterList(List<Chapter> chapters) {
        try {
            if (chapters != null) {
                chapterList = new ArrayList<>(chapters);
                mAdapter = new ChapterListAdapter(getContext(), R.layout.chapter_list_item, chapterList);
                mChapterListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }catch(NullPointerException e){
            Log.e("ChapterList", e.toString() + "\twhile updating chapter list");
        }
    }
}
