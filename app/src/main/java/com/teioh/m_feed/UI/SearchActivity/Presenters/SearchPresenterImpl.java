package com.teioh.m_feed.UI.SearchActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.View.MainActivity;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.UI.SearchActivity.Adapter.GenreListAdapter;
import com.teioh.m_feed.UI.SearchActivity.View.Fragments.SearchResultFragment;
import com.teioh.m_feed.UI.SearchActivity.View.Mappers.SearchActivityMap;
import com.teioh.m_feed.UI.SearchActivity.View.SearchActivity;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.MangaJoy;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class SearchPresenterImpl implements SearchPresenter{
    public final static String TAG = SearchPresenterImpl.class.getSimpleName();

    private SearchActivityMap mSearchMap;


    public SearchPresenterImpl(SearchActivityMap map){
        mSearchMap = map;
    }

    private GenreListAdapter mAdapater;
    @Override
    public void init(Bundle bundle) {
        mAdapater = new GenreListAdapter(mSearchMap.getContext(), new ArrayList<>(Arrays.asList(MangaJoy.genres)));
        mSearchMap.registerAdapter(mAdapater);
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
    public void performSearch() {
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        selection.append("mSource" + " = ?");
        selectionArgs.add(WebSource.getCurrentSource());

        for(String s : mAdapater.getGenreListByStatus(1)) {
            selection.append(" AND ");
            selection.append("mGenres" + " LIKE ?");
            selectionArgs.add("%" + s.replaceAll("\\s","") + "%");
        }

        for(String s : mAdapater.getGenreListByStatus(2)) {
            selection.append(" AND ");
            selection.append("mGenres" + " NOT LIKE ?");
            selectionArgs.add("%" + s.replaceAll("\\s","") + "%");
        }

        QueryResultIterable<Manga> filteredManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).query(Manga.class)
                .withSelection(selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]))
                .query();

        List<Manga> m = filteredManga.list();
        if(m != null && m.size() > 0) {
            Fragment result = SearchResultFragment.getNewInstance(m);
            ((SearchActivity) mSearchMap.getContext()).getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_container, result)
                    .addToBackStack(SearchResultFragment.TAG)
                    .commit();
        }else{
            Toast.makeText(mSearchMap.getContext(), "Search result empty", Toast.LENGTH_SHORT).show();
        }

        //
    }

}
