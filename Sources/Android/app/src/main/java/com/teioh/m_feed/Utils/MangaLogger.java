package com.teioh.m_feed.Utils;

import android.util.Log;

import com.teioh.m_feed.MFeedApplication;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by amgregoi on 11/15/16.
 */
public class MangaLogger
{
    private final static String mApplication = MFeedApplication.class.getSimpleName();

    private static List<String> mCurrentLogs = new ArrayList<>();

    /***
     * TODO..
     *
     * @param aTag
     * @param aMethod
     * @param aMessage
     */
    public static void logInfo(String aTag, String aMethod, String aMessage)
    {
        String lMessage = "INFO >> " + MessageFormat.format("{0}.class >> {1}() > {2}", aTag, aMethod, aMessage);
        if (SharedPrefs.getLoggingStatus()) mCurrentLogs.add(lMessage);
        Log.i(mApplication, lMessage);
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
        String lMessage = "ERROR >> " + MessageFormat.format("{0}.class >> {1}() > {2}", aTag, aMethod, aError);
        if (SharedPrefs.getLoggingStatus()) mCurrentLogs.add(lMessage);
        Log.e(mApplication, lMessage);
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
        String lMessage = "ERROR >> " + MessageFormat.format("{0}.class >> {1}() > {2} > {3}", aTag, aMethod, aExtra, aError);
        if (SharedPrefs.getLoggingStatus()) mCurrentLogs.add(lMessage);
        Log.e(mApplication, lMessage);
    }

    public static void logDebug(String aTag, String aMethod, String aMessage)
    {
        String lMessage = "DEBUG >> " + MessageFormat.format("{0}.class >> {1}() > {2}", aTag, aMethod, aMessage);
        if (SharedPrefs.getLoggingStatus()) mCurrentLogs.add(lMessage);
        Log.i(mApplication, lMessage);
    }

    public static void clearLogs()
    {
        mCurrentLogs = new ArrayList<>();
    }

    public static List<String> getLogs()
    {
        return mCurrentLogs;
    }

}
