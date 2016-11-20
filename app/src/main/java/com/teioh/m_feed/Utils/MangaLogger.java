package com.teioh.m_feed.Utils;

import android.util.Log;

import com.teioh.m_feed.MFeedApplication;

import java.text.MessageFormat;


/**
 * Created by amgregoi on 11/15/16.
 */
public class MangaLogger
{
    private final static String mApplication = MFeedApplication.class.getSimpleName();

    /***
     * TODO..
     *
     * @param aTag
     * @param aMethod
     * @param aMessage
     */
    public static void logInfo(String aTag, String aMethod, String aMessage)
    {
        Log.i(mApplication, MessageFormat.format("{0}.class >> {1}() > {2}", aTag, aMethod, aMessage));
    }

    /***
     * TODO..
     *
     * @param aTag
     * @param aMethod
     * @param aError
     */
    public static void logError(String aTag, String aMethod, String aError)
    {
        Log.e(mApplication, MessageFormat.format("{0}.class >> {1}() > {2}", aTag, aMethod, aError));
    }

    /***
     * TODO..
     *
     * @param aTag
     * @param aMethod
     * @param aError
     * @param aExtra
     */
    public static void logError(String aTag, String aMethod, String aError, String aExtra)
    {
        Log.e(mApplication, MessageFormat.format("{0}.class >> {1}() > {2} > {3}", aTag, aMethod, aExtra, aError));
    }

}
