package com.teioh.m_feed.UI.LoginActivity.Presenters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.teioh.m_feed.UI.LoginActivity.Presenters.Mappers.LoginFragmentMap;
import com.teioh.m_feed.UI.MainActivity.View.MainActivity;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;

import butterknife.ButterKnife;


public class LoginFragmentPresenterImpl implements LoginFragmentPresenter {

    private LoginFragmentMap mLoginFragmentMapper;

    public LoginFragmentPresenterImpl(LoginFragmentMap map) {
        mLoginFragmentMapper = map;
    }

    @Override
    public void onSignupButton(String mUserName, String mPassword) {
        if (mUserName.equals("") || mPassword.equals("")) {
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
                            if(mLoginFragmentMapper.getContext() != null) {
                                Intent intent = new Intent(mLoginFragmentMapper.getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mLoginFragmentMapper.getContext().startActivity(intent);
                                ((Fragment) mLoginFragmentMapper).getFragmentManager().popBackStackImmediate();
                            }

                        } else {
                            Toast.makeText(
                                    mLoginFragmentMapper.getContext(),
                                    "No such user exist, please signup",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mLoginFragmentMapper);
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(mLoginFragmentMapper);
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(mLoginFragmentMapper);
    }
}
