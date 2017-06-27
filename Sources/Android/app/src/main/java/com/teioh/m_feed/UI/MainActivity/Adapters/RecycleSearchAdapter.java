package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.l4digital.fastscroll.FastScroller;
import com.teioh.m_feed.BuildConfig;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.MangaLogger;

import java.util.ArrayList;
import java.util.Collections;

public class RecycleSearchAdapter extends RecyclerView.Adapter<RecycleSearchAdapter.ViewHolder> implements FastScroller.SectionIndexer
{
    private final static String TAG = RecycleSearchAdapter.class.getSimpleName();

    private final ItemSelectedListener mListener;
    private ArrayList<Manga> mOriginalData = null;
    private ArrayList<Manga> mFilteredData = null;
    private TextFilter mFilter = new TextFilter();

    /***
     * This is the constructor for the Recycle Search Adapter.
     *
     * @param aData
     * @param aListener
     */
    public RecycleSearchAdapter(ArrayList<Manga> aData, ItemSelectedListener aListener)
    {
        mFilteredData = new ArrayList<>(aData);
        mOriginalData = new ArrayList<>(aData);
        mListener = aListener;
    }

    /***
     * This function creates the view holder for an item in the adapter.
     *
     * @param aParent
     * @param aViewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup aParent, int aViewType)
    {
        View lView = LayoutInflater.from(aParent.getContext()).inflate(R.layout.main_manga_grid_item, aParent, false);
        return new ViewHolder(lView);
    }

    /***
     * This function binds the view holder for an item in the adapter.
     *
     * @param aHolder
     * @param aPosition
     */
    @Override
    public void onBindViewHolder(ViewHolder aHolder, int aPosition)
    {
        Context lContext = aHolder.itemView.getContext();
        Manga lMangaItem = mFilteredData.get(aPosition);

        switch (lMangaItem.getFollowingValue())
        {
            case 1:
                aHolder.mLayoutFooter.setBackgroundColor(lContext.getResources().getColor(R.color.ColorPrimary));
                aHolder.mTextView.setBackgroundColor(lContext.getResources().getColor(R.color.ColorPrimary));
                aHolder.mTextView.setTextColor(lContext.getResources().getColor(R.color.white));
                break;
            case 2:
                aHolder.mLayoutFooter.setBackgroundColor(lContext.getResources().getColor(R.color.green));
                aHolder.mTextView.setBackgroundColor(lContext.getResources().getColor(R.color.green));
                aHolder.mTextView.setTextColor(lContext.getResources().getColor(R.color.white));
                break;
            case 3:
                aHolder.mLayoutFooter.setBackgroundColor(lContext.getResources().getColor(R.color.red));
                aHolder.mTextView.setBackgroundColor(lContext.getResources().getColor(R.color.red));
                aHolder.mTextView.setTextColor(lContext.getResources().getColor(R.color.white));
                break;
            default:
                aHolder.mLayoutFooter.setBackgroundColor(lContext.getResources().getColor(R.color.white));
                aHolder.mTextView.setBackgroundColor(lContext.getResources().getColor(R.color.white));
                aHolder.mTextView.setTextColor(lContext.getResources().getColor(R.color.black));
        }

        RequestOptions lOptions = new RequestOptions();
        lOptions.skipMemoryCache(true)
                .placeholder(R.drawable.clear_button_background)
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        Glide.with(lContext)
             .asBitmap()
             .load(lMangaItem.getPicUrl())
             .apply(lOptions)
             .transition(new GenericTransitionOptions<>().transition(android.R.anim.fade_in))
             .into(new BitmapImageViewTarget(aHolder.mImageView)
             {
                 @Override
                 public void onLoadFailed(@Nullable Drawable errorDrawable)
                 {
                     super.onLoadFailed(errorDrawable);
                     MangaLogger
                             .logError(TAG, errorDrawable.toString(), "Image URL = " + lMangaItem.getPicUrl());
                 }

                 @Override
                 public void onResourceReady(Bitmap resource, Transition<? super Bitmap> animation)
                 {
                     super.onResourceReady(resource, animation);
                     aHolder.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

                 }
             });

        aHolder.mTextView.setText(lMangaItem.toString());
    }

    /***
     * This function retuns the ID of an item in the adapter specified by its position.
     *
     * @param aPosition
     * @return
     */
    public long getItemId(int aPosition)
    {
        return aPosition;
    }

    /***
     * This function returns the item count of currently displayed data in the adapter.
     *
     * @return
     */
    @Override
    public int getItemCount()
    {
        return mFilteredData.size();
    }

