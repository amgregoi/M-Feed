package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.FollowFragmentMap;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.Utils.OttoBus.UpdateListEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;


public class FollowPresenterImpl implements FollowPresenter {

    private ArrayList<Manga> libraryList;
    private SearchableAdapter mAdapter;
    private Observable<List<Manga>> observableMangaList;

    private FollowFragmentMap mFollowFragmentMapper;

    public FollowPresenterImpl(FollowFragmentMap map) {
        mFollowFragmentMapper = map;
    }

    private void populateLibraryListView(List<Manga> mangaList) {
        libraryList.clear();
        for (Manga m : mangaList) {
            if (!libraryList.contains(m)) {
                libraryList.add(m);
            }
        }
        Collections.sort(libraryList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
        mAdapter.notifyDataSetChanged();
    }

    @Override public void initializeView() {
        MangaFeedDbHelper.getInstance().createDatabase();
        libraryList = new ArrayList<>();
        mAdapter = new SearchableAdapter(mFollowFragmentMapper.getContext(), libraryList);
        setAdapter();
    }

    @Override public void updateGridView() {
        observableMangaList = ReactiveQueryManager.getFollowedMangaObservable();
        observableMangaList.subscribe(manga -> populateLibraryListView(manga));
    }

    @Override public void onItemClick(Manga item) {
        Intent intent = new Intent(mFollowFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        mFollowFragmentMapper.getContext().startActivity(intent);
    }

    @Subscribe public void onMangaAdded(Manga manga) {
        if (!libraryList.contains(manga)) {
            libraryList.add(manga);
            Collections.sort(libraryList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mAdapter.notifyDataSetChanged();
            Log.i("FOLLOW MANGA success ", manga.getTitle());
        }

    }

    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        if (libraryList.contains(manga)) {
            libraryList.remove(manga);
            mAdapter.notifyDataSetChanged();
            Log.i("UNFOLLOW MANGA success", manga.getTitle());
        }
    }

    @Subscribe public void onPushRecieved(UpdateListEvent event) {
        //TODO - potentially get rid of
    }

    @Override public void onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
    }

    @Override public void onDestroyView() {
        ButterKnife.unbind(mFollowFragmentMapper);
    }

    @Override public void onResume() {
        BusProvider.getInstance().register(mFollowFragmentMapper);
        this.updateGridView();

    }

    @Override public void onPause() {
        BusProvider.getInstance().unregister(mFollowFragmentMapper);
    }

    @Override public void setAdapter() {
        mFollowFragmentMapper.registerAdapter(mAdapter);
    }
}
