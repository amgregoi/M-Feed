package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Presenters.AllLibraryPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.AllLibraryPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.LibraryFragmentMap;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.Utils.OttoBus.UpdateListEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class AllLibraryFragment extends Fragment implements LibraryFragmentMap {

    //    @Bind(R.id.search_view_3) SearchView mSearchView;
    @Bind(R.id.all_list_view) GridView mGridView;

    private AllLibraryPresenter mAllLibraryPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3_all_fragment, container, false);
        ButterKnife.bind(this, v);


        mAllLibraryPresenter = new AllLibraryPresenterImpl(this);
        mAllLibraryPresenter.initializeView();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnItemClick(R.id.all_list_view)
    void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        mAllLibraryPresenter.onItemClick(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAllLibraryPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAllLibraryPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAllLibraryPresenter.onDestroyView();
    }

    @Override
    public void registerAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mGridView.setFastScrollEnabled(true);
            mGridView.setVisibility(View.GONE);
            mGridView.setAdapter(adapter);
            mGridView.setTextFilterEnabled(true);
        }
    }

    @Override
    public void hideGridView() {
        mGridView.setVisibility(View.GONE);

    }

    @Override
    public void showGridView() {
        mGridView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAllLibraryPresenter.onQueryTextChange(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}