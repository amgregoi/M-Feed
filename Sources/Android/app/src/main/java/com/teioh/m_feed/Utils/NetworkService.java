package com.teioh.m_feed.Utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.teioh.m_feed.MFeedApplication;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class NetworkService
{
    private static NetworkService sInstance;
    private OkHttpClient mClient;

    /***
     * This is a private constructor.
     */
    private NetworkService()
    {
        mClient = new OkHttpClient();
        mClient.setConnectTimeout(10, TimeUnit.SECONDS);
        mClient.setWriteTimeout(10, TimeUnit.SECONDS);
        mClient.setReadTimeout(30, TimeUnit.SECONDS);
    }

    /***
     * This function retrieves the permanent instance of the network service.
     *
     * @return
     */
    public static NetworkService getPermanentInstance()
    {
        if (sInstance == null)
        {
            sInstance = new NetworkService();
        }

        return sInstance;
    }

    /***
     * This function retrieves a new temporary instance of the network service.
     *
     * @return
     */
    public static NetworkService getTemporaryInstance()
    {
        return new NetworkService();
    }

    /***
     * This function verifies if the network is available.
     *
     * @return
     */
    public static boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) MFeedApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /***
     * This function retrieves an observable response from a network query by url.
     *
     * @param aUrl
     * @return
     */
    public Observable<Response> getResponse(final String aUrl)
    {
        if (!isNetworkAvailable())
        {
            return null;
        }

        return Observable.create((Observable.OnSubscribe<Response>) subscriber ->
        {
            try
            {
                Request request = new Request.Builder().url(aUrl).header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").build();

                subscriber.onNext(mClient.newCall(request).execute());
                subscriber.onCompleted();
            }
            catch (Throwable e)
            {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    /***
     * This function retrieves an observable response from a network query by url and custom headers.
     *
     * @param aUrl
     * @param aHeaders
     * @return
     */
    public Observable<Response> getResponseCustomHeaders(final String aUrl, final Headers aHeaders)
    {
        if (!isNetworkAvailable())
        {
            return null;
        }

        return Observable.create((Observable.OnSubscribe<Response>) subscriber ->
        {
            try
            {
                Request request = new Request.Builder().url(aUrl).headers(aHeaders).build();

                subscriber.onNext(mClient.newCall(request).execute());
                subscriber.onCompleted();
            }
            catch (Throwable e)
            {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    /***
     * This function returns an observable string converted from the response of a query.
     *
     * @param aResponse
     * @return
     */
    public static Observable<String> mapResponseToString(final Response aResponse)
    {
        return Observable.create((Observable.OnSubscribe<String>) subscriber ->
        {
            try
            {
                subscriber.onNext(aResponse.body().string());
                subscriber.onCompleted();
            }
            catch (Throwable e)
            {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }
}
