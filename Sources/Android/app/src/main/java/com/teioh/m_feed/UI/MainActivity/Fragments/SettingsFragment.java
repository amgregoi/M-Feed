package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.LoggingActivity;
import com.teioh.m_feed.UI.Maps.Listeners;
import com.teioh.m_feed.Utils.MangaDB;
import com.teioh.m_feed.Utils.MangaLogger;
import com.teioh.m_feed.Utils.SharedPrefs;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment implements Listeners.DialogYesNoListener
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

        initializeLayout();

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
    public void onShowLogsClick()
    {
        //open logs fragment
        if (SharedPrefs.getLoggingStatus())
        {
            Intent lLogActivity = LoggingActivity.getNewInstance(getContext());
            startActivity(lLogActivity);
        }

    }

    @OnClick(R.id.clear_logs)
    public void onClearLogsClick()
    {
        launchYesNoDialog(R.string.logs, "Are you sure you want to clear the logs?", 99);
    }

    @OnClick(R.id.toggle_logs)
    public void onToggleLogsClick()
    {
        boolean lNewStatus = !SharedPrefs.getLoggingStatus();
        SharedPrefs.setLoggingStatus(lNewStatus);
        mLoggingToggle.setChecked(lNewStatus);
        MangaLogger.makeToast("Toggling internal logger");
    }

    @OnClick(R.id.reset_cached_chapters)
    public void onResetChaptersClick()
    {
        launchYesNoDialog(R.string.chapter_cache, "Are you sure you want to reset your chapter history?", 0);
    }

    @OnClick(R.id.reset_followed_manga_pref)
    public void onResetLibraryClick()
    {
        launchYesNoDialog(R.string.library, "Are you sure you want to clear your library?", 1);
    }

    @OnClick(R.id.reset_downloaded_chapters)
    public void onRemoveDownloadedChaptersClick()
    {
        launchYesNoDialog(R.string.downloaded_chapters, "Are you sure you want to delete all downloaded chapters?", 2);
    }

    @OnClick(R.id.contact_us)
    public void onContactUsClick()
    {
        launchYesNoDialog(R.string.contact_us, "Go ahead to contact us?", 3);
    }

    private void initializeLayout()
    {
        mLoggingToggle.setChecked(SharedPrefs.getLoggingStatus());
        MangaLogger.logInfo(TAG, "initializeLayout", "Finished initializing settings layout");

    }

    @Override public void positive(int aAction)
    {
        switch (aAction)
        {
            case 0://Clear Chapter Cache
                MangaLogger.logError(TAG, "positive", "WATCH FOR SLOW PERFORMANCES, MAY NEED TO MOVE OFF MAIN THREAD");
                MangaDB.getInstance().resetCachedChapters();
                MangaLogger.makeToast("Clearing chapter cache");
                break;

            case 1://Clear User Library
                //TODO... definitely need to move off main thread, and reset Main Activity reviews
                MangaLogger.logError(TAG, "positive", "WATCH FOR SLOW PERFORMANCES, MAY NEED TO MOVE OFF MAIN THREAD");
                MangaDB.getInstance().resetLibrary();
                MangaLogger.makeToast("Resetting user library");
                break;

            case 2: //Remove Downloaded Chapters
                MangaLogger.logError(TAG, "positive", "Method not implemented");
                MangaLogger.makeToast("Method not implemented");
                break;

            case 3://Contact Us
                MangaLogger.logError(TAG, "positive", "Method not implemented");
                MangaLogger.makeToast("Method not implemented");
                break;

            case 99://Logs
                MangaLogger.clearLogs();
                MangaLogger.makeToast("Clearing logs");
                break;

            default:
                MangaLogger.logError(TAG, "positive", "Action not implemented");
                MangaLogger.makeToast("Action not implemented");

        }
    }

    @Override public void negative(int aAction)
    {
        MangaLogger.makeToast("NEGATIVE");

    }

    private void launchYesNoDialog(int aTitleRes, String aMessage, int aAction)
    {
        DialogFragment newFragment = FYesNoDialog.getNewInstance(aTitleRes, aMessage, aAction);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
}
