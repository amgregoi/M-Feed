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

import com.teioh.m_feed.MainPackage.Presenters.Mappers.BaseDirectoryMapper;
import com.teioh.m_feed.MainPackage.Presenters.FollowPresenter;
import com.teioh.m_feed.MainPackage.Adapters.SearchableAdapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.MainPackage.Presenters.FollowPresenterImpl;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class FollowFragment extends Fragment implements BaseDirectoryMapper{

    @Bind(R.id.search_view_2) SearchView mSearchView;
    @Bind(R.id.library_list_view) GridView mListView;

    private FollowPresenter mFollowPresenter;


    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);
        ButterKnife.bind(this, v);

        mFollowPresenter = new FollowPresenterImpl(v, this);
        mFollowPresenter.initializeView();
        return v;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnItemClick(R.id.library_list_view) void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        mFollowPresenter.onItemClick(item);
    }

    @Override public void onResume() {
        super.onResume();
        mFollowPresenter.BusProviderRegister();
        mFollowPresenter.updateGridView();
    }

    @Override public void onPause() {
        super.onPause();
        mFollowPresenter.BusProviderUnregister();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mFollowPresenter.ButterKnifeUnbind();
    }

    @Override public boolean onQueryTextChange(String newText) {
        mFollowPresenter.onQueryTextChange(newText);
        return true;
    }

    @Override public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override public void registerAdapter(SearchableAdapter adapter) {
        if (adapter != null) {
            mListView.setTextFilterEnabled(true);
            mListView.setAdapter(adapter);
            registerForContextMenu(mListView);
        }
    }

    @Override public void initializeSearch() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
    }
}