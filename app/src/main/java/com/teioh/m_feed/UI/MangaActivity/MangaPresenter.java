package com.teioh.m_feed.UI.MangaActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.MAL_Models.MALMangaList;
import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.ReaderActivity;
import com.teioh.m_feed.Utils.MFDBHelper;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaPresenter implements IManga.ActivityPresenter {
    public static final String TAG = MangaPresenter.class.getSimpleName();
    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String MANGA_KEY = TAG + ":MANGA";
    public final static String ORDER_DESCENDING_KEY = TAG + ":DESCENDING";
    public final static String LIST_POSITION_KEY = TAG + ":POSITION";

    private Subscription mChapterListSubscription, mObservableMangaSubscription;
    private ArrayList<Chapter> mChapterList;
    private ChapterListAdapter mAdapter;
    private boolean mChapterOrderDescending;
    private Manga mManga;

    //    MALService mMALService;
    MALMangaList mMALMangaList;

    private IManga.ActivityView mMangaMapper;


    public MangaPresenter(IManga.ActivityView aMap) {
        mMangaMapper = aMap;
    }

    /***
     * TODO...
     *
     * @param aSave
     */
    @Override
    public void onSaveState(Bundle aSave) {
        if (mManga != null) aSave.putParcelable(MANGA_KEY, mManga);
        if (mChapterList != null) aSave.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
        aSave.putBoolean(ORDER_DESCENDING_KEY, mChapterOrderDescending);

    }

    /***
     * TODO...
     *
     * @param aRestore
     */
    @Override
    public void onRestoreState(Bundle aRestore) {
        if (aRestore.containsKey(MANGA_KEY))
            mManga = aRestore.getParcelable(MANGA_KEY);

        if (aRestore.containsKey(CHAPTER_LIST_KEY))
            mChapterList = new ArrayList<>(aRestore.getParcelableArrayList(CHAPTER_LIST_KEY));

        if (aRestore.containsKey(MANGA_KEY))
            mManga = aRestore.getParcelable(MANGA_KEY);

        if (aRestore.containsKey(ORDER_DESCENDING_KEY))
            mChapterOrderDescending = aRestore.getBoolean(ORDER_DESCENDING_KEY);

    }

    /***
     * TODO...
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle) {
        if (mManga == null) {
            String lTitle = aBundle.getString(Manga.TAG);

            //TODO > bundle url instead of title and use MFDBHelper
            mManga = cupboard().withDatabase(MFDBHelper.getInstance().getReadableDatabase())
                    .query(Manga.class)
                    .withSelection("title = ? AND source = ?", lTitle, new SourceFactory().getSourceName())
                    .get();
        }
        mChapterOrderDescending = true;
        mMangaMapper.setActivityTitle(mManga.getTitle());
        mMangaMapper.setupToolBar();
        mMangaMapper.initializeHeaderViews();
        mMangaMapper.setupHeaderButtons();
        mMangaMapper.setupSwipeRefresh();
        mMangaMapper.hideCoverLayout();

        if (mManga.getInitialized() == 1) updateMangaView(mManga);
        else getMangaViewInfo();

        if (mChapterList == null) getChapterList();
        else updateChapterList(mChapterList);

//        shared prefs set on login, eventually
//        username, pass stored in shared prefs
//        mMALService = MALApi.createService(null, null);
//        getMALSyncOptions();
    }

    /***
     * TODO...
     *
     */
    @Override
    public void onResume() {
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }

    /***
     * TODO...
     *
     */
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

    /***
     * TODO...
     *
     */
    @Override
    public void onDestroy() {
        ButterKnife.unbind(mMangaMapper);
        Glide.get(mMangaMapper.getContext()).clearMemory();
        mMangaMapper = null;
    }

    /***
     * TODO...
     *
     */
    @Override
    public void chapterOrderButtonClick() {
        if (mChapterList != null) {
            Collections.reverse(mChapterList);
            mAdapter.reverseChapterListOrder();
            mChapterOrderDescending = !mChapterOrderDescending;
        }
    }

    /***
     * TODO...
     *
     * @param aChapter
     */
    @Override
    public void onChapterClicked(Chapter aChapter) {
        ArrayList<Chapter> newChapterList = new ArrayList<>(mChapterList);

        if (mChapterOrderDescending)
            Collections.reverse(newChapterList);

        int lPosition = newChapterList.indexOf(aChapter);

        //TODO > update ReaderActivity.getInstance()
        Intent lIntent = new Intent(mMangaMapper.getContext(), ReaderActivity.class);
        lIntent.putParcelableArrayListExtra(CHAPTER_LIST_KEY, newChapterList);
        lIntent.putExtra(LIST_POSITION_KEY, lPosition);
        mMangaMapper.getContext().startActivity(lIntent);
    }

    /***
     * TODO...
     *
     */
    @Override
    public void onMALSyncClicked() {
        mMangaMapper.onMALSyncClicked(mMALMangaList);
    }

    /***
     * TODO...
     *
     * @return
     */
    @Override
    public String getImageUrl() {
        return mManga.getPicUrl();
    }

    /***
     * TODO...
     *
     * @param aValue
     */
    @Override
    public void onFollwButtonClick(int aValue) {
        mMangaMapper.changeFollowButton(mManga.getFollowing());
        MFDBHelper.getInstance().updateMangaFollow(mManga.getTitle(), aValue);
    }

    /***
     * TODO...
     *
     */
    @Override
    public void onUnfollowButtonClick(){
        MFDBHelper.getInstance().updateMangaUnfollow(mManga.getTitle());
    }

    /***
     * TODO...
     *
     */
    private void getMALSyncOptions() {
//        mMALService.searchManga(mManga.getMangaTitle(), new Callback<MALMangaList>() {
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

    /***
     * TODO...
     *
     */
    private void getMangaViewInfo() {
        mObservableMangaSubscription = new SourceFactory().getSource().updateMangaObservable(new RequestWrapper(mManga))
                .doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(manga -> updateMangaView(manga));
    }

    /***
     * TODO...
     *
     * @param aManga
     */
    private void updateMangaView(Manga aManga) {
        if(aManga != null) {
            if (mMangaMapper.getContext() != null) {
                mMangaMapper.setMangaViews(aManga);
                mMangaMapper.changeFollowButton(aManga.getFollowing());
            }
            mManga = aManga;
            mManga.setInitialized(1);

            MFDBHelper.getInstance().putManga(aManga);
            if (mChapterListSubscription != null) {
                mObservableMangaSubscription.unsubscribe();
                mObservableMangaSubscription = null;
            }
        }
    }

    /***
     * TODO...
     *
     */
    private void getChapterList() {
        mChapterListSubscription = new SourceFactory().getSource().getChapterListObservable(new RequestWrapper(mManga))
                .doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT))
                .subscribe(chapters -> updateChapterList(chapters));
    }

    /***
     * TODO...
     *
     * @param aChapterList
     */
    private void updateChapterList(List<Chapter> aChapterList) {
        if (mMangaMapper.getContext() != null) {
            mChapterList = new ArrayList<>(aChapterList);
            mAdapter = new ChapterListAdapter(mMangaMapper.getContext(), R.layout.manga_chapter_list_item, mChapterList);
            mMangaMapper.registerAdapter(mAdapter);
            mMangaMapper.stopRefresh();
            mMangaMapper.showCoverLayout();
        }
    }


}
