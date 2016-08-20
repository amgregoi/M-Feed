package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.teioh.m_feed.UI.Maps.Listeners;

public class MALVerifyDialog extends DialogFragment {
    public final static String TAG = MALVerifyDialog.class.getSimpleName();
    private Listeners.MALDialogListener listener;

    public static DialogFragment getNewInstance() {
        MALVerifyDialog fragment = new MALVerifyDialog();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("MAL")
                .setMessage("Sign out of My Anime List?")
                .setNegativeButton("No", (arg0, arg1) -> {
                    getDialog().dismiss();
                })

                .setPositiveButton("Yes", (arg0, arg1) -> {
                    listener.MALSignOut();
                    getDialog().dismiss();
                })
                .create();
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof Listeners.MALDialogListener)
            listener = (Listeners.MALDialogListener) activity;
        super.onAttach(activity);
    }
}
