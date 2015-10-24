package com.teioh.m_feed.Fragment;

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

import com.parse.ParseInstallation;
import com.squareup.picasso.Picasso;
import com.teioh.m_feed.Pojo.Manga;
import com.teioh.m_feed.Pojo.RemoveFromLibrary;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;
import com.teioh.m_feed.Utils.MangaFeedDbHelper;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MangaFragment extends Fragment {


    @Bind(R.id.manga_image) ImageView img;
    @Bind(R.id.ftv) TextView ftv;
    @Bind(R.id.followButton) Button followButton;
    @Bind(R.id.randButton) Button randButton;
    @Bind(R.id.lastUpdate) Button lastUpdateButton;
    private MangaFeedDbHelper mDbHelper;
    private Manga item;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mangafragment, container, false);
        ButterKnife.bind(this, v);

        mDbHelper = new MangaFeedDbHelper(getContext());
        item = getArguments().getParcelable("Manga");

        if (item.getFollowing()) {
            followButton.setText("Unfollow");
        } else {
            followButton.setText("Follow");
        }
        randButton.setText("Read");
        lastUpdateButton.setText("Last Chatper: " + item.getLatestChapter());

        getActivity().setTitle(item.getTitle());
        Picasso.with(getContext()).load(item.getPicUrl()).into(img);
        //TODO need to scrape descriptions, this is just temporary text
        ftv.setText("In the decade since the world became aware of the existence of magic, the world has undergone massive upheaval. However, a boy named Touta lives in seclusion in a rural town far removed from these changes. His ordinary life is highlighted by his magic-using female teacher and his supportive friends. When his tranquil daily life is disrupted, he embarks on a unique adventure.");
        ftv.setTypeface(Typeface.SERIF);

        return v;
    }

    @OnClick(R.id.followButton) void onClick(View v) {
        final boolean follow = item.setFollowing(!item.getFollowing());
        ParseInstallation pi = ParseInstallation.getCurrentInstallation();
        ArrayList<String> channel = new ArrayList<>(Arrays.asList("m_" + item.getMangaId()));
        if (follow) {
            followButton.setText("Unfollow");
            ParseInstallation.getCurrentInstallation().addAllUnique("channels", channel);
        } else {
            followButton.setText("Follow");
            ParseInstallation.getCurrentInstallation().removeAll("channels", channel);
        }
        pi.saveEventually(
                e -> {
                    if (e == null) {
                        //success - send signal to update library
                        if (follow) {
                            mDbHelper.updateMangaFollow(item);
                            BusProvider.getInstance().post(item);
                        } else {
                            mDbHelper.updateMangaFollow(item);
                            BusProvider.getInstance().post(new RemoveFromLibrary(item));
                        }
                    }
                });
    }
}