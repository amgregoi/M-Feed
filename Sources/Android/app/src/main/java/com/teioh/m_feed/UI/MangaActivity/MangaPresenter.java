package com.teioh.m_feed.UI.MangaActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.ReaderActivity;
import com.teioh.m_feed.Utils.MFDBHelper;
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
     * TODO...
     *
     * @param aBundle
     */
    @Override
    public void init(Bundle aBundle)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mManga == null)
            {
                String lMangaUrl = aBundle.getString(Manga.TAG);
                mManga = MFDBHelper.getInstance().getManga(lMangaUrl);
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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aSave
     */
    @Override
    public void onSaveState(Bundle aSave)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mManga != null) aSave.putParcelable(MANGA_KEY, mManga);
            if (mChapterList != null) aSave.putParcelableArrayList(CHAPTER_LIST_KEY, mChapterList);
            aSave.putBoolean(ORDER_DESCENDING_KEY, mChapterOrderDescending);

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * TODO...
     *
     * @param aRestore
     */
    @Override
    public void onRestoreState(Bundle aRestore)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

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
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * TODO...
     */
    @Override
    public void onPause()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

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
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }

    }

    /***
     * TODO...
     */
    @Override
    public void onResume()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
            mManga = MFDBHelper.getInstance().getManga(mManga.getMangaURL()); //get updates manga object

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * TODO...
     */
    @Override
    public void onDestroy()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            Glide.get(mMangaMapper.getContext()).clearMemory();
            mMangaMapper = null;

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * TODO...
     */
    @Override
    public void chapterOrderButtonClick()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aValue
     */
    @Override
    public void onFollowButtonClick(int aValue)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            MFDBHelper.getInstance().updateMangaFollow(mManga.getTitle(), aValue);
            mManga.setFollowing(aValue);
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * TODO...
     */
    @Override
    public void onUnfollowButtonClick()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            mManga.setFollowing(0);
            MFDBHelper.getInstance().updateMangaUnfollow(mManga.getTitle());
            MFDBHelper.getInstance().removeChapters(mManga);
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aChapter
     */
    @Override
    public void onChapterClicked(Chapter aChapter)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ArrayList<Chapter> lNewChapterList = new ArrayList<>(mChapterList);
            if (mChapterOrderDescending) Collections.reverse(lNewChapterList);
            int lPosition = lNewChapterList.indexOf(aChapter);

            mManga.setRecentChapter(aChapter.getChapterUrl());
            MFDBHelper.getInstance().updateManga(mManga);

            Intent lIntent = ReaderActivity.getNewInstance(mMangaMapper.getContext(), lNewChapterList, lPosition, mManga.getMangaURL());
            mMangaMapper.getContext().startActivity(lIntent);
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @return
     */
    @Override
    public String getImageUrl()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            return mManga.getPicUrl();

        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
        return "";
    }

    /***
     * TODO..
     */
    @Override
    public void onContinueReadingButtonClick()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (mManga.getRecentChapter() == null) mManga.setRecentChapter(""); //TODO.. Remove when updates to database fix this default null

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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    @Override public void clearCachedChapters()
    {
        MFDBHelper.getInstance().resetCachedChapters();
    }

    /***
     * TODO...
     */
    private void getMangaViewInfo()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (NetworkService.isNetworkAvailable())
            {
                if (mManga.getInitialized() == 0)
                {
                    mObservableMangaSubscription = new SourceFactory().getSource()
                                                                      .updateMangaObservable(new RequestWrapper(mManga)).cache()
                                                                      .doOnError(throwable -> MangaLogger.logError(TAG, lMethod, throwable.getMessage()))
                                                                      .observeOn(AndroidSchedulers.mainThread())
                                                                      .subscribe(manga -> updateMangaView(manga));
                }
                else
                {
                    updateMangaView(mManga);
                    MangaLogger.logInfo(TAG, lMethod, "Manga was previously initialized");
                }
            }
            else
            {
                updateMangaView(mManga);
                MangaLogger.logInfo(TAG, lMethod, "No internet access ");
            }
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aManga
     */
    private void updateMangaView(Manga aManga)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

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

                MFDBHelper.getInstance().putManga(aManga);
                if (mChapterListSubscription != null)
                {
                    mObservableMangaSubscription.unsubscribe();
                    mObservableMangaSubscription = null;
                }
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     */
    private void getChapterList()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            if (NetworkService.isNetworkAvailable())
            {
                if (!mChapterFlag)
                {
                    mChapterListSubscription = new SourceFactory().getSource()
                                                                  .getChapterListObservable(new RequestWrapper(mManga)).cache()
                                                                  .doOnError(throwable -> MangaLogger.logError(TAG, lMethod, throwable.getMessage()))
                                                                  .subscribe(chapters -> updateChapterList(chapters));
                }
                else
                {
                    MangaLogger.logInfo(TAG, lMethod, "Chapter list is already initialized");
                    updateChapterList(mChapterList);
                }
            }
            else
            {
                MangaLogger.logInfo(TAG, lMethod, "No internet access");
                updateChapterList(new ArrayList<>());
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }

    /***
     * TODO...
     *
     * @param aChapterList
     */
    private void updateChapterList(List<Chapter> aChapterList)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

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
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }


}
