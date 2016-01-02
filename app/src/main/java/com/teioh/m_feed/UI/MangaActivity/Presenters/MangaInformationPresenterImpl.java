package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaInformationMapper;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.WebSources.MangaJoy;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class MangaInformationPresenterImpl implements MangaInformationPresenter {

    private Manga item;
    private MangaInformationMapper mMangaInformationMapper;


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
        Observable<Manga> observableManga;
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
            MangaFeedDbHelper.getInstance().updateMangaFollow(item);
            BusProvider.getInstance().post(item);
        } else {
            MangaFeedDbHelper.getInstance().updateMangaUnfollow(item);
            BusProvider.getInstance().post(new RemoveFromLibrary(item));
        }
    }

    @Override
    public void onReadButtonClick() {
        ViewPager viewPager = (ViewPager) ((Fragment) mMangaInformationMapper).getActivity().findViewById(R.id.pager);
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

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
    }


}
