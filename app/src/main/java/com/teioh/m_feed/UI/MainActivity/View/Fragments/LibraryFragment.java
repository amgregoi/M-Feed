package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.teioh.m_feed.UI.MainActivity.Presenters.HomePresenter;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.Presenters.LibraryPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.LibraryFragmentMapper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LibraryFragment extends Fragment implements LibraryFragmentMapper {
    public final static String TAG = LibraryFragment.class.getSimpleName();

    @Bind(R.id.library_list_view) RecyclerView mGridView;
//    @Bind(R.id.fast_scroller) VerticalRecyclerViewFastScroller mFastScroller;

    private HomePresenter mLibraryPresenter;

    public static Fragment getnewInstance(){
        Fragment dialog = new LibraryFragment();
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_tab3_library_fragment, container, false);
        ButterKnife.bind(this, v);

        mLibraryPresenter = new LibraryPresenterImpl(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLibraryPresenter.onRestoreState(savedInstanceState);
        }
        mLibraryPresenter.init(getArguments());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLibraryPresenter.onSaveState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLibraryPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLibraryPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLibraryPresenter.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onGenreFilterSelected(ArrayList<Manga> list) {
        mLibraryPresenter.onGenreFilterSelected(list);
    }

    @Override
    public void onClearGenreFilter() {
        mLibraryPresenter.onClearGenreFilter();
    }

    @Override
    public void registerAdapter(MoPubRecyclerAdapter mAdapter, RecyclerView.LayoutManager layout, boolean needItemDecoration) {
        if (mAdapter != null) {
            mGridView.setAdapter(mAdapter);
            mGridView.setLayoutManager(layout);

            if (needItemDecoration)
                mGridView.addItemDecoration(new RecycleSearchAdapter.SpacesItemDecoration(20));
        }
    }

    @Override
    public void updateSelection(Manga manga) {
        mLibraryPresenter.updateSelection(manga);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mLibraryPresenter.onQueryTextChange(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void updateSource() {
        mLibraryPresenter.updateSource();
    }

    @Override
    public void onFilterSelected(int filter) {
        mLibraryPresenter.onFilterSelected(filter);
    }


    private Listeners.MainFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listeners.MainFragmentListener)
            listener = (Listeners.MainFragmentListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement Listeners.MainFragmentListener");
    }

    @Override
    public void setRecentSelection(Long id) {
        listener.setRecentSelection(id);
    }

    @Override
    public void updateRecentSelection(Manga manga) {
        mLibraryPresenter.updateSelection(manga);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getContext()).clearMemory();
    }
}