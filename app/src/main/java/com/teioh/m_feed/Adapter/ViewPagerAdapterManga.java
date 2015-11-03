package com.teioh.m_feed.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.teioh.m_feed.Fragment.MangaChapterFragment;
import com.teioh.m_feed.Fragment.MangaFragment;
import com.teioh.m_feed.Fragment.Tab1;
import com.teioh.m_feed.Fragment.Tab2;
import com.teioh.m_feed.Fragment.Tab3;
import com.teioh.m_feed.Pojo.Manga;

public class ViewPagerAdapterManga extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;
    Manga manga;

    public ViewPagerAdapterManga(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, Manga item) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.manga = item;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            Bundle b = new Bundle();
            b.putParcelable("Manga", manga);
            Fragment mangaFragment = new MangaFragment();
            mangaFragment.setArguments(b);
            return mangaFragment;
        } else{
            Bundle b = new Bundle();
            b.putParcelable("Manga", manga);
            Fragment mangaChapterFragment = new MangaChapterFragment();
            mangaChapterFragment.setArguments(b);
            return mangaChapterFragment;
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
