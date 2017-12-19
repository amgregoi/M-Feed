package com.teioh.m_feed.WebSources;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.squareup.okhttp.Headers;
import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.Utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class SourceBase
{
    private String TAG = SourceBase.class.getSimpleName();

    /***
     * This function returns the type of content the source provides.
     * @return
     */
    public abstract MangaEnums.eSourceType getSourceType();

    /***
     * This function retrieves the sources recent update page url.
     * @return
     */
    public abstract String getRecentUpdatesUrl();

    /***
     * This function retrieves the array of genres a source supports.
     *
     * @return
     */
    public abstract String[] getGenres();

    /***
     * This function pareses the response body and builds the Recent Manga list.
     * @param aResponseBody
     * @return
     */
    public abstract List<Manga> parseResponseToRecentList(final String aResponseBody);

    /***
     * This function pareses the response body to a manga object specified by the request.
     *
     * @param aRequest
     * @param aResponseBody
     * @return
     */
    public abstract Manga parseResponseToManga(final RequestWrapper aRequest, final String aResponseBody);

    /***
     * This function pareses the response body and builds a list of chapters from the specified request.
     * @param aRequest
     * @param aResponseBody
     * @return
     */
    public abstract List<Chapter> parseResponseToChapters(RequestWrapper aRequest, String aResponseBody);

    /***
     * This function pareses the response body and returns a list of page urls for the images
     *
     * @param aResponseBody
     * @return
     */
    public abstract List<String> parseResponseToPageUrls(final String aResponseBody);

    /***
     * This function pareses the response body and returns the image url of its page.
     *
     * @param aResponseBody
     * @param aResponseUrl
     * @return
     */
    public abstract String parseResponseToImageUrls(final String aResponseBody, final String aResponseUrl);

    /***
     * This function retrieves a list of recent updates from the current source.
     *
     * @return
     */
    public Observable<List<Manga>> getRecentMangaObservable()
    {
        return NetworkService.getTemporaryInstance()
                             .getResponse(getRecentUpdatesUrl())
                             .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                             .flatMap(aHtml -> Observable.just(parseResponseToRecentList(aHtml)))
                             .subscribeOn(Schedulers.io())
                             .observeOn(AndroidSchedulers.mainThread())
                             .retry(3)
                             .doOnError(aThrowable -> MangaLogger.logError(TAG, aThrowable.getMessage()))
                             .onErrorReturn(null);
    }

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
    public Observable<List<Chapter>> getChapterListObservable(final RequestWrapper aRequest)
    {
//        String lUrl = aRequest.getMangaUrl();
//        if(lUrl.substring(0,2).equals("//")) lUrl = "http:" + lUrl;

        return NetworkService.getPermanentInstance()
                             .getResponseCustomHeaders(aRequest.getMangaUrl(), constructRequestHeaders())
                             .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                             .flatMap(aResponseBody -> Observable.just(parseResponseToChapters(aRequest, aResponseBody)))
                             .observeOn(AndroidSchedulers.mainThread())
                             .doOnError(aThrowable -> MangaLogger.logError(TAG, aThrowable.getMessage()))
                             .onErrorReturn(null);
    }

    /***
     * Adds new Manga and
     * gets missing manga information and updates local database
     *
     * @param aRequest
     * @return
     */
    public Observable<Manga> updateMangaObservable(final RequestWrapper aRequest)
    {
//        String lUrl = aRequest.getMangaUrl();
//        if(lUrl.substring(0,2).equals("//")) lUrl = "http:" + lUrl;

        return NetworkService.getPermanentInstance()
                             .getResponseCustomHeaders(aRequest.getMangaUrl(), constructRequestHeaders())
                             .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                             .flatMap(aResponseBody -> Observable.just(parseResponseToManga(aRequest, aResponseBody)))
                             .doOnError(aThrowable -> MangaLogger.logError(TAG, aThrowable.getMessage()))
                             .onErrorReturn(null);
    }

    /***
     * This function returns the current source.
     *
     * @return
     */
    public MangaEnums.eSource getCurrentSource()
    {
        return MangaEnums.eSource.valueOf(SharedPrefs.getSavedSource());
    }

    /***
     * This function sets the current source.
     *
     * @param aSource
     */
    public void setCurrentSource(MangaEnums.eSource aSource)
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

                        FutureTarget<Drawable> cacheFuture = Glide.with(MFeedApplication.getInstance()).load(imageUrl)
                                                                  .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

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

    /***
     * This function contstructs a custom http header for various sources
     *
     * @return
     */
    private Headers constructRequestHeaders()
    {
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64)");
        headerBuilder.add("Cookie", "lang_option=English");

        return headerBuilder.build();
    }
}
