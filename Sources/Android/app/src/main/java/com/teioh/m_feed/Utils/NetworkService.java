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
     * TODO..
     */
    private NetworkService()
    {
        mClient = new OkHttpClient();
        mClient.setConnectTimeout(10, TimeUnit.SECONDS);
        mClient.setWriteTimeout(10, TimeUnit.SECONDS);
        mClient.setReadTimeout(30, TimeUnit.SECONDS);
    }

    /***
     * TODO..
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
     * TODO..
     *
     * @return
     */
    public static NetworkService getTemporaryInstance()
    {
        return new NetworkService();
    }

    /***
     * TODO..
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
     * TODO...
     *
     * @param aUrl
     * @return
     */
    public Observable<Response> getResponse(final String aUrl)
    {
        return Observable.create(new Observable.OnSubscribe<Response>()
        {
            @Override
            public void call(Subscriber<? super Response> subscriber)
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
            }
        }).subscribeOn(Schedulers.io());
    }

    /***
     * TODO...
     *
     * @param aUrl
     * @param aHeaders
     * @return
     */
    public Observable<Response> getResponseCustomHeaders(final String aUrl, final Headers aHeaders)
    {
        return Observable.create(new Observable.OnSubscribe<Response>()
        {
            @Override
            public void call(Subscriber<? super Response> subscriber)
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
            }
        }).subscribeOn(Schedulers.io());
    }

    /***
     * TODO...
     *
     * @param aResponse
     * @return
     */
    public static Observable<String> mapResponseToString(final Response aResponse)
    {
        return Observable.create(new Observable.OnSubscribe<String>()
        {
            @Override
            public void call(Subscriber<? super String> subscriber)
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
            }
        }).subscribeOn(Schedulers.io());
    }
}
