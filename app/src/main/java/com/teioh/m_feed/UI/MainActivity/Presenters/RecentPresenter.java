package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.widget.Toast;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.UI.MainActivity.MainFragmentPresenterBase;
import com.teioh.m_feed.WebSources.SourceFactory;


public class RecentPresenter extends MainFragmentPresenterBase {
    public final static String TAG = RecentPresenter.class.getSimpleName();

    public RecentPresenter(IMain.FragmentView aMap) {
        super(aMap);
    }

    /***
     * TODO...
     *
     */
    @Override
    public void updateMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }

        mMangaListSubscription = new SourceFactory().getSource()
                .getRecentUpdatesObservable()
                .doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT))
                .subscribe(aManga -> updateMangaGridView(aManga));
    }
}