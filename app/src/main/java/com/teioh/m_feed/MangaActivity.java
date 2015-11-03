package com.teioh.m_feed;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.teioh.m_feed.Adapter.ViewPagerAdapterManga;
import com.teioh.m_feed.Fragment.LoginFragment;
import com.teioh.m_feed.Pojo.Manga;
import com.teioh.m_feed.Utils.BusProvider;
import com.teioh.m_feed.Utils.SlidingTabLayout;

public class MangaActivity extends FragmentActivity {

    private ViewPagerAdapterManga mViewPagerAdapterManga;
    private ViewPager mViewPager;
    private SlidingTabLayout tabs;
    private CharSequence Titles[] = {"Info", "Chapters"};
    private int Numbtabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        Manga item = getIntent().getParcelableExtra("Manga");

        mViewPagerAdapterManga = new ViewPagerAdapterManga(getSupportFragmentManager(), Titles, Numbtabs, item);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapterManga);
        mViewPager.setOffscreenPageLimit(2);


        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(position -> getResources().getColor(R.color.tabsScrollColor));

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
        BusProvider.getInstance().register(this);

        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);

        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
    }
}
