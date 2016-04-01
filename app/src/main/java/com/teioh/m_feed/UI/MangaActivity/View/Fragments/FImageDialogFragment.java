package com.teioh.m_feed.UI.MangaActivity.View.Fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FImageDialogFragment extends DialogFragment {
    public final static String TAG = FImageDialogFragment.class.getSimpleName();
    public final static String IMAGE_URL_KEY = TAG + ":"  + "IMAGE_URL_KEY";

    public static DialogFragment getNewInstance(String url){
        DialogFragment fragment = new FImageDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IMAGE_URL_KEY, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Bind(R.id.image_for_dialog) ImageView mImage;

    private String mImageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_image_dialog, null);
        ButterKnife.bind(this, view);

        if(getArguments().containsKey(IMAGE_URL_KEY))
            mImageUrl = getArguments().getString(IMAGE_URL_KEY);
        else
            mImageUrl = null;

        //Set dialog features
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(mImageUrl != null) {
            Glide.with(getActivity()).load(mImageUrl)
                    .animate(android.R.anim.fade_in)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImage);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
