package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.FollowFragmentMapper;
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


public class FollowedPresenterImpl implements FollowedPresenter {
    public final static String TAG = FollowedPresenterImpl.class.getSimpleName();
    public final static String FOLLOWED_MANGA_LIST_KEY = TAG + ":FOLLOWED_LIST";

    private ArrayList<Manga> mFollowedMangaList;
    private SearchableAdapter mAdapter;
    private Observable<List<Manga>> mObservableMangaList;

    private FollowFragmentMapper mFollowFragmentMapper;

    public FollowedPresenterImpl(FollowFragmentMapper map) {
        mFollowFragmentMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if(mFollowedMangaList != null){
            bundle.putParcelableArrayList(FOLLOWED_MANGA_LIST_KEY, mFollowedMangaList);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if(bundle.containsKey(FOLLOWED_MANGA_LIST_KEY)){
            mFollowedMangaList = new ArrayList<>(bundle.getParcelableArrayList(FOLLOWED_MANGA_LIST_KEY));
        }
    }

    @Override
    public void init() {
//        if(mFollowedMangaList == null){
            this.updateFollowedMangaList();
//        }else{
//            updateFollowedGridView(mFollowedMangaList);
//        }
    }

    @Override
    public void updateFollowedMangaList() {
        if (mObservableMangaList != null) {
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList = null;
        }
        mObservableMangaList = ReactiveQueryManager.getFollowedMangaObservable();
        mObservableMangaList.subscribe(manga -> updateFollowedGridView(manga));
    }

    @Override
    public void onItemClick(String mTitle) {
        Intent intent = new Intent(mFollowFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, mTitle);
        mFollowFragmentMapper.getContext().startActivity(intent);
    }

    @Override
    public void onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mFollowFragmentMapper);
        if(mObservableMangaList != null){
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList = null;
        }
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
        if(mFollowedMangaList != null){
            mObservableMangaList = ReactiveQueryManager.getFollowedMangaObservable();
            mObservableMangaList.subscribe(manga -> {
                if (mFollowFragmentMapper.getContext() != null) {
                    if (manga != null) {
                        mFollowedMangaList.clear();
                        mFollowedMangaList.addAll(manga);
                        mObservableMangaList = null;
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
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
        mFollowFragmentMapper.registerAdapter(mAdapter);
    }

    @Subscribe
    public void onMangaAdded(Manga manga) {
        if (!mFollowedMangaList.contains(manga)) {
            mFollowedMangaList.add(manga);
            Collections.sort(mFollowedMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mAdapter.notifyDataSetChanged();
            Log.e("FOLLOW MANGA success ", manga.getTitle());
        }

    }

    @Subscribe
    public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        if (mFollowedMangaList.contains(manga)) {
            mFollowedMangaList.remove(manga);
            mAdapter.notifyDataSetChanged();
            Log.e("UNFOLLOW MANGA success", manga.getTitle());
        }
    }

    @Subscribe
    public void onPushRecieved(UpdateListEvent event) {
        //TODO - potentially get rid of
    }

    @Subscribe
    public void activityQueryChange(QueryChange q) {
        onQueryTextChange(q.getQuery());
    }

    @Subscribe
    public void onUpdateSource(UpdateSource event) {
        if(mFollowFragmentMapper.getContext() != null) {
            if(mFollowedMangaList != null && mAdapter != null) {
                mFollowedMangaList.clear();
                mAdapter.notifyDataSetChanged();
            }
            updateFollowedMangaList();
        }
    }

    private void updateFollowedGridView(List<Manga> mangaList) {
        if(mFollowFragmentMapper.getContext() != null && mangaList != null) {
            mFollowedMangaList = new ArrayList<>(mangaList);
            Collections.sort(mFollowedMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mAdapter = new SearchableAdapter(mFollowFragmentMapper.getContext(), mFollowedMangaList);
            mFollowFragmentMapper.registerAdapter(mAdapter);
            mObservableMangaList = null;
        }
    }

}
