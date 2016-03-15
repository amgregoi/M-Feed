package com.teioh.m_feed.UI.LoginActivity.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.LoginActivity.Presenters.LoginActivityPresenter;
import com.teioh.m_feed.UI.LoginActivity.Presenters.LoginActivityPresenterImpl;
import com.teioh.m_feed.UI.LoginActivity.Presenters.LoginFragmentPresenter;
import com.teioh.m_feed.UI.LoginActivity.Presenters.LoginFragmentPresenterImpl;
import com.teioh.m_feed.UI.LoginActivity.View.Mappers.LoginActivityMapper;
import com.teioh.m_feed.UI.LoginActivity.View.Mappers.LoginFragmentMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginActivityMapper {
    public final static String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.password) EditText password;
    @Bind(R.id.username) EditText username;

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
        username.clearFocus();
        String user = username.getText().toString();
        mLoginActivityPresenter.onSaveState(outState, user);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mLoginActivityPresenter.onDestroy();
    }

    @OnClick(R.id.signup) void onSignupButton() {
        mLoginActivityPresenter.onSignupButton(username.getText().toString(), password.getText().toString());
    }

    @OnClick(R.id.login) void onLoginButton() {
        mLoginActivityPresenter.onLoginbutton(username.getText().toString(), password.getText().toString());
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