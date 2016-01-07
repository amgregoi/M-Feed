package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapterAlternate;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.RecentFragmentMap;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
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

    private ArrayList<Manga> recentList;
    private SearchableAdapterAlternate mAdapter;
    private Observable<List<Manga>> observableMangaList;
    private RecentFragmentMap mRecentFragmentMapper;

    public RecentPresenterImpl(RecentFragmentMap map) {
        mRecentFragmentMapper = map;
    }

    @Override
    public void initialize() {
        MangaFeedDbHelper.getInstance().createDatabase();
        recentList = new ArrayList<>();
        mAdapter = new SearchableAdapterAlternate(mRecentFragmentMapper.getContext(), recentList);
        mRecentFragmentMapper.startRefresh();
        mRecentFragmentMapper.setupSwipeRefresh();
        this.setAdapter();
        this.updateGridView();
    }

    @Override
    public void updateGridView() {
        if (observableMangaList != null) {
            observableMangaList.unsubscribeOn(Schedulers.io());
            observableMangaList = null;
        }
        observableMangaList = WebSource.getRecentUpdatesObservable();
        observableMangaList.subscribe(manga -> updateRecentList(manga));
    }

    private void updateRecentList(List<Manga> manga) {
        if (mRecentFragmentMapper.getContext() != null) {
            if (manga != null) {
                recentList.clear();
                for (Manga m : manga) {
                    recentList.add(m);
                }
                mAdapter.notifyDataSetChanged();
            }
            mRecentFragmentMapper.stopRefresh();
        }
        observableMangaList = null;
    }

    @Override
    public void onItemClick(Manga item) {
        Intent intent = new Intent(mRecentFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
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
    }

    @Override
    public void setAdapter() {
        mRecentFragmentMapper.registerAdapter(mAdapter);
    }

    @Subscribe
    public void onMangaAdded(Manga manga) {
        for (Manga m : recentList) {
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
        for (Manga m : recentList) {
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
        recentList.clear();
        mAdapter.notifyDataSetChanged();
        mRecentFragmentMapper.startRefresh();
        updateGridView();
    }
}
