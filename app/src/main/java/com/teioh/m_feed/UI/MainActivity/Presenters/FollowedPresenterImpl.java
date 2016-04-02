package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.FollowFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;


public class FollowedPresenterImpl implements HomePresenter {
    public final static String TAG = FollowedPresenterImpl.class.getSimpleName();
    public final static String FOLLOWED_MANGA_LIST_KEY = TAG + ":FOLLOWED_LIST";

    private ArrayList<Manga> mFollowedMangaList;
    private ArrayList<Manga> mGenreFilterList;
    private RecycleSearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mNeedsItemDeocration;
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
    public void init(Bundle bundle) {
        mLayoutManager = new GridLayoutManager(mFollowFragmentMapper.getContext(), 3);
        updateMangaList();
        mNeedsItemDeocration = true;
    }

    @Override
    public void updateMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }

        mMangaListSubscription = ReactiveQueryManager.getFollowedMangaObservable()
                .subscribe(manga -> updateFollowedGridView(manga));
    }

    @Override
    public void onItemClick(Manga manga) {
        mFollowFragmentMapper.setRecentSelection(manga.get_id());
        Intent intent = new Intent(mFollowFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, manga.getTitle());
        mFollowFragmentMapper.getContext().startActivity(intent);
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
        if (mFollowedMangaList != null) {
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
            mGenreFilterList.retainAll(mFollowedMangaList);
            mAdapter.setOriginalData(mGenreFilterList);
        }
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
            mAdapter = new RecycleSearchAdapter(mFollowFragmentMapper.getContext(), mFollowedMangaList, (itemView, item) -> onItemClick(item));
            mFollowFragmentMapper.registerAdapter(mAdapter, mLayoutManager, mNeedsItemDeocration);
            mNeedsItemDeocration = false;

            for (Manga m : mFollowedMangaList)
                System.out.println("MangaFeedDbHelper.getInstance().updateMangaFollow(\"" + m.getTitle() + "\");");

        }
    }

}
