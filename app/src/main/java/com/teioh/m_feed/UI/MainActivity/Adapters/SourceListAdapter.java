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

    public SourceListAdapter(Context context, int resource, ArrayList<String> contents) {
        super(context, resource, contents);
        this.layoutResource =  resource;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.wSourceList = new ArrayList<>(contents);

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SourceHolder holder;
        String curSource = WebSource.getwCurrentSource();
        if (row == null) {
            row = mInflater.inflate(layoutResource, null);

            holder = new SourceHolder();
            holder.wSourceTitle = (TextView) row.findViewById(R.id.sourceTitle);
            holder.wIcon = (ImageView) row.findViewById(R.id.sourceIcon);
            row.setTag(holder);
        } else {
            holder = (SourceHolder) row.getTag();
        }
        String text = wSourceList.get(position);
        holder.wSourceTitle.setText(text);

        //TODO - find a better way / update when I get icons
        if(text.equals("Logout")){
            holder.wIcon.setImageDrawable(context.getDrawable(R.drawable.ic_box_uncheck));
        }else if(text.equals("Advanced Search")){
            holder.wIcon.setImageDrawable(context.getDrawable(R.drawable.ic_user));
        }else {
            if(text.equals(curSource)) holder.wIcon.setImageDrawable(context.getDrawable(R.drawable.ic_box_check));
            else holder.wIcon.setImageDrawable(context.getDrawable(R.drawable.ic_box_uncheck));
        }

        return row;
    }

    static class SourceHolder {
        TextView wSourceTitle;
        ImageView wIcon;
    }
}
