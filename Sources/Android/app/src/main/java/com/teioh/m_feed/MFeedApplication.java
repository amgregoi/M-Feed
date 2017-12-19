package com.teioh.m_feed;

import android.app.Application;
import android.os.StrictMode;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.crashlytics.android.Crashlytics;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;

import io.fabric.sdk.android.Fabric;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MFeedApplication extends Application
{

    private static MFeedApplication aInstance;

    /***
     * This static section registers the two classes to my ORM database.
     */
    static
    {
        cupboard().register(Manga.class);
        cupboard().register(Chapter.class);
    }

    /***
     * This is the constructor for the application
     */
    public MFeedApplication()
    {
        aInstance = this;
    }

    /***
     * This function returns a synchronized isntance of the application.
     * @return
     */
    public static synchronized MFeedApplication getInstance()
    {
        return aInstance;
    }

    /***
     * This function initializes various modules when the application is created.
     */
    @Override
    public void onCreate()
    {
        super.onCreate();

        //creates database if fresh install
        MangaDB.getInstance().createDatabase();
        SharedPrefs.initializePreferences();

        ViewTarget.setTagId(R.id.glide_tag);

        // Fabric init
        Fabric.with(this, new Crashlytics());
        MangaLogger.initialize();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    /***
     * This function saves the logs when the application terminates
     */
    @Override
    public void onTerminate()
    {
        super.onTerminate();

        SharedPrefs.saveLogs();
    }

    /***
     * This function clears Glide cache when low on memory.
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    /***
     * This function trims Glide cache when low on memory.
     * @param aLevel
     */
    @Override
    public void onTrimMemory(int aLevel)
    {
        super.onTrimMemory(aLevel);
        Glide.get(this).trimMemory(aLevel);
    }


}
