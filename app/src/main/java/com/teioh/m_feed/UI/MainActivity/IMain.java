package com.teioh.m_feed.UI.MainActivity;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.DrawerLayoutMap;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.UI.Maps.MangaFilterMap;
import com.teioh.m_feed.UI.Maps.MoPubAdapterMap;
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

        void setupSourceFilterMenu();

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

        void updateQueryChange(String aNewTest);

        void onDrawerItemSelected(int aPosition);

        void onSourceItemChosen(int aPosition);

        void onFilterSelected(MangaEnums.eFilterStatus aFilter);

        void removeSettingsFragment();

        void onGenreFilterSelected(Intent aIntent);

        void onClearGenreFilter();

        boolean genreFilterActive();

        void getRecentManga();

        void setRecentManga(long aMangaId);

        void updateSignIn(GoogleSignInResult aAccount);
    }

    /***
     * TODO..
     */
    interface FragmentView extends MoPubAdapterMap, SwipeRefreshMap, SearchViewListenerMap, BaseContextMap, MangaFilterMap
    {

    }

    /***
     * TODO..
     */
    interface FragmentPresenter extends LifeCycleMap
    {

        void updateMangaList();

        void onQueryTextChange(String aQueryText);

        void updateSource();

        void onFilterSelected(MangaEnums.eFilterStatus aFilter);

        void onGenreFilterSelected(ArrayList<Manga> aMangaList);

        void onClearGenreFilter();

        void updateSelection(Manga aManga);

    }
}
