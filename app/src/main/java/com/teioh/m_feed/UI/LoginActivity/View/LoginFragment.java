package com.teioh.m_feed.UI.LoginActivity.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.LoginActivity.Presenters.LoginFragmentPresenter;
import com.teioh.m_feed.UI.LoginActivity.Presenters.LoginFragmentPresenterImpl;
import com.teioh.m_feed.UI.LoginActivity.Presenters.Mappers.LoginFragmentMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements LoginFragmentMap {
    public final static String TAG = LoginFragment.class.getSimpleName();

    @Bind(R.id.password) EditText password;
    @Bind(R.id.username) EditText username;

    private LoginFragmentPresenter mLoginFragmentPresenter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, v);

        mLoginFragmentPresenter = new LoginFragmentPresenterImpl(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mLoginFragmentPresenter.onRestoreState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        username.clearFocus();
        String user = username.getText().toString();
        mLoginFragmentPresenter.onSaveState(outState, user);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mLoginFragmentPresenter.onDestroyView();
    }

    @OnClick(R.id.signup) void onSignupButton() {
        mLoginFragmentPresenter.onSignupButton(username.getText().toString(), password.getText().toString());
    }

    @OnClick(R.id.login) void onLoginButton() {
        mLoginFragmentPresenter.onLoginbutton(username.getText().toString(), password.getText().toString());
    }

    @Override
    public void updateUsername(String username) {
        this.username.setText(username);
    }
}