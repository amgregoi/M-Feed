package com.teioh.m_feed.UI.MainActivity.Helper;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class FirstViewMatcher extends BaseMatcher<View>
{


    public static boolean mMatchedBefore = false;

    public FirstViewMatcher()
    {
        mMatchedBefore = false;
    }

    @Override
    public boolean matches(Object aObject)
    {
        if (RecyclerView.class.getSimpleName().equals(aObject.getClass().getSimpleName()))
        {
            mMatchedBefore = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void describeTo(Description aDescription)
    {
        aDescription.appendText(" is the first view that comes along ");
    }

    @Factory
    public static <T> Matcher<View> firstView()
    {
        return new FirstViewMatcher();
    }
}