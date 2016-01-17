package com.teioh.m_feed.UI.LoginActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.teioh.m_feed.UI.LoginActivity.View.Mappers.LoginFragmentMapper;
import com.teioh.m_feed.UI.MainActivity.View.MainActivity;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;

import butterknife.ButterKnife;


public class SignupFragmentPresenterImpl implements SignupFragmentPresenter {
    public final static String TAG = SignupFragmentPresenterImpl.class.getSimpleName();
    public final static String USERNAME_KEY = TAG + ":USERNAME";

    private LoginFragmentMapper mSignupFragmentMapper;

    public SignupFragmentPresenterImpl(LoginFragmentMapper map) {
        mSignupFragmentMapper = map;
    }
    private String mUserName;

    @Override
    public void onSaveState(Bundle bundle, String username) {
        if(username != null || !username.equals("")){
            bundle.putString(USERNAME_KEY, username);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if(bundle.containsKey(USERNAME_KEY)){
            mSignupFragmentMapper.updateUsername(bundle.getString(USERNAME_KEY));
        }
    }

    @Override
    public void init(Bundle bundle) {
        if(bundle != null){
            mUserName = bundle.getString(LoginFragmentPresenterImpl.USERNAME_KEY);
        }else{
            mUserName = "";
        }

        mSignupFragmentMapper.updateUsername(mUserName);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mSignupFragmentMapper);
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(mSignupFragmentMapper);
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(mSignupFragmentMapper);
    }

    @Override
    public void onSignupButton(String mUserName, String mEmail, String mPassword) {
        if (mUserName.equals("") || mPassword.equals("") || mEmail.equals("")) {
            Toast.makeText(mSignupFragmentMapper.getContext(),
                    "Please complete the sign up form",
                    Toast.LENGTH_LONG).show();
        } else {
            ParseUser user = new ParseUser();
            user.setUsername(mUserName);
            user.setPassword(mPassword);
            user.signUpInBackground(new SignUpCallback() {


                /*
                 * prase error codes (e)
                 * 125 - invalid email address
                 * 200 - username missing
                 * 201 - password missing
                 * 202 - username taken
                 * 203 - email taken
                 * 204 - email missing
                 *
                 */
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(mSignupFragmentMapper.getContext(),
                                "Successfully Signed up, please log in.",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(mSignupFragmentMapper.getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mSignupFragmentMapper.getContext().startActivity(intent);
                        ((Fragment) mSignupFragmentMapper).getFragmentManager().popBackStackImmediate();

                    } else {
                        switch(e.getCode()){
                            case 125:
                                errorToastMessages("Invalid email address");
                                return;
                            case 200:
                                errorToastMessages("username missing");
                                return;
                            case 201:
                                errorToastMessages("password missing");
                                return;
                            case 202:
                                errorToastMessages("username taken");
                                return;
                            case 203:
                                errorToastMessages("email taken");
                                return;
                            case 204:
                                errorToastMessages("email missing");
                                return;
                            default:
                                errorToastMessages("Error signing up");
                                return;
                        }
                    }
                }
            });
        }
    }

    private void errorToastMessages(String message){
        Toast.makeText(mSignupFragmentMapper.getContext(),message, Toast.LENGTH_LONG).show();

    }
}
