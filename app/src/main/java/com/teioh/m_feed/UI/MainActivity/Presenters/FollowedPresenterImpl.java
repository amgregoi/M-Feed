package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecyclerSearchAdapater;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.FollowFragmentMapper;
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


public class FollowedPresenterImpl implements FollowedPresenter {
    public final static String TAG = FollowedPresenterImpl.class.getSimpleName();
    public final static String FOLLOWED_MANGA_LIST_KEY = TAG + ":FOLLOWED_LIST";

    private ArrayList<Manga> mFollowedMangaList;
    private ArrayList<Manga> mGenreFilterList;
    private RecyclerSearchAdapater mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Subscription mMangaListSubscription;


    private FollowFragmentMapper mFollowFragmentMapper;

    public FollowedPresenterImpl(FollowFragmentMapper map) {
        mFollowFragmentMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mFollowedMangaList != null) {
            bundle.putParcelableArrayList(FOLLOWED_MANGA_LIST_KEY, mFollowedMangaList);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(FOLLOWED_MANGA_LIST_KEY)) {
            mFollowedMangaList = new ArrayList<>(bundle.getParcelableArrayList(FOLLOWED_MANGA_LIST_KEY));
        }
    }

    @Override
    public void init() {
        mLayoutManager = new GridLayoutManager(mFollowFragmentMapper.getContext(), 3);
        updateFollowedMangaList();
    }

    @Override
    public void updateFollowedMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }

        mMangaListSubscription = ReactiveQueryManager.getFollowedMangaObservable()
                .subscribe(manga -> updateFollowedGridView(manga));
    }

    @Override
    public void onItemClick(String mTitle) {
        Intent intent = new Intent(mFollowFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, mTitle);
        mFollowFragmentMapper.getContext().startActivity(intent);
    }

    @Override
    public void onQueryTextChange(String newText) {
        if (mAdapter != null)
            mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroyView() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
    }

    @Override
    public void onResume() {
        if (mFollowedMangaList != null) {
            mFollowFragmentMapper.refreshRecentSelection();

            mMangaListSubscription = ReactiveQueryManager.getFollowedMangaObservable()
                    .subscribe(manga -> {
                        if (mFollowFragmentMapper.getContext() != null) {
                            if (manga != null) {
                                mFollowedMangaList.clear();
                                mFollowedMangaList.addAll(manga);
                                mAdapter.setOriginalData(mFollowedMangaList);
                                mMangaListSubscription = null;
                            }
                        }
                    });
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void updateSource() {
        if (mFollowFragmentMapper.getContext() != null) {
            if (mFollowedMangaList != null && mAdapter != null) {
                mFollowedMangaList.clear();
                mAdapter.notifyDataSetChanged();
            }
            updateFollowedMangaList();
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

        for (String s : keep) {
            selection.append(" AND ");
            selection.append("mGenres" + " LIKE ?");
            selectionArgs.add("%" + s.replaceAll("\\s", "") + "%");
        }

        for (String s : remove) {
            selection.append(" AND ");
            selection.append("mGenres" + " NOT LIKE ?");
            selectionArgs.add("%" + s.replaceAll("\\s", "") + "%");
        }

        QueryResultIterable<Manga> filteredManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).query(Manga.class)
                .withSelection(selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]))
                .query();

        mGenreFilterList = new ArrayList<>(filteredManga.list());
        mGenreFilterList.retainAll(mFollowedMangaList);
        mAdapter.setOriginalData(mGenreFilterList);
    }

    @Override
    public void onClearGenreFilter() {
        mAdapter.setOriginalData(mFollowedMangaList);
    }

    @Override
    public void updateSelection(Manga manga) {
        for (int pos = 0; pos < mFollowedMangaList.size(); pos++) {
            if (mFollowedMangaList.get(pos).equals(manga)) {
                mAdapter.updateItem(pos, manga);
            }
        }
    }

    private void updateFollowedGridView(List<Manga> mangaList) {
        if (mFollowFragmentMapper.getContext() != null && mangaList != null) {
            mFollowedMangaList = new ArrayList<>(mangaList);
            Collections.sort(mFollowedMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mAdapter = new RecyclerSearchAdapater(mFollowFragmentMapper.getContext(), mFollowedMangaList, (itemView, item) -> onItemClick(item.getTitle()));
            mFollowFragmentMapper.registerAdapter(mAdapter, mLayoutManager);
            mMangaListSubscription = null;

            for (Manga m : mFollowedMangaList)
                System.out.println("MangaFeedDbHelper.getInstance().updateMangaFollow(\"" + m.getTitle() + "\");");

        }
    }

}
