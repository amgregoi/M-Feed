package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaInformationMapper;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.WebSources.WebSource;

import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MangaInformationPresenterImpl implements MangaInformationPresenter {
    private static final String TAG = MangaInformationPresenterImpl.class.getSimpleName();
    private static final String MANGA_KEY = TAG + ":MANGA";

    private Manga mManga;
    private MangaInformationMapper mMangaInformationMapper;
    private Observable<Manga> mObservableManga;


    public MangaInformationPresenterImpl(MangaInformationMapper base) {
        mMangaInformationMapper = base;
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
                        .query(Manga.class).withSelection("mTitle = ? AND mSource = ?", title, WebSource.getwCurrentSource()).get();

            }
            this.setFollowButtonText(mManga.getFollowing(), true); //second parameter signifies if the button is being initialized
            if (mManga.getmGenre() != null && mManga.getmAlternate() != null && !mManga.getPicUrl().equals("")) {
                mMangaInformationMapper.setupFollowButton();
                mMangaInformationMapper.setMangaViews(mManga);
                mMangaInformationMapper.showCoverLayout();
            } else {
                mMangaInformationMapper.hideCoverLayout();
                mMangaInformationMapper.setupFollowButton();
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
            BusProvider.getInstance().post(mManga);
        } else {
            MangaFeedDbHelper.getInstance().updateMangaUnfollow(mManga.getTitle());
            BusProvider.getInstance().post(new RemoveFromLibrary(mManga));
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
        if (mObservableManga != null) {
            mObservableManga.unsubscribeOn(Schedulers.io());
            mObservableManga = null;
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
    }

    private void getMangaViewInfo() {
        if (mObservableManga != null) {
            mObservableManga.unsubscribeOn(Schedulers.io());
            mObservableManga = null;
        }
        mObservableManga = WebSource.updateMangaObservable(mManga);
        mObservableManga.subscribe(manga -> {
            mMangaInformationMapper.setMangaViews(manga);
            mMangaInformationMapper.stopRefresh();
            mMangaInformationMapper.showCoverLayout();
        });
    }

}
