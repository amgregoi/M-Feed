package com.teioh.m_feed.UI.SearchActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.SearchableAdapter;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.UI.SearchActivity.View.Mappers.SearchResultMap;

import java.util.ArrayList;


public class SearchResultPresenterImpl implements SearchResultPresenter {
    public final static String TAG = SearchResultPresenterImpl.class.getSimpleName();
    public final static String MANGA_LIST_KEY = TAG + ":" + "MANGA_LIST_KEY";

    private SearchResultMap mSearchResultMap;
    private ArrayList<Manga> mMangaList;
    private SearchableAdapter mAdapter;

    public SearchResultPresenterImpl(SearchResultMap map){
        mSearchResultMap = map;
    }

    @Override
    public void init(Bundle bundle) {
        if(bundle.containsKey(MANGA_LIST_KEY)) {
            mMangaList = new ArrayList<>(bundle.getParcelableArrayList(MANGA_LIST_KEY));

            mAdapter = new SearchableAdapter(mSearchResultMap.getContext(), mMangaList);
            mSearchResultMap.registerAdapter(mAdapter);
        }
    }

    @Override
    public void onSavedState(Bundle save) {

    }

    @Override
    public void onRestoreState(Bundle restore) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onItemClick(String title) {
        Intent intent = new Intent(mSearchResultMap.getContext(), MangaActivity.class);
        intent.putExtra(Manga.TAG, title);
        mSearchResultMap.getContext().startActivity(intent);

    }
}
