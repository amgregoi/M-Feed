package com.teioh.m_feed.MainPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.MangaPackage.MangaActivity;
import com.teioh.m_feed.WebSources.MangaJoy;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.OttoBus.ChangeTitle;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Tab1 extends Fragment {

    @Bind(R.id.recent_list_view) GridView mListView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeContainer;
    private Observable<List<Manga>> observableMangaList;
    private ArrayList<Manga> list;
    private SearchableAdapter mAdapter;
    private int retry;



    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        ButterKnife.bind(this, v);
        MangaFeedDbHelper.getInstance().createDatabase();

        list = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), list);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        retry = 0;

        swipeContainer.post(() -> swipeContainer.setRefreshing(true));  // starts activity with loading icon while retrieving list
        swipeContainer.setOnRefreshListener(() -> observableMangaList.subscribe(manga -> udpateChapterList(manga)));
        observableMangaList = MangaJoy.getRecentUpdatesObservable();
        observableMangaList.subscribe(manga -> udpateChapterList(manga));

        return v;
    }

    private void udpateChapterList(List<Manga> manga) {
        if (manga != null) {
            list.clear();
            for(Manga m : manga) {
                list.add(m);
            }
            swipeContainer.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
            retry = 0;
        } else {
            if (retry > 3) { //allow 3 attempts before we stop
                retry = 0;
                return;
            }
            observableMangaList.subscribe(manga2 -> udpateChapterList(manga2));
            retry++;
        }
    }

    @OnItemClick(R.id.recent_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        Intent intent = new Intent(getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        startActivityForResult(intent, 1);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
