package com.teioh.m_feed.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.teioh.m_feed.R;

public class LoginFragment extends Fragment {
    // Declare Variables
    Button loginbutton;
    Button signup;
    String usernametxt;
    String passwordtxt;
    EditText password;
    EditText username;

    /**
     * Called when the activity is first created.
     */
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.login_fragment, container, false);

        username = (EditText) v.findViewById(R.id.username);
        password = (EditText) v.findViewById(R.id.password);

        loginbutton = (Button) v.findViewById(R.id.login);
        signup = (Button) v.findViewById(R.id.signup);

        loginbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();

                // verify login
                ParseUser.logInInBackground(usernametxt, passwordtxt,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    if (v != null) {
                                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                    }
                                    getFragmentManager().popBackStackImmediate();
                                } else {
                                    Toast.makeText(
                                            v.getContext(),
                                            "No such user exist, please signup",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        // move this to a signup fragment? or leave it? decide later
        // Sign up Button Click Listener
        signup.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();

                if (usernametxt.equals("") && passwordtxt.equals("")) {
                    Toast.makeText(v.getContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(usernametxt);
                    user.setPassword(passwordtxt);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(v.getContext(),
                                        "Successfully Signed up, please log in.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(v.getContext(),
                                        "Sign up Error", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });

        return v;
    }

}