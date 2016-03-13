package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


public class FRemoveDialogFragment extends DialogFragment {

    public static FRemoveDialogFragment newInstance(int title) {
        FRemoveDialogFragment frag = new FRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

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

