package com.teioh.m_feed.WebSources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.SharedPrefsUtil;
import com.teioh.m_feed.WebSources.Source.Batoto;
import com.teioh.m_feed.WebSources.Source.MangaEden;
import com.teioh.m_feed.WebSources.Source.MangaHere;
import com.teioh.m_feed.WebSources.Source.MangaJoy;
import com.teioh.m_feed.WebSources.Source.MangaPark;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/*
 * TODO: interface sources to clean this up
 */
public class WebSource {

    private static String wCurrentSource = SharedPrefsUtil.getSavedSource();

    private static String[] wSources = {MangaHere.SourceKey, MangaPark.SourceKey, MangaJoy.SourceKey, MangaEden.SourceKey, Batoto.SourceKey};

    public static String getCurrentSource() {
        wCurrentSource = SharedPrefsUtil.getSavedSource();
        return wCurrentSource;
    }

    public static void setwCurrentSource(String CurrentSource) {
        SharedPrefsUtil.setSavedSource(CurrentSource);
        wCurrentSource = CurrentSource;
        Glide.get(MFeedApplication.getInstance()).clearMemory();
    }

    public static List<String> getSourceList() {
        return Arrays.asList(wSources);
    }

    public static Observable<List<Manga>> getRecentUpdatesObservable() {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.getRecentUpdatesObservable();
            case (MangaPark.SourceKey):
                return MangaPark.getRecentUpdatesObservable();
            case (MangaHere.SourceKey):
                return MangaHere.getRecentUpdatesObservable();
            case (MangaEden.SourceKey):
                return MangaEden.getRecentUpdatesObservable();
            case (Batoto.SourceKey):
                return Batoto.getRecentUpdatesObservable();
            default:
                return MangaJoy.getRecentUpdatesObservable();
        }
    }

    public static Observable<List<Chapter>> getChapterListObservable(RequestWrapper request) {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.getChapterListObservable(request);
            case (MangaPark.SourceKey):
                return MangaPark.getChapterListObservable(request);
            case (MangaHere.SourceKey):
                return MangaHere.getChapterListObservable(request);
            case (MangaEden.SourceKey):
                return MangaEden.getChapterListObservable(request);
            case (Batoto.SourceKey):
                return Batoto.getChapterListObservable(request);
            default:
                return MangaJoy.getChapterListObservable(request);
        }
    }

    public static Observable<String> getChapterImageListObservable(final RequestWrapper request) {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.getChapterImageList(request);
            case (MangaPark.SourceKey):
                return MangaPark.getChapterImageList(request);
            case (MangaHere.SourceKey):
                return MangaHere.getChapterImageList(request);
            case (MangaEden.SourceKey):
                return MangaEden.getChapterImageList(request);
            case (Batoto.SourceKey):
                return Batoto.getChapterImageList(request);
            default:
                return MangaJoy.getChapterImageList(request);
        }
    }

    public static Observable<Manga> updateMangaObservable(final Manga m) {
        switch (wCurrentSource) {
            case (MangaJoy.SourceKey):
                return MangaJoy.updateMangaObservable(m);
            case (MangaPark.SourceKey):
                return MangaPark.updateMangaObservable(m);
            case (MangaHere.SourceKey):
                return MangaHere.updateMangaObservable(m);
            case (MangaEden.SourceKey):
                return MangaEden.updateMangaObservable(new RequestWrapper(m));
            case (Batoto.SourceKey):
                return Batoto.updateMangaObservable(new RequestWrapper(m));
            default:
                return MangaJoy.updateMangaObservable(m);
        }
    }

    public static Observable<GlideDrawable> cacheFromImagesOfSize(final List<String> imageUrls) {
        return Observable.create(new Observable.OnSubscribe<GlideDrawable>() {
            @Override
            public void call(Subscriber<? super GlideDrawable> subscriber) {
                try {
                    for (String imageUrl : imageUrls) {
                        if (!subscriber.isUnsubscribed()) {
                            FutureTarget<GlideDrawable> cacheFuture = Glide.with(MFeedApplication.getInstance())
                                    .load(imageUrl)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                            subscriber.onNext(cacheFuture.get(30, TimeUnit.SECONDS));
                        }
                    }
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public static String getSourceByPosition(int pos) {
        if (pos < wSources.length)
            return wSources[pos];
        return wSources[1];
    }

}

