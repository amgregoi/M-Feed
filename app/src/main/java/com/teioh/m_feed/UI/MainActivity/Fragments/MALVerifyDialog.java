package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.teioh.m_feed.UI.Maps.Listeners;

public class MALVerifyDialog extends DialogFragment {
    public final static String TAG = MALVerifyDialog.class.getSimpleName();
    private Listeners.MALDialogListener mListener;

    public static DialogFragment getNewInstance() {
        MALVerifyDialog lFragment = new MALVerifyDialog();
        return lFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle aSavedInstanceState) {
        AlertDialog.Builder lBuilder = new AlertDialog.Builder(getActivity());
        return lBuilder
                .setTitle("MAL")
                .setMessage("Sign out of My Anime List?")
                .setNegativeButton("No", (arg0, arg1) -> {
                    getDialog().dismiss();
                })

                .setPositiveButton("Yes", (arg0, arg1) -> {
                    mListener.MALSignOut();
                    getDialog().dismiss();
                })
                .create();
    }

    @Override
    public void onAttach(Activity aActivity) {
        if (aActivity instanceof Listeners.MALDialogListener)
            mListener = (Listeners.MALDialogListener) aActivity;
        super.onAttach(aActivity);
    }
}
