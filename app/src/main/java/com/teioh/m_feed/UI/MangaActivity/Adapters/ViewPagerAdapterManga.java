package com.teioh.m_feed.UI.MangaActivity.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.ChapterListFragment;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.MangaInformationFragment;

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

    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putParcelable("Manga", manga);

        if (position == 0) {
            Fragment mangaFragment = new MangaInformationFragment();
            mangaFragment.setArguments(b);
            return mangaFragment;
        } else {
            Fragment mangaChapterFragment = new ChapterListFragment();
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