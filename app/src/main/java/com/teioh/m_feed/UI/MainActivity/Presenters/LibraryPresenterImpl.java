package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapterAlternate;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.LibraryFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.Utils.OttoBus.UpdateListEvent;
import com.teioh.m_feed.Utils.OttoBus.UpdateSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;


public class LibraryPresenterImpl implements LibraryPresenter {
    public final static String TAG = LibraryPresenterImpl.class.getSimpleName();
    public final static String LIBRARY_LIST_KEY = TAG + ":LIBRARY_LIST";

    private ArrayList<Manga> mLibraryMangaList;
    private SearchableAdapterAlternate mAdapter;
    private Observable<List<Manga>> mObservableMangaList;

    private LibraryFragmentMapper mLibraryFragmentMapper;


    public LibraryPresenterImpl(LibraryFragmentMapper map) {
        mLibraryFragmentMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mLibraryMangaList != null) {
            bundle.putParcelableArrayList(LIBRARY_LIST_KEY, mLibraryMangaList);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(LIBRARY_LIST_KEY)) {
            mLibraryMangaList = new ArrayList<>(bundle.getParcelableArrayList(LIBRARY_LIST_KEY));
        }
    }

    @Override
    public void init() {
//        if (mLibraryMangaList == null) {
            this.updateLibraryMangaList();
//        } else {
//            this.updateLibraryGridView(mLibraryMangaList);
//        }
    }

    @Override
    public void updateLibraryMangaList() {
        if (mObservableMangaList != null) {
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList = null;
        }
        mObservableMangaList = ReactiveQueryManager.getMangaLibraryObservable();
        mObservableMangaList.subscribe(manga -> updateLibraryGridView(manga));
    }

    @Override
    public void onItemClick(String mTitle) {
        Intent intent = new Intent(mLibraryFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, mTitle);
        mLibraryFragmentMapper.getContext().startActivity(intent);
    }

    @Override
    public void onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mLibraryFragmentMapper);
        if(mObservableMangaList != null){
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList = null;
        }
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
        if(mLibraryMangaList != null){
            init();
        }
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);

        if(mObservableMangaList != null) {
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList = null;
        }
    }

    @Override
    public void setAdapter() {
        mLibraryFragmentMapper.registerAdapter(mAdapter);
    }

    @Subscribe
    public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        for (Manga m : mLibraryMangaList) {
            if (m.getTitle().equals(manga.getTitle())) {
                m.setFollowing(manga.getFollowing());
            }
        }
    }

    @Subscribe
    public void activityQueryChange(QueryChange q) {
        onQueryTextChange(q.getQuery());
    }

    @Subscribe
    public void onPushRecieved(UpdateListEvent event) {
    }


    @Subscribe
    public void onUpdateSource(UpdateSource event) {
        if(mLibraryFragmentMapper.getContext() != null) {
            if(mLibraryMangaList != null && mAdapter != null) {
                mLibraryMangaList.clear();
                mAdapter.notifyDataSetChanged();
            }
            updateLibraryMangaList();
        }
    }


    private void updateLibraryGridView(List<Manga> mList) {
        if (mLibraryFragmentMapper.getContext() != null && mList != null) {
            mLibraryMangaList = new ArrayList<>(mList);
            Collections.sort(mLibraryMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));

            mAdapter = new SearchableAdapterAlternate(mLibraryFragmentMapper.getContext(), mLibraryMangaList);
            mLibraryFragmentMapper.registerAdapter(mAdapter);
            mObservableMangaList = null;
        }
    }
}
