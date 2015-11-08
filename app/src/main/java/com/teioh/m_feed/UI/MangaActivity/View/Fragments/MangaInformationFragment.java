package com.teioh.m_feed.UI.MangaActivity.View.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaInformationPresenter;
import com.teioh.m_feed.UI.MangaActivity.Presenters.MangaInformationPresenterImpl;
import com.teioh.m_feed.UI.MangaActivity.Presenters.Mappers.MangaViewMapper;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MangaInformationFragment extends Fragment implements MangaViewMapper {

    @Bind(R.id.manga_image) ImageView img;
    @Bind(R.id.mangaDescription) TextView description;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.artist) TextView artist;
    @Bind(R.id.genre) TextView genres;
    @Bind(R.id.status) TextView status;
    @Bind(R.id.followButton) Button followButton;
    @Bind(R.id.readButton) Button readButton;

    private MangaInformationPresenter mMangaInformationPresenter;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_info_fragment, container, false);
        ButterKnife.bind(this, v);

        mMangaInformationPresenter = new MangaInformationPresenterImpl(this, getArguments());
        mMangaInformationPresenter.initialize();
        return v;
    }

    @Override public void onResume() {
        super.onResume();
        mMangaInformationPresenter.busProviderRegister();
    }

    @Override public void onPause() {
        super.onPause();
        mMangaInformationPresenter.busProviderUnregister();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mMangaInformationPresenter.butterKnifeUnbind();
    }

    @OnClick(R.id.followButton) void onClick(View v) {
        mMangaInformationPresenter.onFollwButtonClick();
    }

    @Override public void setFollowButtonText(String newText) {
        followButton.setText(newText);
    }

    @Override public void setMangaViews(Manga manga) {
        if(manga != null) {
            try {
                description.setText(manga.getDescription());
                description.setTypeface(Typeface.SERIF);
                author.setText(manga.getmAuthor());
                artist.setText(manga.getmArtist());
                genres.setText(manga.getmGenre());
                status.setText(manga.getmStatus());
                followButton.setText((manga.getFollowing() ? "Unfollow" : "Follow"));
                //Picasso.with(getContext()).load(item.getPicUrl()).into(img);
                Glide.with(getContext()).load(manga.getPicUrl()).into(img);
            }catch(NullPointerException e){
                Log.e("MangaInformationFrag", "Changed views to fast \n\t\t\t" + e.toString());
            }
        }
    }
}