package com.teioh.m_feed.UI.LoginActivity.Presenters.Presenters;


public interface LoginFragmentPresenter {

    void onSignupButton(String mUserName, String mPassword);

    void onLoginbutton(String mUserName, String mPassword);

    void onDestroyView();

    void onPause();

    void onResume();
}
