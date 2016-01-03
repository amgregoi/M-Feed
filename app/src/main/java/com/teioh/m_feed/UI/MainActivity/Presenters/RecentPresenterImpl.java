package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;

import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapterAlternate;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.RecentFragmentMap;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.WebSources.MangaJoy;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;

public class RecentPresenterImpl implements RecentPresenter {

    private ArrayList<Manga> recentList;
    private SearchableAdapterAlternate mAdapter;

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
        Observable<List<Manga>> observableMangaList = MangaJoy.getRecentUpdatesObservable();
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
        try {
            mRecentFragmentMapper.stopRefresh();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
        BusProvider.getInstance().register(mRecentFragmentMapper);
        this.updateGridView();

    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(mRecentFragmentMapper);
    }

    @Override
    public void setAdapter() {
        mRecentFragmentMapper.registerAdapter(mAdapter);
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
