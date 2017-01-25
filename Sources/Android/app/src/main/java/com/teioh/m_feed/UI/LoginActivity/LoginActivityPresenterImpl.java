package com.teioh.m_feed.UI.LoginActivity;

import android.os.Bundle;



public class LoginActivityPresenterImpl implements ILogin.ActivityPresenter
{
    public final static String TAG = LoginActivityPresenterImpl.class.getSimpleName();
    public final static String USERNAME_KEY = TAG + ":USERNAME";

    private ILogin.ActivityView mLoginActivityMapper;
    private String mUsername;

    public LoginActivityPresenterImpl(ILogin.ActivityView aMap)
    {
        mLoginActivityMapper = aMap;
    }


    @Override
    public void init(Bundle aBundle)
    {

    }

    @Override
    public void onSaveState(Bundle aSave)
    {
        if (mUsername != null && !mUsername.equals(""))
        {
            aSave.putString(USERNAME_KEY, mUsername);
        }
    }

    @Override
    public void onRestoreState(Bundle aRestore)
    {

    }

    @Override
    public void onDestroy()
    {

    }

    @Override
    public void onPause()
    {
    }

    @Override
    public void onResume()
    {
    }

    @Override
    public void onSignupButton(String aUsername, String aPassword)
    {

    }

    @Override
    public void onLoginbutton(String aUsername, String aPassword)
    {

    }

    @Override
    public void saveUsernameTransition(String aUsername)
    {
        mUsername = aUsername;
    }

}
