package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.TraceCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.l4digital.fastscroll.FastScroller;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.UI.Maps.Listeners;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amgregoi on 8/20/16.
 */
public abstract class MainFragmentBase extends Fragment implements IMain.FragmentView
{
    @Bind(R.id.manga_recycle_view) FastScrollRecyclerView mGridView;

    protected IMain.FragmentPresenter mFragmentPresenter;
    protected Listeners.MainFragmentListener mListener;

    /***
     * TODO.
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    public abstract View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState);

    /***
     * TODO...
     *
     * @param aSave
     */
    @Override
    public void onActivityCreated(@Nullable Bundle aSave)
    {
        super.onActivityCreated(aSave);
        if (aSave != null)
        {
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
    public void onSaveInstanceState(Bundle aRestore)
    {
        super.onSaveInstanceState(aRestore);
        mFragmentPresenter.onSaveState(aRestore);
    }

    /***
     * TODO...
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mFragmentPresenter.onResume();
    }

    /***
     * TODO...
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mFragmentPresenter.onPause();
    }

    /***
     * TODO...
     */
    @Override
    public void onDestroyView()
    {
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
    public void onAttach(Context aContext)
    {
        super.onAttach(aContext);

        if (aContext instanceof Listeners.MainFragmentListener) mListener = (Listeners.MainFragmentListener) aContext;
        else throw new ClassCastException(aContext.toString() + " must implement Listeners.MainFragmentListener");
    }

    /***
     * TODO...
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(getContext()).clearMemory();
    }

    /***
     * TODO...
     */
    @Override
    public void updateSource()
    {
        mFragmentPresenter.updateSource();
    }

    /***
     * TODO...
     *
     * @param aFilter
     */
    public void onFilterSelected(MangaEnums.eFilterStatus aFilter)
    {
        mFragmentPresenter.onFilterSelected(aFilter);
    }

    /***
     * TODO...
     *
     * @param aMangaList
     */
    @Override
    public boolean onGenreFilterSelected(ArrayList<Manga> aMangaList)
    {
        return mFragmentPresenter.onGenreFilterSelected(aMangaList);
    }

    /***
     * TODO...
     */
    @Override
    public boolean onClearGenreFilter()
    {
        return mFragmentPresenter.onClearGenreFilter();
    }

    /***
     * TODO...
     *
     * @param aAdapter
     * @param aLayout
     * @param aNeedsDecoration
     */
    @Override
    public void registerAdapter(RecyclerView.Adapter aAdapter, RecyclerView.LayoutManager aLayout, boolean aNeedsDecoration)
    {
        if (aAdapter != null)
        {
            mGridView.setLayoutManager(aLayout);

            if (aNeedsDecoration)
            {
                mGridView.setAdapter(aAdapter);
                mGridView.addItemDecoration(new RecycleSearchAdapter.SpacesItemDecoration(20));
            }
            else
            {
                mGridView.swapAdapter(aAdapter, true);
            }

        }
    }

    /***
     * TODO...
     *
     * @param aManga
     */
    @Override
    public void updateSelection(Manga aManga)
    {
        mFragmentPresenter.updateSelection(aManga);
    }

    /***
     * TODO...
     *
     * @param aId
     * @return
     */
    @Override
    public boolean setRecentSelection(Long aId)
    {
        return mListener.setRecentSelection(aId);
    }

    /***
     * TODO...
     *
     * @param aManga
     */
    @Override
    public boolean updateRecentSelection(Manga aManga)
    {
        return mFragmentPresenter.updateSelection(aManga);
    }

    /***
     * TODO...
     *
     * @param aQueryText
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String aQueryText)
    {
        return false;
    }

    /***
     * TODO...
     *
     * @param aQueryText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String aQueryText)
    {
        mFragmentPresenter.onQueryTextChange(aQueryText);
        return true;
    }

    /***
     * TODO...
     */
    public abstract void startRefresh();

    /***
     * TODO...
     */
    public abstract void stopRefresh();

    /***
     * TODO...
     */
    public abstract void setupSwipeRefresh();

}
