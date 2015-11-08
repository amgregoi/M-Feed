package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;

import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.Database.ReactiveQueryManager;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.LibraryFragmentMap;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;


public class LibraryPresenterImpl implements LibraryPresenter {

    private ArrayList<Manga> mangaList;
    private SearchableAdapter mAdapter;

    private LibraryFragmentMap mLibraryFragmentMapper;


    public LibraryPresenterImpl(LibraryFragmentMap map){
        mLibraryFragmentMapper = map;
    }

    @Override public void initializeView() {
        mLibraryFragmentMapper.hideView();
        MangaFeedDbHelper.getInstance().createDatabase();
        mangaList = new ArrayList<>();
        mAdapter = new SearchableAdapter(mLibraryFragmentMapper.getContext(), mangaList);
        setAdapter();
    }

    @Override public void updateGridView() {
        if (mangaList.size() == 0) {
            Observable<List<Manga>> observableMangaList = ReactiveQueryManager.getMangaLibraryObservable();
            observableMangaList.subscribe(manga -> udpateChapterList(manga));
        }
    }

    @Override public void udpateChapterList(List<Manga> mList) {
        for (Manga m : mList) {
            mangaList.add(m);
        }
        Collections.sort(mangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
        mAdapter.notifyDataSetChanged();
        mLibraryFragmentMapper.showView();

    }

    @Override public void onItemClick(Manga item) {
        Intent intent = new Intent(mLibraryFragmentMapper.getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        mLibraryFragmentMapper.getContext().startActivity(intent);
    }

    @Override public void onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
    }

    @Override public void ButterKnifeUnbind() {
        ButterKnife.unbind(mLibraryFragmentMapper);
    }

    @Override public void BusProviderRegister() {
        BusProvider.getInstance().register(mLibraryFragmentMapper);
    }

    @Override public void BusProviderUnregister() {
        BusProvider.getInstance().unregister(mLibraryFragmentMapper);
    }

    @Override public void setAdapter() {
        mLibraryFragmentMapper.registerAdapter(mAdapter);
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
