package com.teioh.m_feed.UI.MangaActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaViewMapper;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.Utils.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.WebSources.MangaJoy;

import butterknife.ButterKnife;
import rx.Observable;

public class MangaInformationPresenterImpl implements  MangaInformationPresenter{

    private Manga item;
    private MangaViewMapper mMangaViewMapper;

    public MangaInformationPresenterImpl(MangaViewMapper base, Bundle b){
        this.item = b.getParcelable("Manga");
        mMangaViewMapper = base;
    }

    @Override
    public void initialize() {
        if(item.getDescription() != null) {
            mMangaViewMapper.setMangaViews(item);
        }else{
            this.getMangaViewInfo();
        }
    }

    @Override
    public void getMangaViewInfo() {
        Observable<Manga> observableManga;
        observableManga = MangaJoy.updateMangaObservable(item);
        observableManga.subscribe(manga -> mMangaViewMapper.setMangaViews(manga));
    }

    @Override
    public void onFollwButtonClick(){
        boolean follow = item.setFollowing(!item.getFollowing());
        this.setFollowButtonText(follow);
        if (follow) {
            MangaFeedDbHelper.getInstance().updateMangaFollow(item);
            BusProvider.getInstance().post(item);
        } else {
            MangaFeedDbHelper.getInstance().updateMangaUnfollow(item);
            BusProvider.getInstance().post(new RemoveFromLibrary(item));
        }
    }

    @Override
    public void setFollowButtonText(boolean follow) {
        if (follow) {
            mMangaViewMapper.setFollowButtonText("Unfollow");
        } else {
            mMangaViewMapper.setFollowButtonText("Follow");
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
