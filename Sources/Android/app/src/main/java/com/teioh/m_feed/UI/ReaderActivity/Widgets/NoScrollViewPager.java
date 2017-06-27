package com.teioh.m_feed.UI.ReaderActivity.Widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager
{

    private boolean mPagingEnabled = false;

    public NoScrollViewPager(Context aContext)
    {
        super(aContext);
    }

    public NoScrollViewPager(Context aContext, AttributeSet aAttributeSet)
    {
        super(aContext, aAttributeSet);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent aEvent)
    {
        return this.mPagingEnabled && super.onInterceptTouchEvent(aEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent aEvent)
    {
        return this.mPagingEnabled && super.onTouchEvent(aEvent);
    }

    public void setPagingEnabled(boolean aPagingEnabled)
    {
        this.mPagingEnabled = aPagingEnabled;
    }

    public void incrementCurrentItem()
    {
        int lPosition = getCurrentItem();
        if (lPosition != getAdapter().getCount() - 1)
        {
            setCurrentItem(lPosition + 1, true);
        }
    }

    public void decrememntCurrentItem()
    {
        int lPosition = getCurrentItem();
        if (lPosition != 0)
        {
            setCurrentItem(lPosition - 1, true);
        }
    }
}