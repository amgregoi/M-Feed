// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.MangaActivity.Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FImageDialogFragment$$ViewBinder<T extends com.teioh.m_feed.UI.MangaActivity.Fragments.FImageDialogFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689646, "field 'mImage'");
    target.mImage = finder.castView(view, 2131689646, "field 'mImage'");
  }

  @Override public void unbind(T target) {
    target.mImage = null;
  }
}
