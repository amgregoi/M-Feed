// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class RecentFragment$$ViewBinder<T extends com.teioh.m_feed.UI.MainActivity.Fragments.RecentFragment> extends com.teioh.m_feed.UI.MainActivity.Fragments.MainFragmentBase$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131689607, "field 'mSwipeContainer'");
    target.mSwipeContainer = finder.castView(view, 2131689607, "field 'mSwipeContainer'");
    view = finder.findRequiredView(source, 2131689608, "field 'mWifiView'");
    target.mWifiView = finder.castView(view, 2131689608, "field 'mWifiView'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.mSwipeContainer = null;
    target.mWifiView = null;
  }
}
