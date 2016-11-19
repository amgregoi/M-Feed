package com.teioh.m_feed.UI.ReaderActivity.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.teioh.m_feed.R;
import com.teioh.m_feed.UI.ReaderActivity.Widgets.GestureImageView;

import java.util.List;


public class ImagePageAdapter extends PagerAdapter
{
    final public static String TAG = ImagePageAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> mImageUrlList;
    private LayoutInflater mInflater;

    private SparseArray<View> mImageViews = new SparseArray<>();

    /***
     * TODO..
     * @param aContext
     * @param aImageUrls
     */
    public ImagePageAdapter(Context aContext, List<String> aImageUrls)
    {
        this.mContext = aContext;
        this.mImageUrlList = aImageUrls;
    }

    /***
     * TODO..
     * @return
     */
    @Override
    public int getCount()
    {
        return this.mImageUrlList.size();
    }

    /***
     * TODO..
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
     * TODO..
     * @param aContainer
     * @param aPosition
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup aContainer, int aPosition)
    {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View lView = mInflater.inflate(R.layout.reader_chapter_item, aContainer, false);

        GestureImageView mImage = (GestureImageView) lView.findViewById(R.id.chapterPageImageView);
        Glide.with(mContext).load(mImageUrlList.get(aPosition)).animate(android.R.anim.fade_in).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(mImage)
        {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation)
            {
                super.onResourceReady(resource, animation);
                mImage.initializeView();
                mImage.setTag(TAG + ":" + aPosition);
                mImage.startFling(0, 10000f); //large fling to initialize the image to the top for long pages
            }
        });
        (aContainer).addView(lView);
        mImageViews.put(aPosition, lView);
        return lView;
    }

    /***
     * TODO..
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
     * TODO..
     * @param aImage
     */
    public void addItem(String aImage)
    {
        mImageUrlList.add(aImage);
        notifyDataSetChanged();
    }
}
