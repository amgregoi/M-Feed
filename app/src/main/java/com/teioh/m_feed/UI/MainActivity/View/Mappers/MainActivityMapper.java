package com.teioh.m_feed.UI.MainActivity.View.Mappers;


import android.support.v7.app.ActionBarDrawerToggle;

import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.FollowedFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LibraryFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.RecentFragment;
import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.SearchViewListenerMap;

import java.util.List;
import java.util.Map;

public interface MainActivityMapper extends BaseContextMap, SearchViewListenerMap, RecentFragment.RecentFragmentListener, FollowedFragment.FollowedFragmentListener, LibraryFragment.LibraryFragmentListener {

    void registerAdapter(ViewPagerAdapterMain adapter);

    void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle);

    void onDrawerOpen();

    void onDrawerClose();

    void closeDrawer();

    void openDrawer();

    void setupToolbar();

    void setupTabLayout();

    void setupSearchView();

    void setupDrawerLayout(List<String> mDrawerItems, Map<String, List<String>> mSourceCollections);

    void setupSourceFilterMenu();

    void changeSourceTitle(String source);

    void toggleToolbarElements();

    void filterDialogOpen();

    void setPageAdapterItem(int position);

    void resetFilterImage();
}
