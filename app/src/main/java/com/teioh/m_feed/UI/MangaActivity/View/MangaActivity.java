package com.teioh.m_feed.UI.MangaActivity.View;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.teioh.m_feed.UI.MainActivity.View.Fragments.LoginFragment;
import com.teioh.m_feed.UI.MangaActivity.Adapters.ViewPagerAdapterManga;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.OttoBus.BusProvider;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.SlidingTabLayout;

public class MangaActivity extends AppCompatActivity{

    private ViewPagerAdapterManga mViewPagerAdapterManga;
    private ViewPager mViewPager;
    private SlidingTabLayout tabs;
    private CharSequence Titles[] = {"Info", "Chapters"};
    private int Numbtabs = 2;

    TextView mActivityTitle;
    SearchView mSearchView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        Manga item = getIntent().getParcelableExtra("Manga");

        mActivityTitle = (TextView) findViewById(R.id.activityTitle);
        mSearchView = (SearchView) findViewById(R.id.search_view_1);
        mSearchView.setVisibility(View.GONE);
        mActivityTitle.setText(item.getTitle());


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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE, 0, Menu.NONE, "Logout");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == 0) {
            ParseUser.getCurrentUser().logOut();
            Fragment fragment = new LoginFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment).addToBackStack(null).commit();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override protected void onDestroy(){
        super.onDestroy();
    }
}
