package com.teioh.m_feed.UI.MainActivity.Presenters;


public interface LoginPresenter {

    void onSignupButton(String mUserName, String mPassword);

    void onLoginbutton(String mUserName, String mPassword);

    void butterKnifeUnbind();
}
