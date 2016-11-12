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
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.Fragments.FollowedFragment;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.UI.MangaActivity.MangaActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;

public abstract class MainFragmentPresenterBase implements IMain.FragmentPresenter {
    public final static String TAG = MainFragmentPresenterBase.class.getSimpleName();
    public final static String MANGA_LIST_KEY = TAG + ":MANGA_LIST_KEY";
    private final String NATIVE_AD_1_UNIT_ID = "f27ea659a1084329a656ab28ef29fb6a";

    protected ArrayList<Manga> mMangaList;
    protected ArrayList<Manga> mGenreFilterList;
    protected MoPubRecyclerAdapter mAdAdapter;
    protected RecycleSearchAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    protected boolean mNeedsItemDecoration;
    protected Subscription mMangaListSubscription;
    protected IMain.FragmentView mViewMapper;

    public MainFragmentPresenterBase(IMain.FragmentView map) {
        mViewMapper = map;
    }

    /***
     * TODO...
     */
    @Override
    public abstract void updateMangaList();

    /***
     * TODO...
     *
     * @param aQueryText
     */
    @Override
    public void onQueryTextChange(String aQueryText) {
        if (mAdapter != null)
            mAdapter.getFilter().filter(aQueryText);
    }

    /***
     * TODO...
     */
    @Override
    public void updateSource() {
        if (mViewMapper.getContext() != null) {
            if (mViewMapper != null && mAdapter != null) {
                mMangaList.clear();
                mAdapter.notifyDataSetChanged();
            }

            mViewMapper.startRefresh();
            updateMangaList();
        }
    }

    /***
     * TODO...
     *
     * @param aFilter
     */
    @Override
    public void onFilterSelected(int aFilter) {
        if (mAdapter != null) {
            mAdapter.filterByStatus(aFilter);
        }
    }

    /***
     * TODO...
     *
     * @param aMangaList
     */
    @Override
    public void onGenreFilterSelected(ArrayList<Manga> aMangaList) {
        if (aMangaList != null) {
            mGenreFilterList = new ArrayList<>(aMangaList);
            mGenreFilterList.retainAll(mMangaList);
            mAdapter.setmOriginalData(mGenreFilterList);
        }
    }

    /***
     * TODO...
     */
    @Override
    public void onClearGenreFilter() {
        mAdapter.setmOriginalData(mMangaList);
    }

    /***
     * TODO...
     *
     * @param aManga
     */
    @Override
    public void updateSelection(Manga aManga) {
        if (mAdapter != null) {
            if (mViewMapper instanceof FollowedFragment)
                mAdapter.updateFollowedItem(aManga);
            else
                mAdapter.updateItem(aManga);
        }
    }

    /***
     * TODO...
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle) {
        mViewMapper.setupSwipeRefresh();
        mLayoutManager = new GridLayoutManager(mViewMapper.getContext(), 3);
        ((GridLayoutManager) mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdAdapter.isAd(position)) return 3; // ads take up 3 columns
                else return 1;
            }
        });

        updateMangaList();
        mNeedsItemDecoration = true;
    }

    /***
     * TODO...
     *
     * @param aSave
     */
    @Override
    public void onSaveState(Bundle aSave) {
        if (mMangaList != null) {
            aSave.putParcelableArrayList(MANGA_LIST_KEY, mMangaList);
        }
    }

    /***
     * TODO...
     *
     * @param aRestore
     */
    @Override
    public void onRestoreState(Bundle aRestore) {
        if (aRestore.containsKey(MANGA_LIST_KEY)) {
            mMangaList = new ArrayList<>(aRestore.getParcelableArrayList(MANGA_LIST_KEY));
        }
    }

    /***
     * TODO...
     */
    @Override
    public void onPause() {

    }

    /***
     * TODO...
     */
    @Override
    public void onResume() {
    }

    /***
     * TODO...
     */
    @Override
    public void onDestroy() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }

        mViewMapper = null;
    }

    /***
     * TODO...
     *
     * @param pos
     */
    protected void onItemClick(int pos) {
        Manga manga = mAdapter.getItemAt(mAdAdapter.getOriginalPosition(pos));
        if (mViewMapper.setRecentSelection(manga.get_id())) {
            Intent intent = MangaActivity.getNewInstance(mViewMapper.getContext(), manga.getTitle());
            mViewMapper.getContext().startActivity(intent);
        }
    }

    /***
     * TODO...
     *
     * @param aMangaList
     */
    protected void updateMangaGridView(List<Manga> aMangaList) {
        if (mViewMapper.getContext() != null) {
            if (aMangaList != null) {
                mMangaList = new ArrayList<>(aMangaList);
                //Sorts manga list if Library or Followed fragment
                if (!(this instanceof RecentPresenter))
                    Collections.sort(mMangaList, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            } else {
                // failed to update list, show refresh view,
                mMangaList = new ArrayList<>(); //empty list
            }

            if (mAdapter == null) {
                mAdapter = new RecycleSearchAdapter(mMangaList, (pos) -> onItemClick(pos));
                setupMoPubAdapter();
            } else {
                mAdapter.setmOriginalData(mMangaList);
            }

            mViewMapper.stopRefresh();
            mNeedsItemDecoration = false;

            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;

            Glide.get(mViewMapper.getContext()).clearMemory();
        }
    }

    /***
     * TODO...
     */
    protected void setupMoPubAdapter() {
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
}