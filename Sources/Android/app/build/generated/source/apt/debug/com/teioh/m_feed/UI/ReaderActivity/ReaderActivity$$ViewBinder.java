// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.ReaderActivity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReaderActivity$$ViewBinder<T extends com.teioh.m_feed.UI.ReaderActivity.ReaderActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689569, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131689569, "field 'mViewPager'");
    view = finder.findRequiredView(source, 2131689661, "field 'mToolbarHeader'");
    target.mToolbarHeader = finder.castView(view, 2131689661, "field 'mToolbarHeader'");
    view = finder.findRequiredView(source, 2131689662, "field 'mToolbarHeader2'");
    target.mToolbarHeader2 = finder.castView(view, 2131689662, "field 'mToolbarHeader2'");
    view = finder.findRequiredView(source, 2131689677, "field 'mVerticalScrollButton' and method 'onVerticalScrollToggle'");
    target.mVerticalScrollButton = finder.castView(view, 2131689677, "field 'mVerticalScrollButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onVerticalScrollToggle();
        }
      });
    view = finder.findRequiredView(source, 2131689663, "field 'mToolbarFooter'");
    target.mToolbarFooter = finder.castView(view, 2131689663, "field 'mToolbarFooter'");
    view = finder.findRequiredView(source, 2131689678, "field 'mChapterTitle'");
    target.mChapterTitle = finder.castView(view, 2131689678, "field 'mChapterTitle'");
    view = finder.findRequiredView(source, 2131689611, "field 'mMangaTitle'");
    target.mMangaTitle = finder.castView(view, 2131689611, "field 'mMangaTitle'");
    view = finder.findRequiredView(source, 2131689671, "field 'mCurrentPage'");
    target.mCurrentPage = finder.castView(view, 2131689671, "field 'mCurrentPage'");
    view = finder.findRequiredView(source, 2131689673, "field 'mEndPage'");
    target.mEndPage = finder.castView(view, 2131689673, "field 'mEndPage'");
    view = finder.findRequiredView(source, 2131689668, "method 'onSkipPreviousClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSkipPreviousClick();
        }
      });
    view = finder.findRequiredView(source, 2131689669, "method 'onBackPageClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onBackPageClick();
        }
      });
    view = finder.findRequiredView(source, 2131689675, "method 'onRefreshClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onRefreshClicked();
        }
      });
    view = finder.findRequiredView(source, 2131689667, "method 'onForwardPageClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onForwardPageClick();
        }
      });
    view = finder.findRequiredView(source, 2131689666, "method 'onSkipForwardClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSkipForwardClick();
        }
      });
    view = finder.findRequiredView(source, 2131689676, "method 'onScreenOrientClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onScreenOrientClick();
        }
      });
  }

  @Override public void unbind(T target) {
    target.mViewPager = null;
    target.mToolbarHeader = null;
    target.mToolbarHeader2 = null;
    target.mVerticalScrollButton = null;
    target.mToolbarFooter = null;
    target.mChapterTitle = null;
    target.mMangaTitle = null;
    target.mCurrentPage = null;
    target.mEndPage = null;
  }
}
