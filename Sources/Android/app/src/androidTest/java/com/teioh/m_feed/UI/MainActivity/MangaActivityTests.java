package com.teioh.m_feed.UI.MainActivity;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.MangaActivity;
import com.teioh.m_feed.UI.ReaderActivity.ReaderActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

public class MangaActivityTests
{
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    /***
     * This Test verifies the application can successfully toggle between the various reading status'
     */
    @Test
    public void testToggleReadingStatus()
    {
        open("Naruto");

        /***
         * Reading status, completed to reading, verify reading is present
         */
        onView(allOf(withId(R.id.read_status_button), isDisplayed())).perform(click());
        onView(withText("Reading")).inRoot(isPlatformPopup()).perform(click());
        onView(allOf(withText("READING"), isDisplayed()));

        /***
         * Reading status, reading to on_hold, verify on_hold is present
         */
        onView(allOf(withId(R.id.read_status_button), isDisplayed())).perform(click());
        onView(withText("On Hold")).inRoot(isPlatformPopup()).perform(click());
        onView(allOf(withText("ON_HOLD"), isDisplayed()));

        /***
         * Reading status, on_hold to completed, verify completed is present
         */
        onView(allOf(withId(R.id.read_status_button), isDisplayed())).perform(click());
        onView(withText("Completed")).inRoot(isPlatformPopup()).perform(click());
        onView(allOf(withText("COMPLETED"), isDisplayed()));
    }

    /***
     * This Test verifies the application can follow and unfollow a manga
     */
    @Test
    public void testToggleFollow()
    {
        //Open Manga +Again
        open("+Again");
        //Open context overflow menu
        openContextualActionModeOverflowMenu();
        //Select unfollow to remove from user library
        onView(withText("Unfollow")).inRoot(isPlatformPopup()).perform(click());
        //Select yes to verify wanting to remove manga
        onView(allOf(withText("Yes"), isDisplayed())).perform(click());

        //Swipe down to resolve ui glitch that occurs while running tests
        //Doesnt appear to happen in normal use.
        onView(allOf(withId(R.id.swipe_container), isDisplayed())).perform(swipeDown());

        //Click follow button to re-add manga to library
        onView(allOf(withId(R.id.followButton), isDisplayed())).perform(click());

        //UI glitch work around (above)
        onView(allOf(withId(R.id.swipe_container), isDisplayed())).perform(swipeDown());

        //Verify followbutton does not exist
        onView(withId(R.id.followButton)).check(ViewAssertions.matches(not(isDisplayed())));
    }

    /***
     * This Test verifies the application can toggle the order the chapters are sorted in.
     * Ascending vs Descending.
     */
    @Test
    public void testToggleChapterOrder()
    {
        open("Naruto");

        //Reverses chapter order
        onView(allOf(withId(R.id.orderButton), isDisplayed())).perform(click());

        //Returns chapter order back to normal
        onView(allOf(withId(R.id.orderButton), isDisplayed())).perform(click());
    }

    /***
     * This Test verifies the application can select a chapter and load the reader activity.
     */
    @Test
    public void selectChapter()
    {
        open("Naruto");

        //Quickly scrolls down to start of Chapter ListView
        onView(allOf(withId(R.id.orderButton), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.orderButton), isDisplayed())).perform(click());

        //Select the first item in the list (Because of the Header the first item to select is at index 2)
        onData(anything()).inAdapterView(withId(R.id.chapter_list)).atPosition(2).perform(click());

        //Verify the Reader Activity is launched
        intended(hasComponent(ReaderActivity.class.getName()));
    }

    /***
     * This function opens a pre-defined Manga item (Naruto).
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
    }


}
