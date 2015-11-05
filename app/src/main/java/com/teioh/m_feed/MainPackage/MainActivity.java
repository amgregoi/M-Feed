package com.teioh.m_feed.MainPackage;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.squareup.otto.Subscribe;
import com.teioh.m_feed.OttoBus.ChangeTitle;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.SlidingTabLayout;

//TODO
//http://www.vogella.com/tutorials/AndroidServices/article.html
//look into cupboard, rehaul local database

public class MainActivity extends AppCompatActivity {

    ViewPagerAdapterMain mViewPagerAdapterMain;

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
        setTitle(getString(R.string.app_name));

        //Determines whether user needs to login/signup
//        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
//            Fragment fragment = new LoginFragment();
//            getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();
//
//        } else {
//            ParseUser currentUser = ParseUser.getCurrentUser();
//            if (currentUser == null) {
//                Intent intent = new Intent(MainActivity.this,
//                        LoginFragment.class);
//                Fragment fragment = new LoginFragment();
//                getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack("MangaFragment").commit();
//            }
//        }
        setContentView(R.layout.activity_layout);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mViewPagerAdapterMain = new ViewPagerAdapterMain(getSupportFragmentManager(), Titles, Numbtabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapterMain);
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
