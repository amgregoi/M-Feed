package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.FollowFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Subscription;


public class FollowedPresenterImpl implements FollowedPresenter {
    public final static String TAG = FollowedPresenterImpl.class.getSimpleName();
    public final static String FOLLOWED_MANGA_LIST_KEY = TAG + ":FOLLOWED_LIST";

    private ArrayList<Manga> mFollowedMangaList;
    private SearchableAdapter mAdapter;
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
        this.updateFollowedMangaList();
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
        if(mAdapter != null)
            mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mFollowFragmentMapper);
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
        mFollowFragmentMapper.registerAdapter(mAdapter);
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

    private void updateFollowedGridView(List<Manga> mangaList) {
        if (mFollowFragmentMapper.getContext() != null && mangaList != null) {
            mFollowedMangaList = new ArrayList<>(mangaList);
            Collections.sort(mFollowedMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mAdapter = new SearchableAdapter(mFollowFragmentMapper.getContext(), mFollowedMangaList);
            mFollowFragmentMapper.registerAdapter(mAdapter);
            mMangaListSubscription = null;

            for(Manga m : mFollowedMangaList)
                System.out.println("MangaFeedDbHelper.getInstance().updateMangaFollow(\""+m.getTitle()+"\");");

        }
    }

}
