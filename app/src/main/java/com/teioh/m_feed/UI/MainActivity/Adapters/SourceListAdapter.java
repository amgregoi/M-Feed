package com.teioh.m_feed.UI.MainActivity.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teioh.m_feed.R;
import com.teioh.m_feed.WebSources.WebSource;

import java.util.ArrayList;

public class SourceListAdapter extends ArrayAdapter {

    private ArrayList<String> wSourceList;
    private LayoutInflater mInflater;
    private Context context;
    private int layoutResource;

    public SourceListAdapter(Context context, int resource) {
        super(context, resource, WebSource.getSourceList());
        this.layoutResource =  resource;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.wSourceList = new ArrayList<>(WebSource.getSourceList());

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SourceHolder holder;

        if (row == null) {
            row = mInflater.inflate(layoutResource, null);

            holder = new SourceHolder();
            holder.wSourceTitle = (TextView) row.findViewById(R.id.sourceTitle);
            holder.wIcon = (ImageView) row.findViewById(R.id.sourceIcon);
            row.setTag(holder);
        } else {
            holder = (SourceHolder) row.getTag();
        }

        holder.wSourceTitle.setText(wSourceList.get(position));
        holder.wIcon.setImageDrawable(context.getDrawable(R.drawable.ic_box_uncheck));
        return row;
    }

    static class SourceHolder {
        TextView wSourceTitle;
        ImageView wIcon;
    }
}
