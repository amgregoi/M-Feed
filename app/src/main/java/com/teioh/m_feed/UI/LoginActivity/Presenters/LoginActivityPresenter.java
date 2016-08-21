package com.teioh.m_feed.UI.LoginActivity.Presenters;

import com.teioh.m_feed.UI.Maps.LifeCycleMap;

public interface LoginActivityPresenter extends LifeCycleMap {

    void onSignupButton(String aUsername, String aPassword);

    void onLoginbutton(String aUsername, String aPassword);

    void saveUsernameTransition(String aUsername);

}
