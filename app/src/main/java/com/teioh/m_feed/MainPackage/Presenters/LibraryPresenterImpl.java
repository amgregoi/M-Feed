package com.teioh.m_feed.MainPackage.Presenters;

import android.content.Intent;
import android.view.View;

import com.teioh.m_feed.MainPackage.Presenters.Mappers.AsyncMapper;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.BaseDirectoryMapper;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Database.ReactiveQueryManager;
import com.teioh.m_feed.MainPackage.Adapters.SearchableAdapter;
import com.teioh.m_feed.MangaPackage.View.MangaActivity;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by Asus1 on 11/6/2015.
 */
public class LibraryPresenterImpl implements LibraryPresenter {

    private ArrayList<Manga> mangaList;
    private SearchableAdapter mAdapter;
    private Observable<List<Manga>> observableMangaList;

    private View mLibraryFragmentView;
    private BaseDirectoryMapper mBaseMapper;
    private AsyncMapper mAsyncMapper;

    public LibraryPresenterImpl(View v, BaseDirectoryMapper base)
    {
        mLibraryFragmentView = v;
        mBaseMapper = base;
        mAsyncMapper = (AsyncMapper) base;
    }

    @Override public void initializeView() {
        mAsyncMapper.hideView();
        MangaFeedDbHelper.getInstance().createDatabase();
        mangaList = new ArrayList<>();
        mAdapter = new SearchableAdapter(mLibraryFragmentView.getContext(), mangaList);
        setAdapter();
    }

    @Override public void updateGridView() {
        if (mangaList.size() == 0) {
            observableMangaList = ReactiveQueryManager.getMangaLibraryObservable();
            observableMangaList.subscribe(manga -> udpateChapterList(manga));
        }
    }

    //finishes async task for updating manga library
    public void udpateChapterList(List<Manga> mList) {
        for (Manga m : mList) {
            mangaList.add(m);
        }
        Collections.sort(mangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
        mAdapter.notifyDataSetChanged();
        mAsyncMapper.showView();

    }

    @Override public void onItemClick(Manga item) {
        Intent intent = new Intent(mLibraryFragmentView.getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        mLibraryFragmentView.getContext().startActivity(intent);
    }

    @Override public void onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
    }

    @Override public void ButterKnifeUnbind() {
        ButterKnife.unbind(mLibraryFragmentView);
    }

    @Override public void BusProviderRegister() {
        BusProvider.getInstance().register(mBaseMapper);

    }

    @Override public void BusProviderUnregister() {
        BusProvider.getInstance().unregister(mBaseMapper);

    }

    @Override public void setAdapter() {
        mBaseMapper.registerAdapter(mAdapter);

    }

    @Override public void onMangaRemoved(RemoveFromLibrary rm){
        Manga manga = rm.getManga();
        for (Manga m : mangaList) {
            if (m.getTitle().equals(manga.getTitle())) {
                m.setFollowing(manga.getFollowing());
            }
        }
    }

}
