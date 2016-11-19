package com.teioh.m_feed.UI.MainActivity.Presenters;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.Fragments.FilterDialogFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.FollowedFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.LibraryFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.RecentFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.SettingsFragment;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.UI.MainActivity.MainActivity;
import com.teioh.m_feed.Utils.MFDBHelper;
import com.teioh.m_feed.Utils.MangaLogger;
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

    private final CharSequence mTabTitles[] = {"Recent", "Followed", "Library"};
    private String[] mDrawerItems = {"Home", "Filter Search", "Sources", "Settings"};
    private ViewPagerAdapterMain mViewPagerAdapterMain;
    private IMain.ActivityView mMainMapper;
    private Fragment mSettingsFragment;
    private boolean mGenreFilterActive;
    private long mRecentMangaId;

    public MainPresenter(IMain.ActivityView aMap)
    {
        mMainMapper = aMap;
    }

    /***
     * TODO...
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            //setup base layout first
            mMainMapper.setupTabLayout();
            mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mMainMapper.getContext()).getSupportFragmentManager(), mTabTitles, 3);
            mMainMapper.registerAdapter(mViewPagerAdapterMain);

            //init rest of the layout
            setupDrawerLayouts();
            mMainMapper.setupSearchView();
            mMainMapper.setupToolbar();
            mMainMapper.setupSourceFilterMenu();
            mMainMapper.setDrawerLayoutListener();

            mGenreFilterActive = false;
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aSave
     */
    @Override
    public void onSaveState(Bundle aSave)
    {

    }

    /***
     * TODO...
     *
     * @param aRestore
     */
    @Override
    public void onRestoreState(Bundle aRestore)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mViewPagerAdapterMain = new ViewPagerAdapterMain(((FragmentActivity) mMainMapper.getContext()).getSupportFragmentManager(), mTabTitles, 3);
            mMainMapper.registerAdapter(mViewPagerAdapterMain);
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     */
    @Override
    public void onResume()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mMainMapper.closeDrawer();
            setupDrawerLayouts();
            if (mRecentMangaId >= 0) getRecentManga();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     */
    @Override
    public void onPause()
    {

    }

    /***
     * TODO...
     *
     * @param aNewTest
     */
    @Override
    public void updateQueryChange(String aNewTest)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onQueryTextChange(aNewTest);
                ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onQueryTextChange(aNewTest);
                ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onQueryTextChange(aNewTest);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     */
    @Override
    public void onDestroy()
    {
        ButterKnife.unbind(mMainMapper);
        mMainMapper = null;
    }

    /***
     * TODO...
     *
     * @param aPosition
     */
    @Override
    public void onDrawerItemChosen(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

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
                    //setftings fragment
                    if (mSettingsFragment == null) addSettingsFragment();
                    mMainMapper.closeDrawer();
                    return;
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aPosition
     */
    @Override
    public void onSourceItemChosen(int aPosition)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            SourceBase lSource = new SourceFactory().getSource();
            MangaEnums.eSource lSourceType = lSource.getSourceByPosition(aPosition);

            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                if (lSource.getSourceType() != lSourceType)
                {
                    new SourceFactory().getSource().setSourceType(lSourceType);
                    Toast.makeText(mMainMapper.getContext(), "Changing source to " + lSourceType, Toast.LENGTH_SHORT).show();

                    mMainMapper.setDefaultFilterImage();
                    mGenreFilterActive = false;
                    ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).updateSource();
                    ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).updateSource();
                    ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).updateSource();
                    mMainMapper.setActivityTitle(lSourceType.name());
                }
            }
            else
            {
                MangaLogger.logInfo(TAG, lMethod, "Fragment is null, cannot swithc sources");
                Toast.makeText(mMainMapper.getContext(), "Fragment is null, cannot switch sources", Toast.LENGTH_SHORT).show();

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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aFilter
     */
    @Override
    public void onFilterSelected(MangaEnums.eFilterStatus aFilter)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onFilterSelected(aFilter);
                ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onFilterSelected(aFilter);
                ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onFilterSelected(aFilter);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aIntent
     */
    @Override
    public void onGenreFilterSelected(Intent aIntent)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ArrayList<Manga> list = aIntent.getParcelableArrayListExtra("MANGA");

            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                mGenreFilterActive = true;
                ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onGenreFilterSelected(list);
                ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onGenreFilterSelected(list);
                ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onGenreFilterSelected(list);
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     */
    @Override
    public void onClearGenreFilter()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mViewPagerAdapterMain.hasRegisteredFragments())
            {
                mGenreFilterActive = false;
                ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onClearGenreFilter();
                ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onClearGenreFilter();
                ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onClearGenreFilter();
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     */
    @Override
    public void removeSettingsFragment()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ((MainActivity) mMainMapper.getContext()).getSupportFragmentManager().beginTransaction().remove(mSettingsFragment).commit();
            mSettingsFragment = null;
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @return
     */
    @Override
    public boolean genreFilterActive()
    {
        return mGenreFilterActive;
    }

    /***
     * TODO...
     *
     * @param aMangaId
     */
    @Override
    public void setRecentManga(long aMangaId)
    {
        mRecentMangaId = aMangaId;
    }

    /***
     * TODO...
     */
    @Override
    public void getRecentManga()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            Manga lManga = MFDBHelper.getInstance().getManga(mRecentMangaId);

            if (lManga != null)
            {
                ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).updateRecentSelection(lManga);
                ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).updateRecentSelection(lManga);
                ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).updateRecentSelection(lManga);
                mRecentMangaId = -1;
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     */

    private void setupDrawerLayouts()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            List<String> lDrawerItems = Arrays.asList(mDrawerItems);

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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     */
    private void addSettingsFragment()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mMainMapper.toggleToolbarElements();
            mSettingsFragment = new SettingsFragment();
            ((MainActivity) mMainMapper.getContext()).getSupportFragmentManager().beginTransaction().add(R.id.main_activity_content, mSettingsFragment, SettingsFragment.TAG).commit();
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }
}
