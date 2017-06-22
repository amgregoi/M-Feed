package com.teioh.m_feed.UI.MainActivity;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.util.Log;
import android.view.View;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.MangaActivity;
import com.teioh.m_feed.UI.ReaderActivity.ReaderActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;


public class ReaderActivityTests
{
    private final static String TAG = ReaderActivityTests.class.getSimpleName();

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    /***
     * This Test verifies the application can skip and go back a chapter from the currently selected chapter
     * while in the reader activity.
     */
    @Test
    public void testChapterForwardAndPrevious()
    {
        open("Naruto");

        onView(withId(R.id.skipPreviousButton)).perform(customClickAction());

        onView(withId(R.id.skipForwardButton)).perform(customClickAction());
    }

    /***
     * This Test verifies the application can toggle the set screen orientation in the reader activity.
     */
    @Test
    public void testScreenOrientation()
    {
        open("Naruto");
        //Toggles screen orientation button
        onView(withId(R.id.screen_orient_button)).perform(click());

        //Toggles screen orientation back
        onView(withId(R.id.screen_orient_button)).perform(click());

    }

    /***
     * This Test verifies the application can toggle the vertical scroll and swipe accordingly to get to
     * the next page.
     */
    @Test
    public void testToggleVerticalScroll()
    {
        open("Naruto");

        //Toggle vertical scroll
        onView(allOf(withId(R.id.vertical_scroll_toggle), isDisplayed())).perform(click());

        //Perform swipe up while vertical scroll is active
        onView(allOf(withId(R.id.reader_pager), isDisplayed())).perform(swipeUp());

        //Toggle vertical scroll off
        onView(allOf(withId(R.id.vertical_scroll_toggle), isDisplayed())).perform(click());

        //Perform left swipe while vertical scroll is off
        onView(allOf(withId(R.id.reader_pager), isDisplayed())).perform(swipeLeft());
    }

    /***
     * This Test verifies the application can refresh the currently selected chapter.
     */
    @Test
    public void testRefreshChapter()
    {
        open("Naruto");

        onView(withId(R.id.refresh_button)).perform(click());
    }

    /***
     * This Test verifies the application can go forward and backward a page while in the reader activity.
     */
    @Test
    public void testPageForwardAndBack()
    {
        open("Naruto");

        onView(withId(R.id.forwardPageButton)).perform(click());

        onView(withId(R.id.backPageButton)).perform(click());
    }

    /***
     * This function returns a view action that ignore espressos 90% of view visible requirement
     * @return returns the specific ViewAction
     */
    private ViewAction customClickAction()
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints()
            {
                return isEnabled(); // no constraints, they are checked above
            }

            @Override
            public String getDescription()
            {
                return "click plus button";
            }

            @Override
            public void perform(UiController uiController, View view)
            {
                view.performClick();
            }
        };
    }

    /***
     * This function opens a pre-defined Manga item (Naruto) and selects the first chapter in the list
     */
    private void open(String aName)
    {
        //Verifies RecycleView is displayed
        ViewInteraction lRecyclerView = onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));
        //Goes to catalog fragment
        onView(allOf(withId(R.id.manga_recycle_view), isDisplayed())).perform(swipeLeft());
        onView(withId(R.id.search_view)).perform(typeText(aName));

        //Perform single click on an item in the RecyclerView
        lRecyclerView.perform(actionOnItemAtPosition(0, click()));

        //Verify a new activity is launched successfully
        intended(hasComponent(MangaActivity.class.getName()));

        //Quickly scrolls down to start of Chapter ListView
        onView(allOf(withId(R.id.orderButton), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.orderButton), isDisplayed())).perform(click());

        //Select the first item in the list (Because of the Header the first item to select is at index 2)
        onData(anything()).inAdapterView(withId(R.id.chapter_list)).atPosition(2).perform(click());

        //Verify the Reader Activity is launched
        intended(hasComponent(ReaderActivity.class.getName()));

        try
        {
            Thread.sleep(10000);
        }
        catch (Exception aEx)
        {
            Log.e(TAG, aEx.toString());
        }
    }
}
