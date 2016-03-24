package com.teioh.m_feed;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

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
    }

    public static synchronized MFeedApplication getInstance() {
        return aInstance;
    }

}
