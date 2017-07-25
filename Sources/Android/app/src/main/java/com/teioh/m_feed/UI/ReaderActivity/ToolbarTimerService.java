package com.teioh.m_feed.UI.ReaderActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.Utils.MangaLogger;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ToolbarTimerService extends Service
{
    public final static String TAG = ToolbarTimerService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();
    private Listeners.ReaderTimerListener mListener;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder
    {
        ToolbarTimerService getService()
        {
            return ToolbarTimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public void setToolbarListener(Listeners.ReaderTimerListener aListener)
    {
        mListener = aListener;
    }

    Observable<Long> mTimer = Observable.timer(3, TimeUnit.SECONDS, Schedulers.io());
    Observable<Long> mSystemUI = Observable.timer(6, TimeUnit.SECONDS, Schedulers.io());
    Subscription mTimerSub;
    Subscription mSystemUISub;

    public void startToolBarTimer()
    {
        if (mTimerSub != null)
        {
            mTimerSub.unsubscribe();
            mTimerSub = null;
        }

        mTimerSub = mTimer.subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(aComplete ->
                                     {
                                         mListener.hideToolbar();
                                         mTimerSub.unsubscribe();
                                         mTimerSub = null;
                                         MangaLogger.logInfo(TAG, "ToolBar Service >> Hide Toolbar");
                                     });

    }

    public void stopTimer()
    {
        if (mTimerSub != null)
        {
            mTimerSub.unsubscribe();
            mTimerSub = null;
            restartSystemUiTimer();
        }
    }

    private void restartSystemUiTimer()
    {
        if (mSystemUISub != null)
        {
            mSystemUISub.unsubscribe();
            mSystemUISub = null;
        }

        mSystemUISub = mSystemUI.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(aComplete -> mListener.hideSystemUi());

    }
}
