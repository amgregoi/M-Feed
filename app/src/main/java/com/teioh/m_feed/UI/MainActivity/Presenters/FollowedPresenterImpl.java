package com.teioh.m_feed.UI.MainActivity.Presenters;

import com.teioh.m_feed.UI.MainActivity.View.Mappers.ViewPresenterMapper;
import com.teioh.m_feed.Utils.MFDBHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FollowedPresenterImpl extends ViewPresenter {
    public final static String TAG = FollowedPresenterImpl.class.getSimpleName();

    public FollowedPresenterImpl(ViewPresenterMapper aMap) {
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

        mMangaListSubscription = MFDBHelper.getInstance()
                .getFollowedList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aManga -> updateMangaGridView(aManga));

    }

}