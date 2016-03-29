package com.teioh.m_feed.UI.MangaActivity.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.FImageDialogFragment;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.FProgressDialogFragment;
import com.teioh.m_feed.UI.MangaActivity.View.Fragments.FRemoveDialogFragment;
import com.teioh.m_feed.UI.MangaActivity.View.Mappers.MangaActivityMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MangaActivity extends AppCompatActivity implements MangaActivityMapper {
    final public static String TAG = MangaActivity.class.getSimpleName();

    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.tool_bar) Toolbar mToolBar;
    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.chapter_list) ListView mChapterList;

    private ImageView mMangaImage;
    private TextView mDescriptionText;
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


    private MangaPresenter mMangaPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manga_activity);
        ButterKnife.bind(this);

        mMangaPresenter = new MangaPresenterImpl(this);

        if (savedInstanceState != null) {
            mMangaPresenter.onRestoreState(savedInstanceState);
        }
        mMangaPresenter.init(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMangaPresenter.onSaveState(outState);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mFollowButton.getVisibility() == View.GONE) {
            getMenuInflater().inflate(R.menu.menu_chapter, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.remove_list) {
            //popup dialog
            DialogFragment newFragment = FRemoveDialogFragment.newInstance(R.string.DialogFragmentRemove);
            newFragment.show(getSupportFragmentManager(), "dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(data == null) {
                // After Ok code.
                mSyncMALButton.setVisibility(View.GONE);
                mFollowButton.setVisibility(View.VISIBLE);
                mMangaPresenter.onFollwButtonClick();
                invalidateOptionsMenu();
            }//else if(data.hasextra(etc...)
        } else if (resultCode == Activity.RESULT_CANCELED){
            // After Cancel code.
        }
    }

    @Override
    public void setActivityTitle(String title) {
        mActivityTitle.setText(title);
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
    public void changeFollowButton(boolean following) {
//        if (following) {
//            mFollowButton.setText("Remove from list");
//            //dialog fragment
//        } else {
//            mFollowButton.setText("Add to list");
//        }
    }

    @Override
    public void setMangaViews(Manga manga) {
        if (manga != null && getContext() != null) {
            mDescriptionText.setText(manga.getDescription());
            mDescriptionText.setTypeface(Typeface.SERIF);
            mAuthorText.setText(manga.getmAuthor());
            mArtistText.setText(manga.getmArtist());
            mGenresText.setText(manga.getmGenre());
            mAlternateText.setText(manga.getmAlternate());
            mStatusText.setText(manga.getmStatus());
            Glide.with(getContext()).load(manga.getPicUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mMangaImage);
            mChapterList.addHeaderView(mMangaInfoHeader, null, false);
            mChapterList.addHeaderView(mChapterHeader, null, false);

            if(manga.getFollowing()){
                mFollowButton.setVisibility(View.GONE);
                //TODO update database (add MAL id column)
                //TODO to check if sync set up, and make other buttons visible
                mSyncMALButton.setVisibility(View.VISIBLE); //TODO uncomment when MAL implemented
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
    public void registerAdapter(BaseAdapter adapter) {
        mChapterList.setAdapter(adapter);
    }

    @Override
    public void initializeHeaderViews() {
        mMangaInfoHeader = LayoutInflater.from(getContext()).inflate(R.layout.manga_info_header, null);
        mChapterHeader = LayoutInflater.from(getContext()).inflate(R.layout.manga_chapters_header, null);

        mMangaImage = (ImageView) mMangaInfoHeader.findViewById(R.id.manga_image);
        mDescriptionText = (TextView) mMangaInfoHeader.findViewById(R.id.mangaDescription);
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
    public void onMALSyncClicked(MALMangaList list) {
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
            mMangaPresenter.onFollwButtonClick();
            mFollowButton.setVisibility(View.GONE); //uncomment after menu remove is  put in
            mSyncMALButton.setVisibility(View.VISIBLE);   //TODO uncomment when MAL implemented
            invalidateOptionsMenu();
        });

        //Change follow status (Reading, Plan to read, on hold, etc..) MAL
        mMALStatusButton.setOnClickListener(v -> {

        });

        //Rate the manga (1-10) MAL
        mMALScoreButton.setOnClickListener(v -> {

        });

        //Find MAL equivalent of manga and link to it
        mSyncMALButton.setOnClickListener(v -> {
//            mMangaPresenter.onMALSyncClicked();
            //TODO temp remove button until MAL implemented
            mMangaPresenter.onFollwButtonClick();
            mFollowButton.setVisibility(View.VISIBLE); //uncomment after menu remove is  put in
            mSyncMALButton.setVisibility(View.GONE);   //TODO uncomment when MAL implemented
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
    void onItemClick(AdapterView<?> adapter, View view, int pos) {
        mMangaPresenter.onChapterClicked((Chapter) adapter.getItemAtPosition(pos));
    }

    @OnClick(R.id.orderButton)
    public void orderButton(View view) {
        mMangaPresenter.chapterOrderButtonClick();
        mChapterList.setSelection(1);
    }

}
