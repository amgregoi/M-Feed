package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.RecentFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;


public class RecentPresenterImpl implements HomePresenter {
    public final static String TAG = RecentPresenterImpl.class.getSimpleName();
    public final static String RECENT_MANGA_LIST_KEY = TAG + ":RECENT_MANGA_LIST";
    public final static String LATEST_SOURCE = TAG + ":SOURCE";

    private ArrayList<Manga> mRecentMangaList;
    private ArrayList<Manga> mGenreFilterList;
    private RecycleSearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Subscription mMangaListSubscription;
    private RecentFragmentMapper mRecentFragmentMapper;
    private String mLastSourceQuery;
    private boolean mNeedsItemDeocration;

    public RecentPresenterImpl(RecentFragmentMapper map) {
        mRecentFragmentMapper = map;
    }


    @Override
    public void init(Bundle bundle) {
        mRecentFragmentMapper.setupSwipeRefresh();
        mNeedsItemDeocration = true;
        mLayoutManager = new GridLayoutManager(mRecentFragmentMapper.getContext(), 3);
        if (mRecentMangaList == null) updateMangaList();
        else updateRecentGridView(mRecentMangaList);
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
    public void updateMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
        mLastSourceQuery = WebSource.getCurrentSource();
        mMangaListSubscription = WebSource.getRecentUpdatesObservable()
                .subscribe(manga -> updateRecentGridView(manga));
    }

    @Override
    public void onItemClick(Manga manga) {
        mRecentFragmentMapper.setRecentSelection(manga.get_id());
        Intent intent = new Intent(mRecentFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, manga.getTitle());
        mRecentFragmentMapper.getContext().startActivity(intent);
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
        mRecentFragmentMapper.startRefresh();
        if (mRecentFragmentMapper.getContext() != null) {
            mRecentFragmentMapper.startRefresh();
            updateMangaList();
        }
    }

    @Override
    public void onFilterSelected(int filter) {
        //TODO
        if (mAdapter != null)
            mAdapter.filterByStatus(filter);
    }

    @Override
    public void onGenreFilterSelected(ArrayList<Manga> list) {
        if (list != null) {
            mGenreFilterList = new ArrayList<>(list);
            mGenreFilterList.retainAll(mRecentMangaList);
            mAdapter.setOriginalData(mGenreFilterList);
        }
    }

    @Override
    public void onClearGenreFilter() {
        mAdapter.setOriginalData(mRecentMangaList);
    }

    @Override
    public void updateSelection(Manga manga) {
        for (int pos = 0; pos < mRecentMangaList.size(); pos++) {
            if (mRecentMangaList.get(pos).equals(manga)) {
                mAdapter.updateItem(pos, manga);
            }
        }
    }

    private void updateRecentGridView(List<Manga> manga) {
        if (mRecentFragmentMapper.getContext() != null) {
            if (manga != null) {
                mRecentMangaList = new ArrayList<>(manga);
                mAdapter = new RecycleSearchAdapter(mRecentFragmentMapper.getContext(), mRecentMangaList, (itemView, item) -> onItemClick(item));
                mMangaListSubscription = null;
            } else {
                // failed to update list, show refresh view,
            }
            mRecentFragmentMapper.registerAdapter(mAdapter, mLayoutManager, mNeedsItemDeocration);
            mRecentFragmentMapper.stopRefresh();
            mNeedsItemDeocration = false;

        }
    }

}
