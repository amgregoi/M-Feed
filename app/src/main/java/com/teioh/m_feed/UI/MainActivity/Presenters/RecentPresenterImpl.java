package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.UI.MainActivity.View.Mappers.ViewPresenterMapper;
import com.teioh.m_feed.WebSources.SourceFactory;


public class RecentPresenterImpl extends ViewPresenter {
    public final static String TAG = RecentPresenterImpl.class.getSimpleName();

    public RecentPresenterImpl(ViewPresenterMapper aMap) {
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

        mMangaListSubscription = new SourceFactory().getSource().getRecentUpdatesObservable()
                .subscribe(aManga -> updateMangaGridView(aManga));
    }
}