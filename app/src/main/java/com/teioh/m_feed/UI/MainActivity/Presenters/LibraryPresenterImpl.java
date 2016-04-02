package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.LibraryFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;


public class LibraryPresenterImpl implements HomePresenter {
    public final static String TAG = LibraryPresenterImpl.class.getSimpleName();
    public final static String LIBRARY_LIST_KEY = TAG + ":LIBRARY_LIST";

    private ArrayList<Manga> mLibraryMangaList;
    private ArrayList<Manga> mGenreFilterList;
    private RecycleSearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mNeedsItemDeocration;
    private Subscription mMangaListSubscription;

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
    public void init(Bundle bundle) {
        mLayoutManager = new GridLayoutManager(mLibraryFragmentMapper.getContext(), 3);
        updateMangaList();
        mNeedsItemDeocration = true;
    }

    @Override
    public void updateMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
        mMangaListSubscription = ReactiveQueryManager.getMangaLibraryObservable()
                .subscribe(manga -> updateLibraryGridView(manga));
    }

    @Override
    public void onItemClick(Manga manga) {
        mLibraryFragmentMapper.setRecentSelection(manga.get_id());
        Intent intent = new Intent(mLibraryFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, manga.getTitle());
        mLibraryFragmentMapper.getContext().startActivity(intent);
    }

    @Override
    public void onQueryTextChange(String newText) {
        if (mAdapter != null)
            mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroy() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void updateSource() {
        if (mLibraryFragmentMapper.getContext() != null) {
            if (mLibraryMangaList != null && mAdapter != null) {
                mLibraryMangaList.clear();
                mAdapter.notifyDataSetChanged();
            }
            updateMangaList();
        }
    }

    @Override
    public void onFilterSelected(int filter) {
        if (mAdapter != null)
            mAdapter.filterByStatus(filter);
    }

    @Override
    public void onGenreFilterSelected(ArrayList<Manga> list) {
        if (list != null) {
            mGenreFilterList = new ArrayList<>(list);
            mGenreFilterList.retainAll(mLibraryMangaList);
            mAdapter.setOriginalData(mGenreFilterList);
        }
    }

    @Override
    public void onClearGenreFilter() {
        mAdapter.setOriginalData(mLibraryMangaList);
    }

    @Override
    public void updateSelection(Manga manga) {
        for (int pos = 0; pos < mLibraryMangaList.size(); pos++) {
            if (mLibraryMangaList.get(pos).equals(manga)) {
                mAdapter.updateItem(pos, manga);
            }
        }
    }

    private void updateLibraryGridView(List<Manga> mList) {
        if (mLibraryFragmentMapper.getContext() != null && mList != null) {
            mLibraryMangaList = new ArrayList<>(mList);
            Collections.sort(mLibraryMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));

            mAdapter = new RecycleSearchAdapter(mLibraryFragmentMapper.getContext(), mLibraryMangaList, (itemView, item) -> onItemClick(item));
            mLibraryFragmentMapper.registerAdapter(mAdapter, mLayoutManager, mNeedsItemDeocration);
            mNeedsItemDeocration = false;

            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
    }
}
