package com.teioh.m_feed.UI.MangaActivity.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


public class FRemoveDialogFragment extends DialogFragment
{
    public final static String TAG = FRemoveDialogFragment.class.getSimpleName();
    public final static String TITLE_KEY = TAG + ":" + "TITLE";

    /***
     * TODO..
     *
     * @param title
     * @return
     */
    public static DialogFragment getNewInstance(int title)
    {
        FRemoveDialogFragment lFragment = new FRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TITLE_KEY, title);
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

        AlertDialog.Builder lBuilder = new AlertDialog.Builder(getContext());
        return lBuilder.setTitle(lTitle).setMessage("Are you sure you want to remove this?").setNegativeButton("No", (arg0, arg1) -> {
            getActivity().onActivityReenter(Activity.RESULT_CANCELED, null);
        })

                .setPositiveButton("Yes", (arg0, arg1) -> {
                    getActivity().onActivityReenter(Activity.RESULT_OK, null);
                }).create();
    }


}

