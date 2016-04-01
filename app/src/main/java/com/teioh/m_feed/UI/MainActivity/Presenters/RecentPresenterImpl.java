package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.Manifest;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.RecentFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import nl.qbusict.cupboard.QueryResultIterable;
import rx.Subscription;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class RecentPresenterImpl implements RecentPresenter {
    public final static String TAG = RecentPresenterImpl.class.getSimpleName();
    public final static String RECENT_MANGA_LIST_KEY = TAG + ":RECENT_MANGA_LIST";
    public final static String LATEST_SOURCE = TAG + ":SOURCE";

    private ArrayList<Manga> mRecentMangaList;
    private ArrayList<Manga> mGenreFilterList;
    private SearchableAdapter mAdapter;
    private Subscription mMangaListSubscription;
    private RecentFragmentMapper mRecentFragmentMapper;
    private String mLastSourceQuery;

    public RecentPresenterImpl(RecentFragmentMapper map) {
        mRecentFragmentMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mRecentMangaList != null) {
            bundle.putParcelableArrayList(RECENT_MANGA_LIST_KEY, mRecentMangaList);
        }
        if (mLastSourceQuery != null) {
            bundle.putString(LATEST_SOURCE, mLastSourceQuery);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(RECENT_MANGA_LIST_KEY)) {
            mRecentMangaList = new ArrayList<>(bundle.getParcelableArrayList(RECENT_MANGA_LIST_KEY));
        }
        if (bundle.containsKey(LATEST_SOURCE)) {
            mLastSourceQuery = bundle.getString(LATEST_SOURCE);
        }
    }

    @Override
    public void init() {
        mRecentFragmentMapper.setupSwipeRefresh();

        if (mRecentMangaList == null) this.updateRecentMangaList();
        else updateRecentGridView(mRecentMangaList);
    }

    @Override
    public void updateRecentMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
        mLastSourceQuery = WebSource.getCurrentSource();
        mMangaListSubscription = WebSource.getRecentUpdatesObservable()
                .subscribe(manga -> updateRecentGridView(manga));
    }

    @Override
    public void onItemClick(String mTitle) {
        Intent intent = new Intent(mRecentFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, mTitle);
        mRecentFragmentMapper.getContext().startActivity(intent);
    }

    @Override
    public void onQueryTextChange(String newText) {
        if (mAdapter != null)
            mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mRecentFragmentMapper);
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
    }

    @Override
    public void onResume() {
        if (mRecentMangaList != null) {
            mMangaListSubscription = ReactiveQueryManager.updateRecentMangaListObservable(mRecentMangaList)
                    .subscribe(manga -> {
                        if (mRecentFragmentMapper.getContext() != null) {
                            if (manga != null) {
                                mRecentMangaList.clear();
                                mRecentMangaList.addAll(manga);
                                mMangaListSubscription = null;
                                mAdapter.notifyDataSetChanged();
                            }
                            mRecentFragmentMapper.stopRefresh();
                        }
                    });
        }
        //TODO find way to force refresh item views do for all 3 main fragments

    }

    @Override
    public void onPause() {

    }

    @Override
    public void setAdapter() {
        mRecentFragmentMapper.registerAdapter(mAdapter);
    }

    @Override
    public void updateSource() {
        mRecentFragmentMapper.startRefresh();
        if (mRecentFragmentMapper.getContext() != null) {
            mRecentFragmentMapper.startRefresh();
            updateRecentMangaList();
        }
    }

    @Override
    public void onFilterSelected(int filter) {
        //TODO
        if (mAdapter != null)
            mAdapter.filterByStatus(filter);
    }

    @Override
    public void onGenreFilterSelected(ArrayList<String> keep, ArrayList<Manga> remove) {
        if(remove != null) {
            mGenreFilterList = new ArrayList<>(remove);
            mGenreFilterList.retainAll(mRecentMangaList);
            mAdapter.setOriginalData(mGenreFilterList);
        }
    }

    @Override
    public void onClearGenreFilter() {
        mAdapter.setOriginalData(mRecentMangaList);
    }

    private void updateRecentGridView(List<Manga> manga) {
        if (mRecentFragmentMapper.getContext() != null) {
            if (manga != null) {
                mRecentMangaList = new ArrayList<>(manga);
                mAdapter = new SearchableAdapter(mRecentFragmentMapper.getContext(), mRecentMangaList);
                mMangaListSubscription = null;
            } else {
                // failed to update list, show refresh view,
            }

            mRecentFragmentMapper.registerAdapter(mAdapter);
            mRecentFragmentMapper.stopRefresh();
        }
    }



}
