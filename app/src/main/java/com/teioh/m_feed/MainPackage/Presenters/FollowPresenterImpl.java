package com.teioh.m_feed.MainPackage.Presenters;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.BaseDirectoryMapper;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Database.ReactiveQueryManager;
import com.teioh.m_feed.MainPackage.Adapters.SearchableAdapter;
import com.teioh.m_feed.MangaPackage.MangaActivity;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.OttoBus.UpdateListEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;

public class FollowPresenterImpl implements FollowPresenter {

    private ArrayList<Manga> libraryList;
    private SearchableAdapter mAdapter;

    private View FollowFragmentView;
    private BaseDirectoryMapper mBaseMapper;

    public FollowPresenterImpl(View v, BaseDirectoryMapper map) {
        FollowFragmentView = v;
        mBaseMapper = map;
    }

    private void populateLibraryListView(List<Manga> mangaList) {
        libraryList.clear();
        for (Manga m : mangaList) {
            if (!libraryList.contains(m)) {
                libraryList.add(m);
            }
        }
        Collections.sort(libraryList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
        mAdapter.notifyDataSetChanged();
    }

    @Override public void initializeView() {
        MangaFeedDbHelper.getInstance().createDatabase();
        libraryList = new ArrayList<>();
        mAdapter = new SearchableAdapter(FollowFragmentView.getContext(), libraryList);
        setAdapter();
    }

    @Override public void updateGridView() {
        Observable<List<Manga>> observableMangaList = ReactiveQueryManager.getFollowedMangaObservable();
        observableMangaList.subscribe(manga -> populateLibraryListView(manga));
    }

    @Override public void onItemClick(Manga item) {
        Intent intent = new Intent(FollowFragmentView.getContext(), MangaActivity.class);
        intent.putExtra("Manga", item);
        FollowFragmentView.getContext().startActivity(intent);
    }

    @Subscribe public void onMangaAdded(Manga manga) {
        if (!libraryList.contains(manga)) {
            libraryList.add(manga);
            Collections.sort(libraryList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            mAdapter.notifyDataSetChanged();
            Log.i("FOLLOW MANGA success ", manga.getTitle());
        }

    }

    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        Manga manga = rm.getManga();
        if (libraryList.contains(manga)) {
            libraryList.remove(manga);
            mAdapter.notifyDataSetChanged();
            Log.i("UNFOLLOW MANGA success", manga.getTitle());
        }
    }

    @Subscribe public void onPushRecieved(UpdateListEvent event) {
        //TODO - potentially get rid of
    }

    @Override public void onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
    }

    @Override  public void ButterKnifeUnbind(){
        ButterKnife.unbind(FollowFragmentView);
    }

    @Override  public void BusProviderRegister(){
        BusProvider.getInstance().register(mBaseMapper);
    }

    @Override  public void BusProviderUnregister(){
        BusProvider.getInstance().unregister(mBaseMapper);
    }

    @Override  public void setAdapter(){
        mBaseMapper.registerAdapter(mAdapter);
    }
}
