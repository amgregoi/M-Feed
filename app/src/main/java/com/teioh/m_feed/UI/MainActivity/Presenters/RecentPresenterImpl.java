package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapterAlternate;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.RecentFragmentMap;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.Utils.OttoBus.UpdateSource;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RecentPresenterImpl implements RecentPresenter {
    public final static String TAG = RecentPresenterImpl.class.getSimpleName();
    public final static String RECENT_MANGA_LIST_KEY = TAG + ":RECENT_MANGA_LIST";

    private ArrayList<Manga> mRecentMangaList;
    private SearchableAdapterAlternate mAdapter;
    private Observable<List<Manga>> mObservableMangaList;
    private RecentFragmentMap mRecentFragmentMapper;

    public RecentPresenterImpl(RecentFragmentMap map) {
        mRecentFragmentMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mRecentMangaList != null) {
            bundle.putParcelableArrayList(RECENT_MANGA_LIST_KEY, mRecentMangaList);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(RECENT_MANGA_LIST_KEY)) {
            mRecentMangaList = new ArrayList<>(bundle.getParcelableArrayList(RECENT_MANGA_LIST_KEY));
        }
    }

    @Override
    public void init() {
        if(mRecentMangaList == null) {
            mRecentFragmentMapper.startRefresh();
            mRecentFragmentMapper.setupSwipeRefresh();
            this.updateRecentMangaList();
        }else{
            this.updateRecentGridView(mRecentMangaList);
        }
    }

    @Override
    public void updateRecentMangaList() {
        if (mObservableMangaList != null) {
            mObservableMangaList.unsubscribeOn(Schedulers.io());
            mObservableMangaList = null;
        }
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
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
        //TODO find way to force refresh item views do for all 3 main fragments

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
        mRecentFragmentMapper.registerAdapter(mAdapter);
    }

    @Subscribe
    public void onMangaAdded(Manga manga) {
        for (Manga m : mRecentMangaList) {
            if (m.equals(manga)) {
                m = manga;
                m.setFollowing(false);
                break;
            }
        }
    }

    @Subscribe
    public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        for (Manga m : mRecentMangaList) {
            if (m.equals(manga)) {
                m = manga;
                m.setFollowing(false);
                break;
            }
        }
    }

    @Subscribe
    public void activityQueryChange(QueryChange query) {
        onQueryTextChange(query.getQuery());
    }

    @Subscribe
    public void onUpdateSource(UpdateSource event) {
        mRecentMangaList.clear();
        mAdapter.notifyDataSetChanged();
        mRecentFragmentMapper.startRefresh();
        updateRecentMangaList();
    }

    private void updateRecentGridView(List<Manga> manga) {
        if (mRecentFragmentMapper.getContext() != null && manga != null) {
            if (manga.get(0).getmSource().equals(WebSource.getSourceKey())) {
                mRecentMangaList = new ArrayList<>(manga);
                mAdapter = new SearchableAdapterAlternate(mRecentFragmentMapper.getContext(), mRecentMangaList);
                mRecentFragmentMapper.registerAdapter(mAdapter);
            }
            mRecentFragmentMapper.stopRefresh();
            mObservableMangaList = null;
        }
    }


}
