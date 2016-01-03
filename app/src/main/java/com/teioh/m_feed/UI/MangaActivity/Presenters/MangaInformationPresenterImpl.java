package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaInformationMapper;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.ChapterOrderEvent;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.WebSources.MangaJoy;

import java.util.Collections;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class MangaInformationPresenterImpl implements MangaInformationPresenter {

    private Manga item;
    private MangaInformationMapper mMangaInformationMapper;
    private Observable<Manga> observableManga;


    public MangaInformationPresenterImpl(MangaInformationMapper base, Bundle b) {
        this.item = b.getParcelable("Manga");
        mMangaInformationMapper = base;
    }

    @Override
    public void initialize() {
        try {
            this.setFollowButtonText(item.getFollowing(), true); //second parameter signifies if the button is being initialized
            if (item.getmGenre() != null && item.getmAlternate() != null) {
                mMangaInformationMapper.setupFollowButton();
                mMangaInformationMapper.setMangaViews(item);
                mMangaInformationMapper.showLayout();
            } else {
                mMangaInformationMapper.hideLayout();
                mMangaInformationMapper.setupFollowButton();
                mMangaInformationMapper.setupSwipeRefresh();
                this.getMangaViewInfo();
            }
        } catch (NullPointerException e) {
            Log.e("MangaInformationFrag", "Changed views to fast \n\t\t\t" + e.toString());
        }
    }

    @Override
    public void getMangaViewInfo() {
        observableManga = MangaJoy.updateMangaObservable(item);
        observableManga.subscribe(manga -> {
            mMangaInformationMapper.setMangaViews(manga);
            mMangaInformationMapper.stopRefresh();
            mMangaInformationMapper.showLayout();
        });
    }

    @Override
    public void onFollwButtonClick() {
        boolean follow = item.setFollowing(!item.getFollowing());
        this.setFollowButtonText(follow, false);        //second parameter signifies if the button is being initialized
        if (follow) {
            MangaFeedDbHelper.getInstance().updateMangaFollow(item.getTitle());
            BusProvider.getInstance().post(item);
        } else {
            MangaFeedDbHelper.getInstance().updateMangaUnfollow(item.getTitle());
            BusProvider.getInstance().post(new RemoveFromLibrary(item));
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
    public void busProviderRegister() {
        BusProvider.getInstance().register(this);
    }

    @Override
    public void busProviderUnregister() {
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void butterKnifeUnbind() {
        ButterKnife.unbind(this);
        if(observableManga != null) {
            observableManga.unsubscribeOn(Schedulers.io());
            observableManga = null;
        }
    }
}
