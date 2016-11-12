package com.teioh.m_feed.UI.ReaderActivity.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.ChapterFragment;

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

    public ChapterPageAdapter(FragmentManager aFragmentManager, ArrayList<Chapter> aChapterList) {
        super(aFragmentManager);
        mChapterList = new ArrayList<>(aChapterList);
    }

    @Override
    public Fragment getItem(int aPosition) {

        WeakReference<Fragment> lWeakReference = mPageReferenceMap.get(aPosition);

        if(lWeakReference != null) {
            return lWeakReference.get();
        }else{
            Fragment mChapterFragment = new ChapterFragment();
            Bundle lBundle = new Bundle();

            lBundle.putParcelable(Chapter.TAG + ":" + aPosition, mChapterList.get(aPosition));
            lBundle.putInt(POSITION_KEY, aPosition);
            mChapterFragment.setArguments(lBundle);
            mPageReferenceMap.put(aPosition, new WeakReference<>(mChapterFragment));

            return mChapterFragment;
        }
    }

    @Override
    public void destroyItem(ViewGroup aContainer, int aPosition, Object aObject) {
        super.destroyItem(aContainer, aPosition, aObject);
        mPageReferenceMap.remove(aPosition);
    }


    @Override
    public Object instantiateItem(ViewGroup aContainer, int aPosition) {
        Fragment lFragment = null;
        try {
            lFragment = (ChapterFragment) super.instantiateItem(aContainer, aPosition);
            mPageReferenceMap.put(aPosition, new WeakReference<>(lFragment));
        }catch (NullPointerException e){
            Log.e(TAG, "Failed to instantiate fragment: " + e.getMessage());
        }
        return lFragment;
    }

    @Override
    public int getCount() {
        return mChapterList.size();
    }

}
