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

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.MangaPackage.MangaActivity;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
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

public class RecentUpdatesFragment extends Fragment {

    @Bind(R.id.recent_list_view) GridView mListView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeContainer;
    private Observable<List<Manga>> observableMangaList;
    private ArrayList<Manga> recentList;
    private SearchableAdapter mAdapter;



    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        ButterKnife.bind(this, v);
        MangaFeedDbHelper.getInstance().createDatabase();

        recentList = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), recentList);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);

        swipeContainer.post(() -> swipeContainer.setRefreshing(true));  // starts activity with loading icon while retrieving list
        swipeContainer.setOnRefreshListener(() -> observableMangaList.subscribe(manga -> udpateChapterList(manga)));
        observableMangaList = MangaJoy.getRecentUpdatesObservable();
        observableMangaList.subscribe(manga -> udpateChapterList(manga));

        return v;
    }

    private void udpateChapterList(List<Manga> manga) {
        if (manga != null) {
            recentList.clear();
            for(Manga m : manga) {
                recentList.add(m);
            }
            swipeContainer.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
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

    @Subscribe public void onMangaAdded(Manga manga) {
        for(Manga m : recentList)
        {
            if(m.equals(manga))
            {
                m.setFollowing(false);
            }
        }
    }

    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        for(Manga m : recentList)
        {
            if(m.equals(manga))
            {
                m.setFollowing(false);
            }
        }
    }

}
