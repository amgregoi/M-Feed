package com.teioh.m_feed.MainPackage.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.AsyncMapper;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.BaseDirectoryMapper;
import com.teioh.m_feed.MainPackage.Presenters.LibraryPresenter;
import com.teioh.m_feed.MainPackage.Presenters.LibraryPresenterImpl;
import com.teioh.m_feed.MainPackage.Adapters.SearchableAdapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.OttoBus.UpdateListEvent;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class LibraryFragment extends Fragment implements BaseDirectoryMapper, AsyncMapper {

    @Bind(R.id.search_view_3) SearchView mSearchView;
    @Bind(R.id.all_list_view) GridView mListView;

    private LibraryPresenter mLibraryPresenter;


    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3, container, false);
        ButterKnife.bind(this, v);

        mLibraryPresenter = new LibraryPresenterImpl(v, this);
        mLibraryPresenter.initializeView();
        return v;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnItemClick(R.id.all_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        mLibraryPresenter.onItemClick(item);
    }

    @Override public void onResume() {
        super.onResume();
        mLibraryPresenter.BusProviderRegister();
        mLibraryPresenter.updateGridView();
    }

    @Override public void onPause() {
        super.onPause();
        mLibraryPresenter.BusProviderUnregister();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mLibraryPresenter.ButterKnifeUnbind();
    }

    @Override public void registerAdapter(SearchableAdapter adapter) {
        if (adapter != null) {
            mListView.setFastScrollEnabled(true);
            mListView.setVisibility(View.GONE);
            mListView.setAdapter(adapter);
            mListView.setTextFilterEnabled(true);
        }
    }

    @Override public void initializeSearch() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
    }

    @Override public void hideView() {
        mListView.setVisibility(View.GONE);

    }

    @Override public void showView() {
        mListView.setVisibility(View.VISIBLE);
    }

    @Subscribe public void onMangaRemoved(RemoveFromLibrary rm) {
        mLibraryPresenter.onMangaRemoved(rm);
    }

    @Subscribe public void onPushRecieved(UpdateListEvent event) {
    }

    @Override public boolean onQueryTextChange(String newText) {
        mLibraryPresenter.onQueryTextChange(newText);
        return true;
    }

    @Override public boolean onQueryTextSubmit(String query) {
        return false;
    }
}