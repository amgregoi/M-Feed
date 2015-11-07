package com.teioh.m_feed.MangaPackage.View.Fragments;

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
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.R;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.MangaJoy;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;


public class MangaInformationFragment extends Fragment {

    @Bind(R.id.manga_image) ImageView img;
    @Bind(R.id.mangaDescription) TextView description;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.artist) TextView artist;
    @Bind(R.id.genre) TextView genres;
    @Bind(R.id.status) TextView status;

    @Bind(R.id.followButton) Button followButton;
    @Bind(R.id.readButton) Button readButton;

    private Manga item;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manga_info_fragment, container, false);
        ButterKnife.bind(this, v);

        item = getArguments().getParcelable("Manga");
        if (item.getFollowing()) {
            followButton.setText("Unfollow");
        } else {
            followButton.setText("Follow");
        }

        //Picasso.with(getContext()).load(item.getPicUrl()).into(img);
        Glide.with(getContext()).load(item.getPicUrl()).into(img);

        //TODO need to scrape descriptions, this is just temporary text
        if(item.getDescription() != null) {
            description.setText(item.getDescription());
            description.setTypeface(Typeface.SERIF);
            author.setText(item.getmAuthor());
            artist.setText(item.getmArtist());
            genres.setText(item.getmGenre());
            status.setText(item.getmStatus());
        }
        else {
            Observable<Manga> observableManga;
            observableManga = MangaJoy.updateMangaObservable(item);
            observableManga.subscribe(manga -> updateView(manga));
        }
        return v;
    }

    private void updateView(Manga manga)
    {
        item = manga;
        if(manga.getDescription() != null) {
            description.setText(item.getDescription());
            description.setTypeface(Typeface.SERIF);
            author.setText(item.getmAuthor());
            artist.setText(item.getmArtist());
            genres.setText(item.getmGenre());
            status.setText(item.getmStatus());
            Glide.with(getContext()).load(manga.getPicUrl()).into(img);

        }
    }

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick(R.id.followButton) void onClick(View v) {
        final boolean follow = item.setFollowing(!item.getFollowing());
        if (follow) {
            followButton.setText("Unfollow");
            MangaFeedDbHelper.getInstance().updateMangaFollow(item);
            BusProvider.getInstance().post(item);
        } else {
            followButton.setText("Follow");
            MangaFeedDbHelper.getInstance().updateMangaUnfollow(item);
            BusProvider.getInstance().post(new RemoveFromLibrary(item));
        }
    }
}