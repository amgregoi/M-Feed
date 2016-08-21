package com.teioh.m_feed.UI.LoginActivity;

import com.teioh.m_feed.UI.Maps.BaseContextMap;
import com.teioh.m_feed.UI.Maps.LifeCycleMap;

/**
 * Created by amgregoi on 8/21/16.
 */
public interface ILogin {
    interface ActivityView extends BaseContextMap {

        void onLoginSuccess();

        void onLoginFail();
    }

    interface ActivityModel{

    }

    interface ActivityPresenter extends LifeCycleMap {

        void onSignupButton(String aUsername, String aPassword);

        void onLoginbutton(String aUsername, String aPassword);

        void saveUsernameTransition(String aUsername);
    }
}

