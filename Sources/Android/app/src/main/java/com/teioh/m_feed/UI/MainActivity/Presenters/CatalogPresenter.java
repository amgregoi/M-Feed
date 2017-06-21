package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.widget.Toast;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CatalogPresenter extends MainFragmentPresenterBase
{
    public final static String TAG = RecentPresenter.class.getSimpleName();

    public CatalogPresenter(IMain.FragmentView aMap)
    {
        super(aMap);
    }

    /***
     * This function performs the update to the presenter data list.
     */
    @Override
    public void updateMangaList()
    {
        if (mMangaListSubscription != null)
        {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }

        try
        {
            mMangaListSubscription = MangaDB.getInstance()
                                            .getCatalogList().cache()
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT))
                                            .subscribe(aManga -> updateMangaGridView(aManga));
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());

        }
    }

}