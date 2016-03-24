package com.teioh.m_feed.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teioh.m_feed.MFeedApplication;

public class SharedPrefsUtil {
    final public static String MAL_USERNAME = "MAL_USERNAME";
    final public static String MAL_PASSWORD = "MAL_PASSWORD";
    final public static String APP_LAYOUT_IS_GRID = "APP_LAYOUT_IS_GRID";
    final public static String APP_THEME_IS_LIGHT = "APP_LAYOUT_IS_GRID";

    /**
     * Sets the users MyAnimeList(MAL) login credentials for authorized API calls
     *
     * @param username, The users MyAnimeList Username
     * @param password, The users MyAnimeList Password
     */
    public static void setMALCredential(String username, String password){
        Context context = MFeedApplication.getInstance();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(MAL_USERNAME, username);
        editor.putString(MAL_PASSWORD, password);
        editor.apply();
    }

    /**
     * Get the users MAL username
     *
     * @return The users MAL username
     */
    public static String getMALUsername(){
        Context context = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(context).getString(MAL_USERNAME, null);
    }

    /**
     * Get the users MAL password
     *
     * @return The users MAL password
     */
    public static String getMALPassword(){
        Context context = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(context).getString(MAL_PASSWORD, null);
    }


    /**
     * Set the users application layout preference
     *
     * @param isGrid, User preference for application layout
     *                True = GridLayout
     *                False = LinearLayout
     */
    public static void setLayoutFormat(boolean isGrid){
        Context context = MFeedApplication.getInstance();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(APP_LAYOUT_IS_GRID, isGrid);
        editor.apply();
    }


    /**
     * Get the users application layout preferences
     *
     * @return The users App layout preference
     *                True = GridLayout
     *                False = LinearLayout
     */
    public static boolean getLayoutFormat(){
        Context context = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(APP_LAYOUT_IS_GRID, true);
    }

    /**
     * Set the users application theme preference
     *
     * @param isLight, User preference for application theme
     *                 True = Light theme
     *                 False = Dark theme
     */
    public static void setLayoutTheme(boolean isLight){
        Context context = MFeedApplication.getInstance();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(APP_THEME_IS_LIGHT, isLight);
        editor.apply();
    }

    /**
     * Get the users application theme preference
     *
     * @return The users  application theme preference
     *                 True = Light theme
     *                 False = Dark theme
     */
    public static boolean getLayoutTheme(){
        Context context = MFeedApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(APP_THEME_IS_LIGHT, true);
    }




}
