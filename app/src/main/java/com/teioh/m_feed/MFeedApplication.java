package com.teioh.m_feed;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.SharedPrefsUtil;

import io.fabric.sdk.android.Fabric;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MFeedApplication extends Application {


    static {
        cupboard().register(Manga.class);
        cupboard().register(Chapter.class);
    }

    private static MFeedApplication aInstance;

    public MFeedApplication() {
        aInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        //creates database if fresh install
        MangaFeedDbHelper.getInstance().createDatabase();
        SharedPrefsUtil.initializePreferences();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    public static synchronized MFeedApplication getInstance() {
        return aInstance;
    }

}
