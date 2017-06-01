package com.teioh.m_feed.UI.MainActivity;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.MangaActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
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

public class MangaActivityTests
{
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    /***
     * This function opens a pre-defined Manga item (Naruto).
     */
    private void open(String aName){
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

    /***
     * This Test verifies we can successfully toggle between the various reading status'
     */
    @Test
    public void toggleReadingStatus()
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

    @Test
    public void toggleFollow()
    {
        open("Again");
        openContextualActionModeOverflowMenu();
    }

}
