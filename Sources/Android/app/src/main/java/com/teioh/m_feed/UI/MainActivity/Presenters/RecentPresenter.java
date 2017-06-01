package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.UI.MainActivity.Fragments.RecentFragment;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.ArrayList;


public class RecentPresenter extends MainFragmentPresenterBase
{
    public final static String TAG = RecentPresenter.class.getSimpleName();

    public RecentPresenter(IMain.FragmentView aMap)
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
            if (NetworkService.isNetworkAvailable())
            {
                mViewMapper.startRefresh();
                mMangaListSubscription = new SourceFactory().getSource()
                                                            .getRecentUpdatesObservable().cache()
                                                            .subscribe(aManga -> updateMangaGridView(aManga));

            }
            else
            {
                ((RecentFragment) mViewMapper).showNoWifiView();
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, lMethod, aException.getMessage());

        }
    }
}