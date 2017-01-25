package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.LoggingActivity;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment
{
    public final static String TAG = SettingsFragment.class.getSimpleName();

    //TODO implement settings with shared prefs

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
    };

    @OnClick(R.id.toggle_logs)
    public void onToggleLogsClick(){
        SharedPrefs.setLoggingStatus(!SharedPrefs.getLoggingStatus());
    }
}
