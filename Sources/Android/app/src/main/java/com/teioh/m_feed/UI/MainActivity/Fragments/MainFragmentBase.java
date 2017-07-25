package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.l4digital.fastscroll.FastScrollRecyclerView;
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
    protected IMain.FragmentPresenter mFragmentPresenter;
    protected Listeners.MainFragmentListener mListener;
    @Bind(R.id.manga_recycle_view) FastScrollRecyclerView mGridView;

    /***
     * This function is called in the fragment lifecycle
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
     * This function initializes the view of the fragment.
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    public abstract View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState);

    /***
     * This function restores/initializes the fragment presenter layer.
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
     * This function is called in the fragment lifecycle
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mFragmentPresenter.onResume();
    }

    /***
     * This function saves the state of the fragment when a transition is invoked.
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
     * This function is called in the fragment lifecycle
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mFragmentPresenter.onPause();
    }

    /***
     * This function clears the Glide cache to cleanup memory when necessary.
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(getContext()).clearMemory();
    }

    /***
     * This function is called in the fragment lifecycle
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mFragmentPresenter.onDestroy();
        ButterKnife.unbind(this);
    }

    /***
     * This function updates the current source.
     */
    @Override
    public void updateSource()
    {
        mFragmentPresenter.updateSource();
    }

    /***
     * This function performs the filter by status filter.
     *
     * @param aFilter
     */
    public void onFilterSelected(MangaEnums.eFilterStatus aFilter)
    {
        mFragmentPresenter.onFilterSelected(aFilter);
    }

    /***
     * This function performs the Genre query filter.
     *
     * @param aMangaList
     */
    @Override
    public boolean onGenreFilterSelected(ArrayList<Manga> aMangaList)
    {
        return mFragmentPresenter.onGenreFilterSelected(aMangaList);
    }

    /***
     * This function clears the genre query filter.
     */
    @Override
    public boolean onClearGenreFilter()
    {
        return mFragmentPresenter.onClearGenreFilter();
    }

    /***
     * This function registers the adapter to the recycler view.
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
     * This function updates the specified object in the fragment.
     *
     * @param aManga
     */
    @Override
    public void updateSelection(Manga aManga)
    {
        mFragmentPresenter.updateSelection(aManga);
    }

    /***
     * This function sets the recently selected item according to its url.
     *
     * @param aUrl
     * @return
     */
    @Override
    public boolean setRecentSelection(String aUrl)
    {
        return mListener.setRecentSelection(aUrl);
    }

    /***
     * This function updates the recently selected item according to the object.
     *
     * @param aManga
     */
    @Override
    public boolean updateRecentSelection(Manga aManga)
    {
        return mFragmentPresenter.updateSelection(aManga);
    }

    /***
     * Not implemented
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
     * This function performs the text query filter.
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
     * This function updates three main views.
     */
    @Override
    public void updateFragmentViews()
    {
        mFragmentPresenter.updateMangaList();
    }

    /***
     * This function starts the swipe refresh layout refresh animation.
     */
    public abstract void startRefresh();

    /***
     * This function stops the swipe refresh layout refresh animation.
     */
    public abstract void stopRefresh();

    /***
     * This function initializes the swipe refresh layout.
     */
    public abstract void setupSwipeRefresh();

}
