package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.LoggingActivity;
import com.teioh.m_feed.Utils.MFDBHelper;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment
{
    public final static String TAG = SettingsFragment.class.getSimpleName();

    //TODO implement settings with shared prefs
    @Bind(R.id.logging_toggle) ToggleButton mLoggingToggle;

    /***
     * TODO..
     * @return
     */
    public static Fragment getnewInstance()
    {
        Fragment lDialog = new SettingsFragment();
        return lDialog;
    }

    /***
     * TODO..
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState)
    {
        View lView = aInflater.inflate(R.layout.settings_fragment, aContainer, false);
        ButterKnife.bind(this, lView);
        return lView;
    }

    /***
     * TODO..
     * @param aSave
     */
    @Override
    public void onActivityCreated(@Nullable Bundle aSave)
    {
        super.onActivityCreated(aSave);
    }

    /***
     * TODO..
     */
    @Override
    public void onResume()
    {
        super.onResume();
    }

    /***
     * TODO..
     */
    @Override
    public void onPause()
    {
        super.onPause();
    }

    /***
     * TODO..
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.show_logs)
    public void onShowLogsClick(){
        //open logs fragment
        if(SharedPrefs.getLoggingStatus())
        {
            Intent lLogActivity = LoggingActivity.getNewInstance(getContext());
            startActivity(lLogActivity);
        }

    }

    @OnClick(R.id.clear_logs)
    public void onClearLogsClick(){
        MangaLogger.clearLogs();
        MangaLogger.makeToast("Clearing logs");
    }

    @OnClick(R.id.toggle_logs)
    public void onToggleLogsClick(){
        SharedPrefs.setLoggingStatus(!SharedPrefs.getLoggingStatus());
        mLoggingToggle.setChecked(SharedPrefs.getLoggingStatus());
        MangaLogger.makeToast("Toggling internal logger");
    }

    @OnClick(R.id.reset_downloaded_chapters)
    public void onRemoveDownloadedChaptersClick(){
        MangaLogger.logError(TAG, "onRemoveDownloadedChaptersClick", "Method not implemented");
        MangaLogger.makeToast("Method not implemented");
    }

    @OnClick(R.id.reset_cached_chapters)
    public void onResetChaptersClick(){
        MangaLogger.logError(TAG, "onResetChaptersClick", "WATCH FOR SLOW PERFORMANCES, MAY NEED TO MOVE OFF MAIN THREAD");
        MFDBHelper.getInstance().resetCachedChapters();
        MangaLogger.makeToast("Clearing chapter cache");
    }

    @OnClick(R.id.reset_followed_manga_pref)
    public void onResetLibraryClick(){
        MangaLogger.logError(TAG, "onResetLibraryClick", "WATCH FOR SLOW PERFORMANCES, MAY NEED TO MOVE OFF MAIN THREAD");
        MFDBHelper.getInstance().resetLibrary();
        MangaLogger.makeToast("Resetting user library");
    }

    @OnClick(R.id.contact_us)
    public void onContactUsClick(){
        MangaLogger.logError(TAG, "onContactUsClick", "Method not implemented");
        MangaLogger.makeToast("Method not implemented");
    }
}
