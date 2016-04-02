package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;

import com.teioh.m_feed.MAL_Models.MALMangaList;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ChapterListAdapter;
import com.teioh.m_feed.UI.MangaActivity.View.Mappers.MangaActivityMapper;
import com.teioh.m_feed.UI.ReaderActivity.View.ReaderActivity;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Subscription;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaPresenterImpl implements MangaPresenter {
    public static final String TAG = MangaPresenterImpl.class.getSimpleName();
    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String MANGA_KEY = TAG + ":MANGA";
    public final static String ORDER_DESCENDING_KEY = TAG + ":DESCENDING";
    public final static String LIST_POSITION_KEY = TAG + ":POSITION";

    private Subscription mChapterListSubscription;
    private Subscription mObservableMangaSubscription;
    private ArrayList<Chapter> mChapterList;
    private ChapterListAdapter mAdapter;
    private boolean mChapterOrderDescending;
    private Manga mManga;

//    MALService mMALService;
    MALMangaList mMALMangaList;

    private MangaActivityMapper mMangaMapper;


    public MangaPresenterImpl(MangaActivityMapper map) {
        mMangaMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mManga != null) bundle.putParcelable(MANGA_KEY, mManga);
        if (mChapterList != null) bundle.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
        bundle.putBoolean(ORDER_DESCENDING_KEY, mChapterOrderDescending);

    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(MANGA_KEY))
            mManga = bundle.getParcelable(MANGA_KEY);

        if (bundle.containsKey(CHAPTER_LIST_KEY))
            mChapterList = new ArrayList<>(bundle.getParcelableArrayList(CHAPTER_LIST_KEY));

        if (bundle.containsKey(MANGA_KEY))
            mManga = bundle.getParcelable(MANGA_KEY);

        if (bundle.containsKey(ORDER_DESCENDING_KEY))
            mChapterOrderDescending = bundle.getBoolean(ORDER_DESCENDING_KEY);

    }

    @Override
    public void init(Bundle bundle) {
        if (mManga == null) {
            String title = bundle.getString(Manga.TAG);
            mManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                    .query(Manga.class)
                    .withSelection("mTitle = ? AND mSource = ?", title, WebSource.getCurrentSource())
                    .get();
        }
        mChapterOrderDescending = true;
        mMangaMapper.setActivityTitle(mManga.getTitle());
        mMangaMapper.setupToolBar();
        mMangaMapper.initializeHeaderViews();
        mMangaMapper.setupHeaderButtons();
        mMangaMapper.setupSwipeRefresh();
        mMangaMapper.hideCoverLayout();

        if (mManga.getmIsInitialized() == 1) updateMangaView(mManga);
        else getMangaViewInfo();

        if (mChapterList == null) getChapterList();
        else updateChapterList(mChapterList);

//        shared prefs set on login, eventually
//        username, pass stored in shared prefs
//        mMALService = MALApi.createService(null, null);
//        getMALSyncOptions();
    }

    @Override
    public void onResume() {
        if(mAdapter != null)mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        if (mObservableMangaSubscription != null) {
            mObservableMangaSubscription.unsubscribe();
            mObservableMangaSubscription = null;
        }

        if (mChapterListSubscription != null) {
            mChapterListSubscription.unsubscribe();
            mChapterListSubscription = null;
        }
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(mMangaMapper);
    }

    @Override
    public void chapterOrderButtonClick() {
        if (mChapterList != null) {
            Collections.reverse(mChapterList);
            mAdapter.reverseChapterListOrder();
            mChapterOrderDescending = !mChapterOrderDescending;
        }
    }

    @Override
    public void onChapterClicked(Chapter chapter) {
        ArrayList<Chapter> newChapterList = new ArrayList<>(mChapterList);

        if (mChapterOrderDescending)
            Collections.reverse(newChapterList);

        int position = newChapterList.indexOf(chapter);
        Intent intent = new Intent(mMangaMapper.getContext(), ReaderActivity.class);
        intent.putParcelableArrayListExtra(CHAPTER_LIST_KEY, newChapterList);
        intent.putExtra(LIST_POSITION_KEY, position);
        mMangaMapper.getContext().startActivity(intent);
    }

    @Override
    public void onMALSyncClicked() {
        mMangaMapper.onMALSyncClicked(mMALMangaList);
    }

    @Override
    public String getImageUrl() {
        return mManga.getPicUrl();
    }

    @Override
    public void onFollwButtonClick() {
        boolean follow = mManga.setFollowing(!mManga.getFollowing());
        mMangaMapper.changeFollowButton(mManga.getFollowing());
        if (follow) {
            MangaFeedDbHelper.getInstance().updateMangaFollow(mManga.getTitle());
        } else {
            MangaFeedDbHelper.getInstance().updateMangaUnfollow(mManga.getTitle());
        }
    }

    private void getMALSyncOptions(){
//        mMALService.searchManga(mManga.getTitle(), new Callback<MALMangaList>() {
//            @Override
//            public void success(MALMangaList list, Response response) {
//                Log.e(TAG, list.toString());
//                mMALMangaList = list;
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.e(TAG, error.getMessage());
//            }
//        });
    }

    private void getMangaViewInfo() {
        mObservableMangaSubscription = WebSource.updateMangaObservable(mManga).subscribe(manga -> updateMangaView(manga));
    }

    private void updateMangaView(Manga manga) {
        if (mMangaMapper.getContext() != null) {
            mMangaMapper.setMangaViews(manga);
            mMangaMapper.changeFollowButton(manga.getFollowing());
        }
        mManga = manga;
        mManga.setmIsInitialized(1);
        cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(manga);
        if (mChapterListSubscription != null) {
            mObservableMangaSubscription.unsubscribe();
            mObservableMangaSubscription = null;
        }
    }

    private void getChapterList() {
        mChapterListSubscription = WebSource.getChapterListObservable(mManga.getMangaURL()).subscribe(chapters -> updateChapterList(chapters));
    }

    private void updateChapterList(List<Chapter> chapters) {
        if (mMangaMapper.getContext() != null) {
            mChapterList = new ArrayList<>(chapters);
            mAdapter = new ChapterListAdapter(mMangaMapper.getContext(), R.layout.chapter_list_item, mChapterList);
            mMangaMapper.registerAdapter(mAdapter);
                mMangaMapper.stopRefresh();
                mMangaMapper.showCoverLayout();
        }
    }


}
