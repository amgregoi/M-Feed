package com.teioh.m_feed.UI.MainActivity.Presenters;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.teioh.m_feed.BuildConfig;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.Fragments.CatalogFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.FilterDialogFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.LibraryFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.RecentFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.SettingsFragment;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.UI.MainActivity.MainActivity;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;
import com.teioh.m_feed.WebSources.SourceBase;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class MainPresenter implements IMain.ActivityPresenter
{
    public final static String TAG = MainPresenter.class.getSimpleName();

    private final CharSequence mTabTitles[] = {"Recent", "Library", "Catalog"};
    private String[] mDrawerItems = {"Home", "Filter Search", "Sources", "Settings"};
    private ViewPagerAdapterMain mViewPagerAdapterMain;
    private IMain.ActivityView mMainMapper;
    private Fragment mSettingsFragment;
    private boolean mGenreFilterActive;
    private long mRecentMangaId;

    private GoogleSignInAccount mGoogleAccount;

    public MainPresenter(IMain.ActivityView aMap)
    {
        mMainMapper = aMap;
    }

    /***
     * This function initializes the presenter.
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle)
    {
        try
        {
            //setup base layout first
            mMainMapper.setupTabLayout();
            mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mMainMapper.getContext())
                                                                     .getSupportFragmentManager(), mTabTitles, 3);
            mMainMapper.registerAdapter(mViewPagerAdapterMain);

            //init rest of the layout
            setupDrawerLayouts();
            mMainMapper.setupSearchView();
            mMainMapper.setupToolbar();
            mMainMapper.setDrawerLayoutListener();

            mGenreFilterActive = false;
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aSave
     */
    @Override
    public void onSaveState(Bundle aSave)
    {

    }

    /***
     * This function restores data that needed to persist between device state changes.
     *
     * @param aRestore
     */
    @Override
    public void onRestoreState(Bundle aRestore)
    {
        try
        {
            mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mMainMapper.getContext())
                                                                     .getSupportFragmentManager(), mTabTitles, 3);
            mMainMapper.registerAdapter(mViewPagerAdapterMain);
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    public void onPause()
    {

    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    public void onResume()
    {
        try
        {
            mMainMapper.closeDrawer();
            setupDrawerLayouts();
            if (mRecentMangaId >= 0) updateRecentManga();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    public void onDestroy()
    {
        ButterKnife.unbind(mMainMapper);
        mMainMapper = null;
    }

    /***
     * This function initializes the drawer layout.
     */

    private void setupDrawerLayouts()
    {
        try
        {
            ArrayList<String> lDrawerItems = new ArrayList<>(Arrays.asList(mDrawerItems));
            //check if signed in or signed out
            if (SharedPrefs.isSignedIn())
            {
                lDrawerItems.add("Sign out");
            }
            else
            {
                lDrawerItems.add("Sign in with Google");
            }

            Map<String, List<String>> lSourceCollections = new LinkedHashMap<>();
            for (String iDrawerItem : lDrawerItems)
            {
                List<String> lDrawerChildren = new ArrayList<>();
                if (iDrawerItem.equals("Sources"))
                {
                    for (MangaEnums.eSource iType : Arrays.asList(MangaEnums.eSource.values()))
                        lDrawerChildren.add(iType.name());
                }
                lSourceCollections.put(iDrawerItem, lDrawerChildren);
            }

            mMainMapper.setupDrawerLayout(lDrawerItems, lSourceCollections);
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function updates the text query filter.
     *
     * @param aNewTest
     */
    @Override
    public boolean updateQueryChange(String aNewTest)
    {
        boolean lResult = true;
        try
        {
            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                lResult &= ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onQueryTextChange(aNewTest);
                lResult &= ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onQueryTextChange(aNewTest);
                lResult &= ((CatalogFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onQueryTextChange(aNewTest);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function performs the various actions when a drawer item is selected specified by the item position.
     *
     * @param aPosition
     */
    @Override
    public void onDrawerItemSelected(int aPosition)
    {
        try
        {
            switch (aPosition)
            {
                case (0):
                    //home button
                    if (mSettingsFragment != null)
                    {
                        removeSettingsFragment();
                        mMainMapper.closeDrawer();
                        mMainMapper.toggleToolbarElements();
                        if (mGenreFilterActive) mMainMapper.setActivityTitle(mMainMapper.getContext().getString(R.string.filter_active));
                    }
                    else mMainMapper.closeDrawer();
                    return;
                case (1):
                    //advanced search fragment
                    mMainMapper.closeDrawer();
                    mMainMapper.setPageAdapterItem(2);
                    if (mSettingsFragment != null)
                    {
                        removeSettingsFragment();
                        mMainMapper.toggleToolbarElements();
                    }
                    DialogFragment dialog = FilterDialogFragment.getnewInstance();
                    dialog.show(((AppCompatActivity) mMainMapper).getSupportFragmentManager(), null);
                    return;
                case (3):
                    //settings fragment
                    if (mSettingsFragment == null) addSettingsFragment();
                    mMainMapper.closeDrawer();
                    return;
                case (4):
                    //sign in / sign out
                    if (mGoogleAccount == null)
                    {
                        mMainMapper.signIn();
                        setupDrawerLayouts();

                    }
                    else
                    {
                        mMainMapper.signOut();
                        updateSignIn(null);
                        setupDrawerLayouts();
                    }
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function updates the source when a new source is chosen specified by its position.
     *
     * @param aPosition
     */
    @Override
    public boolean onSourceItemChosen(int aPosition)
    {
        boolean lResult = true;

        try
        {
            SourceBase lSource = new SourceFactory().getSource();
            MangaEnums.eSource lSelectedSource = lSource.getSourceByPosition(aPosition);

            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                if (lSource.getCurrentSource() != lSelectedSource)
                {
                    new SourceFactory().getSource().setCurrentSource(lSelectedSource);
                    Toast.makeText(mMainMapper.getContext(), "Changing source to " + lSelectedSource, Toast.LENGTH_SHORT).show();

                    mMainMapper.setDefaultFilterImage();
                    mGenreFilterActive = false;
                    ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).updateSource();
                    ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).updateSource();
                    ((CatalogFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).updateSource();
                    mMainMapper.setActivityTitle(lSelectedSource.name());
                }
            }
            else
            {
                MangaLogger.logInfo(TAG, "Fragment is null, cannot switch sources");
                Toast.makeText(mMainMapper.getContext(), "Fragment is null, cannot switch sources", Toast.LENGTH_SHORT).show();
                lResult = false;
            }

            if (mSettingsFragment != null)
            {
                removeSettingsFragment();
                mMainMapper.openDrawer();
                mMainMapper.toggleToolbarElements();
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function performs the filter by status filter specified by the FilterStatus.
     *
     * @param aFilter
     */
    @Override
    public void onFilterSelected(MangaEnums.eFilterStatus aFilter)
    {
        try
        {
            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onFilterSelected(aFilter);
                ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onFilterSelected(aFilter);
                ((CatalogFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onFilterSelected(aFilter);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function removes the settings fragment from view.
     */
    @Override
    public boolean removeSettingsFragment()
    {
        boolean lResult = true;
        try
        {
            ((MainActivity) mMainMapper.getContext()).getSupportFragmentManager().beginTransaction().remove(mSettingsFragment).commit();
            mSettingsFragment = null;
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }

        if (BuildConfig.DEBUG)
        {
            if (mSettingsFragment != null) lResult = false;
        }

        return lResult;
    }

    /***
     * This function performs the Genre filter.
     *
     * @param aIntent
     */
    @Override
    public boolean onGenreFilterSelected(Intent aIntent)
    {
        boolean lResult = true;

        try
        {
            ArrayList<Manga> list = aIntent.getParcelableArrayListExtra("MANGA");

            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                mGenreFilterActive = true;
                lResult &= ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onGenreFilterSelected(list);
                lResult &= ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onGenreFilterSelected(list);
                lResult &= ((CatalogFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onGenreFilterSelected(list);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function clears the genre filter.
     */
    @Override
    public boolean onClearGenreFilter()
    {
        boolean lResult = true;

        try
        {
            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                mGenreFilterActive = false;
                lResult &= ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onClearGenreFilter();
                lResult &= ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onClearGenreFilter();
                lResult &= ((CatalogFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onClearGenreFilter();
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function returns whether the GenreFilter is active.
     *
     * @return
     */
    @Override
    public boolean genreFilterActive()
    {
        return mGenreFilterActive;
    }

    /***
     * This function updates the recently selected manga.
     */
    @Override
    public boolean updateRecentManga()
    {
        boolean lResult = true;

        try
        {
            Manga lManga = MangaDB.getInstance().getManga(mRecentMangaId);

            if (lManga != null)
            {
                lResult &= ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).updateRecentSelection(lManga);
                lResult &= ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).updateRecentSelection(lManga);
                lResult &= ((CatalogFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).updateRecentSelection(lManga);
                mRecentMangaId = -1;
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function updates the last clicked manga.
     *
     * @param aMangaId
     */
    @Override
    public void setRecentManga(long aMangaId)
    {
        mRecentMangaId = aMangaId;
    }

    /***
     * This function performs the google sign in
     *
     * @param aAccount
     */
    @Override
    public boolean updateSignIn(GoogleSignInResult aAccount)
    {
        boolean lResult = false;

        try
        {
            if (aAccount != null)
            {
                if (aAccount.isSuccess())
                {
                    mGoogleAccount = aAccount.getSignInAccount();
                    SharedPrefs.setGoogleEmail(mGoogleAccount.getEmail());
                    lResult = true;
                    //TODO..
                    //update favorites list from back end etc..( when implemented )

                }
                else
                {
                    mGoogleAccount = null;
                    SharedPrefs.setGoogleEmail(null);
                    MangaLogger.logInfo(TAG, "Sign in failed, logging out: " + aAccount.getStatus());
                }
            }
            else
            {
                mGoogleAccount = null;
                SharedPrefs.setGoogleEmail(null);
                MangaLogger.logInfo(TAG, "Sign in result was null, logging out");

            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }

        return lResult;
    }

    /***
     * This function brings the settings fragments into view.
     */
    private void addSettingsFragment()
    {
        try
        {
            mMainMapper.toggleToolbarElements();
            mSettingsFragment = SettingsFragment.getnewInstance();
            ((MainActivity) mMainMapper.getContext()).getSupportFragmentManager()
                                                     .beginTransaction()
                                                     .add(R.id.main_activity_content, mSettingsFragment, SettingsFragment.TAG)
                                                     .commit();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }
}
