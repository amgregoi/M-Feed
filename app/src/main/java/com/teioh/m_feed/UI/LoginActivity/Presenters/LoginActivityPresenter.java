package com.teioh.m_feed.UI.LoginActivity.Presenters;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface LoginActivityPresenter extends LifeCycleMap {

    void onSignupButton(String mUserName, String mPassword);

    void onLoginbutton(String mUserName, String mPassword);

    void saveUsernameTransition(String user);

}
