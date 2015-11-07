package com.teioh.m_feed.MainPackage.Presenters;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.teioh.m_feed.MainPackage.Presenters.Mappers.BaseDirectoryMapper;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.MainPackage.Adapters.SearchableAdapter;
import com.teioh.m_feed.MangaPackage.View.MangaActivity;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.SwipeRefreshMapper;
import com.teioh.m_feed.WebSources.MangaJoy;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;

public class RecentPresenterImpl implements RecentPresenter {

    private Observable<List<Manga>> observableMangaList;
    private ArrayList<Manga> recentList;
    private SearchableAdapter mAdapter;

    private View mRecentFragmentView;
    private BaseDirectoryMapper mBaseMapper;
    private SwipeRefreshMapper mSwipeMapper;

    public RecentPresenterImpl(View v, BaseDirectoryMapper base) {
        mRecentFragmentView = v;
        mBaseMapper = base;
        mSwipeMapper = (SwipeRefreshMapper) base;
    }

    @Override
    public void initializeView() {
        MangaFeedDbHelper.getInstance().createDatabase();
        recentList = new ArrayList<>();
        mAdapter = new SearchableAdapter(mRecentFragmentView.getContext(), recentList);
        mSwipeMapper.startRefresh();
        mSwipeMapper.setupRefreshListener();
    }

    @Override
    public void updateGridView() {
        observableMangaList = MangaJoy.getRecentUpdatesObservable();
        observableMangaList.subscribe(manga -> udpateChapterList(manga));
    }

    private void udpateChapterList(List<Manga> manga) {
        if (manga != null) {
            recentList.clear();
            for (Manga m : manga) {
                recentList.add(m);
            }
            mAdapter.notifyDataSetChanged();
        }
        mSwipeMapper.stopRefresh();
    }

    @Override
    public void onItemClick(Manga item) {
        Intent intent = new Intent(mRecentFragmentView.getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        mRecentFragmentView.getContext().startActivity(intent);
    }

    @Override
    public void onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
    }

    @Override
    public void ButterKnifeUnbind() {
        ButterKnife.unbind(mBaseMapper);
    }

    @Override
    public void BusProviderRegister() {
        BusProvider.getInstance().register(mBaseMapper);
    }

    @Override
    public void BusProviderUnregister() {
        BusProvider.getInstance().unregister(mBaseMapper);
    }

    @Override
    public void setAdapter() {
        mBaseMapper.registerAdapter(mAdapter);
    }


    @Override
    public void onMangaAdd(Manga manga) {
        for (Manga m : recentList) {
            if (m.equals(manga)) {
                m = manga;
                m.setFollowing(false);
                break;
            }
        }
    }

    @Override
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
}
