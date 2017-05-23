package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.content.Context;
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
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;
import com.teioh.m_feed.Utils.MangaLogger;

import java.util.ArrayList;
import java.util.Collections;

public class RecycleSearchAdapter extends RecyclerView.Adapter<RecycleSearchAdapter.ViewHolder> implements SectionTitleProvider
{
    private final static String TAG = RecycleSearchAdapter.class.getSimpleName();

    private final ItemSelectedListener mListener;
    private ArrayList<Manga> mOriginalData = null;
    private ArrayList<Manga> mFilteredData = null;
    private TextFilter mFilter = new TextFilter();

    /***
     * TODO..
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
     * TODO..
     *
     * @param position
     * @return
     */
    @Override
    public String getSectionTitle(int position)
    {
        return mFilteredData.get(position).toString().substring(0, 1);
    }

    /***
     * TODO..
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
     * TODO..
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
             .load(lMangaItem.getPicUrl())
             .apply(lOptions)
             .transition(new GenericTransitionOptions<>().transition(android.R.anim.fade_in))
             .into(new DrawableImageViewTarget(aHolder.mImageView)
             {
                 @Override
                 public void onResourceReady(Drawable resource, Transition<? super Drawable> animation)
                 {
                     super.onResourceReady(resource, animation);
                     aHolder.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

                 }

                 @Override public void onLoadFailed(@Nullable Drawable errorDrawable)
                 {
                     super.onLoadFailed(errorDrawable);
                     MangaLogger.logError(TAG, "OnBindViewHolder.OnLoadFailed()", errorDrawable.toString(), "url=" + lMangaItem.getPicUrl());
                 }
             });

        aHolder.mTextView.setText(lMangaItem.toString());
    }

    /***
     * TODO..
     *
     * @param aPosition
     * @return
     */
    public long getItemId(int aPosition)
    {
        return aPosition;
    }

    /***
     * TODO..
     *
     * @return
     */
    @Override
    public int getItemCount()
    {
        return mFilteredData.size();
    }

    /***
     * TODO..
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
     * TODO..
     *
     * @param aPosition
     * @return
     */
    public Manga getItemAt(int aPosition)
    {
        return mFilteredData.get(aPosition);
    }

    /***
     * TODO..
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
     * TODO..
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
     * TODO..
     *
     * @param aQuery
     */
    public void performTextFilter(String aQuery)
    {
        mFilter.filter(aQuery);
    }

    /***
     * TODO..
     *
     * @return
     */
    public ArrayList<Manga> getData()
    {
        return mOriginalData;
    }

    /**
     * Filter
     *
     * @return
     */
    public Filter getFilter()
    {
        return mFilter;
    }

    public ArrayList<Manga> getOriginalData()
    {
        return mOriginalData;
    }

    /***
     * TODO..
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

    public void filterByStatus(MangaEnums.eFilterStatus aFilterType)
    {
        mFilter.filterByStatus(aFilterType);
        mFilter.filter(mFilter.mLastQuery);
        notifyDataSetChanged();
    }

    /***
     * TODO..
     */
    public interface ItemSelectedListener
    {
        void onItemSelected(int aPosition);
    }

    /***
     * TOOD..
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
     * TODO..
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView mTextView;
        public ImageView mImageView;
        public LinearLayout mLayoutFooter;

        /***
         * TODO..
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
         * TODO..
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
     * TODO..
     */
    public class TextFilter extends Filter
    {
        public CharSequence mLastQuery = "";
        public MangaEnums.eFilterStatus mLastFilter = MangaEnums.eFilterStatus.NONE;

        /***
         * TODO..
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
         * TODO..
         *
         * @param aFilterText
         * @param aFilterResult
         */
        @SuppressWarnings( "unchecked" )
        @Override
        protected void publishResults(CharSequence aFilterText, FilterResults aFilterResult)
        {
            mFilteredData = (ArrayList<Manga>) aFilterResult.values;
            notifyDataSetChanged();
        }

        /***
         * TODO..
         *
         * @param aFilterType
         */
        public void filterByStatus(MangaEnums.eFilterStatus aFilterType)
        {
            mLastFilter = aFilterType;
        }


    }

}
