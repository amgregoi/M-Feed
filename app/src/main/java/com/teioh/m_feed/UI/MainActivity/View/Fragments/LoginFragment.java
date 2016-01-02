package com.teioh.m_feed.UI.MainActivity.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.teioh.m_feed.UI.MainActivity.Presenters.LoginPresenter;
import com.teioh.m_feed.UI.MainActivity.Presenters.LoginPresenterImpl;
import com.teioh.m_feed.UI.MainActivity.Presenters.Mappers.LoginFragmentMap;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements LoginFragmentMap {
    @Bind(R.id.password) EditText password;
    @Bind(R.id.username) EditText username;

    private LoginPresenter mLoginPresenter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, v);

        mLoginPresenter = new LoginPresenterImpl(this);
        return v;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mLoginPresenter.butterKnifeUnbind();
    }

    @OnClick(R.id.signup) void onSignupButton(){
        mLoginPresenter.onSignupButton(username.getText().toString(), password.getText().toString());
    }

    @OnClick(R.id.login) void onLoginButton(){
        mLoginPresenter.onLoginbutton(username.getText().toString(), password.getText().toString());
    }

}