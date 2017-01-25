// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SettingsFragment$$ViewBinder<T extends com.teioh.m_feed.UI.MainActivity.Fragments.SettingsFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689690, "method 'onShowLogsClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onShowLogsClick();
        }
      });
    view = finder.findRequiredView(source, 2131689691, "method 'onClearLogsClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClearLogsClick();
        }
      });
    view = finder.findRequiredView(source, 2131689692, "method 'onToggleLogsClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onToggleLogsClick();
        }
      });
  }

  @Override public void unbind(T target) {
  }
}
