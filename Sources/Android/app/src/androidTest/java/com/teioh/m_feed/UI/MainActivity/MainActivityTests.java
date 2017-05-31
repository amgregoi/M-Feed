package com.teioh.m_feed.UI.MainActivity;


import android.support.annotation.IdRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Helper.FirstViewMatcher;
import com.teioh.m_feed.UI.MangaActivity.MangaActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.crypto.Mac;

import dalvik.annotation.TestTargetClass;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTests
{

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void recentFragmentItemSelect()
    {
        //Verify RecyclerView is displayed
        ViewInteraction lRecyclerView = onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));

        //Perform single click on an item in the RecyclerView
        lRecyclerView.perform(actionOnItemAtPosition(0, click()));

        //Verify a new activity is launched successfully
        intended(hasComponent(MangaActivity.class.getName()));
    }

    @Test
    public void textFilterSearch()
    {
        //Verifies RecyclerView is displayed
        ViewInteraction lRecyclerView = onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));

        //Verify search view text is empty
        ViewInteraction lSearchView = onView(allOf(withId(R.id.search_view), isDisplayed()));

        //Selects search view, enters text, and presses the enter/search key
        lSearchView.perform(click(), typeText("zz"), pressKey(KeyEvent.KEYCODE_ENTER));
    }

    @Test
    public void genreFilterDialog()
    {
        //Verifies RecycleView is displayed
        onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));

        //Retrieves view for filter button icon
        ViewInteraction lFilterButton = onView(withId(R.id.filter_view));

        //Performs click on filter button
        lFilterButton.perform(click());

        //Verifies filter dialog appears by search for its dialog title
        onView(withText("Filter Manga")).check(matches(isDisplayed()));
    }

    /***
     * This Test verifies the navigation drawer opens and closes successfully.
     */
    @Test
    public void navigationDrawerToggle()
    {
        //Verifies RecycleView is displayed
        onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));

        //Opens Navigation Drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        //Verifies drawer was opened by searching for Text in drawer
        onView(withText("Guest (Sign in)")).check(matches(isDisplayed()));

        //Closes Navigation Drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());

        //Verifies Drawer text is now gone
        onView(withText("Guest (Sign in)")).check(matches(not(isDisplayed())));

    }

    @Test
    public void navigationDrawerOptions()
    {
        //Verifies RecycleView is displayed
        onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));
        //Opens Navigation Drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        //TODO.. Finish

    }



}
