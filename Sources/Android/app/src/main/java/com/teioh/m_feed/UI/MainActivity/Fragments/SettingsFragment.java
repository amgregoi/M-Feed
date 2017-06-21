package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
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

    @Bind(R.id.logging_toggle) ToggleButton mLoggingToggle;

    /***
     * This function creates and returns a new instance of the fragment.
     * @return
     */
    public static Fragment getnewInstance()
    {
        Fragment lDialog = new SettingsFragment();
        return lDialog;
    }

    /***
     * This function initializes the view of the fragment.
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
     * This function is called in the fragment lifecycle
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /***
     * This function intializes relavent parts of the layout based on saved SharedPrefs.
     */
    private void initializeLayout()
    {
        mLoggingToggle.setChecked(SharedPrefs.getLoggingStatus());
        MangaLogger.logInfo(TAG, "Finished initializing settings layout");

    }

    /***
     * This function performs the show logs item select.
     */
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

    /***
     * This function performs the clear logs item select.
     */
    @OnClick(R.id.clear_logs)
    public void onClearLogsClick()
    {
        launchYesNoDialog(R.string.logs, getString(R.string.settings_clear_logs), 99);
    }

    /***
     * This function launches the YesNo dialog to verify by the user an action should be executed.
     * @param aTitleRes
     * @param aMessage
     * @param aAction
     */
    private void launchYesNoDialog(int aTitleRes, String aMessage, int aAction)
    {
        DialogFragment newFragment = FYesNoDialog.getNewInstance(aTitleRes, aMessage, aAction, false);
        newFragment.setTargetFragment(this, 1);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    /***
     * This function performs the toggle logs item select.
     */
    @OnClick(R.id.toggle_logs)
    public void onToggleLogsClick()
    {
        boolean lNewStatus = !SharedPrefs.getLoggingStatus();
        SharedPrefs.setLoggingStatus(lNewStatus);
        mLoggingToggle.setChecked(lNewStatus);
        MangaLogger.makeToast("Toggling internal logger");
    }

    /***
     * This function performs the clear chapter cache item select.
     */
    @OnClick(R.id.reset_cached_chapters)
    public void onResetChaptersClick()
    {
        launchYesNoDialog(R.string.chapter_cache, getString(R.string.settings_chapter_cache), 0);
    }

    /***
     * This function performs the clear user library item select.
     */
    @OnClick(R.id.reset_followed_manga_pref)
    public void onResetLibraryClick()
    {
        launchYesNoDialog(R.string.library, getString(R.string.settings_library), 1);
    }

    /***
     * This function performs the delete all downloaded chapters item select.
     */
    @OnClick(R.id.reset_downloaded_chapters)
    public void onRemoveDownloadedChaptersClick()
    {
        launchYesNoDialog(R.string.downloaded_chapters, getString(R.string.settings_downloaded_chapters), 2);
    }

    /***
     * This function performs the contact us item select.
     */
    @OnClick(R.id.contact_us)
    public void onContactUsClick()
    {
        launchYesNoDialog(R.string.contact_us, getString(R.string.settings_contact_us), 3);
    }

    /***
     * This function performs various (above) operations based on the action ID specified.
     * @param aAction
     */
    @Override
    public void positive(int aAction)
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
                MangaLogger.logError(TAG, "positive", getString(R.string.method_not_implemented));
                MangaLogger.makeToast(getString(R.string.method_not_implemented));
                break;

            case 3://Contact Us
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                String aEmailList[] = {"teioh08@gmail.com"};
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MangaFeed Contact:");
                emailIntent.setType("plain/text");

                PackageManager packageManager = getActivity().getPackageManager();
                if (emailIntent.resolveActivity(packageManager) != null)
                {
                    startActivity(emailIntent);
                }
                else
                {
                    MangaLogger.logInfo(TAG, "There is no activity to support email intent");
                    MangaLogger.makeToast("No application available to send an email.");
                }

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

    /***
     * This function is called and logged when an action is declined by the user.
     * @param aAction
     */
    @Override
    public void negative(int aAction)
    {
        MangaLogger.logInfo(TAG, "No was selected for action (" + aAction + ")");
    }
}
