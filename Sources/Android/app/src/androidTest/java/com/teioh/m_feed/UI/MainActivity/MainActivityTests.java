package com.teioh.m_feed.UI.MainActivity;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.KeyEvent;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.MangaActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
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

    /***
     * This Test verifies a Manga Activity is successfully loaded when a Manga item is selected.
     */
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

    /***
     * This Test verifies that the filter search performs successfully.
     */
    @Test
    public void textFilterSearch()
    {
        //Verifies RecyclerView is displayed
        onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));

        //Verify search view text is empty
        ViewInteraction lSearchView = onView(allOf(withId(R.id.search_view), isDisplayed()));

        //Selects search view, enters text, and presses the enter/search key
        lSearchView.perform(click(), typeText("zz"), pressKey(KeyEvent.KEYCODE_ENTER));
    }

    /***
     * This Test verifies the Genre Filter Dialog is displayed successfully when pressing
     * the button icon in the header.
     */
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
     * This function performs the neccessary steps to open the Navigation Drawer
     */
    private void openNavigationDrawer()
    {
        //Opens Navigation Drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        //Verifies drawer was opened by searching for Text in drawer
        onView(withText("Guest (Sign in)")).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.drawerLayoutListView), isDisplayed()));

    }

    /***
     * This Test verifies the navigation drawer opens and closes successfully.
     */
    @Test
    public void navigationDrawerToggle()
    {
        //Verifies RecycleView is displayed
        onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));
        openNavigationDrawer();

        //Closes Navigation Drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());

        //Verifies Drawer text is now gone
        onView(withText("Guest (Sign in)")).check(matches(not(isDisplayed())));


    }

    /***
     * This Test verifies the navigation drawer functionality performs successfully.
     */
    @Test
    public void navigationDrawerOptions()
    {
        /***
         * Init
         */
        //Verifies RecycleView is displayed
        onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));
        openNavigationDrawer();

        /***
         * Test Home
         */
        //Click "Home" option
        onView(withText("Home")).check(matches(isDisplayed())).perform(click());
        //Verify drawer closes
        onView(withText("Home")).check(matches(not(isDisplayed())));

        /***
         * Test Filter
         */
        openNavigationDrawer();
        //Click "Filter Search" option
        onView(withText("Filter Search")).check(matches(isDisplayed())).perform(click());
        //Verifies filter dialog appears by search for its dialog title
        onView(withText("Filter Manga")).check(matches(isDisplayed()));
        //Back press to close filter dialog
        Espresso.pressBack();

        /***
         * Test Source
         */
        openNavigationDrawer();
        //Select "Source" option to expand source list
        onView(withText("Sources")).check(matches(isDisplayed())).perform(click());
        //Verify Batoto source is visible when source list is expanded
        onView(withText("Batoto")).check(matches(isDisplayed()));
        //Select "Source" option again to collapse source list
        onView(withText("Sources")).check(matches(isDisplayed())).perform(click());

        /***
         * Test Settings
         */
        //Select "Settings" option to bring up settings fragment
        onView(withText("Settings")).check(matches(isDisplayed())).perform(click());
        //Verify Settings fragment is displayed by searching for text "Contact Us"
        onView(withText("Contact Us")).check(matches(isDisplayed()));
        //Back press to close settings fragment
        Espresso.pressBack();

    }

    @Test
    public void statusFilterToggle()
    {
        //Verifies RecycleView is displayed
        onView(allOf(withId(R.id.manga_recycle_view), isDisplayed()));
        //Goes to catalog fragment
        onView(withText("Catalog")).check(matches(isDisplayed())).perform(click());

        //Performs click on action menu for status filter
        //performs click on various filter options
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.fab_on_hold)).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.fab_complete)).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.fab_reading)).check(matches(isDisplayed())).perform(click());


        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.fab_library)).check(matches(isDisplayed())).perform(click());


        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.fab_all)).check(matches(isDisplayed())).perform(click());

    }

}
