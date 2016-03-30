package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
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
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import java.util.ArrayList;
import java.util.List;

// The standard text view adapter only seems to search from the beginning of whole words
// so we've had to write this whole class to make it possible to search
// for parts of the arbitrary string we want
public class SearchableAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Manga> originalData = null;
    private ArrayList<Manga> filteredData = null;
    private LayoutInflater mInflater;
    private TextFilter mFilter = new TextFilter();
    private Context context;

    public SearchableAdapter(Context context, ArrayList<Manga> data) {
        this.context = context;
        this.filteredData = data;
        this.originalData = data;
        mInflater = LayoutInflater.from(context);

    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MangaHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.gridview_manga_item, null);

            holder = new MangaHolder();
            holder.txt = (TextView) row.findViewById(R.id.itemTitleField);
            holder.img = (ImageView) row.findViewById(R.id.imageView);
            holder.follow = (ImageView) row.findViewById(R.id.followStatus);
            holder.footer = (LinearLayout) row.findViewById(R.id.footerLinearLayout);
            holder.card = (CardView) row.findViewById(R.id.card_view);
            row.setTag(holder);
        } else {
            holder = (MangaHolder) row.getTag();
        }

        Manga tManga = filteredData.get(position);

        if (tManga == null) {
            return row;
        }

        if (tManga.getFollowing()) {
            holder.follow.setVisibility(View.VISIBLE);
            holder.footer.setBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
            holder.txt.setBackgroundColor(context.getResources().getColor(R.color.ColorPrimary));
            holder.txt.setTextColor(context.getResources().getColor(R.color.white));

        } else {
            holder.follow.setVisibility(View.GONE);
            holder.footer.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.txt.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.txt.setTextColor(context.getResources().getColor(R.color.black));
        }

        //Picasso.with(context).load(tManga.getPicUrl()).resize(139, 200).into(holder.img);
        Glide.with(context).load(tManga.getPicUrl()).into(holder.img);
        holder.txt.setText(tManga.toString());
        return row;
    }

    static class MangaHolder {
        TextView txt;
        ImageView img;
        ImageView follow;
        LinearLayout footer;
        CardView card;
    }

    public Filter getFilter() {
        return mFilter;
    }

    public void filterByStatus(int filter) {
        mFilter.filterByStatus(filter);
    }

    public class TextFilter extends Filter {
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
}