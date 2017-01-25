// Generated code from Butter Knife. Do not modify!
package com.teioh.m_feed.UI.MainActivity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.teioh.m_feed.UI.MainActivity.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689696, "field 'mSearchView'");
    target.mSearchView = finder.castView(view, 2131689696, "field 'mSearchView'");
    view = finder.findRequiredView(source, 2131689697, "field 'mFilterView'");
    target.mFilterView = finder.castView(view, 2131689697, "field 'mFilterView'");
    view = finder.findRequiredView(source, 2131689698, "field 'mActivityTitle'");
    target.mActivityTitle = finder.castView(view, 2131689698, "field 'mActivityTitle'");
    view = finder.findRequiredView(source, 2131689569, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131689569, "field 'mViewPager'");
    view = finder.findRequiredView(source, 2131689568, "field 'mTabLayout'");
    target.mTabLayout = finder.castView(view, 2131689568, "field 'mTabLayout'");
    view = finder.findRequiredView(source, 2131689694, "field 'mToolBar'");
    target.mToolBar = finder.castView(view, 2131689694, "field 'mToolBar'");
    view = finder.findRequiredView(source, 2131689586, "field 'mDrawerLayout'");
    target.mDrawerLayout = finder.castView(view, 2131689586, "field 'mDrawerLayout'");
    view = finder.findRequiredView(source, 2131689589, "field 'mDrawerList'");
    target.mDrawerList = finder.castView(view, 2131689589, "field 'mDrawerList'");
    view = finder.findRequiredView(source, 2131689588, "field 'mMultiActionMenu'");
    target.mMultiActionMenu = finder.castView(view, 2131689588, "field 'mMultiActionMenu'");
  }

  @Override public void unbind(T target) {
    target.mSearchView = null;
    target.mFilterView = null;
    target.mActivityTitle = null;
    target.mViewPager = null;
    target.mTabLayout = null;
    target.mToolBar = null;
    target.mDrawerLayout = null;
    target.mDrawerList = null;
    target.mMultiActionMenu = null;
  }
}
