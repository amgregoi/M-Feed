package com.teioh.m_feed.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Adapter.SearchableAdapter;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.MangaActivity;
import com.teioh.m_feed.MangaJoy;
import com.teioh.m_feed.Pojo.Manga;
import com.teioh.m_feed.Utils.RemoveFromLibrary;
import com.teioh.m_feed.Utils.UpdateListEvent;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class Tab1 extends Fragment {

    @Bind(R.id.recent_list_view) GridView mListView;
    private ArrayList<Manga> list;
    private ArrayList<Manga> temp;
    private SearchableAdapter mAdapter;
    private MangaFeedDbHelper mDbHelper;
    private ArrayList<String> recent;
    private int retry;


    MangaJoy mj = new MangaJoy();
    Observable<List<String>> temp2 = mj.pullChaptersFromWebsite()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn(new Func1<Throwable, List<String>>() {
                @Override
                public List<String> call(Throwable throwable) {
                    Log.e("throwable", throwable.toString());
                    return null;
                }
            });

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        ButterKnife.bind(this, v);

        getActivity().setTitle("Manga Feed");
        list = new ArrayList<>();
        temp = new ArrayList<>();
        recent = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), list);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
        mDbHelper = new MangaFeedDbHelper(getContext());
        mDbHelper.createDatabase();
        retry = 0;

        temp2.subscribe(manga -> udpateChapterList(manga));
        return v;
    }

    private void udpateChapterList(List<String> manga) {
        if (manga != null) {
            recent = new ArrayList<>(manga);
            Log.e("as;ldf", Integer.toString(recent.size()));
            parseGetRecentUpdates();
            retry = 0;
        } else {
            if (retry > 3) { //allow 3 attempts before we stop
                retry = 0;
                return;
            }
            temp2.subscribe(manga2 -> udpateChapterList(manga2));
            retry++;
        }
    }

    @OnItemClick(R.id.recent_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        Intent intent = new Intent(getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        startActivityForResult(intent, 1);
    }


    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // Event method
    // Do not currently do anything with this method, we do not want to arbitrarily add to the recent updates list
    @Subscribe public void onMangaAdded(Manga manga) {
        Log.e("FOLLOW MANGA", manga.getTitle());
        return;
    }

    //Event method, when a manga is unfollowed, we remove it from our list
    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        Log.e("UNFOLLOW MANGA", manga.getTitle());
        for (Manga m : list) {
            if (m.getMangaId().equals(manga.getMangaId())) {
                temp.remove(m);
                list.remove(m);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    //Event method, update list when we recieve push for updated manga
    @Subscribe public void onPushRecieved(UpdateListEvent event) {
        //TODO
    }

    // querys parse for manga we follow and populates with most recently updated
    private void parseGetRecentUpdates() {
        Observable.just("").subscribeOn(Schedulers.newThread())
                .doOnCompleted(() -> {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            for (Manga m : temp) {
                                if (!list.contains(m)) list.add(m);
                            }
                            mAdapter.notifyDataSetChanged();
                        });
                })
                .subscribe(s -> {
                    for (String title : recent) {
                        Manga manga = cupboard()
                                .withDatabase(mDbHelper.getReadableDatabase())
                                .query(Manga.class).withSelection("mTitle = ?", title).get();
                        temp.add(manga);
                    }
                    //TODO set limit later
                });
    }


}
