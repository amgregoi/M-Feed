package com.teioh.m_feed.UI.ReaderActivity.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.View.Fragments.ChapterFragment;

import java.util.ArrayList;

/**
 * Created by amgregoi on 1/11/16.
 */
public class ChapterPageAdapter extends FragmentStatePagerAdapter {
    public final static String TAG = ChapterPageAdapter.class.getSimpleName();
    public final static String POSITION_KEY = TAG + ":POSITION";

    private ArrayList<Chapter> mChapterList;
    private Fragment mChapterFragment;

    public ChapterPageAdapter(FragmentManager fm, ArrayList<Chapter> chapters) {
        super(fm);
        mChapterList = new ArrayList<>(chapters);
    }

    @Override
    public Fragment getItem(int position) {
        mChapterFragment = new ChapterFragment();
        Bundle bundle = new Bundle();

        bundle.putParcelable(Chapter.TAG + ":" + position, mChapterList.get(position));
        bundle.putInt(POSITION_KEY, position);
        mChapterFragment.setArguments(bundle);
        return mChapterFragment;
    }

    @Override
    public int getCount() {
        return mChapterList.size();
    }
}
