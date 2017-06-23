package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.UI.MainActivity.Fragments.RecentFragment;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.WebSources.SourceFactory;


public class RecentPresenter extends MainFragmentPresenterBase
{
    public final static String TAG = RecentPresenter.class.getSimpleName();

    public RecentPresenter(IMain.FragmentView aMap)
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
            if (NetworkService.isNetworkAvailable())
            {
                mViewMapper.startRefresh();
                mMangaListSubscription = SourceFactory.getInstance().getSource()
                                                            .getRecentMangaObservable()
                                                            .cache()
                                                            .subscribe(aManga -> updateMangaGridView(aManga));

            }
            else
            {
                ((RecentFragment) mViewMapper).showNoWifiView();
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());

        }
    }
}