package com.teioh.m_feed.MainPackage.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.R;

import java.util.ArrayList;

// The standard text view adapter only seems to search from the beginning of whole words
// so we've had to write this whole class to make it possible to search
// for parts of the arbitrary string we want
public class SearchableAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Manga> originalData = null;
    private ArrayList<Manga> filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
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
            row = mInflater.inflate(R.layout.list_item, null);

            holder = new MangaHolder();
            holder.txt = (TextView) row.findViewById(R.id.itemTitleField);
            holder.img = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (MangaHolder) row.getTag();
        }

        Manga tManga = filteredData.get(position);

        //Picasso.with(context).load(tManga.getPicUrl()).resize(139, 200).into(holder.img);
        if(tManga == null)
        {
            return row;
        }
        Glide.with(context).load(tManga.getPicUrl()).into(holder.img);
        holder.txt.setText(tManga.toString());
        return row;
    }

    static class MangaHolder {
        TextView txt;
        ImageView img;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            final ArrayList<Manga> list = originalData;

            int count = list.size();
            final ArrayList<Manga> nlist = new ArrayList<>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).toString();
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

    }
}