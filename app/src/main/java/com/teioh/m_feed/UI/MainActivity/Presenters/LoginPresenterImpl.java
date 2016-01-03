package com.teioh.m_feed.UI.MainActivity.Presenters;

import android.app.Fragment;
import android.content.Context;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.LoginFragmentMap;

import butterknife.ButterKnife;


public class LoginPresenterImpl implements LoginPresenter {

    private LoginFragmentMap mLoginFragmentMapper;

    public LoginPresenterImpl(LoginFragmentMap map) {
        mLoginFragmentMapper = map;
    }

    @Override
    public void onSignupButton(String mUserName, String mPassword) {
        if (mUserName.equals("") && mPassword.equals("")) {
            Toast.makeText(mLoginFragmentMapper.getContext(),
                    "Please complete the sign up form",
                    Toast.LENGTH_LONG).show();

        } else {
            ParseUser user = new ParseUser();
            user.setUsername(mUserName);
            user.setPassword(mPassword);
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(mLoginFragmentMapper.getContext(),
                                "Successfully Signed up, please log in.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mLoginFragmentMapper.getContext(),
                                "Sign up Error", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        }
    }

    @Override
    public void onLoginbutton(String mUserName, String mPassword) {
        ParseUser.logInInBackground(mUserName, mPassword,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            ((Fragment) mLoginFragmentMapper).getFragmentManager().popBackStackImmediate();
                        } else {
//                            Toast.makeText(
//                                    mLoginFragmentMapper.getContext(),
//                                    "No such user exist, please signup",
//                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mLoginFragmentMapper);

    }
}
