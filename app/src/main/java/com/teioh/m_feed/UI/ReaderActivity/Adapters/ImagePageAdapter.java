package com.teioh.m_feed.UI.ReaderActivity.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.teioh.m_feed.UI.ReaderActivity.View.Widgets.GestureImageView;
import com.teioh.m_feed.R;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class ImagePageAdapter extends PagerAdapter {
    final public static String TAG = ImagePageAdapter.class.getSimpleName();

    private Context context;
    private List<String> imageUrls;
    private LayoutInflater inflater;

    SparseArray<View> views = new SparseArray<View>();


    public ImagePageAdapter(Context c, List<String> imagePaths) {
        this.context = c;
        this.imageUrls = imagePaths;
    }

    @Override public int getCount() {
        return this.imageUrls.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.chapter_reader_item, container, false);

        GestureImageView mImage = (GestureImageView) viewLayout.findViewById(R.id.chapterPageImageView);
        Glide.with(context)
                .load(imageUrls.get(position))
                .animate(android.R.anim.fade_in)
                .into(new GlideDrawableImageViewTarget(mImage) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        mImage.initializeView();
                        mImage.setTag(TAG + ":" + position);
                    }
                });
        (container).addView(viewLayout);
        views.put(position, viewLayout);
        return viewLayout;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((RelativeLayout) object);
        views.remove(position);
    }

    public void refreshView(int position){
        views.get(position).invalidate();
    }



}
