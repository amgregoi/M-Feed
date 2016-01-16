package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapterAlternate;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.RecentFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.Utils.OttoBus.UpdateSource;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class RecentPresenterImpl implements RecentPresenter {
    public final static String TAG = RecentPresenterImpl.class.getSimpleName();
    public final static String RECENT_MANGA_LIST_KEY = TAG + ":RECENT_MANGA_LIST";
    public final static String LATEST_SOURCE = TAG + ":SOURCE";

    private ArrayList<Manga> mRecentMangaList;
    private SearchableAdapterAlternate mAdapter;
    private Observable<List<Manga>> mObservableMangaList;
    private RecentFragmentMapper mRecentFragmentMapper;
    private String mLastSourceQuery;
    private int mGridViewScrollY;

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
        if (mRecentMangaList == null) {
            mRecentFragmentMapper.startRefresh();
            this.updateRecentMangaList();
        } else {
            this.updateRecentGridView(mRecentMangaList);
        }
    }

    @Override
    public void updateRecentMangaList() {
        if (mObservableMangaList != null) {
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList = null;
        }
        mLastSourceQuery = WebSource.getCurrentSource();
        mObservableMangaList = WebSource.getRecentUpdatesObservable();
        mObservableMangaList.subscribe(manga -> updateRecentGridView(manga));
    }

    @Override
    public void onItemClick(String mTitle) {
        Intent intent = new Intent(mRecentFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, mTitle);
        mRecentFragmentMapper.getContext().startActivity(intent);
    }

    @Override
    public void onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mRecentFragmentMapper);
        if(mObservableMangaList != null){
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList.publish().refCount();
            mObservableMangaList = null;
        }
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
        if(mRecentMangaList != null){
            mObservableMangaList = ReactiveQueryManager.updateRecentMangaListObservable(mRecentMangaList);
            mObservableMangaList.subscribe(manga -> {
                if (mRecentFragmentMapper.getContext() != null) {
                    if (manga != null) {
                        mRecentMangaList.clear();
                        mRecentMangaList.addAll(manga);
                        mObservableMangaList = null;
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
        BusProvider.getInstance().unregister(this);

        if (mObservableMangaList != null) {
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList = null;
        }
    }

    @Override
    public void setAdapter() {
        mRecentFragmentMapper.registerAdapter(mAdapter);
    }

    @Subscribe
    public void activityQueryChange(QueryChange query) {
        onQueryTextChange(query.getQuery());
    }

    @Subscribe
    public void onUpdateSource(UpdateSource event) {
        if (mRecentFragmentMapper.getContext() != null) {
            mRecentFragmentMapper.startRefresh();
            updateRecentMangaList();
        }
    }

    private void updateRecentGridView(List<Manga> manga) {
        if (mRecentFragmentMapper.getContext() != null) {
            if (manga != null) {
                mRecentMangaList = new ArrayList<>(manga);
                mAdapter = new SearchableAdapterAlternate(mRecentFragmentMapper.getContext(), mRecentMangaList);
                mObservableMangaList = null;
            } else {
                // failed to update list, show refresh view,
            }

            mRecentFragmentMapper.registerAdapter(mAdapter);
            mRecentFragmentMapper.stopRefresh();
        }
    }


}
