package com.teioh.m_feed.Utils;

import android.util.Log;
import android.widget.Toast;

import com.teioh.m_feed.MFeedApplication;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by amgregoi on 11/15/16.
 */
public class MangaLogger
{
    private final static String mApplication = MFeedApplication.class.getSimpleName();

    private static List<String> mCurrentLogs = new ArrayList<>();


    public static void initialize(){
        mCurrentLogs = new ArrayList<>(SharedPrefs.getLogs());
    }

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
        addMessage(lMessage);
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
        addMessage(lMessage);
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
        addMessage(lMessage);
        Log.e(mApplication, lMessage);
    }

    public static void logDebug(String aTag, String aMethod, String aMessage)
    {
        String lMessage = "DEBUG >> " + MessageFormat.format("{0}.class >> {1}() > {2}", aTag, aMethod, aMessage);
        addMessage(lMessage);
        Log.i(mApplication, lMessage);
    }

    private static void addMessage(String aMessage){
        if (SharedPrefs.getLoggingStatus())
        {
            String lResult = new Date().toString() + " | " + aMessage;

            mCurrentLogs.add(lResult);
        }
    }

    public static void clearLogs()
    {
        mCurrentLogs = new ArrayList<>();
    }

    public static List<String> getLogs()
    {
        return mCurrentLogs;
    }

    public static void makeToast(String aMessage){
        Toast.makeText(MFeedApplication.getInstance(), aMessage, Toast.LENGTH_SHORT).show();
    }

}
