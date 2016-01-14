package com.teioh.m_feed.UI.ReaderActivity.View.Widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {

    private boolean isPagingEnabled = false;

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    public void incrementCurrentItem(){
        int position = getCurrentItem();
        if (position != getAdapter().getCount() - 1) {
            setCurrentItem(position + 1, true);
        }
    }

    public void decrememntCurrentItem(){
        int position = getCurrentItem();
        if (position != 0) {
            setCurrentItem(position - 1, true);
        }
    }
}