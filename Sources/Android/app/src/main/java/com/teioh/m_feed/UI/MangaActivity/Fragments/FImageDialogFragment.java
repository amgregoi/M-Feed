package com.teioh.m_feed.UI.MangaActivity.Fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.teioh.m_feed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FImageDialogFragment extends DialogFragment
{
    public final static String TAG = FImageDialogFragment.class.getSimpleName();
    public final static String IMAGE_URL_KEY = TAG + ":" + "IMAGE_URL_KEY";

    @Bind(R.id.image_for_dialog) ImageView mImage;

    private String mImageUrl;

    /***
     * This function creates and returns a new instance of the fragment.
     *
     * @param url
     * @return
     */
    public static DialogFragment getNewInstance(String url)
    {
        Bundle lBundle = new Bundle();
        lBundle.putString(IMAGE_URL_KEY, url);

        DialogFragment lFragment = new FImageDialogFragment();
        lFragment.setArguments(lBundle);

        return lFragment;
    }

    /***
     * This function initializes the view of the fragment.
     *
     * @param aInflater
     * @param aContainer
     * @param aSavedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedInstanceState)
    {
        View lView = aInflater.inflate(R.layout.manga_info_image_dialog, null);
        ButterKnife.bind(this, lView);

        if (getArguments().containsKey(IMAGE_URL_KEY)) mImageUrl = getArguments().getString(IMAGE_URL_KEY);
        else mImageUrl = null;

        //Set dialog features
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        return lView;
    }

    /***
     * This function is called in the fragment lifecycle.
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        if (mImageUrl != null)
        {
            RequestOptions lOptions = new RequestOptions();
            lOptions.skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(getActivity()).load(mImageUrl)
                 .apply(lOptions)
                 .into(mImage);
        }
        super.onActivityCreated(savedInstanceState);
    }

    /***
     * This function is called when a fragment or activities onDestroy is called in their life cycle chain.
     */
    @Override
    public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