    /***
     * This function recycles the viewholder of an item in the adapter.
     *
     * @param aHolder
     */
    @Override
    public void onViewRecycled(ViewHolder aHolder)
    {
        super.onViewRecycled(aHolder);
        Glide.with(aHolder.mImageView.getContext()).clear(aHolder.mImageView);
    }

    /***
     * This function gets an item in the adapter specified by its position.
     *
     * @param aPosition The position of the item to be retrieved.
     * @return
     */
    public Manga getItemAt(int aPosition)
    {
        return mFilteredData.get(aPosition);
    }

    /***
     * This function updates an item in the adapter specified by the param object.
     *
     * @param aManga
     */
    public void updateItem(Manga aManga)
    {
        int lPosition;

        if ((lPosition = mFilteredData.indexOf(aManga)) != -1)
        {
            mFilteredData.remove(lPosition);
            mFilteredData.add(lPosition, aManga);
            notifyItemChanged(lPosition);
        }

        if ((lPosition = mOriginalData.indexOf(aManga)) != -1)
        {
            mOriginalData.remove(lPosition);
            mOriginalData.add(lPosition, aManga);
            notifyItemChanged(lPosition);
        }
    }

    /***
     * This function updates a library item in the adapter specified by the param object.
     *
     * @param aManga
     */
    public void updateLibraryItem(Manga aManga)
    {
        int lPosition;
        //updates item, adds item if not in list and following
        if ((lPosition = mFilteredData.indexOf(aManga)) != -1)
        {
            mFilteredData.remove(lPosition);
            if (aManga.getFollowing()) mFilteredData.add(lPosition, aManga);
            notifyItemChanged(lPosition);
        }
        else if (aManga.getFollowing())
        {
            mFilteredData.add(aManga);
            Collections.sort(mFilteredData, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            notifyDataSetChanged();
        }

        //updates item, adds item if not in list and following
        if ((lPosition = mOriginalData.indexOf(aManga)) != -1)
        {
            mOriginalData.remove(lPosition);
            if (aManga.getFollowing()) mOriginalData.add(lPosition, aManga);
            notifyDataSetChanged();
        }
        else if (aManga.getFollowing())
        {
            mOriginalData.add(aManga);
            Collections.sort(mOriginalData, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            notifyDataSetChanged();
        }
    }

    /***
     * This function performs the text search filter on the current set of adapter data.
     *
     * @param aQuery
     */
    public void performTextFilter(String aQuery)
    {
        mFilter.filter(aQuery);
    }

    /***
     * This function returns the original set of data of the adapter.
     *
     * @return
     */
    public ArrayList<Manga> getData()
    {
        return mOriginalData;
    }

    /***
     * This function returns the original set of data of the adapter.
     *
     * @return
     */
    public ArrayList<Manga> getOriginalData()
    {
        return mOriginalData;
    }

    /***
     * This function updates the original adapter data set to the specified list.
     *
     * @param aData
     */
    public void setOriginalData(ArrayList<Manga> aData)
    {
        //Reset both sets of data
        mOriginalData = new ArrayList<>(aData);
        mFilteredData = new ArrayList<>(aData);
        //
        getFilter().filter(mFilter.mLastQuery);
    }

    /**
     * This function returns the adapter filter object.
     *
     * @return
     */
    public Filter getFilter()
    {
        return mFilter;
    }

    /***
     * This function filters the adapter data by a specified FilterType status.
     * @param aFilterType
     * @return
     */
    public boolean filterByStatus(MangaEnums.eFilterStatus aFilterType)
    {
        boolean lResult = true;

        mFilter.filterByStatus(aFilterType);
        mFilter.filter(mFilter.mLastQuery);
        notifyDataSetChanged();

        if (BuildConfig.DEBUG)
        {
            if (mFilteredData.size() == mOriginalData.size() && aFilterType != MangaEnums.eFilterStatus.NONE)
            {
                lResult = false;
            }
        }

        return lResult;
    }

    /***
     * This function returns the character shown in the fast scroller bubble
     *
     * @param position The current position in the recycler view
     * @return The character to the be displayed in the fast scroll bubble
     */
    @Override
    public String getSectionText(int position)
    {
        char lChar = mFilteredData.get(position).getTitle().charAt(0);
        if (!Character.isLetterOrDigit(lChar))
            return "#";
        return Character.toString(lChar);
    }

    /***
     * This interface defines the function to be used when an item is selected from this adapter.
     */
    public interface ItemSelectedListener
    {
        void onItemSelected(int aPosition);
    }

    /***
     * This class defines the Space Decoration of the RecyclerView that will use this adapter.
     */
    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {

        private int lHalfSpace;

        /***
         * TODO..
         *
         * @param space
         */
        public SpacesItemDecoration(int space)
        {
            this.lHalfSpace = space / 2;
        }

        /***
         * TODO..
         *
         * @param aOutRect
         * @param aView
         * @param aParent
         * @param aState
         */
        @Override
        public void getItemOffsets(Rect aOutRect, View aView, RecyclerView aParent, RecyclerView.State aState)
        {

            if (aParent.getPaddingLeft() != lHalfSpace)
            {
                aParent.setPadding(lHalfSpace, lHalfSpace, lHalfSpace, lHalfSpace);
                aParent.setClipToPadding(false);
            }

            aOutRect.top = lHalfSpace;
            aOutRect.bottom = lHalfSpace;
            aOutRect.left = lHalfSpace;
            aOutRect.right = lHalfSpace;
        }
    }

    /***
     * This class is the view holder for item data in this adapter.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView mTextView;
        public ImageView mImageView;
        public LinearLayout mLayoutFooter;

        /***
         * This is the ViewHolder constructor
         *
         * @param aView
         */
        public ViewHolder(View aView)
        {
            super(aView);
            mTextView = (TextView) aView.findViewById(R.id.itemTitleField);
            mImageView = (ImageView) aView.findViewById(R.id.imageView);
            mLayoutFooter = (LinearLayout) aView.findViewById(R.id.footerLinearLayout);
            aView.setOnClickListener(this);
        }

        /***
         * This function performs the ViewHolders item select.
         *
         * @param aView
         */
        @Override
        public void onClick(View aView)
        {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(getAdapterPosition());
        }

    }

    /***
     * This class defines the Filter used to de-limit data that is viewed from this adapter.
     */
    public class TextFilter extends Filter
    {
        public CharSequence mLastQuery = "";
        public MangaEnums.eFilterStatus mLastFilter = MangaEnums.eFilterStatus.NONE;

        /***
         * This function performs the text filter based on the specified CharSequence.
         *
         * @param aFilterText
         * @return
         */
        @Override
        protected FilterResults performFiltering(CharSequence aFilterText)
        {

            String lFilterString = aFilterText.toString().toLowerCase();
            FilterResults lResult = new FilterResults();

            final ArrayList<Manga> lBaseData = mOriginalData;

            int lCount = lBaseData.size();
            final ArrayList<Manga> lFilteredList = new ArrayList<>(lCount);

            String filterableString;
            Manga lManga;
            for (int iIndex = 0; iIndex < lCount; iIndex++)
            {
                lManga = lBaseData.get(iIndex);

                //Filter by Title and Alternate titles
                filterableString = lManga.toString();
                if (lManga.getAlternate() != null) filterableString += ", " + lBaseData.get(iIndex).getAlternate();

                if (filterableString.toLowerCase().contains(lFilterString))
                {
                    //Filter Type NONE
                    if (mLastFilter == MangaEnums.eFilterStatus.NONE)
                    {
                        lFilteredList.add(lBaseData.get(iIndex));
                    }
                    //Filter TYPE READING, COMPLETE, AND ON_HOLD
                    else if (mLastFilter == MangaEnums.eFilterStatus.FOLLOWING)
                    {
                        if (lManga.getFollowingValue() > 0) lFilteredList.add(lBaseData.get(iIndex));
                    }
                    //Filter Type SPECIFIC
                    else if (lManga.getFollowingValue() == mLastFilter.getValue())
                    {
                        lFilteredList.add(lBaseData.get(iIndex));
                    }
                }
            }

            lResult.values = lFilteredList;
            lResult.count = lFilteredList.size();

            mLastQuery = aFilterText.toString();
            return lResult;
        }

        /***
         * This function sets the result of the filtering to the mFilteredData class variable.
         *
         * @param aFilterText
         * @param aFilterResult
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence aFilterText, FilterResults aFilterResult)
        {
            mFilteredData = (ArrayList<Manga>) aFilterResult.values;
            notifyDataSetChanged();
        }

        /***
         * This function performs the filter specified by the FilterStatus parameter.
         *
         * @param aFilterType
         */
        public void filterByStatus(MangaEnums.eFilterStatus aFilterType)
        {
            mLastFilter = aFilterType;
        }


    }

}
