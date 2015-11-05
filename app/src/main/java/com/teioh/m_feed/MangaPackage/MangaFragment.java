package com.teioh.m_feed.MangaPackage;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseInstallation;
import com.teioh.m_feed.OttoBus.ChangeTitle;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.OttoBus.RemoveFromLibrary;
import com.teioh.m_feed.R;
import com.teioh.m_feed.OttoBus.BusProvider;
import com.teioh.m_feed.Database.MangaFeedDbHelper;
import com.teioh.m_feed.ReactiveQueryManager;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MangaFragment extends Fragment {

    @Bind(R.id.manga_image) ImageView img;
    @Bind(R.id.mangaDescription) TextView description;
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
        description.setText("In the decade since the world became aware of the existence of magic, the world has undergone massive upheaval. However, a boy named Touta lives in seclusion in a rural town far removed from these changes. His ordinary life is highlighted by his magic-using female teacher and his supportive friends. When his tranquil daily life is disrupted, he embarks on a unique adventure.");
        description.setTypeface(Typeface.SERIF);

        return v;
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