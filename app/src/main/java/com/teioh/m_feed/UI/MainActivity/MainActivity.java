package com.teioh.m_feed.UI.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.ExpandableListAdapter;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.Presenters.MainPresenter;
import com.teioh.m_feed.UI.MainActivity.Fragments.FilterDialogFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.SettingsFragment;
import com.teioh.m_feed.UI.MainActivity.Widgets.SlidingTabLayout;
import com.teioh.m_feed.Utils.SharedPrefs;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements IMain.ActivityView {
    public final static String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.search_view) SearchView mSearchView;
    @Bind(R.id.filter_view) ImageView mFilterView;
    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.tabs) SlidingTabLayout mTabLayout;
    @Bind(R.id.tool_bar) Toolbar mToolBar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.drawerLayoutListView) ExpandableListView mDrawerList;
    @Bind(R.id.actionMenu) FloatingActionsMenu mMultiActionMenu;

    private View mDrawerHeader;
    private Toast mToast;
    private ActionBarDrawerToggle mDrawerToggle;
    private IMain.ActivityPresenter mMainPresenter;

    public static Intent getNewInstance(Context aContext){
        Intent lIntent = new Intent(aContext, MainActivity.class);
        return lIntent;
    }

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.main_activity_layout);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenter(this);

        if (aSavedInstanceState != null) {
            mMainPresenter.onRestoreState(aSavedInstanceState);
        }

        mMainPresenter.init(getIntent().getExtras());
        mToast = Toast.makeText(this, "Press back again to exit!", Toast.LENGTH_SHORT);

        //start service in new thread, substantial slow down on main thread
        //startService(new Intent(this, RecentUpdateService.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle aSave) {
        super.onSaveInstanceState(aSave);
        mMainPresenter.onSaveState(aSave);
    }

    @Override
    protected void onPostCreate(Bundle aSavedInstanceState) {
        super.onPostCreate(aSavedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration aNewConfig) {
        super.onConfigurationChanged(aNewConfig);
        mDrawerToggle.onConfigurationChanged(aNewConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainPresenter.onDestroy();
    }

    @Override
    public void onTrimMemory(int aLevel) {
        super.onTrimMemory(aLevel);
        Glide.get(this).trimMemory(aLevel);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu aMenu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aItem) {
        int lId = aItem.getItemId();

        if (lId == R.id.settings) {
            return true;
        } else if (lId == R.id.refresh) {
            return true;
        } else if (lId == R.id.views) {
            return true;
        }

        return super.onOptionsItemSelected(aItem);
    }

    @Override
    public boolean onQueryTextSubmit(String aQueryText) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String aQueryText) {
        mMainPresenter.updateQueryChange(aQueryText);
        return false;
    }

    @Override
    public void onActivityReenter(int aResultCode, Intent aData) {
        super.onActivityReenter(aResultCode, aData);

        if (aResultCode == Activity.RESULT_OK) {
            if (aData != null) {
                mMainPresenter.onGenreFilterSelected(aData);
                mActivityTitle.setText(getString(R.string.filter_active));
                mFilterView.setImageDrawable(getDrawable(R.drawable.filter_remove_outline_24dp));

            } else {
                mMainPresenter.onClearGenreFilter();
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void registerAdapter(ViewPagerAdapterMain aAdapter) {
        if (aAdapter != null) {
            mViewPager.setAdapter(aAdapter);
            mViewPager.setOffscreenPageLimit(3);
            mTabLayout.setViewPager(mViewPager);
        }
    }

    @Override
    public void setDrawerLayoutListener() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolBar, R.string.app_name, R.string.Login) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();  // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawerList);
    }

    @Override
    public void setActivityTitle(String aTitle) {
        mActivityTitle.setText(aTitle);
    }

    @Override
    public void toggleToolbarElements() {
        if (mSearchView.getVisibility() == View.GONE) {
            mSearchView.setVisibility(View.VISIBLE);
            mFilterView.setVisibility(View.VISIBLE);
            setActivityTitle(new SourceFactory().getSourceName());
        } else {
            mSearchView.setVisibility(View.GONE);
            mFilterView.setVisibility(View.GONE);
            setActivityTitle("Settings");
        }
    }

    @Override
    public void setupToolbar() {
        setSupportActionBar(mToolBar);
        mActivityTitle.setText(new SourceFactory().getSourceName());

        mFilterView.setOnClickListener(v -> {
            if (!mMainPresenter.genreFilterActive()) {
                DialogFragment dialog = FilterDialogFragment.getnewInstance();
                dialog.show(getSupportFragmentManager(), null);
            } else {
                mMainPresenter.onClearGenreFilter();
                mFilterView.setImageDrawable(getDrawable(R.drawable.filter_outline_24dp));
                mActivityTitle.setText(new SourceFactory().getSourceName());
            }
        });
    }

    @Override
    public void setupSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextFocusChangeListener((view, queryTextFocused) -> {
            if (!queryTextFocused) {
                mActivityTitle.setVisibility(View.VISIBLE);
                mFilterView.setVisibility(View.VISIBLE);
                mSearchView.setIconified(true);
                mSearchView.setQuery("", true);
            } else {
                mActivityTitle.setVisibility(View.GONE);
                mFilterView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setupTabLayout() {
        mTabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the mTabLayout Space Evenly in Available width
        mTabLayout.setCustomTabColorizer(position -> getResources().getColor(R.color.tabsScrollColor));
    }

    @Override
    public void setPageAdapterItem(int aPosition){
        mViewPager.setCurrentItem(aPosition);
    }

    @Override
    public void setDefaultFilterImage(){
        mFilterView.setImageDrawable(getDrawable(R.drawable.filter_outline_24dp));
    }

    @Override
    public void setupSourceFilterMenu() {
        FloatingActionButton lFAB1 = new FloatingActionButton(getBaseContext());
        lFAB1.setTitle("Reading");
        lFAB1.setSize(FloatingActionButton.SIZE_MINI);
        lFAB1.setColorNormalResId(R.color.ColorAccent);
        lFAB1.setIcon(R.drawable.ic_favorite_white_18dp);
        lFAB1.setOnClickListener(v -> {
            mMultiActionMenu.collapse();
            mMainPresenter.onFilterSelected(1);
        });

        FloatingActionButton lFAB2 = new FloatingActionButton(getBaseContext());
        lFAB2.setTitle("All");
        lFAB2.setIcon(R.drawable.ic_favorite_border_white_18dp);
        lFAB2.setSize(FloatingActionButton.SIZE_MINI);
        lFAB2.setColorNormalResId(R.color.ColorAccent);
        lFAB2.setOnClickListener(v -> {
            mSearchView.clearFocus();
            mMultiActionMenu.collapse();
            mMainPresenter.onFilterSelected(0);
        });

        FloatingActionButton lFAB3 = new FloatingActionButton(getBaseContext());
        lFAB3.setTitle("Completed");
        lFAB3.setSize(FloatingActionButton.SIZE_MINI);
        lFAB3.setColorNormalResId(R.color.ColorAccent);
        lFAB3.setIcon(R.drawable.ic_favorite_white_18dp);
        lFAB3.setOnClickListener(v -> {
            mMultiActionMenu.collapse();
            mMainPresenter.onFilterSelected(2);
        });

        FloatingActionButton lFAB4 = new FloatingActionButton(getBaseContext());
        lFAB4.setTitle("On Hold");
        lFAB4.setSize(FloatingActionButton.SIZE_MINI);
        lFAB4.setColorNormalResId(R.color.ColorAccent);
        lFAB4.setIcon(R.drawable.ic_favorite_white_18dp);
        lFAB4.setOnClickListener(v -> {
            mMultiActionMenu.collapse();
            mMainPresenter.onFilterSelected(3);
        });

        FloatingActionButton lFAB5 = new FloatingActionButton(getBaseContext());
        lFAB5.setTitle("Followed");
        lFAB5.setSize(FloatingActionButton.SIZE_MINI);
        lFAB5.setColorNormalResId(R.color.ColorAccent);
        lFAB5.setIcon(R.drawable.ic_favorite_white_18dp);
        lFAB5.setOnClickListener(v -> {
            mMultiActionMenu.collapse();
            mMainPresenter.onFilterSelected(5);
        });

        mMultiActionMenu.addButton(lFAB4);
        mMultiActionMenu.addButton(lFAB3);
        mMultiActionMenu.addButton(lFAB1);
        mMultiActionMenu.addButton(lFAB5);
        mMultiActionMenu.addButton(lFAB2);

    }

    @Override
    public void setupDrawerLayout(List<String> aDrawerItems, Map<String, List<String>> aSourceCollections) {
        final ExpandableListAdapter adapter = new ExpandableListAdapter(this, aDrawerItems, aSourceCollections);
        if (mDrawerHeader != null) mDrawerList.removeHeaderView(mDrawerHeader);

        mDrawerHeader = LayoutInflater.from(getContext()).inflate(R.layout.drawer_header, null);
        TextView lUsernameTextView = (TextView) mDrawerHeader.findViewById(R.id.drawer_username);
        lUsernameTextView.setText(SharedPrefs.getMALUsername());

        mDrawerList.addHeaderView(mDrawerHeader);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnGroupClickListener((aParent, aView, aGroupPosition, aId) -> {
            mMainPresenter.onDrawerItemChosen(aGroupPosition);
            return false;
        });

        mDrawerList.setOnChildClickListener((aParent, aView, aGroupPosition, aChildPosition, aId) -> {
            mMainPresenter.onSourceItemChosen(aChildPosition);
            return true;
        });

        mDrawerHeader.setOnClickListener(v -> mMainPresenter.onSignIn());
    }

    @Override
    public void onBackPressed() {
        if (mMultiActionMenu.isExpanded()) { //closes action menu
            mMultiActionMenu.collapse();
        } else if (!mToast.getView().isShown() && mDrawerLayout.isDrawerOpen(mDrawerList)) { //closes drawer, if exit mToast isn't active
            mDrawerLayout.closeDrawer(mDrawerList);
            mToast.show();
        } else if (getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG) != null) {
            mMainPresenter.removeSettingsFragment();
            toggleToolbarElements();
            openDrawer();
        } else if(mMainPresenter.genreFilterActive()){
            mMainPresenter.onClearGenreFilter();
            mFilterView.setImageDrawable(getDrawable(R.drawable.filter_outline_24dp));
            mActivityTitle.setText(new SourceFactory().getSourceName());
        }else if (!mToast.getView().isShown()) { //opens drawer, and shows exit mToast to verify exit
            mDrawerLayout.openDrawer(mDrawerList);
            mToast.show();
        } else {    //user double back pressed to exit within time frame (mToast length)
            mToast.cancel();
            super.onBackPressed();
        }
    }

    @Override
    public boolean setRecentSelection(Long aId) {
        if(mMultiActionMenu.isExpanded()) {
            mMultiActionMenu.collapse();
            return false;
        }
        mMainPresenter.setRecentManga(aId);
        return true;
    }

    @Override
    public void updateRecentSelection(Manga aManga) {
        mMainPresenter.getRecentManga();
    }

    @Override
    public void removeFilters(){
        //reset genre filter and UI
        mMainPresenter.onClearGenreFilter();
        mFilterView.setImageDrawable(getDrawable(R.drawable.filter_outline_24dp));
        mActivityTitle.setText(new SourceFactory().getSourceName());

        //reset
        mMainPresenter.onFilterSelected(0);
        mSearchView.clearFocus();
    }

    @Override
    public void MALSignOut() {
        mMainPresenter.onSignOut();
    }


}