// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.MainActivity.Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FilterDialogFragment$$ViewBinder<T extends com.teioh.m_feed.UI.MainActivity.Fragments.FilterDialogFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689600, "field 'mGenreGridView'");
    target.mGenreGridView = finder.castView(view, 2131689600, "field 'mGenreGridView'");
    view = finder.findRequiredView(source, 2131689602, "field 'mSearchButton'");
    target.mSearchButton = finder.castView(view, 2131689602, "field 'mSearchButton'");
    view = finder.findRequiredView(source, 2131689603, "field 'mCancelButton'");
    target.mCancelButton = finder.castView(view, 2131689603, "field 'mCancelButton'");
    view = finder.findRequiredView(source, 2131689604, "field 'mClearButton'");
    target.mClearButton = finder.castView(view, 2131689604, "field 'mClearButton'");
  }

  @Override public void unbind(T target) {
    target.mGenreGridView = null;
    target.mSearchButton = null;
    target.mCancelButton = null;
    target.mClearButton = null;
  }
}
