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
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.Presenters.HomePresenter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.ViewPresenterMapper;
import com.teioh.m_feed.UI.Maps.Listeners;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amgregoi on 8/20/16.
 */
public abstract class MainFragmentBase extends Fragment implements ViewPresenterMapper{
    @Bind(R.id.manga_recycle_view) RecyclerView mGridView;

    protected HomePresenter mFragmentPresenter;
    protected Listeners.MainFragmentListener mListener;


    public abstract View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState);

    /***
     * TODO...
     * @param aSave
     */
    @Override
    public void onActivityCreated(@Nullable Bundle aSave) {
        super.onActivityCreated(aSave);
        if (aSave != null) {
            mFragmentPresenter.onRestoreState(aSave);
        }
        mFragmentPresenter.init(getArguments());
    }

    /***
     * TODO...
     *
     * @param aRestore
     */
    @Override
    public void onSaveInstanceState(Bundle aRestore) {
        super.onSaveInstanceState(aRestore);
        mFragmentPresenter.onSaveState(aRestore);
    }

    /***
     * TODO...
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        mFragmentPresenter.onResume();
    }

    /***
     * TODO...
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        mFragmentPresenter.onPause();
    }

    /***
     * TODO...
     *
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentPresenter.onDestroy();
        ButterKnife.unbind(this);
    }

    /***
     * TODO...
     *
     * @param aContext
     */
    @Override
    public void onAttach(Context aContext) {
        super.onAttach(aContext);

        if (aContext instanceof Listeners.MainFragmentListener)
            mListener = (Listeners.MainFragmentListener) aContext;
        else
            throw new ClassCastException(aContext.toString() + " must implement Listeners.MainFragmentListener");
    }

    /***
     * TODO...
     *
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getContext()).clearMemory();
    }

    /***
     * TODO...
     *
     */
    @Override
    public void updateSource() {
        mFragmentPresenter.updateSource();
    }

    /***
     * TODO...
     *
     * @param aFilter
     */
    @Override
    public void onFilterSelected(int aFilter) {
        mFragmentPresenter.onFilterSelected(aFilter);
    }

    /***
     * TODO...
     *
     * @param aMangaList
     */
    @Override
    public void onGenreFilterSelected(ArrayList<Manga> aMangaList) {
        mFragmentPresenter.onGenreFilterSelected(aMangaList);
    }

    /***
     * TODO...
     *
     */
    @Override
    public void onClearGenreFilter() {
        mFragmentPresenter.onClearGenreFilter();
    }

    /***
     * TODO...
     *
     * @param aAdapter
     * @param aLayout
     * @param aNeedsDecoration
     */
    @Override
    public void registerAdapter(MoPubRecyclerAdapter aAdapter, RecyclerView.LayoutManager aLayout, boolean aNeedsDecoration) {
        if (aAdapter != null) {
            mGridView.swapAdapter(aAdapter, true);
            mGridView.setLayoutManager(aLayout);

            if (aNeedsDecoration)
                mGridView.addItemDecoration(new RecycleSearchAdapter.SpacesItemDecoration(20));
        }
    }

    /***
     * TODO...
     *
     * @param aManga
     */
    @Override
    public void updateSelection(Manga aManga) {
        mFragmentPresenter.updateSelection(aManga);
    }

    /***
     * TODO...
     *
     * @param aId
     * @return
     */
    @Override
    public boolean setRecentSelection(Long aId) {
        return mListener.setRecentSelection(aId);
    }

    /***
     * TODO...
     *
     * @param aManga
     */
    @Override
    public void updateRecentSelection(Manga aManga) {
        mFragmentPresenter.updateSelection(aManga);
    }

    /***
     * TODO...
     *
     * @param aQueryText
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String aQueryText) {
        return false;
    }

    /***
     * TODO...
     *
     * @param aQueryText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String aQueryText) {
        mFragmentPresenter.onQueryTextChange(aQueryText);
        return true;
    }

    /***
     * TODO...
     *
     */
    public abstract void startRefresh();

    /***
     * TODO...
     *
     */
    public abstract void stopRefresh();

    /***
     * TODO...
     *
     */
    public abstract void setupSwipeRefresh();

}
