package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.os.Bundle;
import android.widget.Toast;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.Utils.MFDBHelper;
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
     * TODO...
     */
    @Override
    public void updateMangaList()
    {
        String lMethod = Thread.currentThread().getStackTrace()[2].getMethodName();

        if (mMangaListSubscription != null)
        {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }

        try
        {
            mMangaListSubscription = MFDBHelper.getInstance()
                                               .getCatalogList().cache()
                                               .subscribeOn(Schedulers.io())
                                               .observeOn(AndroidSchedulers.mainThread())
                                               .doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT))
                                               .subscribe(aManga -> updateMangaGridView(aManga));
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());

        }
    }

}