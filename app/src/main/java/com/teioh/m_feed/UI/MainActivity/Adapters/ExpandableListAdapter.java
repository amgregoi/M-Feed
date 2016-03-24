package com.teioh.m_feed.UI.MainActivity.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.teioh.m_feed.R;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> sourceCollections;
    private List<String> drawerItems;

    public ExpandableListAdapter(Activity context, List<String> drawerItems,
                                 Map<String, List<String>> laptopCollections) {
        this.context = context;
        this.sourceCollections = laptopCollections;
        this.drawerItems = drawerItems;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return sourceCollections.get(drawerItems.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String laptop = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_source_list_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.sourceTitle);
        item.setText(laptop);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return sourceCollections.get(drawerItems.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return drawerItems.get(groupPosition);
    }

    public int getGroupCount() {
        return drawerItems.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String source = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.sourceTitle);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(source);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
