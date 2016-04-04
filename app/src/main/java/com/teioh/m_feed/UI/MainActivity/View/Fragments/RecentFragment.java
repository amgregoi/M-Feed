package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.Presenters.HomePresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.RecentPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.RecentFragmentMapper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecentFragment extends Fragment implements RecentFragmentMapper {
    public final static String TAG = RecentFragment.class.getSimpleName();

    @Bind(R.id.recent_list_view)RecyclerView mGridView;
    @Bind(R.id.swipe_container)SwipeRefreshLayout swipeContainer;

    private HomePresenter mRecentPresenter;

    public static Fragment getnewInstance(){
        Fragment dialog = new RecentFragment();
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1_recent_fragment, container, false);
        ButterKnife.bind(this, v);

        mGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecentPresenter = new RecentPresenterImpl(this);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mRecentPresenter.onSaveState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mRecentPresenter.onRestoreState(savedInstanceState);
        }

        mRecentPresenter.init(getArguments());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecentPresenter.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecentPresenter.onResume();
        mGridView.refreshDrawableState();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRecentPresenter.onPause();
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mRecentPresenter.onQueryTextChange(newText);
        return false;
    }

    @Override
    public void startRefresh() {
        mGridView.setVisibility(View.GONE);
        swipeContainer.post(() -> swipeContainer.setRefreshing(true));
    }

    @Override
    public void stopRefresh() {
        swipeContainer.setRefreshing(false);
        mGridView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setupSwipeRefresh() {
        swipeContainer.post(() -> swipeContainer.setRefreshing(true));
        swipeContainer.setOnRefreshListener(() -> mRecentPresenter.updateMangaList());
    }

    @Override
    public void updateSource() {
        mRecentPresenter.updateSource();
    }

    @Override
    public void onFilterSelected(int filter) {
        mRecentPresenter.onFilterSelected(filter);
    }

    @Override
    public void onGenreFilterSelected(ArrayList<Manga> list) {
        swipeContainer.setEnabled(false);
        mRecentPresenter.onGenreFilterSelected(list);
    }

    @Override
    public void onClearGenreFilter() {
        swipeContainer.setEnabled(true);
        mRecentPresenter.onClearGenreFilter();
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
        mRecentPresenter.updateSelection(manga);
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
        mRecentPresenter.updateSelection(manga);
    }

}
