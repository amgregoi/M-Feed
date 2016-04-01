package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.LibraryFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import nl.qbusict.cupboard.QueryResultIterable;
import rx.Subscription;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class LibraryPresenterImpl implements LibraryPresenter {
    public final static String TAG = LibraryPresenterImpl.class.getSimpleName();
    public final static String LIBRARY_LIST_KEY = TAG + ":LIBRARY_LIST";

    private ArrayList<Manga> mLibraryMangaList;
    private ArrayList<Manga> mGenreFilterList;
    private SearchableAdapter mAdapter;
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
    public void init() {
        updateLibraryMangaList();
    }

    @Override
    public void updateLibraryMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
        mMangaListSubscription = ReactiveQueryManager.getMangaLibraryObservable()
                .subscribe(manga -> updateLibraryGridView(manga));
    }

    @Override
    public void onItemClick(String mTitle) {
        Intent intent = new Intent(mLibraryFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, mTitle);
        mLibraryFragmentMapper.getContext().startActivity(intent);
    }

    @Override
    public void onQueryTextChange(String newText) {
        if(mAdapter != null)
            mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mLibraryFragmentMapper);
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
    }

    @Override
    public void onResume() {
        if (mLibraryMangaList != null) {
            mMangaListSubscription = ReactiveQueryManager.getMangaLibraryObservable()
                    .subscribe(manga -> {
                        if (mLibraryFragmentMapper.getContext() != null) {
                            if (manga != null) {
                                mLibraryMangaList.clear();
                                mLibraryMangaList.addAll(manga);
                                mMangaListSubscription = null;
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void setAdapter() {
        mLibraryFragmentMapper.registerAdapter(mAdapter);
    }

    @Override
    public void updateSource() {
        if (mLibraryFragmentMapper.getContext() != null) {
            if (mLibraryMangaList != null && mAdapter != null) {
                mLibraryMangaList.clear();
                mAdapter.notifyDataSetChanged();
            }
            updateLibraryMangaList();
        }
    }

    @Override
    public void onFilterSelected(int filter) {
        if (mAdapter != null)
            mAdapter.filterByStatus(filter);
    }

    @Override
    public void onGenreFilterSelected(ArrayList<String> keep, ArrayList<String> remove) {
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        selection.append("mSource" + " = ?");
        selectionArgs.add(WebSource.getCurrentSource());

        for(String s : keep) {
            selection.append(" AND ");
            selection.append("mGenres" + " LIKE ?");
            selectionArgs.add("%" + s.replaceAll("\\s","") + "%");
        }

        for(String s : remove) {
            selection.append(" AND ");
            selection.append("mGenres" + " NOT LIKE ?");
            selectionArgs.add("%" + s.replaceAll("\\s","") + "%");
        }

        QueryResultIterable<Manga> filteredManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).query(Manga.class)
                .withSelection(selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]))
                .query();

        mGenreFilterList = new ArrayList<>(filteredManga.list());
        mGenreFilterList.retainAll(mLibraryMangaList);
        mAdapter.setOriginalData(mGenreFilterList);
    }

    @Override
    public void onClearGenreFilter() {
        mAdapter.setOriginalData(mLibraryMangaList);
    }

    private void updateLibraryGridView(List<Manga> mList) {
        if (mLibraryFragmentMapper.getContext() != null && mList != null) {
            mLibraryMangaList = new ArrayList<>(mList);
            Collections.sort(mLibraryMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));

            mAdapter = new SearchableAdapter(mLibraryFragmentMapper.getContext(), mLibraryMangaList);
            mLibraryFragmentMapper.registerAdapter(mAdapter);
            mMangaListSubscription = null;
        }
    }
}
