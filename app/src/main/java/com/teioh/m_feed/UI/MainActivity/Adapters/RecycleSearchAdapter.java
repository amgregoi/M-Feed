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
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.Collections;


public class RecycleSearchAdapter extends RecyclerView.Adapter<RecycleSearchAdapter.ViewHolder> {

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
            mListener.onItemSelected(getAdapterPosition());

        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(int pos);
    }

    public RecycleSearchAdapter(Context context, ArrayList<Manga> data, ItemSelectedListener listener) {
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.originalData = new ArrayList<>(data);
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_manga_grid_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Manga item = filteredData.get(position);

        switch (item.getFollowingValue()){
            case 1:
                holder.footer.setBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
                holder.txt.setBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
                holder.txt.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 2:
                holder.footer.setBackgroundColor(context.getResources().getColor(R.color.green));
                holder.txt.setBackgroundColor(context.getResources().getColor(R.color.green));
                holder.txt.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 3:
                holder.footer.setBackgroundColor(context.getResources().getColor(R.color.red));
                holder.txt.setBackgroundColor(context.getResources().getColor(R.color.red));
                holder.txt.setTextColor(context.getResources().getColor(R.color.white));
                break;
            default:
                holder.footer.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.txt.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.txt.setTextColor(context.getResources().getColor(R.color.black));
        }

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

    public Manga getItemAt(int pos) {
        return filteredData.get(pos);
    }

    public void updateItem(Manga manga) {
        int pos;
        if ((pos = filteredData.indexOf(manga)) != -1) {
            filteredData.remove(pos);
            filteredData.add(pos, manga);
            notifyItemChanged(pos);
        }

        if ((pos = originalData.indexOf(manga)) != -1) {
            originalData.remove(pos);
            originalData.add(pos, manga);
            notifyDataSetChanged();
        }

    }

    public void updateFollowedItem(Manga manga) {

        int pos;
        //updates item, adds item if not in list and following
        if ((pos = filteredData.indexOf(manga)) != -1) {
            filteredData.remove(pos);
            if (manga.getFollowing()) filteredData.add(pos, manga);
            notifyItemChanged(pos);
        }else if(manga.getFollowing()){
            filteredData.add(manga);
            Collections.sort(filteredData, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            notifyDataSetChanged();
        }

        //updates item, adds item if not in list and following
        if ((pos = originalData.indexOf(manga)) != -1) {
            originalData.remove(pos);
            if (manga.getFollowing()) originalData.add(pos, manga);
            notifyDataSetChanged();
        }else if(manga.getFollowing()){
            originalData.add(manga);
            Collections.sort(originalData, (emp1, emp2) -> emp1.getTitle().compareToIgnoreCase(emp2.getTitle()));
            notifyDataSetChanged();
        }
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

    public ArrayList<Manga> getData() {
        return originalData;
    }

    /**
     * Filter
     *
     * @return
     */
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
                if (manga.getAlternate() != null)
                    filterableString += ", " + list.get(i).getAlternate();

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
            } else if (filter == 5) {
                for (Manga m : originalData) {
                    if (m.getFollowingValue() > 0) {
                        result.add(m);
                    }
                }
            }else{
                for (Manga m : originalData) {
                    if (m.getFollowingValue() == filter) {
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
