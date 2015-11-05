package com.teioh.m_feed.MainPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.MangaPackage.MangaActivity;
import com.teioh.m_feed.OttoBus.ChangeTitle;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.OttoBus.UpdateListEvent;
import com.teioh.m_feed.R;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.ReactiveQueryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import rx.Observable;

public class Tab3 extends Fragment implements SearchView.OnQueryTextListener {
    @Bind(R.id.search_view_3) SearchView mSearchView;
    @Bind(R.id.all_list_view) GridView mListView;
    private ArrayList<Manga> mangaList;
    private SearchableAdapter mAdapter;
    private Observable<List<Manga>> observableMangaList;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3, container, false);
        ButterKnife.bind(this, v);
        MangaFeedDbHelper.getInstance().createDatabase();

        mangaList = new ArrayList<>();
        mAdapter = new SearchableAdapter(getContext(), mangaList);
        mListView.setFastScrollEnabled(true);
        mListView.setVisibility(View.GONE);
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);


        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);

        return v;
    }

    @OnItemClick(R.id.all_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        Intent intent = new Intent(getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        startActivity(intent);
    }

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        if (mangaList.size() == 0) {
            observableMangaList = ReactiveQueryManager.getMangaLibraryObservable();
            observableMangaList.subscribe(manga -> populateListView(manga));
        }
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //Event method, when a manga is unfollowed, we remove it from our list
    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        for (Manga m : mangaList) {
            if (m.getTitle().equals(manga.getTitle())) {
                m.setFollowing(manga.getFollowing());
            }
        }
    }

    //Event method, update list when we recieve push for updated manga
    @Subscribe public void onPushRecieved(UpdateListEvent event) {
        // TODO
        // pull recently updated mangas from parse
        // update local storage with latest chapter
        // update objects currently in listview
    }

    //querychange for searchview
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }

    //text submit returns false, because we update dynamically while query changes in the above method <onQueryTextChange()>
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //finishes async task for updating manga library
    public void populateListView(List<Manga> mList) {
        for (Manga m : mList) {
            mangaList.add(m);
        }
        Collections.sort(mangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
        mListView.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }
}