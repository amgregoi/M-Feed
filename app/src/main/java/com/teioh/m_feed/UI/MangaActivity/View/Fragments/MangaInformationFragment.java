package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaInformationPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaInformationPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaInformationMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.Toast.LENGTH_SHORT;


public class MangaInformationFragment extends Fragment implements MangaInformationMapper {

    @Bind(R.id.manga_image) ImageView img;
    @Bind(R.id.mangaDescription) TextView description;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.artist) TextView artist;
    @Bind(R.id.genre) TextView genres;
    @Bind(R.id.status) TextView status;

    @Bind(R.id.pink_icon) FloatingActionButton floatButton;

    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.relativeLayout) RelativeLayout mRelativeLayout;

    private MangaInformationPresenter mMangaInformationPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_info_fragment, container, false);
        ButterKnife.bind(this, v);
        mMangaInformationPresenter = new MangaInformationPresenterImpl(this, getArguments());
        mMangaInformationPresenter.initialize();
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMangaInformationPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMangaInformationPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMangaInformationPresenter.onDestroyView();
    }

    @OnClick(R.id.pink_icon) void onClick(View v) {
        mMangaInformationPresenter.onFollwButtonClick();
    }

    @Override
    public void setFollowButtonText(int resourceId, boolean isInit) {
        if (resourceId == R.drawable.ic_done && !isInit) {
            Toast.makeText(getContext(), "Now following", LENGTH_SHORT).show();
        }
        floatButton.setImageResource(resourceId);
    }

    @Override
    public void setupFollowButton() {
        floatButton.setColorNormal(getResources().getColor(R.color.ColorPrimary));
    }

    @Override
    public void setMangaViews(Manga manga) {
        if (manga != null && getContext() != null) {
            description.setText(manga.getDescription());
            description.setTypeface(Typeface.SERIF);
            author.setText(manga.getmAuthor());
            artist.setText(manga.getmArtist());
            genres.setText(manga.getmGenre());
            status.setText(manga.getmStatus());
            //Picasso.with(getContext()).load(item.getPicUrl()).into(img);
            Glide.with(getContext()).load(manga.getPicUrl()).into(img);
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
        mRelativeLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showCoverLayout() {
        mRelativeLayout.setVisibility(View.VISIBLE);
    }
}