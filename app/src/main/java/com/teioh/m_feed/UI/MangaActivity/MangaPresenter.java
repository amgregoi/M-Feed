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
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

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
    private boolean mRestoreActivity;
    private Manga mManga;

    private IManga.ActivityView mMangaMapper;


    public MangaPresenter(IManga.ActivityView aMap)
    {
        mMangaMapper = aMap;
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
                String lSource = new SourceFactory().getSourceName();

                //TODO > bundle url instead of title and use MFDBHelper
//                mManga = cupboard().withDatabase(MFDBHelper.getInstance().getReadableDatabase()).query(Manga.class).withSelection("title = ? AND source = ?", lTitle, new SourceFactory().getSourceName()).get();
                mManga = MFDBHelper.getInstance().getManga(lMangaUrl, lSource);
            }
            if(!mRestoreActivity) mChapterOrderDescending = true;
            mMangaMapper.setActivityTitle(mManga.getTitle());
            mMangaMapper.setupToolBar();
            mMangaMapper.initializeHeaderViews();
            mMangaMapper.setupHeaderButtons();
            mMangaMapper.setupSwipeRefresh();
            mMangaMapper.hideCoverLayout();

            getMangaViewInfo();

            if (mChapterList == null) getChapterList();
            else updateChapterList(mChapterList);

        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
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
     * @param aChapter
     */
    @Override
    public void onChapterClicked(Chapter aChapter)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            ArrayList<Chapter> newChapterList = new ArrayList<>(mChapterList);

            if (mChapterOrderDescending) Collections.reverse(newChapterList);

            int lPosition = newChapterList.indexOf(aChapter);

            //TODO > update ReaderActivity.getInstance()
            Intent lIntent = new Intent(mMangaMapper.getContext(), ReaderActivity.class);
            lIntent.putParcelableArrayListExtra(CHAPTER_LIST_KEY, newChapterList);
            lIntent.putExtra(LIST_POSITION_KEY, lPosition);
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
     * TODO...
     *
     * @param aValue
     */
    @Override
    public void onFollwButtonClick(int aValue)
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        try
        {
            MFDBHelper.getInstance().updateMangaFollow(mManga.getTitle(), aValue);

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
            MFDBHelper.getInstance().updateMangaUnfollow(mManga.getTitle());
        }
        catch (Exception lException)
        {
            MangaLogger.logError(TAG, lMethod, lException.getMessage());
        }
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
                    mObservableMangaSubscription = new SourceFactory().getSource().updateMangaObservable(new RequestWrapper(mManga)).doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT)).observeOn(AndroidSchedulers.mainThread()).subscribe(manga -> updateMangaView(manga));
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
                if (mChapterList == null)
                {
                    mChapterListSubscription = new SourceFactory().getSource().getChapterListObservable(new RequestWrapper(mManga)).doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT)).subscribe(chapters -> updateChapterList(chapters));
                }
                else
                {
                    MangaLogger.logInfo(TAG, lMethod, "Chapter list is already initialized");
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
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());
        }
    }


}
