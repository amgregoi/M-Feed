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

    private Activity mContext;
    private Map<String, List<String>> mSourceCollection;
    private List<String> mDrawerItems;

    public ExpandableListAdapter(Activity aContext, List<String> aDrawerItems, Map<String, List<String>> aSourceCollection) {
        mContext = aContext;
        mSourceCollection = aSourceCollection;
        mDrawerItems = aDrawerItems;
    }

    public Object getChild(int aGroupPosition, int aChildPosition) {
        return mSourceCollection.get(mDrawerItems.get(aGroupPosition)).get(aChildPosition);
    }

    public long getChildId(int aGroupPosition, int aChildPosition) {
        return aChildPosition;
    }


    public View getChildView(final int aGroupPosition, final int aChildPosition, boolean aLastChild, View aConvertView, ViewGroup aParent) {
        final String lSource = (String) getChild(aGroupPosition, aChildPosition);
        LayoutInflater lInflater = mContext.getLayoutInflater();

        if (aConvertView == null) {
            aConvertView = lInflater.inflate(R.layout.drawer_source_list_item, null);
        }

        TextView lSourceTextView = (TextView) aConvertView.findViewById(R.id.sourceTitle);
        lSourceTextView.setText(lSource);
        return aConvertView;
    }

    public int getChildrenCount(int aGroupPosition) {
        return mSourceCollection.get(mDrawerItems.get(aGroupPosition)).size();
    }

    public Object getGroup(int aGroupPosition) {
        return mDrawerItems.get(aGroupPosition);
    }

    public int getGroupCount() {
        return mDrawerItems.size();
    }

    public long getGroupId(int aGroupPosition) {
        return aGroupPosition;
    }

    public View getGroupView(int aGroupPosition, boolean aExpanded, View aConvertView, ViewGroup aParent) {
        String lSource = (String) getGroup(aGroupPosition);

        if (aConvertView == null) {
            LayoutInflater lInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            aConvertView = lInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView lSourceTextView = (TextView) aConvertView.findViewById(R.id.sourceTitle);
        lSourceTextView.setTypeface(null, Typeface.BOLD);
        lSourceTextView.setText(lSource);
        return aConvertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int aGroupPosition, int aChildPosition) {
        return true;
    }
}
