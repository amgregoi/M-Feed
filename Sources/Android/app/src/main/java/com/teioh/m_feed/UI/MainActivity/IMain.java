package com.teioh.m_feed.UI.MainActivity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.DrawerLayoutMap;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.Maps.MangaFilterMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;
import com.teioh.m_feed.UI.Maps.SignInMap;
import com.teioh.m_feed.UI.Maps.SwipeRefreshMap;

import java.util.ArrayList;

public interface IMain
{
    /***
     * TODO..
     */
    interface ActivityView extends BaseContextMap, SearchViewListenerMap, Listeners.MainFragmentListener, DrawerLayoutMap, SignInMap
    {

        void registerAdapter(ViewPagerAdapterMain aAdapter);

        void setupToolbar();

        void setupTabLayout();

        void setupSearchView();

        void setActivityTitle(String aTitle);

        void setPageAdapterItem(int aPosition);

        void setDefaultFilterImage();

        void toggleToolbarElements();
    }

    /***
     * TODO..
     */
    interface ActivityPresenter extends LifeCycleMap
    {

        boolean updateQueryChange(String aNewTest);

        void onDrawerItemSelected(int aPosition);

        boolean onSourceItemChosen(int aPosition);

        void onFilterSelected(MangaEnums.eFilterStatus aFilter);

        boolean removeSettingsFragment();

        boolean onGenreFilterSelected(Intent aIntent);

        boolean onClearGenreFilter();

        boolean genreFilterActive();

        boolean updateRecentManga();

        void setRecentManga(long aMangaId);

        boolean updateSignIn(GoogleSignInResult aAccount);
    }

    /***
     * TODO..
     */
    interface FragmentView extends SwipeRefreshMap, SearchViewListenerMap, BaseContextMap, MangaFilterMap, Listeners.MainFragmentListener
    {
        void registerAdapter(RecyclerView.Adapter aAdapter, RecyclerView.LayoutManager aLayout, boolean aNeedsDecoration);

        void updateSelection(Manga aManga);

        boolean updateRecentSelection(Manga aManga);

        boolean setRecentSelection(Long aId);

    }

    /***
     * TODO..
     */
    interface FragmentPresenter extends LifeCycleMap
    {

        void updateMangaList();

        boolean onQueryTextChange(String aQueryText);

        boolean updateSource();

        boolean onFilterSelected(MangaEnums.eFilterStatus aFilter);

        boolean onGenreFilterSelected(ArrayList<Manga> aMangaList);

        boolean onClearGenreFilter();

        boolean updateSelection(Manga aManga);
    }
}
