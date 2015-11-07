package com.teioh.m_feed.MainPackage.View;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.BaseContextMapper;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.BaseDirectoryMapper;
import com.teioh.m_feed.MainPackage.Presenters.Mappers.MainActivityMapper;
import com.teioh.m_feed.MainPackage.View.Fragments.LoginFragment;
import com.teioh.m_feed.MainPackage.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.OttoBus.QueryChange;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.SlidingTabLayout;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityMapper, BaseContextMapper, SearchView.OnQueryTextListener{

    @Bind(R.id.search_view_1)SearchView mSearchView;
    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.tabs) SlidingTabLayout tabs;

    private MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        ButterKnife.bind(this);

        mMainPresenter = new MainPresenterImpl(this, this);
        mMainPresenter.parseLogin();
        mMainPresenter.initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            mMainPresenter.onLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.busProviderRegister();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.busProviderUnregister();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mMainPresenter.ButterKnifeUnbind();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mMainPresenter.updateQueryChange(newText);
        return false;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void registerAdapter(ViewPagerAdapterMain adapter) {
        if(adapter != null)
        {
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(3);
            tabs.setViewPager(mViewPager);
        }
    }

    @Override
    public void setupSearchview() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextFocusChangeListener((view, queryTextFocused) -> {
            if (!queryTextFocused) {
                mActivityTitle.setVisibility(View.VISIBLE);
                mSearchView.setIconified(true);
                mSearchView.setQuery("", true);
            }else{
                mActivityTitle.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setupTabLayout() {
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
    }
}
