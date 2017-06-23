package com.teioh.m_feed.UI.MangaActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.ReaderActivity;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MangaPresenter implements IManga.ActivityPresenter
{
    public static final String TAG = MangaPresenter.class.getSimpleName();
    public final static String CHAPTER_LIST_KEY = TAG + ":CHAPTER_LIST";
    public final static String MANGA_KEY = TAG + ":MANGA";
    public final static String ORDER_DESCENDING_KEY = TAG + ":DESCENDING";
    public final static String LIST_POSITION_KEY = TAG + ":POSITION";

    private Subscription mChapterListSubscription, mObservableMangaSubscription;
    private ArrayList<Chapter> mChapterList;
    private ChapterListAdapter mAdapter;
    private boolean mChapterOrderDescending;
    private boolean mRestoreActivity, mChapterFlag = false;
    private Manga mManga;

    private IManga.ActivityView mMangaMapper;


    public MangaPresenter(IManga.ActivityView aMap)
    {
        mMangaMapper = aMap;
    }

    /***
     * This function initializes the presenter.
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle)
    {
        try
        {
            if (mManga == null)
            {
                String lMangaUrl = aBundle.getString(Manga.TAG);
                mManga = MangaDB.getInstance().getManga(lMangaUrl);
            }

            if (!mRestoreActivity) mChapterOrderDescending = true;
            mMangaMapper.setActivityTitle(mManga.getTitle());
            mMangaMapper.setupToolBar();
            mMangaMapper.initializeHeaderViews();
            mMangaMapper.setupHeaderButtons();
            mMangaMapper.setupSwipeRefresh();
            mMangaMapper.hideCoverLayout();

            getMangaViewInfo();
            getChapterList();

        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aSave
     */
    @Override
    public void onSaveState(Bundle aSave)
    {
        try
        {
            if (mManga != null) aSave.putParcelable(MANGA_KEY, mManga);
            if (mChapterList != null) aSave.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
            aSave.putBoolean(ORDER_DESCENDING_KEY, mChapterOrderDescending);

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function restores data that needed to persist between device state changes.
     *
     * @param aRestore
     */
    @Override
    public void onRestoreState(Bundle aRestore)
    {
        try
        {
            mRestoreActivity = true;

            if (aRestore.containsKey(MANGA_KEY)) mManga = aRestore.getParcelable(MANGA_KEY);

            if (aRestore.containsKey(CHAPTER_LIST_KEY)) mChapterList = new ArrayList<>(aRestore.getParcelableArrayList(CHAPTER_LIST_KEY));

            if (aRestore.containsKey(MANGA_KEY)) mManga = aRestore.getParcelable(MANGA_KEY);

            if (aRestore.containsKey(ORDER_DESCENDING_KEY)) mChapterOrderDescending = aRestore.getBoolean(ORDER_DESCENDING_KEY);

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    public void onPause()
    {
        try
        {

            if (mObservableMangaSubscription != null)
            {
                mObservableMangaSubscription.unsubscribe();
                mObservableMangaSubscription = null;
            }

            if (mChapterListSubscription != null)
            {
                mChapterListSubscription.unsubscribe();
                mChapterListSubscription = null;
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }

    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    public void onResume()
    {
        try
        {
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
            mManga = MangaDB.getInstance().getManga(mManga.getMangaURL()); //get updates manga object

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    public void onDestroy()
    {
        try
        {
            Glide.get(mMangaMapper.getContext()).clearMemory();
            mMangaMapper = null;

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function retrieves the current items information
     */
    private void getMangaViewInfo()
    {
        try
        {
            if (NetworkService.isNetworkAvailable())
            {
                if (mManga.getInitialized() == 0)
                {
                    mObservableMangaSubscription = SourceFactory.getInstance().getSource()
                                                                .updateMangaObservable(new RequestWrapper(mManga)).cache()
                                                                .doOnError(throwable -> MangaLogger
                                                                        .logError(TAG, throwable.getMessage()))
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(manga -> updateMangaView(manga));
                }
                else
                {
                    updateMangaView(mManga);
                    MangaLogger.logInfo(TAG, "Manga was previously initialized");
                }
            }
            else
            {
                updateMangaView(mManga);
                MangaLogger.logInfo(TAG, "No internet access ");
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
    }

    /***
     * This function retrieves the current items chapter list.
     */
    private void getChapterList()
    {
        try
        {
            if (NetworkService.isNetworkAvailable())
            {
                if (!mChapterFlag)
                {
                    mChapterListSubscription = SourceFactory.getInstance().getSource()
                                                            .getChapterListObservable(new RequestWrapper(mManga)).cache()
                                                            .doOnError(throwable -> MangaLogger
                                                                    .logError(TAG, throwable.getMessage()))
                                                            .subscribe(chapters -> updateChapterList(chapters));
                }
                else
                {
                    MangaLogger.logInfo(TAG, "Chapter list is already initialized");
                    updateChapterList(mChapterList);
                }
            }
            else
            {
                MangaLogger.logInfo(TAG, "No internet access");
                updateChapterList(new ArrayList<>());
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function updates the manga view.
     *
     * @param aManga
     */
    private void updateMangaView(Manga aManga)
    {
        try
        {
            if (aManga != null)
            {
                if (mMangaMapper.getContext() != null)
                {
                    mMangaMapper.setMangaViews(aManga);
                }
                mManga = aManga;

                String lInitTest = mManga.getDescription();
                if (!lInitTest.isEmpty())
                {
                    mManga.setInitialized(1);
                }
                mManga.setInitialized(0); // TODO remove..

                MangaDB.getInstance().putManga(aManga);
                if (mChapterListSubscription != null)
                {
                    mObservableMangaSubscription.unsubscribe();
                    mObservableMangaSubscription = null;
                }
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function updates the chapter list class variable.
     *
     * @param aChapterList
     */
    private void updateChapterList(List<Chapter> aChapterList)
    {
        try
        {
            if (mMangaMapper.getContext() != null)
            {
                mChapterList = new ArrayList<>(aChapterList);
                mAdapter = new ChapterListAdapter(mMangaMapper.getContext(), R.layout.manga_chapter_list_item, mChapterList);
                mMangaMapper.registerAdapter(mAdapter);
                mMangaMapper.stopRefresh();
                mMangaMapper.showCoverLayout();
                mChapterFlag = true;
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    /***
     * This function reverses the chapter list items.
     */
    @Override
    public boolean chapterOrderButtonClick()
    {
        boolean lResult = true;

        try
        {
            if (mChapterList != null)
            {
                Collections.reverse(mChapterList);
                mAdapter.reverseChapterListOrder();
                mChapterOrderDescending = !mChapterOrderDescending;
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function adds the current item to the user library.
     *
     * @param aValue
     */
    @Override
    public boolean onFollowButtonClick(int aValue)
    {
        boolean lResult = true;

        try
        {
            MangaDB.getInstance().updateMangaFollow(mManga.getTitle(), aValue);
            mManga.setFollowing(aValue);
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function removes the current item from the user library.
     */
    @Override
    public boolean onUnfollowButtonClick()
    {
        boolean lResult = true;

        try
        {
            mManga.setFollowing(0);
            MangaDB.getInstance().updateMangaUnfollow(mManga.getTitle());
            MangaDB.getInstance().removeChapters(mManga);
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function handles when a chapter is selected.
     *
     * @param aChapter
     */
    @Override
    public boolean onChapterClicked(Chapter aChapter)
    {
        boolean lResult = true;
        try
        {
            ArrayList<Chapter> lNewChapterList = new ArrayList<>(mChapterList);
            if (mChapterOrderDescending) Collections.reverse(lNewChapterList);
            int lPosition = lNewChapterList.indexOf(aChapter);

            mManga.setRecentChapter(aChapter.getChapterUrl());
            MangaDB.getInstance().updateManga(mManga);

            Intent lIntent = ReaderActivity.getNewInstance(mMangaMapper.getContext(), lNewChapterList, lPosition, mManga.getMangaURL());
            mMangaMapper.getContext().startActivity(lIntent);
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function retrieves the current items image url.
     *
     * @return
     */
    @Override
    public String getImageUrl()
    {

        try
        {
            return mManga.getPicUrl();

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
        }
        return "";
    }

    /***
     * This function handles the continue reading button click.
     */
    @Override
    public boolean onContinueReadingButtonClick()
    {
        boolean lResult = true;

        try
        {
            if (mManga.getRecentChapter() == null)
                mManga.setRecentChapter(""); //TODO.. Remove when updates to database fix this default null

            Chapter lChapter = null;
            ArrayList<Chapter> lNewChapterList = new ArrayList<>(mChapterList);
            if (mChapterOrderDescending) Collections.reverse(lNewChapterList);

            for (Chapter iChapter : lNewChapterList)
            {
                if (iChapter.getChapterUrl().equals(mManga.getRecentChapter()))
                {
                    lChapter = iChapter;
                    mManga.setRecentChapter(lChapter.getChapterUrl());

                }
            }

            // defaults to original chapter, if one is not set/found
            if (lChapter == null) lChapter = lNewChapterList.get(0);

            int lPosition = lNewChapterList.indexOf(lChapter);

            Intent lIntent = ReaderActivity.getNewInstance(mMangaMapper.getContext(), lNewChapterList, lPosition, mManga.getMangaURL());
            mMangaMapper.getContext().startActivity(lIntent);
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
            lResult = false;
        }

        return lResult;
    }

    /***
     * This function clears the chapter cache for the current item.
     */
    @Override
    public boolean clearCachedChapters()
    {
        boolean lResult = true;
        try
        {
            MangaDB.getInstance().resetCachedChapters();
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lException.getMessage());
            lResult = false;
        }

        return lResult;
    }


}
