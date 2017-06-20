package com.teioh.m_feed.WebSources;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.SharedPrefs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public abstract class SourceBase
{

    /***
     * This function retrieves a list of recent updates from the current source.
     *
     * @return
     */
    public abstract Observable<List<Manga>> getRecentUpdatesObservable();

    /***
     * This function retrieves a list of chapter image urls from the specified request.
     *
     * @param aRequest
     * @return
     */
    public abstract Observable<String> getChapterImageListObservable(final RequestWrapper aRequest);

    /***
     * This function retrieves a list of chapters from the specified request.
     *
     * @param aRequest
     * @return
     */
    public abstract Observable<List<Chapter>> getChapterListObservable(final RequestWrapper aRequest);

    /***
     * This function updates a manga item based on the specified request.
     *
     * @param aRequest
     * @return
     */
    public abstract Observable<Manga> updateMangaObservable(final RequestWrapper aRequest);

    /***
     * This function returns the current source.
     *
     * @return
     */
    public MangaEnums.eSource getSourceType()
    {
        return MangaEnums.eSource.valueOf(SharedPrefs.getSavedSource());
    }

    /***
     * This function sets the current source.
     *
     * @param aSource
     */
    public void setSourceType(MangaEnums.eSource aSource)
    {
        SharedPrefs.setSavedSource(aSource.name());
    }

    /***
     * This function returns a source specified by its position in the source enum.
     *
     * @param aPosition
     * @return
     */
    public MangaEnums.eSource getSourceByPosition(int aPosition)
    {
        MangaEnums.eSource[] lSources = MangaEnums.eSource.values();
        if (aPosition < lSources.length) return lSources[aPosition];
        return lSources[1];
    }

    /***
     * This function caches images.
     *
     * @param aImageUrls
     * @return
     */
    public Observable<Drawable> cacheFromImagesOfSize(final List<String> aImageUrls)
    {
        return Observable.create((Observable.OnSubscribe<Drawable>) subscriber ->
        {
            try
            {
                for (String imageUrl : aImageUrls)
                {
                    if (!subscriber.isUnsubscribed())
                    {
                        RequestOptions lOptions = new RequestOptions();
                        lOptions.skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                        FutureTarget<Drawable> cacheFuture = Glide.with(MFeedApplication.getInstance()).load(imageUrl).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                        subscriber.onNext(cacheFuture.get(30, TimeUnit.SECONDS));
                    }
                }
                subscriber.onCompleted();
            }
            catch (Throwable e)
            {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.newThread());
    }

    //TODO..
    //This is temporary, will be moved back to MangaJoy at a later time when I have lists for all sources
    final public String genres[] = {"Action", "Adult", "Adventure", "Comedy", "Doujinshi", "Drama", "Ecchi", "Fantasy", "Gender Bender", "Harem", "Historical", "Horror", "Josei", "Lolicon", "Manga", "Manhua", "Manhwa", "Martial Arts", "Mature", "Mecha", "Mystery", "One shot", "Psychological", "Romance", "School Life", "Sci fi", "Seinen", "Shotacon", "Shoujo", "Shoujo Ai", "Shounen", "Shounen Ai", "Slice of Life", "Smut", "Sports", "Supernatural", "Tragedy", "Yaoi", "Yuri"};
}
