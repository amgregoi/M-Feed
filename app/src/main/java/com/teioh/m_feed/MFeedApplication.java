package com.teioh.m_feed;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Asus1 on 11/3/2015.
 */
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

        Parse.initialize(this, "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS", "9gtF6Yrz0XWpqvpXT8p7okWCto2e7szOS1oiShCC");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        defaultACL.setPublicReadAccess(true);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseACL.setDefaultACL(defaultACL, true);
    }

    public static synchronized MFeedApplication getInstance() {
        return aInstance;
    }

}
