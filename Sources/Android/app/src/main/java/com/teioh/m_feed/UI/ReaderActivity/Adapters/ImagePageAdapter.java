package com.teioh.m_feed.UI.ReaderActivity.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.GestureImageView;
import com.teioh.m_feed.Utils.MangaLogger;

import java.util.List;


public class ImagePageAdapter extends PagerAdapter
{
    final public static String TAG = ImagePageAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> mImageUrlList;
    private LayoutInflater mInflater;

    private SparseArray<View> mImageViews = new SparseArray<>();

    /***
     * This is the constructor for the Image Page Adapter.
     *
     * @param aContext
     * @param aImageUrls
     */
    public ImagePageAdapter(Context aContext, List<String> aImageUrls)
    {
        this.mContext = aContext;
        this.mImageUrlList = aImageUrls;
    }

    /***
     * This function returns the count of pages in the chapter.
     *
     * @return
     */
    @Override
    public int getCount()
    {
        return this.mImageUrlList.size();
    }

    /***
     * This function instantiates the item specified by its position.
     *
     * @param aContainer
     * @param aPosition
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup aContainer, int aPosition)
    {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View lView = mInflater.inflate(R.layout.reader_chapter_item, aContainer, false);

        GestureImageView mImage = (GestureImageView) lView.findViewById(R.id.chapter_page_image_view);

        RequestOptions lOptions = new RequestOptions();
        lOptions.fitCenter()
                .override(1024, 8192)//OpenGLRenderer max image size, if larger in X or Y it will scale the image
                .placeholder(mContext.getResources().getDrawable(R.drawable.ic_book_white_18dp))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(mContext)
             .asBitmap()
             .load(mImageUrlList.get(aPosition))
             .apply(lOptions)
             .transition(new GenericTransitionOptions<>().transition(android.R.anim.fade_in))
             .into(new BitmapImageViewTarget(mImage)
             {
                 @Override
                 public void onLoadFailed(@Nullable Drawable errorDrawable)
                 {
                     super.onLoadFailed(errorDrawable);
                     mImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_refresh_white_24dp));
                 }

                 @Override
                 public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                 {
                     super.onResourceReady(resource, glideAnimation);
                     mImage.initializeView();
                     try
                     {
                         mImage.setTag(TAG + ":" + aPosition);
                     }
                     catch (Exception aException)
                     {
                         MangaLogger.logError(TAG, aException.toString(), "Position: " + aPosition);
                     }
                     mImage.startFling(0, 100000f); //large fling to initialize the image to the top for long pages
                 }
             });
        (aContainer).addView(lView);
        mImageViews.put(aPosition, lView);
        return lView;
    }

    /***
     * This function destroys the item specified by its position.
     *
     * @param aContainer
     * @param aPosition
     * @param aObject
     */
    @Override
    public void destroyItem(ViewGroup aContainer, int aPosition, Object aObject)
    {
        (aContainer).removeView((RelativeLayout) aObject);
        mImageViews.remove(aPosition);
    }

    /***
     *
     *
     * @param aView
     * @param aObject
     * @return
     */
    @Override
    public boolean isViewFromObject(View aView, Object aObject)
    {
        return aView == (aObject);
    }

    /***
     * This function adds an item to the adapter specified by it string url.
     *
     * @param aImage
     */
    public void addItem(String aImage)
    {
        mImageUrlList.add(aImage);
        notifyDataSetChanged();
    }
}
