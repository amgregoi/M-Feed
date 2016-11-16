package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.util.Log;
import android.widget.Toast;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.UI.MainActivity.IMain;
import com.teioh.m_feed.Utils.MFDBHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class LibraryPresenter extends MainFragmentPresenterBase {
    public final static String TAG = RecentPresenter.class.getSimpleName();

    public LibraryPresenter(IMain.FragmentView aMap) {
        super(aMap);
    }

    /***
     * TODO...
     */
    @Override
    public void updateMangaList() {
        if (mMangaListSubscription != null) {
            mMangaListSubscription.unsubscribe();
            mMangaListSubscription = null;
        }

        try {
            mMangaListSubscription = MFDBHelper.getInstance().getLibraryList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT)).subscribe(aManga -> updateMangaGridView(aManga));
        } catch (Exception aException) {
            Log.e(TAG, aException.getMessage());
        }
    }
}