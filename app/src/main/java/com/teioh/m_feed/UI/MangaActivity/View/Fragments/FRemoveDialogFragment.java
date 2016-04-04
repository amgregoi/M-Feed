package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


public class FRemoveDialogFragment extends DialogFragment {
    public final static String TAG = FRemoveDialogFragment.class.getSimpleName();
    public final static String TITLE_KEY = TAG + ":" + "TITLE";

    public static DialogFragment getNewInstance(int title) {
        FRemoveDialogFragment fragment = new FRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TITLE_KEY, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(TITLE_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setTitle(title)
                .setMessage("Are you sure you want to remove this?")
                .setNegativeButton("No", (arg0, arg1) -> {
                    getActivity().onActivityReenter(Activity.RESULT_CANCELED, null);
                })

                .setPositiveButton("Yes", (arg0, arg1) -> {
                    getActivity().onActivityReenter(Activity.RESULT_OK, null);
                })
                .create();
    }


}

