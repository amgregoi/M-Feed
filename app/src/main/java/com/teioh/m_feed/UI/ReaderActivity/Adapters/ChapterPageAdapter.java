package com.teioh.m_feed.UI.ReaderActivity.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.View.Fragments.ChapterFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by amgregoi on 1/11/16.
 */
public class ChapterPageAdapter extends FragmentStatePagerAdapter {
    public final static String TAG = ChapterPageAdapter.class.getSimpleName();
    public final static String POSITION_KEY = TAG + ":POSITION";

    private ArrayList<Chapter> mChapterList;

    private SparseArray<WeakReference<Fragment>> mPageReferenceMap = new SparseArray<WeakReference<Fragment>>();

    public ChapterPageAdapter(FragmentManager fm, ArrayList<Chapter> chapters) {
        super(fm);
        mChapterList = new ArrayList<>(chapters);
    }

    @Override
    public Fragment getItem(int position) {

        WeakReference<Fragment> weakReference = mPageReferenceMap.get(position);

        if(weakReference != null) {
            return weakReference.get();
        }else{
            Fragment mChapterFragment = new ChapterFragment();
            Bundle bundle = new Bundle();

            bundle.putParcelable(Chapter.TAG + ":" + position, mChapterList.get(position));
            bundle.putInt(POSITION_KEY, position);
            mChapterFragment.setArguments(bundle);
            mPageReferenceMap.put(Integer.valueOf(position), new WeakReference<Fragment>(mChapterFragment));
            return mChapterFragment;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(Integer.valueOf(position));
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (ChapterFragment) super.instantiateItem(container, position);
        mPageReferenceMap.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    @Override
    public int getCount() {
        return mChapterList.size();
    }
}
