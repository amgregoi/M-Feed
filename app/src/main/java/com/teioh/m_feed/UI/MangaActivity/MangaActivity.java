package com.teioh.m_feed.UI.MangaActivity;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.teioh.m_feed.MAL_Models.MALMangaList;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Fragments.FImageDialogFragment;
import com.teioh.m_feed.UI.MangaActivity.Fragments.FProgressDialogFragment;
import com.teioh.m_feed.UI.MangaActivity.Fragments.FRemoveDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MangaActivity extends AppCompatActivity implements IManga.ActivityView {
    final public static String TAG = MangaActivity.class.getSimpleName();

    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.tool_bar) Toolbar mToolBar;
    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.chapter_list) ListView mChapterList;

    private ImageView mMangaImage;
    private TextView mDescriptionText;
    private TextView mTitleText;
    private TextView mAuthorText;
    private TextView mArtistText;
    private TextView mGenresText;
    private TextView mAlternateText;
    private TextView mStatusText;
    private Button mFollowButton;
    private Button mMALStatusButton;
    private Button mMALScoreButton;
    private Button mSyncMALButton;
    private Button mChapterIncButton;
    private Button mVolumeIncButton;
    private Button mMultiIncButton;

    private View mMangaInfoHeader;
    private View mChapterHeader;


    private IManga.ActivityPresenter mMangaPresenter;

    public static Intent getNewInstance(Context aContext, String aTitle) {
        Intent intent = new Intent(aContext, MangaActivity.class);
        intent.putExtra(Manga.TAG, aTitle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.manga_activity);
        ButterKnife.bind(this);

        mMangaPresenter = new MangaPresenter(this);

        if (aSavedInstanceState != null) {
            mMangaPresenter.onRestoreState(aSavedInstanceState);
        }
        mMangaPresenter.init(getIntent().getExtras());
    }

    @Override
    protected void onSaveInstanceState(Bundle aOutState) {
        super.onSaveInstanceState(aOutState);
        mMangaPresenter.onSaveState(aOutState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMangaPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMangaPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMangaPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu aMenu) {
        if(mFollowButton.getVisibility() == View.GONE) {
            getMenuInflater().inflate(R.menu.menu_chapter, aMenu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aMenuItem) {
        int lId = aMenuItem.getItemId();
        if (lId == R.id.remove_list) {
            //popup dialog
            DialogFragment newFragment = FRemoveDialogFragment.getNewInstance(R.string.DialogFragmentRemove);
            newFragment.show(getSupportFragmentManager(), "dialog");
            return true;
        }

        return super.onOptionsItemSelected(aMenuItem);
    }

    @Override
    public void onActivityReenter(int aResultCode, Intent aData) {
        super.onActivityReenter(aResultCode, aData);
        if (aResultCode == Activity.RESULT_OK) {
            if(aData == null) {
                // After Ok code.
                mSyncMALButton.setVisibility(View.GONE);
                mFollowButton.setVisibility(View.VISIBLE);
                mMangaPresenter.onUnfollowButtonClick();
                invalidateOptionsMenu();
            }//else if(data.hasextra(etc...)
        } else if (aResultCode == Activity.RESULT_CANCELED){
            // After Cancel code.
        }
    }

    @Override
    public void setActivityTitle(String aTitle) {
        mActivityTitle.setText(aTitle);
    }

    @Override
    public void setupToolBar() {
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_back);
        mToolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void changeFollowButton(boolean aFollowing) {
//        if (following) {
//            mFollowButton.setText("Remove from list");
//            //dialog fragment
//        } else {
//            mFollowButton.setText("Add to list");
//        }
    }

    @Override
    public void setMangaViews(Manga aManga) {
        if (aManga != null && getContext() != null) {
            mDescriptionText.setText(aManga.getDescription());
            mDescriptionText.setTypeface(Typeface.SERIF);
            mTitleText.setText(aManga.getTitle() + "\n" + aManga.getSource());
            mAuthorText.setText(aManga.getAuthor());
            mArtistText.setText(aManga.getArtist());
            mGenresText.setText(aManga.getmGenre());
            mAlternateText.setText(aManga.getAlternate());
            mStatusText.setText(aManga.getStatus());
            Glide.with(getContext())
                    .load(aManga.getPicUrl())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mMangaImage);
            mChapterList.addHeaderView(mMangaInfoHeader, null, false);
            mChapterList.addHeaderView(mChapterHeader, null, false);

            if(aManga.getFollowing()){
                mFollowButton.setVisibility(View.GONE);
                //TODO update database (add MAL id column)
                //TODO to check if sync set up, and make other buttons visible
                mSyncMALButton.setVisibility(View.VISIBLE); //TODO uncomment when MAL implemented
                mMALStatusButton.setVisibility(View.VISIBLE);
                mMALStatusButton.setText(Manga.FollowType.values()[aManga.getFollowingValue()-1].toString());
                invalidateOptionsMenu();
            }
        }
    }

    @Override
    public void startRefresh() {
        mSwipeRefresh.setRefreshing(true);

    }

    @Override
    public void stopRefresh() {
        mSwipeRefresh.post(() -> mSwipeRefresh.setRefreshing(false));
        mSwipeRefresh.setEnabled(false);
    }

    @Override
    public void setupSwipeRefresh() {
        mSwipeRefresh.post(() -> mSwipeRefresh.setRefreshing(true));
    }

    @Override
    public void hideCoverLayout() {
        mChapterList.setVisibility(View.GONE);
    }

    @Override
    public void showCoverLayout() {
        mChapterList.setVisibility(View.VISIBLE);
    }

    @Override
    public void registerAdapter(BaseAdapter aAdapter) {
        mChapterList.setAdapter(aAdapter);
    }

    @Override
    public void initializeHeaderViews() {
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
        mMALStatusButton = (Button) mMangaInfoHeader.findViewById(R.id.read_status_mal);
        mMALScoreButton = (Button) mMangaInfoHeader.findViewById(R.id.score_mal);
        mSyncMALButton = (Button) mMangaInfoHeader.findViewById(R.id.syncMALButton);
        mChapterIncButton = (Button) mMangaInfoHeader.findViewById(R.id.chapter_plus);
        mVolumeIncButton = (Button) mMangaInfoHeader.findViewById(R.id.volume_plus);
        mMultiIncButton = (Button) mMangaInfoHeader.findViewById(R.id.multi_update);
    }

    @Override
    public void onMALSyncClicked(MALMangaList aList) {
        //start fragment or activity
    }

    @Override
    public void setupHeaderButtons() {

        //Image Click
        mMangaImage.setOnClickListener(v -> {
            DialogFragment dialog = FImageDialogFragment.getNewInstance(mMangaPresenter.getImageUrl());
            dialog.show(getSupportFragmentManager(), null);
        });

        //Follow Button
        mFollowButton.setOnClickListener(v -> {
            mMangaPresenter.onFollwButtonClick(1);
            mFollowButton.setVisibility(View.GONE); //uncomment after menu remove is  put in
            mSyncMALButton.setVisibility(View.VISIBLE);   //TODO uncomment when MAL implemented
            mMALStatusButton.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
        });

        //Change follow status (Reading, Plan to read, on hold, etc..) MAL
        mMALStatusButton.setOnClickListener(v -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(MangaActivity.this, mMALStatusButton);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.menu_follow, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(item -> {
                Manga.FollowType lValues[] = Manga.FollowType.values();
                switch(item.getItemId()) {
                    case R.id.reading:
                        mMangaPresenter.onFollwButtonClick(1);
                        mMALStatusButton.setText(lValues[0].toString());
                        break;
                    case R.id.complete:
                        mMangaPresenter.onFollwButtonClick(2);
                        mMALStatusButton.setText(lValues[1].toString());
                        break;
                    case R.id.hold:
                        mMangaPresenter.onFollwButtonClick(3);
                        mMALStatusButton.setText(lValues[2].toString());
                        break;
                }
                return true;
            });

            popup.show(); //showing popup menu


        });

        //Rate the manga (1-10) MAL
        mMALScoreButton.setOnClickListener(v -> {

        });

        //Find MAL equivalent of manga and link to it
        mSyncMALButton.setOnClickListener(v -> {
//            mMangaPresenter.onMALSyncClicked();
            mMangaPresenter.onUnfollowButtonClick();
            mFollowButton.setVisibility(View.VISIBLE); //uncomment after menu remove is  put in
            mSyncMALButton.setVisibility(View.GONE);   //TODO uncomment when MAL implemented
            mMALStatusButton.setVisibility(View.GONE);
            invalidateOptionsMenu();

        });

        //Increment Chapter button
        mChapterIncButton.setOnClickListener(v -> {

        });

        //Increment Volume button
        mVolumeIncButton.setOnClickListener(v -> {

        });

        //Manually set chapter/volume
        mMultiIncButton.setOnClickListener(v -> {
            DialogFragment newFragment = new FProgressDialogFragment();
            newFragment.show(getSupportFragmentManager(), "dialog");

        });
    }

    @OnItemClick(R.id.chapter_list)
    void onItemClick(AdapterView<?> aAdapter, View aView, int aPosition) {
        mMangaPresenter.onChapterClicked((Chapter) aAdapter.getItemAtPosition(aPosition));
    }

    @OnClick(R.id.orderButton)
    public void orderButton(View aView) {
        mMangaPresenter.chapterOrderButtonClick();
        mChapterList.setSelection(1);
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

}