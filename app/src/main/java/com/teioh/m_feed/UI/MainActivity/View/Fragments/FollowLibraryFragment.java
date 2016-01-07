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
import com.teioh.m_feed.UI.MainActivity.Presenters.FollowLibraryPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.FollowLibraryPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.FollowFragmentMap;
import com.teioh.m_feed.Utils.OttoBus.QueryChange;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class FollowLibraryFragment extends Fragment implements FollowFragmentMap {

    @Bind(R.id.library_list_view) GridView mGridView;

    private FollowLibraryPresenter mFollowLibraryPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2_library_fragment, container, false);
        ButterKnife.bind(this, v);

        mFollowLibraryPresenter = new FollowLibraryPresenterImpl(this);
        mFollowLibraryPresenter.initializeView();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnItemClick(R.id.library_list_view)
    void onItemClick(AdapterView<?> adapter, View view, int pos) {
        final Manga item = (Manga) adapter.getItemAtPosition(pos);
        mFollowLibraryPresenter.onItemClick(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFollowLibraryPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFollowLibraryPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFollowLibraryPresenter.onDestroyView();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mFollowLibraryPresenter.onQueryTextChange(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void registerAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mGridView.setTextFilterEnabled(true);
            mGridView.setAdapter(adapter);
            registerForContextMenu(mGridView);
        }
    }
}