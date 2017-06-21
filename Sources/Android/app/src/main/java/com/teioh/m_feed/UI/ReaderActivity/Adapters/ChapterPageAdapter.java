package com.teioh.m_feed.UI.ReaderActivity.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.ChapterFragment;
import com.teioh.m_feed.Utils.MangaLogger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by amgregoi on 1/11/16.
 */
public class ChapterPageAdapter extends FragmentStatePagerAdapter
{
    public final static String TAG = ChapterPageAdapter.class.getSimpleName();

    private ArrayList<Chapter> mChapterList;
    private SparseArray<WeakReference<Fragment>> mPageReferenceMap = new SparseArray<WeakReference<Fragment>>();

    private boolean mParentFollowing;

    /***
     * This is the constructor for the Chapter Page Adapter
     * @param aFragmentManager
     * @param aChapterList
     */
    public ChapterPageAdapter(FragmentManager aFragmentManager, ArrayList<Chapter> aChapterList, boolean aParentFollowing)
    {
        super(aFragmentManager);
        mChapterList = new ArrayList<>(aChapterList);
        mParentFollowing = aParentFollowing;
    }

    /***
     * This function returns the chapter fragment specified by its position.
     * @param aPosition
     * @return
     */
    @Override
    public Fragment getItem(int aPosition)
    {
        try
        {
            WeakReference<Fragment> lWeakReference = mPageReferenceMap.get(aPosition);

            if (lWeakReference != null)
            {
                return lWeakReference.get();
            }
            else
            {
                Fragment lChapterFragment = ChapterFragment.getNewInstance(mParentFollowing , mChapterList.get(aPosition), aPosition);
                mPageReferenceMap.put(aPosition, new WeakReference<>(lChapterFragment));

                return lChapterFragment;
            }
        }
        catch (Exception aException)
        {
            return null;
        }
    }

    /***
     * This function destroys the chapter fragment item specified by its position.
     * @param aContainer
     * @param aPosition
     * @param aObject
     */
    @Override
    public void destroyItem(ViewGroup aContainer, int aPosition, Object aObject)
    {
        super.destroyItem(aContainer, aPosition, aObject);
        mPageReferenceMap.remove(aPosition);
    }

    /***
     * This function instantiates the item specified by its position.
     * @param aContainer
     * @param aPosition
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup aContainer, int aPosition)
    {
        Fragment lFragment = null;
        try
        {
            lFragment = (ChapterFragment) super.instantiateItem(aContainer, aPosition);
            mPageReferenceMap.put(aPosition, new WeakReference<>(lFragment));
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }

        return lFragment;
    }

    /***
     * This function returns the chapter count.
     * @return
     */
    @Override
    public int getCount()
    {
        return mChapterList.size();
    }

}
