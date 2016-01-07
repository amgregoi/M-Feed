package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.teioh.m_feed.UI.MainActivity.View.Fragments.AllLibraryFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.FollowLibraryFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.RecentFragment;

public class ViewPagerAdapterMain extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;

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
            return new RecentFragment();
        } else if (position == 1) {
            return new FollowLibraryFragment();
        } else {
            return new AllLibraryFragment();
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
}
