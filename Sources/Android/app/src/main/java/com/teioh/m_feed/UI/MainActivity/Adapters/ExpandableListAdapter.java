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
import com.teioh.m_feed.WebSources.SourceBase;
import com.teioh.m_feed.WebSources.SourceFactory;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter
{

    private Activity mContext;
    private Map<String, List<String>> mSourceCollection;
    private List<String> mDrawerItems;

    /***
     * This is the constructor for the Expandable List Adapter.
     *
     * @param aContext
     * @param aDrawerItems
     * @param aSourceCollection
     */
    public ExpandableListAdapter(Activity aContext, List<String> aDrawerItems, Map<String, List<String>> aSourceCollection)
    {
        mContext = aContext;
        mSourceCollection = aSourceCollection;
        mDrawerItems = aDrawerItems;
    }

    /***
     * This function returns the child of a parent item in the adapter.
     *
     * @param aGroupPosition The position of the parent view.
     * @param aChildPosition The position of the child view to be returned.
     * @return
     */
    public Object getChild(int aGroupPosition, int aChildPosition)
    {
        return mSourceCollection.get(mDrawerItems.get(aGroupPosition)).get(aChildPosition);
    }

    /***
     * This function returns the ID of the child item in the adapter..
     *
     * @param aGroupPosition
     * @param aChildPosition
     * @return
     */
    public long getChildId(int aGroupPosition, int aChildPosition)
    {
        return aChildPosition;
    }

    /***
     * This function returns the view of a child item in the adapter.
     *
     * @param aGroupPosition
     * @param aChildPosition
     * @param aLastChild
     * @param aConvertView
     * @param aParent
     * @return
     */
    public View getChildView(final int aGroupPosition, final int aChildPosition, boolean aLastChild, View aConvertView, ViewGroup aParent)
    {
        final String lSource = (String) getChild(aGroupPosition, aChildPosition);
        LayoutInflater lInflater = mContext.getLayoutInflater();

        if (aConvertView == null)
        {
            aConvertView = lInflater.inflate(R.layout.drawer_source_list_item, null);
        }

        View lBar = aConvertView.findViewById(R.id.bar);
        TextView lSourceTextView = (TextView) aConvertView.findViewById(R.id.sourceTitle);
        lSourceTextView.setText(lSource);

        if (lSource.equals(new SourceFactory().getSourceName()))
        {
            lSourceTextView.setTextColor(mContext.getResources().getColor(R.color.ColorAccent));
            lBar.setBackgroundColor(mContext.getResources().getColor(R.color.ColorAccent));
        }
        else
        {
            lSourceTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            lBar.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        
        return aConvertView;
    }

    /***
     * This function gets the child item count in a specified parent group.
     *
     * @param aGroupPosition
     * @return
     */
    public int getChildrenCount(int aGroupPosition)
    {
        return mSourceCollection.get(mDrawerItems.get(aGroupPosition)).size();
    }

    /***
     * This function returns the group (parent) item in the adapter.
     *
     * @param aGroupPosition
     * @return
     */
    public Object getGroup(int aGroupPosition)
    {
        return mDrawerItems.get(aGroupPosition);
    }

    /***
     * This function returns the group (parent) item count in the adapter.
     *
     * @return
     */
    public int getGroupCount()
    {
        return mDrawerItems.size();
    }

    /***
     * This function returns the group (parent) ID in the adapter.
     *
     * @param aGroupPosition
     * @return
     */
    public long getGroupId(int aGroupPosition)
    {
        return aGroupPosition;
    }

    /***
     * This function returns the view of the group (parent) in the adapter.
     *
     * @param aGroupPosition
     * @param aExpanded
     * @param aConvertView
     * @param aParent
     * @return
     */
    public View getGroupView(int aGroupPosition, boolean aExpanded, View aConvertView, ViewGroup aParent)
    {
        String lSource = (String) getGroup(aGroupPosition);

        if (aConvertView == null)
        {
            LayoutInflater lInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            aConvertView = lInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView lSourceTextView = (TextView) aConvertView.findViewById(R.id.sourceTitle);
        lSourceTextView.setTypeface(null, Typeface.BOLD);
        lSourceTextView.setText(lSource);
        return aConvertView;
    }

    /***
     * This function returns whether the adapter has stable Ids.
     *
     * @return
     */
    public boolean hasStableIds()
    {
        return true;
    }

    /***
     * This function returns whether a child item is selectable specified by the group and child position.
     *
     * @param aGroupPosition
     * @param aChildPosition
     * @return
     */
    public boolean isChildSelectable(int aGroupPosition, int aChildPosition)
    {
        return true;
    }
}
