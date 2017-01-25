// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.LoginActivity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginActivity$$ViewBinder<T extends com.teioh.m_feed.UI.LoginActivity.LoginActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689582, "field 'mPasswordTextView'");
    target.mPasswordTextView = finder.castView(view, 2131689582, "field 'mPasswordTextView'");
    view = finder.findRequiredView(source, 2131689581, "field 'mUsernameTextView'");
    target.mUsernameTextView = finder.castView(view, 2131689581, "field 'mUsernameTextView'");
    view = finder.findRequiredView(source, 2131689584, "method 'onSignupButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSignupButton();
        }
      });
    view = finder.findRequiredView(source, 2131689583, "method 'onLoginButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLoginButton();
        }
      });
  }

  @Override public void unbind(T target) {
    target.mPasswordTextView = null;
    target.mUsernameTextView = null;
  }
}
