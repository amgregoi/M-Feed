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
     * This function logs info to the console as well as to the in app logger.
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
     * This function logs errors to the console as well as to the in app logger.
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
     * This function logs errors to the console as well as to the in app logger.
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

    /***
     * This function logs debug info to the console as well as to the in app logger.
     * @param aTag
     * @param aMethod
     * @param aMessage
     */
    public static void logDebug(String aTag, String aMethod, String aMessage)
    {
        String lMessage = "DEBUG >> " + MessageFormat.format("{0}.class >> {1}() > {2}", aTag, aMethod, aMessage);
        addMessage(lMessage);
        Log.i(mApplication, lMessage);
    }

    /***
     * This function adds a message to the in app logger.
     * @param aMessage
     */
    private static void addMessage(String aMessage){
        if (SharedPrefs.getLoggingStatus())
        {
            String lResult = new Date().toString() + " | " + aMessage;

            mCurrentLogs.add(lResult);
        }
    }

    /***
     * This function clears the in app log.
     */
    public static void clearLogs()
    {
        mCurrentLogs = new ArrayList<>();
    }

    /***
     * This function retrieves the in app logs.
     * @return
     */
    public static List<String> getLogs()
    {
        return mCurrentLogs;
    }

    /***
     * This function creates a toast.
     * @param aMessage
     */
    public static void makeToast(String aMessage){
        Toast.makeText(MFeedApplication.getInstance(), aMessage, Toast.LENGTH_SHORT).show();
    }

}
