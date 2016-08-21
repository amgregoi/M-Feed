package com.teioh.m_feed.WebSources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.SharedPrefs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public abstract class Source {

    /***
     * TODO...
     *
     * @return
     */
    public abstract Observable<List<Manga>> getRecentUpdatesObservable();

    /***
     * TODO...
     *
     * @param aRequest
     * @return
     */
    public abstract Observable<String> getChapterImageListObservable(final RequestWrapper aRequest);

    /***
     * TODO...
     *
     * @param aRequest
     * @return
     */
    public abstract Observable<List<Chapter>> getChapterListObservable(final RequestWrapper aRequest);

    /***
     * TODO...
     *
     * @param aRequest
     * @return
     */
    public abstract Observable<Manga> updateMangaObservable(final RequestWrapper aRequest);

    /***
     * TODO...
     *
     * @return
     */
    public SourceType getSourceType() {
        return SourceType.valueOf(SharedPrefs.getSavedSource());
    }

    /***
     * TODO...
     *
     * @param aSource
     */
    public void setSourceType(SourceType aSource) {
        SharedPrefs.setSavedSource(aSource.name());
    }

    /***
     * TODO...
     *
     * @param aPosition
     * @return
     */
    public SourceType getSourceByPosition(int aPosition) {
        SourceType[] lSources = SourceType.values();
        if (aPosition < lSources.length)
            return lSources[aPosition];
        return lSources[1];
    }

    /***
     * TODO...
     *
     * @param aImageUrls
     * @return
     */
    public Observable<GlideDrawable> cacheFromImagesOfSize(final List<String> aImageUrls) {
        return Observable.create(new Observable.OnSubscribe<GlideDrawable>() {
            @Override
            public void call(Subscriber<? super GlideDrawable> subscriber) {
                try {
                    for (String imageUrl : aImageUrls) {
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

    //This is temporary, will be moved back to MangaJoy at a later time when I have lists for all sources
    final public String genres[] = {
            "Action",
            "Adult",
            "Adventure",
            "Comedy",
            "Doujinshi",
            "Drama",
            "Ecchi",
            "Fantasy",
            "Gender Bender",
            "Harem",
            "Historical",
            "Horror",
            "Josei",
            "Lolicon",
            "Manga",
            "Manhua",
            "Manhwa",
            "Martial Arts",
            "Mature",
            "Mecha",
            "Mystery",
            "One shot",
            "Psychological",
            "Romance",
            "School Life",
            "Sci fi",
            "Seinen",
            "Shotacon",
            "Shoujo",
            "Shoujo Ai",
            "Shounen",
            "Shounen Ai",
            "Slice of Life",
            "Smut",
            "Sports",
            "Supernatural",
            "Tragedy",
            "Yaoi",
            "Yuri"
    };
}
