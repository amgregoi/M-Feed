package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.Maps.Listeners;


public class FYesNoDialog extends DialogFragment
{
    public final static String TAG = FYesNoDialog.class.getSimpleName();
    public final static String TITLE_KEY = TAG + ":" + "TITLE";
    public final static String MESSAGE_KEY = TAG + ":" + "MESSAGE";
    public final static String ACTION_KEY = TAG + ":" + "ACTION";

    private Listeners.DialogYesNoListener mListener;

    /***
     * TODO..
     *
     * @param aTitleRes
     * @return
     */
    public static DialogFragment getNewInstance(int aTitleRes, String aMessage, int aActionId)
    {
        Bundle args = new Bundle();
        args.putInt(TITLE_KEY, aTitleRes);
        args.putString(MESSAGE_KEY, aMessage);
        args.putInt(ACTION_KEY, aActionId);

        FYesNoDialog lFragment = new FYesNoDialog();
        lFragment.setArguments(args);
        return lFragment;
    }

    /***
     * TODO..
     *
     * @param aSavedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle aSavedInstanceState)
    {
        int lTitle = getArguments().getInt(TITLE_KEY);
        String lMessage = getArguments().getString(MESSAGE_KEY);
        int lAction = getArguments().getInt(ACTION_KEY);

        AlertDialog.Builder lBuilder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
        return lBuilder.setTitle(lTitle)
                       .setMessage(lMessage)
                       .setNegativeButton("No", (arg0, arg1) ->
                               ((Listeners.DialogYesNoListener) getTargetFragment()).negative(lAction))
                       .setPositiveButton("Yes", (arg0, arg1) ->
                               ((Listeners.DialogYesNoListener) getTargetFragment()).positive(lAction))
                       .create();
    }
}

