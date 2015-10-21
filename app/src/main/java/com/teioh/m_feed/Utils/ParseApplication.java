package com.teioh.m_feed.Utils;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS", "9gtF6Yrz0XWpqvpXT8p7okWCto2e7szOS1oiShCC");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseACL.setDefaultACL(defaultACL, true);
    }

}