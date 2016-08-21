package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teioh.m_feed.R;

public class SettingsFragment extends Fragment {
    public final static String TAG = SettingsFragment.class.getSimpleName();

    //TODO implement settings with shared prefs

    public static Fragment getnewInstance(){
        Fragment lDialog = new SettingsFragment();
        return lDialog;
    }

    @Override
    public View onCreateView(LayoutInflater aInflater, @Nullable ViewGroup aContainer, @Nullable Bundle aSavedInstanceState) {
        View lView = aInflater.inflate(R.layout.settings_fragment, aContainer, false);
        return lView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle aSave) {
        super.onActivityCreated(aSave);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
