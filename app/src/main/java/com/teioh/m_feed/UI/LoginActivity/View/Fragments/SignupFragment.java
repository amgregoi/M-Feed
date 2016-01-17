package com.teioh.m_feed.UI.LoginActivity.View.Fragments;

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
import com.teioh.m_feed.UI.LoginActivity.Presenters.SignupFragmentPresenter;
import com.teioh.m_feed.UI.LoginActivity.Presenters.SignupFragmentPresenterImpl;
import com.teioh.m_feed.UI.LoginActivity.View.Mappers.LoginFragmentMapper;
import com.teioh.m_feed.UI.LoginActivity.View.Mappers.SignupFragmentMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupFragment extends Fragment implements LoginFragmentMapper {
    public final static String TAG = SignupFragment.class.getSimpleName();

    @Bind(R.id.password) EditText password;
    @Bind(R.id.username) EditText username;
    @Bind(R.id.email) EditText email;

    private SignupFragmentPresenter mSignupFragmentPresenter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.singup_fragment, container, false);
        ButterKnife.bind(this, v);

        mSignupFragmentPresenter = new SignupFragmentPresenterImpl(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mSignupFragmentPresenter.onRestoreState(savedInstanceState);
        }
        mSignupFragmentPresenter.init(getArguments());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        username.clearFocus();
        String user = username.getText().toString();
        mSignupFragmentPresenter.onSaveState(outState, user);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mSignupFragmentPresenter.onDestroyView();
    }

    @OnClick(R.id.signup) void onSignupButton() {
        mSignupFragmentPresenter.onSignupButton(username.getText().toString(), email.getText().toString(), password.getText().toString());
    }

    @Override
    public void updateUsername(String username) {
        this.username.setText(username);
    }
}