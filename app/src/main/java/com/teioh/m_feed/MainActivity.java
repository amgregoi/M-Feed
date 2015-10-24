package com.teioh.m_feed;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.teioh.m_feed.Adapter.ViewPagerAdapter;
import com.teioh.m_feed.Fragment.LoginFragment;
import com.teioh.m_feed.Utils.BusProvider;
import com.teioh.m_feed.Utils.SlidingTabLayout;


/*
 *http://www.androidhive.info/2013/10/android-tab-layout-with-swipeable-views-1/
 *http://developer.android.com/training/implementing-navigation/lateral.html
 *http://www.androidbegin.com/tutorial/android-parse-com-listview-images-and-texts-tutorial/
 * optional read in app - Webview
 *
 * fix keyboard staying open
 * http://stackoverflow.com/questions/4841228/after-type-in-edittext-how-to-make-keyboard-disappear
 */

public class MainActivity extends AppCompatActivity {

    ViewPagerAdapter mViewPagerAdapter;

    //TODO loading circle for async tasks (tab 3)
    //TODO webview for reading
    //TODO listen for push notifications to update recent and library
    //TODO update all with current info
    //just query latest updated, and update the manga object in listview

    private ViewPager mViewPager;
    private SlidingTabLayout tabs;
    private CharSequence Titles[] = {"Recent", "Library", "All"};
    private int Numbtabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Determines whether user needs to login/signup
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Fragment fragment = new LoginFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();

        } else {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser == null) {
                Intent intent = new Intent(MainActivity.this,
                        LoginFragment.class);
                Fragment fragment = new LoginFragment();
                getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();
            }
        }
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numbtabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);


        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE, 0, Menu.NONE, "Logout");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == 0) {
            ParseUser.getCurrentUser().logOut();
            Fragment fragment = new LoginFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
    }
}
