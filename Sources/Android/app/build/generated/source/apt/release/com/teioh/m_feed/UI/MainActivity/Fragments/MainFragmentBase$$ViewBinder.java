// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainFragmentBase$$ViewBinder<T extends com.teioh.m_feed.UI.MainActivity.Fragments.MainFragmentBase> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689605, "field 'mGridView'");
    target.mGridView = finder.castView(view, 2131689605, "field 'mGridView'");
    view = finder.findRequiredView(source, 2131689606, "field 'mFastScroller'");
    target.mFastScroller = finder.castView(view, 2131689606, "field 'mFastScroller'");
  }

  @Override public void unbind(T target) {
    target.mGridView = null;
    target.mFastScroller = null;
  }
}
