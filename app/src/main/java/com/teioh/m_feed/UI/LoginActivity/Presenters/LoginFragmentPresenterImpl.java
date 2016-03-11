package com.teioh.m_feed.UI.LoginActivity.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.teioh.m_feed.UI.LoginActivity.View.Fragments.SignupFragment;
import com.teioh.m_feed.UI.LoginActivity.View.Mappers.LoginFragmentMapper;
import com.teioh.m_feed.UI.MainActivity.View.MainActivity;

import butterknife.ButterKnife;


public class LoginFragmentPresenterImpl implements LoginFragmentPresenter {
    public final static String TAG = SignupFragmentPresenterImpl.class.getSimpleName();
    public final static String USERNAME_KEY = TAG + ":USERNAME";

    private LoginFragmentMapper mLoginFragmentMapper;

    public LoginFragmentPresenterImpl(LoginFragmentMapper map) {
        mLoginFragmentMapper = map;
    }

    @Override
    public void onSaveState(Bundle bundle, String username) {
        if(username != null && !username.equals("")){
            bundle.putString(USERNAME_KEY, username);
        }
    }

    @Override
    public void onRestoreState(Bundle bundle) {
        if(bundle.containsKey(USERNAME_KEY)){
            mLoginFragmentMapper.updateUsername(bundle.getString(USERNAME_KEY));
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(mLoginFragmentMapper);
    }

    @Override
    public void onPause() {
//        BusProvider.getInstance().unregister(mLoginFragmentMapper);
    }

    @Override
    public void onResume() {
//        BusProvider.getInstance().register(mLoginFragmentMapper);
    }

    @Override
    public void onSignupButton(String mUserName, String mPassword) {
        Fragment signupFragment = new SignupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME_KEY, mUserName);
        signupFragment.setArguments(bundle);
        ((FragmentActivity) mLoginFragmentMapper.getContext()).getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, signupFragment, SignupFragment.TAG).commit();
    }

    @Override
    public void onLoginbutton(String mUserName, String mPassword) {
        ParseUser.logInInBackground(mUserName, mPassword,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            if (mLoginFragmentMapper.getContext() != null) {
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

}
