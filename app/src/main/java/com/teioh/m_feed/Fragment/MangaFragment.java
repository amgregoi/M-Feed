package com.teioh.m_feed.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.teioh.m_feed.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.BusProvider;

import java.util.ArrayList;

import uk.co.deanwild.flowtextview.FlowTextView;


public class MangaFragment extends Fragment {

    Manga item;
    ImageView img;
    FlowTextView ftv;
    ImageButton ib;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mangafragment, container, false);
        ftv = (FlowTextView) v.findViewById(R.id.ftv);
        img = (ImageView) v.findViewById(R.id.manga_image);
        item = getArguments().getParcelable("Manga");
        ib = (ImageButton) v.findViewById(R.id.followButton);

        getActivity().setTitle(item.getTitle());
        Picasso.with(getContext()).load(item.getPicUrl()).resize(500, 700).into(img);
        ftv.setText("In the decade since the world became aware of the existence of magic, the world has undergone massive upheaval. However, a boy named Touta lives in seclusion in a rural town far removed from these changes. His ordinary life is highlighted by his magic-using female teacher and his supportive friends. When his tranquil daily life is disrupted, he embarks on a unique adventure.");
        ftv.setTextSize(60);
        ftv.setTypeface(Typeface.SERIF);


        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseInstallation pi = ParseInstallation.getCurrentInstallation();
                ArrayList<String> temp = new ArrayList<>();
                temp.add("m_" + item.getMangaId());
                ParseInstallation.getCurrentInstallation().addAllUnique("channels", temp);
                pi.saveInBackground(
                        new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //success
                                    //send signal to update library
                                    BusProvider.getInstance().post(item);
                                } else {
                                    Log.e("ADDING to channels: ", e.toString());
                                }
                            }
                        });
            }
        });


        return v;
    }

}