package com.teioh.m_feed.UI.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Adapters.ExpandableListAdapter;
import com.teioh.m_feed.UI.MainActivity.Adapters.ViewPagerAdapterMain;
import com.teioh.m_feed.UI.MainActivity.Fragments.FilterDialogFragment;
import com.teioh.m_feed.UI.MainActivity.Fragments.SettingsFragment;
import com.teioh.m_feed.UI.MainActivity.Presenters.MainPresenter;
import com.teioh.m_feed.UI.MainActivity.Widgets.SlidingTabLayout;
import com.teioh.m_feed.Utils.SharedPrefs;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements IMain.ActivityView, GoogleApiClient.OnConnectionFailedListener
{
    public final static String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.search_view) SearchView mSearchView;
    @Bind(R.id.filter_view) ImageView mFilterView;
    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.no_scroll_pager) ViewPager mViewPager;
    @Bind(R.id.tabs) SlidingTabLayout mTabLayout;
    @Bind(R.id.tool_bar) Toolbar mToolBar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.drawerLayoutListView) ExpandableListView mDrawerList;

    @Bind(R.id.actionMenu) FloatingActionsMenu mMultiActionMenu;

    private View mDrawerHeader;
    private Toast mToast;
    private ActionBarDrawerToggle mDrawerToggle;
    private IMain.ActivityPresenter mMainPresenter;
    private GoogleApiClient mGoogleApiClient;

    /***
     * This function creates and returns a new intent for this activity.
     *
     * @param aContext
     * @return
     */
    public static Intent getNewInstance(Context aContext)
    {
        Intent lIntent = new Intent(aContext, MainActivity.class);
        return lIntent;
    }

    public IMain.ActivityPresenter getPresenter()
    {
        return mMainPresenter;
    }

    /***
     * This function initializes the view of the fragment.
     *
     * @param aSavedInstanceState
     */
    @Override
    protected void onCreate(Bundle aSavedInstanceState)
    {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.main_activity_layout);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenter(this);

        if (aSavedInstanceState != null)
        {
            mMainPresenter.onRestoreState(aSavedInstanceState);
        }

        mMainPresenter.init(getIntent().getExtras());
        mToast = Toast.makeText(this, "Press back again to exit!", Toast.LENGTH_SHORT);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

    }

    /***
     * This function is called in the fragment lifecycle.
     *
     * @param aSavedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle aSavedInstanceState)
    {
        super.onPostCreate(aSavedInstanceState);
        mDrawerToggle.syncState();
    }

    /***
     * This function is called in the fragment lifecycle.
     *
     * @param aNewConfig
     */
    @Override
    public void onConfigurationChanged(Configuration aNewConfig)
    {
        super.onConfigurationChanged(aNewConfig);
        mDrawerToggle.onConfigurationChanged(aNewConfig);
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mMainPresenter.onDestroy();
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aSave
     */
    @Override
    protected void onSaveInstanceState(Bundle aSave)
    {
        super.onSaveInstanceState(aSave);
        mMainPresenter.onSaveState(aSave);
    }

    /***
     * This function trims Glide cache when memory is low.
     *
     * @param aLevel
     */
    @Override
    public void onTrimMemory(int aLevel)
    {
        super.onTrimMemory(aLevel);
        Glide.get(this).trimMemory(aLevel);
    }

    /***
     * This function is called when an activity re-enters from another view (like a dialog fragment)
     *
     * @param aResultCode
     * @param aData
     */
    @Override
    public void onActivityReenter(int aResultCode, Intent aData)
    {
        super.onActivityReenter(aResultCode, aData);

        if (aResultCode == Activity.RESULT_OK)
        {
            if (aData != null)
            {
                mMainPresenter.onGenreFilterSelected(aData);
                mActivityTitle.setText(getString(R.string.filter_active));
                mFilterView.setImageDrawable(getResources().getDrawable(R.drawable.filter_remove_outline_24dp));

            }
            else
            {
                mMainPresenter.onClearGenreFilter();
            }
        }
    }

    /***
     * Not Implemented.
     *
     * @param aQueryText
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String aQueryText)
    {
        return false;
    }

    /***
     * This function performs the text query filter.
     *
     * @param aQueryText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String aQueryText)
    {
        mMainPresenter.updateQueryChange(aQueryText);
        return false;
    }

    /***
     * This function registers the pager adapter.
     * @param aAdapter
     */
    @Override
    public void registerAdapter(ViewPagerAdapterMain aAdapter)
    {
        if (aAdapter != null)
        {
            mViewPager.setAdapter(aAdapter);
            mViewPager.setOffscreenPageLimit(3);
            mTabLayout.setViewPager(mViewPager);
        }
    }

    /***
     * This function initializes the activity toolbar.
     */
    @Override
    public void setupToolbar()
    {
        setSupportActionBar(mToolBar);
        mActivityTitle.setText(SourceFactory.getInstance().getSourceName());

        mFilterView.setOnClickListener(v ->
                                       {
                                           if (!mMainPresenter.genreFilterActive())
                                           {
                                               DialogFragment dialog = FilterDialogFragment.getnewInstance();
                                               dialog.show(getSupportFragmentManager(), null);
                                           }
                                           else
                                           {
                                               mMainPresenter.onClearGenreFilter();
                                               mFilterView.setImageDrawable(getResources().getDrawable(R.drawable.filter_outline_24dp));
                                               mActivityTitle.setText(SourceFactory.getInstance().getSourceName());
                                           }
                                       });
    }

    /***
     * This function initializes the tab layout.
     */
    @Override
    public void setupTabLayout()
    {
        // To make the Tabs Fixed set this true, This makes the mTabLayout Space Evenly in Available width
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setCustomTabColorizer(position -> getResources().getColor(R.color.ColorAccent));
    }

    /***
     * This function initializes the search view.
     */
    @Override
    public void setupSearchView()
    {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextFocusChangeListener((view, queryTextFocused) ->
                                                      {
                                                          if (!queryTextFocused)
                                                          {
                                                              mActivityTitle.setVisibility(View.VISIBLE);
                                                              mFilterView.setVisibility(View.VISIBLE);
                                                              mSearchView.setIconified(true);
                                                              mSearchView.setQuery("", true);
                                                          }
                                                          else
                                                          {
                                                              mActivityTitle.setVisibility(View.GONE);
                                                              mFilterView.setVisibility(View.GONE);
                                                          }
                                                      });
    }

    /***
     * This function updates the activity title in the toolbar.
     * @param aTitle
     */
    @Override
    public void setActivityTitle(String aTitle)
    {
        mActivityTitle.setText(aTitle);
    }

    /***
     * This function sets the current item of the viewpager.
     * @param aPosition
     */
    @Override
    public void setPageAdapterItem(int aPosition)
    {
        mViewPager.setCurrentItem(aPosition);
    }

    /***
     * This function sets the default filter image inside the toolbar (Non active filter image)
     */
    @Override
    public void setDefaultFilterImage()
    {
        mFilterView.setImageDrawable(getResources().getDrawable(R.drawable.filter_outline_24dp));
    }

    /***
     * This function toggles toolbar elements.
     */
    @Override
    public void toggleToolbarElements()
    {
        if (mSearchView.getVisibility() == View.GONE)
        {
            mSearchView.setVisibility(View.VISIBLE);
            mFilterView.setVisibility(View.VISIBLE);
            setActivityTitle(SourceFactory.getInstance().getSourceName());
        }
        else
        {
            mSearchView.setVisibility(View.GONE);
            mFilterView.setVisibility(View.GONE);
            setActivityTitle("Settings");
        }
    }

    @Override
    public void updateFragmentViews()
    {
        mMainPresenter.updateFragmentViews();
    }

    /***
     * This function initializes drawer layout listener.
     */
    @Override
    public void setDrawerLayoutListener()
    {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.app_name, R.string.app_name)
        {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();  // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /***
     * This function closes the drawer layout.
     */
    @Override
    public void closeDrawer()
    {
        mDrawerLayout.closeDrawers();
    }

    /***
     * This function opens the drawer layout.
     */
    @Override
    public void openDrawer()
    {
        mDrawerLayout.openDrawer(mDrawerList);
    }

    /***
     * This functin initializes the drawer layout.
     * @param aDrawerItems
     * @param aSourceCollections
     */
    @Override
    public void setupDrawerLayout(List<String> aDrawerItems, Map<String, List<String>> aSourceCollections)
    {
        final ExpandableListAdapter adapter = new ExpandableListAdapter(this, aDrawerItems, aSourceCollections);
        if (mDrawerHeader != null) mDrawerList.removeHeaderView(mDrawerHeader);

        mDrawerHeader = LayoutInflater.from(getContext()).inflate(R.layout.drawer_header, null);
        TextView lUsernameTextView = (TextView) mDrawerHeader.findViewById(R.id.drawer_username);
        lUsernameTextView.setText(SharedPrefs.getGoogleEmail());

        mDrawerList.addHeaderView(mDrawerHeader);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnGroupClickListener((aParent, aView, aGroupPosition, aId) ->
                                            {
                                                mMainPresenter.onDrawerItemSelected(aGroupPosition);
                                                return false;
                                            });

        mDrawerList.setOnChildClickListener((aParent, aView, aGroupPosition, aChildPosition, aId) ->
                                            {
                                                mMainPresenter.onSourceItemChosen(aChildPosition);
                                                aParent.invalidateViews();
                                                return true;
                                            });
    }

    /***
     * This function returns the activity's context.
     *
     * @return
     */
    @Override
    public Context getContext()
    {
        return this;
    }

    /***
     * this function sets the recently selected item.
     * @param aUrl
     * @return
     */
    @Override
    public boolean setRecentSelection(String aUrl)
    {
        if (mMultiActionMenu.isExpanded())
        {
            mMultiActionMenu.collapse();
            return false;
        }
        mMainPresenter.setRecentManga(aUrl);
        return true;
    }

    /***
     * This function updates the recently selected item.
     * @param aManga
     * @return
     */
    @Override
    public boolean updateRecentSelection(Manga aManga)
    {
        return mMainPresenter.updateRecentManga();
    }

    /***
     * This function removes search and query filters.
     * @return
     */
    @Override
    public boolean removeFilters()
    {
        //reset genre filter and UI
        mMainPresenter.onClearGenreFilter();
        mFilterView.setImageDrawable(getResources().getDrawable(R.drawable.filter_outline_24dp));
        mActivityTitle.setText(SourceFactory.getInstance().getSourceName());

        //reset
        mMainPresenter.onFilterSelected(MangaEnums.eFilterStatus.NONE);
        mSearchView.clearFocus();

        return true;  //update
    }

    /***
     * Not Implemented
     * @param aResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult aResult)
    {
        //do nothing
    }

    /***
     * This function performs the google sign in auth.
     */
    @Override
    public void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 8008);
    }

    /***
     * This function performs the google sign out.
     */
    @Override
    public void signOut()
    {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    /***
     * This function retrieves the result of the google sign in.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 8008)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mMainPresenter.updateSignIn(result);

        }
    }

    /***
     * This function handles various outcomes of perform a back press in the activity.
     */
    @Override
    public void onBackPressed()
    {

        // Clear search view regardless of state when back is pressed (might change)
        if (!mSearchView.isIconified())
        {
            mSearchView.clearFocus();
            mSearchView.setIconified(true);
        }

        if (mMultiActionMenu.isExpanded())
        {
            //closes action menu
            mMultiActionMenu.collapse();
        }
        else if (!mToast.getView().isShown() && mDrawerLayout.isDrawerOpen(mDrawerList))
        {
            //closes drawer, if exit toast message isn't active
            mDrawerLayout.closeDrawer(mDrawerList);
            mToast.show();
        }
        else if (getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG) != null)
        {
            //closes settings fragment if open
            mMainPresenter.removeSettingsFragment();
            toggleToolbarElements();
            openDrawer();
        }
        else if (mMainPresenter.genreFilterActive())
        {
            mMainPresenter.onClearGenreFilter();
            mFilterView.setImageDrawable(getResources().getDrawable(R.drawable.filter_outline_24dp));
            mActivityTitle.setText(SourceFactory.getInstance().getSourceName());
        }
        else if (!mToast.getView().isShown())
        {
            //opens drawer, and shows exit mToast to verify exit
            mDrawerLayout.openDrawer(mDrawerList);
            mToast.show();
        }
        else
        {
            //user double back pressed to exit within time frame (mToast length)
            mToast.cancel();
            super.onBackPressed();
        }
    }

    /***
     * This function clears the Glide cache when low on memory.
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    /***
     * This function is called when a fragment or activities onPause() is called in their life cycle chain.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        mMainPresenter.onPause();
    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        mMainPresenter.onResume();
    }

    /***
     * This function performs filter by status (ALL)
     */
    @OnClick(R.id.fab_all)
    public void onFABAll()
    {
        mSearchView.clearFocus();
        mMultiActionMenu.collapse();
        mMainPresenter.onFilterSelected(MangaEnums.eFilterStatus.NONE);
    }

    /***
     * This function performs filter by status (COMPLETE)
     */
    @OnClick(R.id.fab_complete)
    public void onFABComplete()
    {
        mMultiActionMenu.collapse();
        mMainPresenter.onFilterSelected(MangaEnums.eFilterStatus.COMPLETE);
    }

    /***
     * This function performs filter by status (LIBRARY)
     */
    @OnClick(R.id.fab_library)
    public void onFABLibrary()
    {
        mMultiActionMenu.collapse();
        mMainPresenter.onFilterSelected(MangaEnums.eFilterStatus.FOLLOWING);
    }

    /***
     * This function performs filter by status (ON_HOLD)
     */
    @OnClick(R.id.fab_on_hold)
    public void onFABHold()
    {
        mMultiActionMenu.collapse();
        mMainPresenter.onFilterSelected(MangaEnums.eFilterStatus.ON_HOLD);
    }

    /***
     * This function performs filter by status (READING)
     */
    @OnClick(R.id.fab_reading)
    public void onFABReading()
    {
        mMultiActionMenu.collapse();
        mMainPresenter.onFilterSelected(MangaEnums.eFilterStatus.READING);
    }
}
