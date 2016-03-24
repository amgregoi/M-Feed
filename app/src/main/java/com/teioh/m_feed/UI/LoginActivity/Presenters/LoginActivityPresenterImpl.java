package com.teioh.m_feed.UI.LoginActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.MAL_Models.verify_credentials;
import com.teioh.m_feed.UI.LoginActivity.View.Mappers.LoginActivityMapper;
import com.teioh.m_feed.Utils.MAL.MALApi;
import com.teioh.m_feed.Utils.MAL.MALService;
import com.teioh.m_feed.Utils.SharedPrefsUtil;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivityPresenterImpl implements LoginActivityPresenter {
    public final static String TAG = LoginActivityPresenterImpl.class.getSimpleName();
    public final static String USERNAME_KEY = TAG + ":USERNAME";

    private LoginActivityMapper mLoginActivityMapper;
    MALService mMALService;

    public LoginActivityPresenterImpl(LoginActivityMapper map) {
        mLoginActivityMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle, String username) {
        if(username != null && !username.equals("")){
            bundle.putString(USERNAME_KEY, username);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onSignupButton(String mUserName, String mPassword) {

    }

    @Override
    public void onLoginbutton(String mUserName, String mPassword) {
        mMALService = MALApi.createService(mUserName, mPassword);
        mMALService.verifyUserAccount(new Callback<verify_credentials>() {
            @Override
            public void success(verify_credentials credentials, Response response) {
                //add to shared prefs
//                Context context = MFeedApplication.getInstance();
//                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//                editor.putString("USER", mUserName);
//                editor.putString("PASS", mPassword);
//                editor.apply();
                SharedPrefsUtil.setMALCredential(mUserName, mPassword);
                mLoginActivityMapper.onLoginSuccess();
            }

            @Override
            public void failure(RetrofitError error) {
                //fail toast try again
                mLoginActivityMapper.onLoginFail();
            }
        });
    }

}
