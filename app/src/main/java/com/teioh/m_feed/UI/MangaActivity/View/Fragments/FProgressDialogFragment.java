package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teioh.m_feed.R;

public class FProgressDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static DialogFragment getNewInstance(String aUrl){
        DialogFragment lFragment = new FProgressDialogFragment();
        return lFragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);

        return super.onCreateView(aInflater, aContainer, aSavedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle aSavedInstanceState) {
        // Set a theme on the dialog builder constructor!
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyCustomTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        return builder.setView(inflater.inflate(R.layout.manga_info_header_inc_dialog, null))
                .setPositiveButton("OK", (dialog, which) -> {
                    FProgressDialogFragment.this.getActivity().onActivityReenter(Activity.RESULT_OK, FProgressDialogFragment.this.getActivity().getIntent());
                }).create();
    }

    @Override
    public boolean onEditorAction(TextView lView, int aActionId, KeyEvent aEvent) {
        return false;
    }
}
