package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.Fragments.LibraryFragment;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.UI.MangaActivity.MangaActivity;
import com.teioh.m_feed.Utils.MangaLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;

public abstract class MainFragmentPresenterBase implements IMain.FragmentPresenter
{
    public final static String TAG = MainFragmentPresenterBase.class.getSimpleName();
    public final static String MANGA_LIST_KEY = TAG + ":MANGA_LIST_KEY";
    private final String NATIVE_AD_1_UNIT_ID = null; //"f27ea659a1084329a656ab28ef29fb6a"; //Debug Ad string

    protected ArrayList<Manga> mMangaList;
    protected ArrayList<Manga> mGenreFilterList;
    protected MoPubRecyclerAdapter mAdAdapter;
    protected RecycleSearchAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    protected boolean mNeedsItemDecoration, mMangaSubFlag = false;
    protected Subscription mMangaListSubscription;
    protected IMain.FragmentView mViewMapper;

    public MainFragmentPresenterBase(IMain.FragmentView aMap)
    {
        mViewMapper = aMap;
    }

    /***
     * This function performs the Async task of querying, parsing a sources front page and building
     * a list of manga objects for the user to view
     */
    @Override
    public abstract void updateMangaList();

    /***
     * This function sets the specific query text to search in the three core adapters
     *
     * @param aQueryText The text used for a search.
     */
    @Override
    public boolean onQueryTextChange(String aQueryText)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        boolean lResult = true;
        try
        {
            if (mAdapter != null)
            {
                mAdapter.performTextFilter(aQueryText);
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function clears the view and refreshes the Recent Adapter with new(er) data.
     */
    @Override
    public boolean updateSource()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        boolean lResult = false;

        try
        {
            if (mViewMapper.getContext() != null)
            {
                if (mViewMapper != null && mAdapter != null)
                {
                    mMangaList.clear();
                    mAdapter.notifyDataSetChanged();
                }

                mViewMapper.startRefresh();
                updateMangaList();
                lResult = true;
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function sets the filter type specified by the user
     *
     * @param aFilter The filter type that has been selected
     */
    @Override
    public boolean onFilterSelected(MangaEnums.eFilterStatus aFilter)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        boolean lResult = false;
        try
        {
            if (mAdapter != null)
            {
                lResult = mAdapter.filterByStatus(aFilter);
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

        return lResult;
    }

    /***
     * This function sets refreshes the GenreFilter list based on the specified list that has been filtered.
     *
     * @param aMangaList The object list that has been filtered.
     */
    @Override
    public boolean onGenreFilterSelected(ArrayList<Manga> aMangaList)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        boolean lResult = true;

        try
        {
            if (aMangaList != null)
            {
                mGenreFilterList = new ArrayList<>(aMangaList);
                mGenreFilterList.retainAll(mMangaList);
                mAdapter.setOriginalData(mGenreFilterList);
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function will clear any set Genre Filter by refreshing the adapters "original data"
     */
    @Override
    public boolean onClearGenreFilter()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        boolean lResult = true;

        try
        {
            mAdapter.setOriginalData(mMangaList);
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function updates the specified object in the three core adapters.
     *
     * @param aManga The object that is being updated.
     */
    @Override
    public boolean updateSelection(Manga aManga)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        boolean lResult = true;

        try
        {
            if (mAdapter != null)
            {
                if (mViewMapper instanceof LibraryFragment) mAdapter.updateLibraryItem(aManga);
                else mAdapter.updateItem(aManga);

                mMangaList = mAdapter.getOriginalData();
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function initializes the MainFragmentPresenterBase object, as well as the users view.
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mViewMapper.setupSwipeRefresh();
            mLayoutManager = new GridLayoutManager(mViewMapper.getContext(), 3);
            ((GridLayoutManager) mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
            {
                @Override
                public int getSpanSize(int position)
                {
                    if (NATIVE_AD_1_UNIT_ID == null)
                    {
                        return 1;
                    }
                    else
                    {
                        if (mAdAdapter.isAd(position)) return 3; // ads take up 3 columns
                        else return 1;
                    }
                }
            });

            updateMangaList();
            mNeedsItemDecoration = true;
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());

        }
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aSave The object that will hold the saved data.
     */
    @Override
    public void onSaveState(Bundle aSave)
    {

    }

    /***
     * This function restores data that needed to persist between device state changes.
     *
     * @param aRestore The object containing the relevant data.
     */
    @Override
    public void onRestoreState(Bundle aRestore)
    {

    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    public void onPause()
    {

    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    public void onResume()
    {
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     * It will clean up any items that shouldn't persist.
     */
    @Override
    public void onDestroy()
    {
        if (mMangaListSubscription != null)
        {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }

        mViewMapper = null;
    }

    /***
     * This function starts a Manga Activity when a click is performed on a Manga Object
     *
     * @param aPos This is the position of the selected object
     */
    protected void onItemClick(int aPos)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            Manga manga;
            if (NATIVE_AD_1_UNIT_ID == null)
            {
                manga = mAdapter.getItemAt(aPos);
            }
            else
            {
                manga = mAdapter.getItemAt(mAdAdapter.getOriginalPosition(aPos));
            }
            if (mViewMapper.setRecentSelection(manga.get_id()))
            {
                Intent intent = MangaActivity.getNewInstance(mViewMapper.getContext(), manga.getMangaURL());
                mViewMapper.getContext().startActivity(intent);
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * This function updates the users view when the application is finished processing the Source HTML
     *
     * @param aMangaList This is the list of objects created after parsing the HTML
     */
    protected void updateMangaGridView(List<Manga> aMangaList)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {

            if (mViewMapper.getContext() != null)
            {
                if (aMangaList != null)
                {
                    mMangaList = new ArrayList<>(aMangaList);
                    //Sorts manga list of Library or Catalog fragment
                    if (!(this instanceof RecentPresenter))
                        Collections.sort(mMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
                }
                else
                {
                    // failed to update list, show refresh view,
                    mMangaList = new ArrayList<>(); //empty list
                }

                if (mAdapter == null)
                {
                    mAdapter = new RecycleSearchAdapter(mMangaList, (pos) -> onItemClick(pos));
                    mAdapter.setHasStableIds(true);
                    if (NATIVE_AD_1_UNIT_ID == null)
                    {
                        mViewMapper.registerAdapter(mAdapter, mLayoutManager, mNeedsItemDecoration);
                    }
                    else
                    {
                        setupMoPubAdapter();
                    }
                }
                else
                {
                    mAdapter.setOriginalData(mMangaList);
                }

                mViewMapper.stopRefresh();
                mNeedsItemDecoration = false;

                mMangaListSubscription.unsubscribe();
                mMangaListSubscription = null;

                Glide.get(mViewMapper.getContext()).clearMemory();

                mMangaSubFlag = true;
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * This function sets up the MoPub adapter.
     * This function is only called if NATIVE_AD_1_UNIT_ID is initialized
     */
    protected void setupMoPubAdapter()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            MoPubNativeAdPositioning.MoPubServerPositioning lAdPositioning = MoPubNativeAdPositioning.serverPositioning();

            mAdAdapter = new MoPubRecyclerAdapter(((Fragment) mViewMapper).getActivity(), mAdapter, lAdPositioning);

            MoPubStaticNativeAdRenderer lRenderer = new MoPubStaticNativeAdRenderer(new ViewBinder.Builder(R.layout.ad_layout)
                                                                                            .titleId(R.id.native_ad_title)
                                                                                            .textId(R.id.native_ad_text)
                                                                                            .mainImageId(R.id.native_ad_main_image)
                                                                                            .iconImageId(R.id.native_ad_icon_image)
                                                                                            .build());

            mAdAdapter.registerAdRenderer(lRenderer);
            if (NATIVE_AD_1_UNIT_ID != null) mAdAdapter.loadAds(NATIVE_AD_1_UNIT_ID);

            mViewMapper.registerAdapter(mAdAdapter, mLayoutManager, mNeedsItemDecoration);

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }
}
