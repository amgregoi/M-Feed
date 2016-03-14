package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.teioh.m_feed.UI.MainActivity.View.Fragments.FollowedFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LibraryFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.RecentFragment;

public class ViewPagerAdapterMain extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


    public ViewPagerAdapterMain(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            Fragment recent = new RecentFragment();
            registeredFragments.put(0, recent);
            return recent;
        } else if (position == 1) {
            Fragment follwed = new FollowedFragment();
            registeredFragments.put(1, follwed);
            return follwed;
        } else {
            Fragment library = new LibraryFragment();
            registeredFragments.put(2, library);
            return library;
        }
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public boolean hasRegisteredFragments(){
        return registeredFragments.get(0) != null;
    }
}
