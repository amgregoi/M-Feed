package com.teioh.m_feed.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.R;
import com.teioh.m_feed.WebSources.SourceType;

public class SharedPrefs {


    public static void initializePreferences(){
        Context lContext = MFeedApplication.getInstance();

        // Need to set storage preference per device
        SharedPreferences lSharedPrefs = PreferenceManager.getDefaultSharedPreferences(lContext);
        if (lSharedPrefs.getString(lContext.getString(R.string.PREF_STORAGE_LOCATION), null) == null) {
            SharedPreferences.Editor editor = lSharedPrefs.edit();
            editor.putString(lContext.getString(R.string.PREF_STORAGE_LOCATION), lContext.getFilesDir().getAbsolutePath());
            editor.commit();
        }
    }

    /**
     * Sets the users MyAnimeList(MAL) login credentials for authorized API calls
     *
     * @param aUsername, The users MyAnimeList Username
     * @param aPassword, The users MyAnimeList Password
     */
    public static void setMALCredential(String aUsername, String aPassword){
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putString(lContext.getString(R.string.PREF_MAL_USERNAME), aUsername);
        lEditor.putString(lContext.getString(R.string.PREF_MAL_PASSWORD), aPassword);
        lEditor.apply();
    }

    /**
     * Get the users MAL username
     *
     * @return The users MAL username
     */
    public static String getMALUsername(){
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext).getString(lContext.getString(R.string.PREF_MAL_USERNAME), "Guest (Sign in)");
    }

    /**
     * Get the users MAL password
     *
     * @return The users MAL password
     */
    public static String getMALPassword(){
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext).getString(lContext.getString(R.string.PREF_MAL_PASSWORD), null);
    }

    public static boolean isSignedIn(){
        if(getMALPassword() == null) return false;
        return true;
    }


    /**
     * Set the users application layout preference
     *
     * @param aGrid, User preference for application layout
     *                True = GridLayout
     *                False = LinearLayout
     */
    public static void setLayoutFormat(boolean aGrid){
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putBoolean(lContext.getString(R.string.PREF_APP_LAYOUT_IS_GRID), aGrid);
        lEditor.apply();
    }


    /**
     * Get the users application layout preferences
     *
     * @return The users App layout preference
     *                True = GridLayout
     *                False = LinearLayout
     */
    public static boolean getLayoutFormat(){
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext).getBoolean(lContext.getString(R.string.PREF_APP_LAYOUT_IS_GRID), true);
    }

    /**
     * Set the users application theme preference
     *
     * @param aLightTheme, User preference for application theme
     *                 True = Light theme
     *                 False = Dark theme
     */
    public static void setLayoutTheme(boolean aLightTheme){
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putBoolean(lContext.getString(R.string.PREF_APP_THEME_IS_LIGHT), aLightTheme);
        lEditor.apply();
    }

    /**
     * Get the users application theme preference
     *
     * @return The users  application theme preference
     *                 True = Light theme
     *                 False = Dark theme
     */
    public static boolean getLayoutTheme(){
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext).getBoolean(lContext.getString(R.string.PREF_APP_THEME_IS_LIGHT), false);
    }

    /***
     * TODO...
     *
     * @param aSource
     */
    public static void setSavedSource(String aSource){
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putString(lContext.getString(R.string.PREF_USER_SOURCE), aSource);
        lEditor.apply();
    }

    /***
     * TODO...
     *
     * @return
     */
    public static String getSavedSource(){
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext).getString(lContext.getString(R.string.PREF_USER_SOURCE), SourceType.MangaJoy.name());
    }

    /***
     * TODO...
     *
     * @param aVertical
     */
    public static void setChapterScrollVertical(boolean aVertical){
        Context lContext = MFeedApplication.getInstance();
        SharedPreferences.Editor lEditor = PreferenceManager.getDefaultSharedPreferences(lContext).edit();
        lEditor.putBoolean(lContext.getString(R.string.PREF_CHAPTER_SCROLL_VERTICAL), aVertical);
        lEditor.apply();
    }

    /***
     * TODO...
     *
     * @return
     */
    public static boolean getChapterScrollVertical(){
        Context lContext = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(lContext).getBoolean(lContext.getString(R.string.PREF_CHAPTER_SCROLL_VERTICAL), false);
    }



}
