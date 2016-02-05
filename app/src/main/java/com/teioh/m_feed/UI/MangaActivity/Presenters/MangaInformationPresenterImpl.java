package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.MangaInformationFragment;
import com.teioh.m_feed.UI.MangaActivity.View.Mappers.MangaInformationMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.WebSources.WebSource;

import butterknife.ButterKnife;
import rx.Subscription;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaInformationPresenterImpl implements MangaInformationPresenter {
    private static final String TAG = MangaInformationPresenterImpl.class.getSimpleName();
    private static final String MANGA_KEY = TAG + ":MANGA";

    private Manga mManga;
    private MangaInformationMapper mMangaInformationMapper;
    private Subscription mObservableMangaSubscription;

    public MangaInformationPresenterImpl(MangaInformationMapper map) {
        mMangaInformationMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle) {
        if (mManga != null) {
            bundle.putParcelable(MANGA_KEY, mManga);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if (bundle.containsKey(MANGA_KEY)) {
            mManga = bundle.getParcelable(MANGA_KEY);
        }
    }

    @Override
    public void init(Bundle bundle) {
        if (mMangaInformationMapper.getContext() != null) {
            if (mManga == null) {
                String title = bundle.getString(Manga.TAG);
                mManga = cupboard().withDatabase(MangaFeedDbHelper.getInstance().getReadableDatabase())
                        .query(Manga.class).withSelection("mTitle = ? AND mSource = ?", title, WebSource.getCurrentSource()).get();

            }

            if (mManga.getmIsInitialized() == 1) {
                mMangaInformationMapper.setMangaViews(mManga);
                mMangaInformationMapper.showCoverLayout();
                mMangaInformationMapper.setupFollowButton();
                this.setFollowButtonText(mManga.getFollowing(), true); //second parameter signifies if the button is being initialized
            } else {
                mMangaInformationMapper.hideCoverLayout();
                mMangaInformationMapper.setupSwipeRefresh();
                this.getMangaViewInfo();
            }
        }
    }

    @Override
    public void onFollwButtonClick() {
        boolean follow = mManga.setFollowing(!mManga.getFollowing());
        this.setFollowButtonText(follow, false);        //second parameter signifies if the button is being initialized
        if (follow) {
            MangaFeedDbHelper.getInstance().updateMangaFollow(mManga.getTitle());
        } else {
            MangaFeedDbHelper.getInstance().updateMangaUnfollow(mManga.getTitle());
        }
    }

    @Override
    public void setFollowButtonText(boolean follow, boolean notInit) {
        if (follow) {
            mMangaInformationMapper.setFollowButtonText(R.drawable.ic_done, notInit);
        } else {
            mMangaInformationMapper.setFollowButtonText(R.drawable.fab_bg_normal, notInit);
        }
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        if (mObservableMangaSubscription != null) {
            mObservableMangaSubscription.unsubscribe();
            mObservableMangaSubscription = null;
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
    }

    private void getMangaViewInfo() {
        mObservableMangaSubscription = WebSource.updateMangaObservable(mManga).subscribe(manga -> {
            if(mMangaInformationMapper.getContext() != null) {
                mMangaInformationMapper.setMangaViews(manga);
                mMangaInformationMapper.stopRefresh();
                mMangaInformationMapper.showCoverLayout();
                mMangaInformationMapper.setupFollowButton();
                this.setFollowButtonText(mManga.getFollowing(), true); //fix this
            }
                manga.setmIsInitialized(1);
                cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(manga);
            mObservableMangaSubscription.unsubscribe();
            mObservableMangaSubscription = null;

        });
    }

}
