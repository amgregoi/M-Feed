package com.teioh.m_feed.UI.LoginActivity.View;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.LoginActivity.Presenters.LoginActivityPresenter;
import com.teioh.m_feed.UI.LoginActivity.Presenters.LoginActivityPresenterImpl;
import com.teioh.m_feed.UI.LoginActivity.View.Mappers.LoginActivityMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginActivityMapper {
    public final static String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.password) EditText mPasswordTextView;
    @Bind(R.id.username) EditText mUsernameTextView;

    private LoginActivityPresenter mLoginActivityPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragment);
        ButterKnife.bind(this);

        mLoginActivityPresenter = new LoginActivityPresenterImpl(this);

        if(savedInstanceState != null){
            mLoginActivityPresenter.onRestoreState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUsernameTextView.clearFocus();
        mLoginActivityPresenter.saveUsernameTransition(mUsernameTextView.getText().toString());
        mLoginActivityPresenter.onSaveState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mLoginActivityPresenter.onDestroy();
    }

    @OnClick(R.id.signup) void onSignupButton() {
        mLoginActivityPresenter.onSignupButton(mUsernameTextView.getText().toString(), mPasswordTextView.getText().toString());
    }

    @OnClick(R.id.login) void onLoginButton() {
        mLoginActivityPresenter.onLoginbutton(mUsernameTextView.getText().toString(), mPasswordTextView.getText().toString());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onLoginSuccess(){
        onBackPressed();
    }

    @Override
    public void onLoginFail(){
        Toast.makeText(LoginActivity.this, "Failed to verify username, please try again.", Toast.LENGTH_SHORT).show();
    }
}