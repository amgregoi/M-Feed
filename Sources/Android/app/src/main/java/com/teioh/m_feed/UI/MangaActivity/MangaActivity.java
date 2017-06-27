package com.teioh.m_feed.UI.MangaActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MainActivity.Fragments.FYesNoDialog;
import com.teioh.m_feed.UI.MainActivity.MainActivity;
import com.teioh.m_feed.UI.MangaActivity.Fragments.FImageDialogFragment;
import com.teioh.m_feed.UI.Maps.Listeners;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


public class MangaActivity extends AppCompatActivity implements IManga.ActivityView, Listeners.DialogYesNoListener
{
    final public static String TAG = MangaActivity.class.getSimpleName();

    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.tool_bar) Toolbar mToolBar;
    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.chapter_list) ListView mChapterList;
    @Bind(R.id.failed_to_load_view) LinearLayout mFailedToLoad;

    //Views inside the manga information header
    private ImageView mMangaImage;
    private TextView mDescriptionText;
    private TextView mTitleText;
    private TextView mAuthorText;
    private TextView mArtistText;
    private TextView mGenresText;
    private TextView mAlternateText;
    private TextView mStatusText;
    private Button mFollowButton;
    private Button mReadingStatusButton;
    private Button mContinueReadingButton;

    private View mMangaInfoHeader;
    private View mChapterHeader;

    private IManga.ActivityPresenter mMangaPresenter;

    /***
     * This function creates and returns a new intent for the activity.
     *
     * @param aContext
     * @param aUrl
     * @return
     */
    public static Intent getNewInstance(Context aContext, String aUrl)
    {
        Intent intent = new Intent(aContext, MangaActivity.class);
        intent.putExtra(Manga.TAG, aUrl);
        return intent;
    }

    /***
     * This function initializes the view of the activity.
     *
     * @param aSavedInstanceState
     */
    @Override
    protected void onCreate(Bundle aSavedInstanceState)
    {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.manga_activity);
        ButterKnife.bind(this);

        mMangaPresenter = new MangaPresenter(this);

        if (aSavedInstanceState != null)
        {
            mMangaPresenter.onRestoreState(aSavedInstanceState);
        }
        mMangaPresenter.init(getIntent().getExtras());
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ButterKnife.unbind(this);
        mMangaPresenter.onDestroy();
    }

    /***
     * This function saves relevant data that needs to persist between device state changes.
     *
     * @param aOutState
     */
    @Override
    protected void onSaveInstanceState(Bundle aOutState)
    {
        super.onSaveInstanceState(aOutState);
        mMangaPresenter.onSaveState(aOutState);
    }

    /***
     * This function sets the activity title.
     *
     * @param aTitle
     */
    @Override
    public void setActivityTitle(String aTitle)
    {
        mActivityTitle.setText(aTitle);
    }

    /***
     * This function initializes the toolbar.
     */
    @Override
    public void setupToolBar()
    {
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_back);
        mToolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /***
     * This function initializes the current item information views.
     *
     * @param aManga
     */
    @Override
    public void setMangaViews(Manga aManga)
    {
        if (aManga != null && getContext() != null)
        {
            mDescriptionText.setText(aManga.getDescription());
            mDescriptionText.setTypeface(Typeface.SERIF);
            mTitleText.setText(aManga.getTitle()); // + "\n" + aManga.getSource());
            mAuthorText.setText(aManga.getAuthor());
            mArtistText.setText(aManga.getArtist());
            mGenresText.setText(aManga.getmGenre());
            mAlternateText.setText(aManga.getAlternate());
            mStatusText.setText(aManga.getStatus());
            Glide.with(getContext()).load(aManga.getPicUrl())
                 .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(mMangaImage);
            mChapterList.addHeaderView(mMangaInfoHeader, null, false);
            mChapterList.addHeaderView(mChapterHeader, null, false);

            if (aManga.getFollowing())
            {
                mFollowButton.setVisibility(View.GONE);
                mContinueReadingButton.setVisibility(View.VISIBLE);
                mReadingStatusButton.setVisibility(View.VISIBLE);
                mReadingStatusButton.setText(MangaEnums.eFollowType.values()[aManga.getFollowingValue() - 1].toString());
                invalidateOptionsMenu();
            }
        }
    }

    /***
     * This function initializes information header button listeners.
     */
    @Override
    public void setupHeaderButtons()
    {

        //Image Click
        mMangaImage.setOnClickListener(v ->
                                       {
                                           DialogFragment dialog = FImageDialogFragment.getNewInstance(mMangaPresenter.getImageUrl());
                                           dialog.show(getSupportFragmentManager(), null);
                                       });

        //Follow Button
        mFollowButton.setOnClickListener(v ->
                                         {
                                             mMangaPresenter.onFollowButtonClick(1);
                                             mFollowButton.setVisibility(View.GONE);
                                             mContinueReadingButton.setVisibility(View.VISIBLE);
                                             mReadingStatusButton.setVisibility(View.VISIBLE);
                                             invalidateOptionsMenu();
                                         });

        //Change follow status (Reading, Plan to read, on hold, etc..) MAL
        mReadingStatusButton.setOnClickListener(v ->
                                                {
                                                    //Creating the instance of PopupMenu
                                                    PopupMenu lPopupMenu = new PopupMenu(MangaActivity.this, mReadingStatusButton);
                                                    //Inflating the Popup using xml file
                                                    lPopupMenu.getMenuInflater().inflate(R.menu.menu_follow, lPopupMenu.getMenu());

                                                    //registering lPopupMenu with OnMenuItemClickListener
                                                    lPopupMenu.setOnMenuItemClickListener(item ->
                                                                                          {
                                                                                              MangaEnums.eFollowType lValues[] = MangaEnums.eFollowType
                                                                                                      .values();
                                                                                              switch (item.getItemId())
                                                                                              {
                                                                                                  case R.id.reading_menu:
                                                                                                      mMangaPresenter
                                                                                                              .onFollowButtonClick(1);
                                                                                                      mReadingStatusButton
                                                                                                              .setText(lValues[0]
                                                                                                                               .toString());
                                                                                                      break;
                                                                                                  case R.id.complete_menu:
                                                                                                      mMangaPresenter
                                                                                                              .onFollowButtonClick(2);
                                                                                                      mReadingStatusButton
                                                                                                              .setText(lValues[1]
                                                                                                                               .toString());
                                                                                                      break;
                                                                                                  case R.id.hold_menu:
                                                                                                      mMangaPresenter
                                                                                                              .onFollowButtonClick(3);
                                                                                                      mReadingStatusButton
                                                                                                              .setText(lValues[2]
                                                                                                                               .toString());
                                                                                                      break;
                                                                                              }
                                                                                              return true;
                                                                                          });

                                                    lPopupMenu.show(); //showing lPopupMenu menu


                                                });

        mContinueReadingButton.setOnClickListener(v ->
                                                  {
                                                      mMangaPresenter.onContinueReadingButtonClick();
                                                  });
    }

    /***
     * This function initializes the header view.
     */
    @Override
    public void initializeHeaderViews()
    {
        mMangaInfoHeader = LayoutInflater.from(getContext()).inflate(R.layout.manga_info_header, null);
        mChapterHeader = LayoutInflater.from(getContext()).inflate(R.layout.manga_chapter_list_header, null);

        mMangaImage = (ImageView) mMangaInfoHeader.findViewById(R.id.manga_image);
        mDescriptionText = (TextView) mMangaInfoHeader.findViewById(R.id.mangaDescription);
        mTitleText = (TextView) mMangaInfoHeader.findViewById(R.id.title);
        mAuthorText = (TextView) mMangaInfoHeader.findViewById(R.id.author);
        mArtistText = (TextView) mMangaInfoHeader.findViewById(R.id.artist);
        mGenresText = (TextView) mMangaInfoHeader.findViewById(R.id.genre);
        mAlternateText = (TextView) mMangaInfoHeader.findViewById(R.id.alternate);
        mStatusText = (TextView) mMangaInfoHeader.findViewById(R.id.status);


        mFollowButton = (Button) mMangaInfoHeader.findViewById(R.id.followButton);
        mReadingStatusButton = (Button) mMangaInfoHeader.findViewById(R.id.read_status_button);
        mContinueReadingButton = (Button) mMangaInfoHeader.findViewById(R.id.continue_reading_button);
    }

    /***
     * This function shows the failed to load view.
     */
    @Override
    public void showFailedToLoad()
    {
        mFailedToLoad.setVisibility(View.VISIBLE);
        mChapterList.setVisibility(View.GONE);
    }

    /***
     * This function handles backPress' for the activity.
     */
    @Override
    public void onBackPressed()
    {
        if (isTaskRoot())
        {
            Intent lIntent = MainActivity.getNewInstance(this);
            startActivity(lIntent);
            finish();
        }
        else
        {
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
        mMangaPresenter.onPause();
    }

    /***
     * This function is called when a fragment or activities onResume() is called in their life cycle chain.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        mMangaPresenter.onResume();
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
     * This function starts the swipe refresh layout refresh animation.
     */
    @Override
    public void startRefresh()
    {
        mSwipeRefresh.setRefreshing(true);

    }

    /***
     * This function stops the swipe refresh layout refresh animation.
     */
    @Override
    public void stopRefresh()
    {
        mSwipeRefresh.post(() -> mSwipeRefresh.setRefreshing(false));
        mSwipeRefresh.setEnabled(false);
    }

    /***
     * This function initializes the swipe refresh layout.
     */
    @Override
    public void setupSwipeRefresh()
    {
        mSwipeRefresh.post(() -> mSwipeRefresh.setRefreshing(true));
    }

    /***
     * This function hides the chapter list view.
     */
    @Override
    public void hideCoverLayout()
    {
        mChapterList.setVisibility(View.GONE);
    }

    /***
     * This function shows the chapter list view.
     */
    @Override
    public void showCoverLayout()
    {
        mChapterList.setVisibility(View.VISIBLE);
    }

    /***
     * This function registers the adapter to the list view.
     *
     * @param aAdapter
     */
    @Override
    public void registerAdapter(BaseAdapter aAdapter)
    {
        mChapterList.setAdapter(aAdapter);
    }

    /***
     * This function performs chapter item selections.
     *
     * @param aAdapter
     * @param aView
     * @param aPosition
     */
    @OnItemClick(R.id.chapter_list)
    void onItemClick(AdapterView<?> aAdapter, View aView, int aPosition)
    {
        mMangaPresenter.onChapterClicked((Chapter) aAdapter.getItemAtPosition(aPosition));
    }

    /***
     * This function reverses the chapter list items.
     *
     * @param aView
     */
    @OnClick(R.id.orderButton)
    public void orderButton(View aView)
    {
        mMangaPresenter.chapterOrderButtonClick();
        mChapterList.setSelection(1);
    }

    /***
     * This function trims the Glide cache when low on memory.
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
     * This function inflates an overflow context menu if the current item is being followed.
     *
     * @param aMenu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu aMenu)
    {
        if (mFollowButton.getVisibility() == View.GONE)
        {
            getMenuInflater().inflate(R.menu.menu_chapter, aMenu);
            return true;
        }
        return false;
    }

    /***
     * This function handles overflow menu selections.
     *
     * @param aMenuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem aMenuItem)
    {
        int lId = aMenuItem.getItemId();
        if (lId == R.id.remove_list)
        {
            //popup dialog
            DialogFragment newFragment = FYesNoDialog
                    .getNewInstance(R.string.remove_from_library_title, getString(R.string.remove_from_library_text), 0, true);
            newFragment.show(getSupportFragmentManager(), "dialog");
            return true;
        }
        else if (lId == R.id.clear_chapters)
        {
            mMangaPresenter.clearCachedChapters();
        }

        return super.onOptionsItemSelected(aMenuItem);
    }

    /***
     * This function unfollows the current item if the user verifies they want this by the YesNo dialog.
     * @param aAction
     */
    @Override
    public void positive(int aAction)
    {
        // After Ok code.
        mFollowButton.setVisibility(View.VISIBLE);
        mReadingStatusButton.setVisibility(View.GONE);
        mContinueReadingButton.setVisibility(View.GONE);
        mMangaPresenter.onUnfollowButtonClick();
        invalidateOptionsMenu();
    }

    /***
     * This function is the negative response from the YesNo Dialog.
     * No action is taken.
     * @param aAction
     */
    @Override
    public void negative(int aAction)
    {
        //Do nothing
    }
}
