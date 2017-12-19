package com.teioh.m_feed.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPrefs
{


    /***
     * This function initializes the shared prefs.
     */
    public static void initializePreferences()
    {
        Context lContext = MFeedApplication.getInstance();

        // Need to set storage preference per device
        SharedPreferences lSharedPrefs = PreferenceManager.getDefaultSharedPreferences(lContext);
        if (lSharedPrefs.getString(lContext.getString(R.string.PREF_STORAGE_LOCATION), null) == null)
        {
            SharedPreferences.Editor editor = lSharedPrefs.edit();
            editor.putString(lContext.getString(R.string.PREF_STORAGE_LOCATION), lContext.getFilesDir().getAbsolutePath());
            editor.commit();
        }
    }

    /***
     * This function verifies if a user is signed into google.
     * @return
     */
    public static boolean isSignedIn()
    {
        if (getGoogleEmail().contains("Guest")) return false;
        return true;
    }

    /**
     * Get the users Google email
     *
     * @return The users Google Email
     */
    public static String getGoogleEmail()
    {
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext)
                                .getString(lContext.getString(R.string.PREF_GOOGLE_EMAIL), "Guest (Sign in)");
    }

    /**
     * Sets the users MyAnimeList(MAL) login credentials for authorized API calls
     *
     * @param aEmail, The users Google email
     */
    public static void setGoogleEmail(String aEmail)
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putString(lContext.getString(R.string.PREF_GOOGLE_EMAIL), aEmail);
        lEditor.apply();
    }

    /**
     * Get the users application layout preferences
     *
     * @return The users App layout preference
     * True = GridLayout
     * False = LinearLayout
     */
    public static boolean getLayoutFormat()
    {
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext)
                                .getBoolean(lContext.getString(R.string.PREF_APP_LAYOUT_IS_GRID), true);
    }

    /**
     * Set the users application layout preference
     *
     * @param aGrid, User preference for application layout
     *               True = GridLayout
     *               False = LinearLayout
     */
    public static void setLayoutFormat(boolean aGrid)
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putBoolean(lContext.getString(R.string.PREF_APP_LAYOUT_IS_GRID), aGrid);
        lEditor.apply();
    }

    /**
     * Get the users application theme preference
     *
     * @return The users  application theme preference
     * True = Light theme
     * False = Dark theme
     */
    public static boolean getLayoutTheme()
    {
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext)
                                .getBoolean(lContext.getString(R.string.PREF_APP_THEME_IS_LIGHT), false);
    }

    /**
     * Set the users application theme preference
     *
     * @param aLightTheme, User preference for application theme
     *                     True = Light theme
     *                     False = Dark theme
     */
    public static void setLayoutTheme(boolean aLightTheme)
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putBoolean(lContext.getString(R.string.PREF_APP_THEME_IS_LIGHT), aLightTheme);
        lEditor.apply();
    }

    /***
     * This function retrieves the current source.
     *
     * @return
     */
    public static String getSavedSource()
    {
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext)
                                .getString(lContext.getString(R.string.PREF_USER_SOURCE), MangaEnums.eSource.MangaJoy.name());
    }

    /***
     * This function sets the current source.
     *
     * @param aSource
     */
    public static void setSavedSource(String aSource)
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putString(lContext.getString(R.string.PREF_USER_SOURCE), aSource);
        lEditor.apply();
    }

    /***
     * This function returns the chapter vertical scroll setting.
     *
     * @return
     */
    public static boolean getChapterScrollVertical()
    {
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext)
                                .getBoolean(lContext.getString(R.string.PREF_CHAPTER_SCROLL_VERTICAL), false);
    }

    /***
     * This function sets the chapter vertical scroll setting.
     *
     * @param aVertical
     */
    public static void setChapterScrollVertical(boolean aVertical)
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putBoolean(lContext.getString(R.string.PREF_CHAPTER_SCROLL_VERTICAL), aVertical);
        lEditor.apply();
    }

    /***
     * This function returns the chapter screen orientation setting.
     *
     * @return true if LandScape, false otherwise
     */
    public static boolean getChapterScreenOrientation()
    {
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext)
                                .getBoolean(lContext.getString(R.string.PREF_CHAPTER_SCREEN_ORIENTATION), false);
    }

    /***
     * This function sets the chapter screen orientation setting.
     *
     * @param aLandscape
     */
    public static void setChapterScreenOrientation(boolean aLandscape)
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putBoolean(lContext.getString(R.string.PREF_CHAPTER_SCREEN_ORIENTATION), aLandscape);
        lEditor.apply();
    }

    /***
     * This function gets the in app logging status.
     *
     * @return true if logging, false otherwise
     */
    public static boolean getLoggingStatus()
    {
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext).getBoolean(lContext.getString(R.string.PREF_LOGGING_STATUS), true);
    }

    /***
     * This function sets the in app logging status.
     *
     * @param aLogging
     */
    public static void setLoggingStatus(boolean aLogging)
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putBoolean(lContext.getString(R.string.PREF_LOGGING_STATUS), aLogging);
        lEditor.apply();
    }

    /***
     * This function saves a set of in app logs to persist over multiple application launches.
     */
    public static void saveLogs()
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putStringSet(lContext.getString(R.string.PREF_SAVE_LOGS), new HashSet<>(MangaLogger.getLogs()));
        lEditor.apply();
    }

    /***
     * this function returns a set of in app logs to persist over multiple application launches.
     * @return
     */
    public static List<String> getLogs()
    {
        Context lContext = MFeedApplication.getInstance();
        Set lLogSet = PreferenceManager.getDefaultSharedPreferences(lContext)
                                       .getStringSet(lContext.getString(R.string.PREF_SAVE_LOGS), new HashSet<>(0));

        return new ArrayList<>(lLogSet);
    }


    /***
     * This function gets the in app logging status.
     *
     * @return true if logging, false otherwise
     */
    public static int getNovelTextSize()
    {
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext).getInt(lContext.getString(R.string.NOVEL_TEXT_SIZE), 0);
    }

    public static void setNovelTextSize(int aSize)
    {
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putInt(lContext.getString(R.string.NOVEL_TEXT_SIZE), aSize);
        lEditor.apply();
    }


}
