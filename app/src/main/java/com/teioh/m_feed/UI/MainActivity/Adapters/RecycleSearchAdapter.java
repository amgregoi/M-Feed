package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.Collections;

public class RecycleSearchAdapter extends RecyclerView.Adapter<RecycleSearchAdapter.ViewHolder> implements SectionTitleProvider{

    private ArrayList<Manga> mOriginalData = null;
    private ArrayList<Manga> mFilteredData = null;
    private TextFilter mFilter = new TextFilter();

    private final ItemSelectedListener mListener;

    @Override
    public String getSectionTitle(int position) {
        return mFilteredData.get(position).toString().substring(0, 1);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public ImageView mImageView;
        public LinearLayout mLayoutFooter;

        public ViewHolder(View aView) {
            super(aView);
            mTextView = (TextView) aView.findViewById(R.id.itemTitleField);
            mImageView = (ImageView) aView.findViewById(R.id.imageView);
            mLayoutFooter = (LinearLayout) aView.findViewById(R.id.footerLinearLayout);
            aView.setOnClickListener(this);
        }

        @Override
        public void onClick(View aView) {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(getAdapterPosition());
        }

    }

    public interface ItemSelectedListener {
        void onItemSelected(int aPosition);
    }

    public RecycleSearchAdapter(ArrayList<Manga> aData, ItemSelectedListener aListener) {
        mFilteredData = new ArrayList<>(aData);
        mOriginalData = new ArrayList<>(aData);
        mListener = aListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup aParent, int aViewType) {
        View v = LayoutInflater.from(aParent.getContext()).inflate(R.layout.main_manga_grid_item, aParent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onViewRecycled(ViewHolder aHolder) {
        super.onViewRecycled(aHolder);
        Glide.clear(aHolder.mImageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder aHolder, int aPosition) {
        Context lContext = aHolder.itemView.getContext();
        Manga lMangaItem = mFilteredData.get(aPosition);

        switch (lMangaItem.getFollowingValue()) {
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

        Glide.with(lContext)
                .load(lMangaItem.getPicUrl())
                .animate(android.R.anim.fade_in)
                .skipMemoryCache(true)
                .into(new GlideDrawableImageViewTarget(aHolder.mImageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        aHolder.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

                    }
                });

        aHolder.mTextView.setText(lMangaItem.toString());
    }

    public long getItemId(int aPosition) {
        return aPosition;
    }

    public Manga getItemAt(int aPosition) {
        return mFilteredData.get(aPosition);
    }

    public void updateItem(Manga aManga) {
        int lPosition;

        if ((lPosition = mFilteredData.indexOf(aManga)) != -1) {
            mFilteredData.remove(lPosition);
            mFilteredData.add(lPosition, aManga);
            notifyItemChanged(lPosition);
        }

        if ((lPosition = mOriginalData.indexOf(aManga)) != -1) {
            mOriginalData.remove(lPosition);
            mOriginalData.add(lPosition, aManga);
            notifyItemChanged(lPosition);
        }
    }

    public void updateFollowedItem(Manga aManga) {

        int lPosition;
        //updates item, adds item if not in list and following
        if ((lPosition = mFilteredData.indexOf(aManga)) != -1) {
            mFilteredData.remove(lPosition);
            if (aManga.getFollowing()) mFilteredData.add(lPosition, aManga);
            notifyItemChanged(lPosition);
        } else if (aManga.getFollowing()) {
            mFilteredData.add(aManga);
            Collections.sort(mFilteredData, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            notifyDataSetChanged();
        }

        //updates item, adds item if not in list and following
        if ((lPosition = mOriginalData.indexOf(aManga)) != -1) {
            mOriginalData.remove(lPosition);
            if (aManga.getFollowing()) mOriginalData.add(lPosition, aManga);
            notifyDataSetChanged();
        } else if (aManga.getFollowing()) {
            mOriginalData.add(aManga);
            Collections.sort(mOriginalData, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredData.size();
    }

    public void setOriginalData(ArrayList<Manga> aData) {
        this.mOriginalData = new ArrayList<>(aData);
        this.mFilteredData = new ArrayList<>(aData);
        getFilter().filter(mFilter.lastQuery);
        notifyDataSetChanged();
    }

    public ArrayList<Manga> getData() {
        return mOriginalData;
    }

    /**
     * Filter
     *
     * @return
     */
    public Filter getFilter() {
        return mFilter;
    }

    public void filterByStatus(int aFilterType) {
        mFilter.filterByStatus(aFilterType);
    }

    public class TextFilter extends Filter {
        public CharSequence lastQuery = "";

        @Override
        protected FilterResults performFiltering(CharSequence aFilterText) {

            String lFilterString = aFilterText.toString().toLowerCase();
            FilterResults lResult = new FilterResults();

            final ArrayList<Manga> lBaseData = mFilteredData;

            int lCount = lBaseData.size();
            final ArrayList<Manga> lFilteredList = new ArrayList<>(lCount);

            String filterableString;
            Manga manga;
            for (int i = 0; i < lCount; i++) {
                manga = lBaseData.get(i);

                //filter by title & alternate titles
                filterableString = manga.toString();
                if (manga.getAlternate() != null)
                    filterableString += ", " + lBaseData.get(i).getAlternate();

                if (filterableString.toLowerCase().contains(lFilterString)) {
                    lFilteredList.add(lBaseData.get(i));
                }
            }

            lResult.values = lFilteredList;
            lResult.count = lFilteredList.size();

            lastQuery = aFilterText.toString();
            return lResult;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence aFilterText, FilterResults aFilterResult) {
            mFilteredData = (ArrayList<Manga>) aFilterResult.values;
            notifyDataSetChanged();
        }

        public void filterByStatus(int aFilterType) {
            ArrayList<Manga> lResult = new ArrayList<>();

            //can later expand to plan to read, reading, on hold etc..
            mFilteredData = mOriginalData;
            getFilter().filter(mFilter.lastQuery);

            switch (aFilterType) {
                case 0:
                    // Everything
                    lResult = mOriginalData;
                    break;
                case 5:
                    // All types of followed manga
                    for (Manga m : mFilteredData) {
                        if (m.getFollowingValue() > 0) {
                            lResult.add(m);
                        }
                    }
                    break;
                default:
                    // Specific Follow type
                    for (Manga m : mFilteredData) {
                        if (m.getFollowingValue() == aFilterType) {
                            lResult.add(m);
                        }
                    }
            }

            mFilteredData = lResult;
            notifyDataSetChanged();
        }


    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int lHalfSpace;

        public SpacesItemDecoration(int space) {
            this.lHalfSpace = space / 2;
        }

        @Override
        public void getItemOffsets(Rect aOutRect, View aView, RecyclerView aParent, RecyclerView.State aState) {

            if (aParent.getPaddingLeft() != lHalfSpace) {
                aParent.setPadding(lHalfSpace, lHalfSpace, lHalfSpace, lHalfSpace);
                aParent.setClipToPadding(false);
            }

            aOutRect.top = lHalfSpace;
            aOutRect.bottom = lHalfSpace;
            aOutRect.left = lHalfSpace;
            aOutRect.right = lHalfSpace;
        }
    }

}
