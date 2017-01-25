package com.teioh.m_feed;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MFDBHelper;
import com.teioh.m_feed.Utils.SharedPrefs;

import io.fabric.sdk.android.Fabric;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MFeedApplication extends Application
{

    private static MFeedApplication aInstance;

    /***
     * TODO..
     */
    static
    {
        cupboard().register(Manga.class);
        cupboard().register(Chapter.class);
    }

    /***
     * TODO..
     */
    public MFeedApplication()
    {
        aInstance = this;
    }

    /***
     * TODO..
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        //creates database if fresh install
        MFDBHelper.getInstance().createDatabase();
        SharedPrefs.initializePreferences();
    }

    /***
     * TODO..
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    /***
     * TODO..
     * @param aLevel
     */
    @Override
    public void onTrimMemory(int aLevel)
    {
        super.onTrimMemory(aLevel);
        Glide.get(this).trimMemory(aLevel);
    }

    /***
     * TODO..
     * @return
     */
    public static synchronized MFeedApplication getInstance()
    {
        return aInstance;
    }

}
