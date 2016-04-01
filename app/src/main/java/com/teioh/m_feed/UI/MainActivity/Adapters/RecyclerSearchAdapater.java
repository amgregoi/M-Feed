package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecyclerSearchAdapater extends RecyclerView.Adapter<RecyclerSearchAdapater.ViewHolder> {

    private ArrayList<Manga> originalData = null;
    private ArrayList<Manga> filteredData = null;
    private LayoutInflater mInflater;
    private TextFilter mFilter = new TextFilter();
    private Context context;
    private final ItemSelectedListener mListener;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txt;
        public ImageView img;
        public LinearLayout footer;

        public ViewHolder(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(R.id.itemTitleField);
            img = (ImageView) itemView.findViewById(R.id.imageView);
            footer = (LinearLayout) itemView.findViewById(R.id.footerLinearLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(v, filteredData.get(getAdapterPosition()));
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Manga item);
    }

    public RecyclerSearchAdapater(Context context, ArrayList<Manga> data, ItemSelectedListener listener) {
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.originalData = new ArrayList<>(data);
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_manga_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Manga item = filteredData.get(position);

        if (item.getFollowing()) {
            holder.footer.setBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
            holder.txt.setBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
            holder.txt.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.footer.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.txt.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.txt.setTextColor(context.getResources().getColor(R.color.black));
        }

        //Picasso.with(context).load(tManga.getPicUrl()).resize(139, 200).into(holder.img);
        Glide.with(context)
                .load(item.getPicUrl())
                .animate(android.R.anim.fade_in)
                .into(new GlideDrawableImageViewTarget(holder.img) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        holder.img.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                });
        holder.txt.setText(item.toString());

    }

    public long getItemId(int position) {
        return position;
    }

    public void updateItem(int position, Manga manga){
        int pos = filteredData.indexOf(manga);
        filteredData.remove(pos);
        filteredData.add(pos, manga);

        notifyItemChanged(pos);

        pos = originalData.indexOf(manga);
        originalData.remove(pos);
        originalData.add(pos, manga);

    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public void setOriginalData(ArrayList<Manga> data) {
        this.originalData = new ArrayList<>(data);
        this.filteredData = new ArrayList<>(data);
        getFilter().filter(mFilter.lastQuery);

        notifyDataSetChanged();
    }


    public Filter getFilter() {
        return mFilter;
    }

    public void filterByStatus(int filter) {
        mFilter.filterByStatus(filter);
    }

    public class TextFilter extends Filter {
        public CharSequence lastQuery = "";
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            final ArrayList<Manga> list = originalData;

            int count = list.size();
            final ArrayList<Manga> nlist = new ArrayList<>(count);

            String filterableString;
            Manga manga;
            for (int i = 0; i < count; i++) {
                manga = list.get(i);

                //filter by title & alternate titles
                filterableString = manga.toString();
                if (manga.getmAlternate() != null)
                    filterableString += ", " + list.get(i).getmAlternate();

                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            lastQuery = constraint.toString();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Manga>) results.values;
            notifyDataSetChanged();
        }

        public void filterByStatus(int filter) {
            ArrayList<Manga> result = new ArrayList<>();

            //can later expand to plan to read, reading, on hold etc..
            if (filter == 0) {
                result = originalData;
            } else if (filter == 1) {
                for (Manga m : filteredData) {
                    if (m.getFollowing()) {
                        result.add(m);
                    }
                }
            }

            filteredData = result;
            notifyDataSetChanged();
        }


    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int halfSpace;

        public SpacesItemDecoration(int space) {
            this.halfSpace = space / 2;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if (parent.getPaddingLeft() != halfSpace) {
                parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
                parent.setClipToPadding(false);
            }

            outRect.top = halfSpace;
            outRect.bottom = halfSpace;
            outRect.left = halfSpace;
            outRect.right = halfSpace;
        }
    }

}
