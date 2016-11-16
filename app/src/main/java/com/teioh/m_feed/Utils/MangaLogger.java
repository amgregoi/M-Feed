package com.teioh.m_feed.Utils;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by amgregoi on 11/15/16.
 */
public class MangaLogger
{
    public static void logInfo(String aTag, String aClass, String aMessage)
    {
        Log.i(aTag, aClass + " >> " + aMessage);
    }

    public static void logError(String aTag, String aClass, String aError)
    {
        Log.e(aTag, aClass + " >> " + aError);
    }

    public static void logError(String aTag, String aClass, String aError, String aExtra)
    {
        Log.e(aTag, aClass + " >> " + aExtra + " >> " + aError);
    }

}
