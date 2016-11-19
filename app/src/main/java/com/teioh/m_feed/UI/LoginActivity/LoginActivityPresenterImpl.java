package com.teioh.m_feed.UI.LoginActivity;

import android.os.Bundle;

import com.teioh.m_feed.MAL_Models.verify_credentials;
import com.teioh.m_feed.Utils.MAL.MALApi;
import com.teioh.m_feed.Utils.MAL.MALService;
import com.teioh.m_feed.Utils.SharedPrefs;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivityPresenterImpl implements ILogin.ActivityPresenter
{
    public final static String TAG = LoginActivityPresenterImpl.class.getSimpleName();
    public final static String USERNAME_KEY = TAG + ":USERNAME";

    private ILogin.ActivityView mLoginActivityMapper;
    private String mUsername;
    MALService mMALService;

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
        mMALService = MALApi.createService(aUsername, aPassword);
        mMALService.verifyUserAccount(new Callback<verify_credentials>()
        {
            @Override
            public void success(verify_credentials credentials, Response response)
            {
                SharedPrefs.setMALCredential(aUsername, aPassword);
                mLoginActivityMapper.onLoginSuccess();
            }

            @Override
            public void failure(RetrofitError error)
            {
                mLoginActivityMapper.onLoginFail();
            }
        });
    }

    @Override
    public void saveUsernameTransition(String aUsername)
    {
        mUsername = aUsername;
    }

}
