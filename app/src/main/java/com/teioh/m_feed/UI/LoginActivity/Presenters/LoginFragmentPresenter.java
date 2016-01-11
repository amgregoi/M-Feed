package com.teioh.m_feed.UI.LoginActivity.Presenters;


import android.os.Bundle;

public interface LoginFragmentPresenter {

    void onSaveState(Bundle bundle, String username);

    void onRestoreState(Bundle bundle);

    void onDestroyView();

    void onPause();

    void onResume();

    void onSignupButton(String mUserName, String mPassword);

    void onLoginbutton(String mUserName, String mPassword);

}
