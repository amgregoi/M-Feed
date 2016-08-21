package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.teioh.m_feed.UI.MainActivity.View.Fragments.FollowedFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LibraryFragment;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.RecentFragment;

public class ViewPagerAdapterMain extends FragmentStatePagerAdapter {

    private CharSequence mTabTitles[];
    private int mTabCount;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<Fragment>();


    public ViewPagerAdapterMain(FragmentManager aFragmentManager, CharSequence aTabTitles[], int aTabCount) {
        super(aFragmentManager);

        this.mTabTitles = aTabTitles;
        this.mTabCount = aTabCount;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int aPosition) {

        Fragment lFragment;
        switch (aPosition) {
            case 0:
                lFragment = new RecentFragment();
                mRegisteredFragments.put(0, lFragment);
                break;
            case 1:
                lFragment = new FollowedFragment();
                mRegisteredFragments.put(1, lFragment);
                break;
            default:
                lFragment = new LibraryFragment();
                mRegisteredFragments.put(2, lFragment);
        }
        return lFragment;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int aPosition) {
        return mTabTitles[aPosition];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return mTabCount;
    }

    public Fragment getRegisteredFragment(int aPosition) {
        return mRegisteredFragments.get(aPosition);
    }

    public boolean hasRegisteredFragments() {
        return mRegisteredFragments.get(0) != null;
    }
}
