package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.RecycleSearchAdapter;
import com.teioh.m_feed.UI.MainActivity.View.Mappers.RecentFragmentMapper;
import com.teioh.m_feed.UI.MangaActivity.View.MangaActivity;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;


public class RecentPresenterImpl implements HomePresenter {
    public final static String TAG = RecentPresenterImpl.class.getSimpleName();
    public final static String RECENT_MANGA_LIST_KEY = TAG + ":RECENT_MANGA_LIST";
    public final static String LATEST_SOURCE = TAG + ":SOURCE";

    public final String NATIVE_AD_1_UNIT_ID = "f27ea659a1084329a656ab28ef29fb6a";

    private ArrayList<Manga> mRecentMangaList;
    private ArrayList<Manga> mGenreFilterList;
    private MoPubRecyclerAdapter mAdAdapter;
    private RecycleSearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean mNeedsItemDeocration;
    private Subscription mMangaListSubscription;
    private String mLastSourceQuery;

    private RecentFragmentMapper mRecentFragmentMapper;


    public RecentPresenterImpl(RecentFragmentMapper map) {
        mRecentFragmentMapper = map;
    }


    @Override
    public void init(Bundle bundle) {
        mRecentFragmentMapper.setupSwipeRefresh();
        mNeedsItemDeocration = true;
        mLayoutManager = new GridLayoutManager(mRecentFragmentMapper.getContext(), 3);
        ((GridLayoutManager) mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdAdapter.isAd(position)) return 3; // ads take up 3 columns
                else return 1;
            }
        });

        if (mRecentMangaList == null) updateMangaList();
        else updateRecentGridView(mRecentMangaList);
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mRecentMangaList != null) {
            bundle.putParcelableArrayList(RECENT_MANGA_LIST_KEY, mRecentMangaList);
        }
        if (mLastSourceQuery != null) {
            bundle.putString(LATEST_SOURCE, mLastSourceQuery);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(RECENT_MANGA_LIST_KEY)) {
            mRecentMangaList = new ArrayList<>(bundle.getParcelableArrayList(RECENT_MANGA_LIST_KEY));
        }
        if (bundle.containsKey(LATEST_SOURCE)) {
            mLastSourceQuery = bundle.getString(LATEST_SOURCE);
        }
    }

    @Override
    public void updateMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
        mLastSourceQuery = WebSource.getCurrentSource();
        mMangaListSubscription = WebSource.getRecentUpdatesObservable()
                .subscribe(manga -> updateRecentGridView(manga));
    }

    public void onItemClick(int pos) {
        Manga manga = mAdapter.getItemAt(mAdAdapter.getOriginalPosition(pos));
        if(mRecentFragmentMapper.setRecentSelection(manga.get_id())) {
            Intent intent = new Intent(mRecentFragmentMapper.getContext(), MangaActivity.class);
            intent.putExtra(Manga.TAG, manga.getTitle());
            mRecentFragmentMapper.getContext().startActivity(intent);
        }
    }

    @Override
    public void onQueryTextChange(String newText) {
        if (mAdapter != null)
            mAdapter.getFilter().filter(newText);
    }

    @Override
    public void onDestroy() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }
        if(mAdAdapter != null) mAdAdapter.destroy();
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {

    }

    @Override
    public void updateSource() {
        mRecentFragmentMapper.startRefresh();
        if (mRecentFragmentMapper.getContext() != null) {
            mRecentFragmentMapper.startRefresh();
            updateMangaList();
        }
    }

    @Override
    public void onFilterSelected(int filter) {
        if (mAdapter != null)
            mAdapter.filterByStatus(filter);
    }

    @Override
    public void onGenreFilterSelected(ArrayList<Manga> list) {
        if (list != null) {
            mGenreFilterList = new ArrayList<>(list);
            mGenreFilterList.retainAll(mRecentMangaList);
            mAdapter.setOriginalData(mGenreFilterList);
        }
    }

    @Override
    public void onClearGenreFilter() {
        mAdapter.setOriginalData(mRecentMangaList);
    }

    @Override
    public void updateSelection(Manga manga) {
        if(mAdAdapter != null)
            mAdapter.updateItem(manga);
    }

    private void updateRecentGridView(List<Manga> manga) {
        if (mRecentFragmentMapper.getContext() != null) {
            if (manga != null) {
                mRecentMangaList = new ArrayList<>(manga);
                mMangaListSubscription = null;
            } else {
                // failed to update list, show refresh view,
                mRecentMangaList = new ArrayList<>();
            }
            mAdapter = new RecycleSearchAdapter(mRecentFragmentMapper.getContext(), mRecentMangaList, (position) -> onItemClick(position));
            setupMoPubAdapter();
            mRecentFragmentMapper.stopRefresh();
            mNeedsItemDeocration = false;

        }
    }

    private void setupMoPubAdapter() {
        MoPubNativeAdPositioning.MoPubServerPositioning adPositioning = MoPubNativeAdPositioning.serverPositioning();
        mAdAdapter = new MoPubRecyclerAdapter(((Fragment) mRecentFragmentMapper).getActivity(), mAdapter, adPositioning);
        MoPubStaticNativeAdRenderer myRenderer = new MoPubStaticNativeAdRenderer(new ViewBinder.Builder(R.layout.ad_layout)
                .titleId(R.id.native_ad_title)
                .textId(R.id.native_ad_text)
                .mainImageId(R.id.native_ad_main_image)
                .iconImageId(R.id.native_ad_icon_image)
                .build());


        mAdAdapter.registerAdRenderer(myRenderer);
        if (NATIVE_AD_1_UNIT_ID != null) mAdAdapter.loadAds(NATIVE_AD_1_UNIT_ID);

        mRecentFragmentMapper.registerAdapter(mAdAdapter, mLayoutManager, mNeedsItemDeocration);
    }

}
