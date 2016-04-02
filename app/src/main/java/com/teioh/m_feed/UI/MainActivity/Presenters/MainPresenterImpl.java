package com.teioh.m_feed.UI.MainActivity.Presenters;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.LoginActivity.View.LoginActivity;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.FilterDialogFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.FollowedFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LibraryFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.RecentFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.SettingsFragment;
import com.teioh.m_feed.UI.MainActivity.View.MainActivity;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.MainActivityMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.SharedPrefsUtil;
import com.teioh.m_feed.WebSources.MangaHere;
import com.teioh.m_feed.WebSources.MangaJoy;
import com.teioh.m_feed.WebSources.MangaPark;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainPresenterImpl implements MainPresenter {
    public final static String TAG = MainPresenterImpl.class.getSimpleName();

    private final CharSequence mTabTitles[] = {"Recent", "Followed", "Library"};
    private ViewPagerAdapterMain mViewPagerAdapterMain;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivityMapper mMainMapper;
    private Fragment settings;
    private boolean mGenreFilterActive;
    private long mRecentMangaId;

    public MainPresenterImpl(MainActivityMapper main) {
        mMainMapper = main;
    }

    @Override
    public void init(Bundle bundle) {
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

    @Override
    public void onSaveState(Bundle save) {

    }

    @Override
    public void onRestoreState(Bundle restore) {

    }

    @Override
    public void onSignIn() {
        Intent intent = new Intent(mMainMapper.getContext(), LoginActivity.class);
        mMainMapper.getContext().startActivity(intent);
    }

    @Override
    public void onResume() {
        mMainMapper.closeDrawer();
        setupDrawerLayouts();
        if (mRecentMangaId >= 0)
            getRecentManga();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void updateQueryChange(String newTest) {
        if (mViewPagerAdapterMain.hasRegisteredFragments()) {
            ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onQueryTextChange(newTest);
            ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onQueryTextChange(newTest);
            ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onQueryTextChange(newTest);
        }
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(mMainMapper);
    }

    @Override
    public void onDrawerItemChosen(int position) {
        switch (position) {
            case (0):
                //home button
                if (settings != null) {
                    removeSettingsFragment();
                    mMainMapper.closeDrawer();
                    mMainMapper.toggleToolbarElements();
                    if (mGenreFilterActive)
                        mMainMapper.setActivityTitle(mMainMapper.getContext().getString(R.string.filter_active));
                } else mMainMapper.closeDrawer();
                return;
            case (1):
                //advanced search fragment
                mMainMapper.closeDrawer();
                mMainMapper.setPageAdapterItem(2);
                if (settings != null) removeSettingsFragment();
                DialogFragment dialog = FilterDialogFragment.getnewInstance();
                dialog.show(((AppCompatActivity) mMainMapper).getSupportFragmentManager(), null);
                return;
            case (3):
                //setftings fragment
                if (settings == null) {
                    addSettingsFragment();
                    mMainMapper.closeDrawer();
                }
                return;
        }
    }

    @Override
    public void onSourceItemChosen(int position) {
        String source;
        switch (position) {
            case (0):
                source = MangaHere.SourceKey;
                break;
            case (1):
                source = MangaPark.SourceKey;
                break;
            default:
                source = MangaJoy.SourceKey;
                break;
        }

        if (!source.equals(WebSource.getCurrentSource())) {
            WebSource.setwCurrentSource(source);
            Toast.makeText(mMainMapper.getContext(), "Changing source to " + source, Toast.LENGTH_SHORT).show();
            if (mViewPagerAdapterMain.hasRegisteredFragments()) {
                mMainMapper.setDefaultFilterImage();
                mGenreFilterActive = false;
                ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).updateSource();
                ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).updateSource();
                ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).updateSource();
            }
            mMainMapper.setActivityTitle(source);
        }

        if (settings != null) {
            removeSettingsFragment();
            mMainMapper.openDrawer();
            mMainMapper.toggleToolbarElements();
        }
    }

    @Override
    public void onFilterSelected(int filter) {
        if (mViewPagerAdapterMain.hasRegisteredFragments()) {
            ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onFilterSelected(filter);
            ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onFilterSelected(filter);
            ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onFilterSelected(filter);
        }
    }

    public void onGenreFilterSelected(Intent intent) {
        ArrayList<Manga> list = intent.getParcelableArrayListExtra("MANGA");

        if (mViewPagerAdapterMain.hasRegisteredFragments()) {
            mGenreFilterActive = true;
            ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onGenreFilterSelected(list);
            ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onGenreFilterSelected(list);
            ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onGenreFilterSelected(list);
        }
    }

    @Override
    public void onClearGenreFilter() {
        if (mViewPagerAdapterMain.hasRegisteredFragments()) {
            mGenreFilterActive = false;
            ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).onClearGenreFilter();
            ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).onClearGenreFilter();
            ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).onClearGenreFilter();
        }
    }

    private void setupDrawerLayouts() {
        List<String> mDrawerItems = new ArrayList<>();
        mDrawerItems.add("Home");
        mDrawerItems.add("Search");
        mDrawerItems.add("Sources");
        mDrawerItems.add("Settings");


        Map<String, List<String>> mSourceCollections = new LinkedHashMap<>();
        for (String item : mDrawerItems) {
            List<String> mDrawerChildren = new ArrayList<>();
            if (item.equals("Sources")) {
                for (String model : WebSource.getSourceList())
                    mDrawerChildren.add(model);
            }
            mSourceCollections.put(item, mDrawerChildren);
        }
        mMainMapper.setupDrawerLayout(mDrawerItems, mSourceCollections);
    }

    @Override
    public void removeSettingsFragment() {
        ((MainActivity) mMainMapper.getContext()).getSupportFragmentManager().beginTransaction()
                .remove(settings)
                .commit();
        settings = null;
    }

    private void addSettingsFragment() {
        mMainMapper.toggleToolbarElements();
        settings = new SettingsFragment();
        ((MainActivity) mMainMapper.getContext()).getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity_content, settings, SettingsFragment.TAG)
                .commit();
    }

    @Override
    public boolean genreFilterActive() {
        return mGenreFilterActive;
    }

    @Override
    public void setRecentManga(long id) {
        mRecentMangaId = id;
    }

    @Override
    public void getRecentManga() {
        Manga manga = cupboard()
                .withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase()).query(Manga.class)
                .withSelection("_id = ?", Long.toString(mRecentMangaId))
                .get();

        if (manga != null) {
            ((RecentFragment) mViewPagerAdapterMain.getRegisteredFragment(0)).updateRecentSelection(manga);
            ((FollowedFragment) mViewPagerAdapterMain.getRegisteredFragment(1)).updateRecentSelection(manga);
            ((LibraryFragment) mViewPagerAdapterMain.getRegisteredFragment(2)).updateRecentSelection(manga);
            mRecentMangaId = -1;
        }
    }

    @Override
    public void onMALSignIn() {
        //TODO need to do more research in MAL api options
        if (SharedPrefsUtil.isSignedIn()) {
            SharedPrefsUtil.setMALCredential(null, null);
            setupDrawerLayouts();
        } else {
            onSignIn();
        }
    }


}
