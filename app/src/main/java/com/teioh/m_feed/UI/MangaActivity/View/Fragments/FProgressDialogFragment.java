package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.teioh.m_feed.R;

public class FProgressDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Set a theme on the dialog builder constructor!
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyCustomTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        return builder.setView(inflater.inflate(R.layout.multi_increment_dialog_fragment, null))
                .setPositiveButton("OK", (dialog, which) -> {
                    FProgressDialogFragment.this.getActivity().onActivityReenter(Activity.RESULT_OK, FProgressDialogFragment.this.getActivity().getIntent());
                }).create();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }
}