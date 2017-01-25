// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.ReaderActivity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ChapterFragment$$ViewBinder<T extends com.teioh.m_feed.UI.ReaderActivity.ChapterFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689569, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131689569, "field 'mViewPager'");
  }

  @Override public void unbind(T target) {
    target.mViewPager = null;
  }
}
