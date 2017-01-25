// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.MangaActivity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MangaActivity$$ViewBinder<T extends com.teioh.m_feed.UI.MangaActivity.MangaActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689698, "field 'mActivityTitle'");
    target.mActivityTitle = finder.castView(view, 2131689698, "field 'mActivityTitle'");
    view = finder.findRequiredView(source, 2131689694, "field 'mToolBar'");
    target.mToolBar = finder.castView(view, 2131689694, "field 'mToolBar'");
    view = finder.findRequiredView(source, 2131689607, "field 'mSwipeRefresh'");
    target.mSwipeRefresh = finder.castView(view, 2131689607, "field 'mSwipeRefresh'");
    view = finder.findRequiredView(source, 2131689609, "field 'mChapterList' and method 'onItemClick'");
    target.mChapterList = finder.castView(view, 2131689609, "field 'mChapterList'");
    ((android.widget.AdapterView<?>) view).setOnItemClickListener(
      new android.widget.AdapterView.OnItemClickListener() {
        @Override public void onItemClick(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.onItemClick(p0, p1, p2);
        }
      });
    view = finder.findRequiredView(source, 2131689610, "field 'mFailedToLoad'");
    target.mFailedToLoad = finder.castView(view, 2131689610, "field 'mFailedToLoad'");
    view = finder.findRequiredView(source, 2131689699, "method 'orderButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.orderButton(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.mActivityTitle = null;
    target.mToolBar = null;
    target.mSwipeRefresh = null;
    target.mChapterList = null;
    target.mFailedToLoad = null;
  }
}
