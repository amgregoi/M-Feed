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

public class ExpandableListAdapter extends BaseExpandableListAdapter
{

    private Activity mContext;
    private Map<String, List<String>> mSourceCollection;
    private List<String> mDrawerItems;

    /***
     * TODO..
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
     * TODO..
     *
     * @param aGroupPosition
     * @param aChildPosition
     * @return
     */
    public Object getChild(int aGroupPosition, int aChildPosition)
    {
        return mSourceCollection.get(mDrawerItems.get(aGroupPosition)).get(aChildPosition);
    }

    /***
     * TODO..
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
     * TODO..
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

        TextView lSourceTextView = (TextView) aConvertView.findViewById(R.id.sourceTitle);
        lSourceTextView.setText(lSource);
        return aConvertView;
    }

    /***
     * TODO..
     *
     * @param aGroupPosition
     * @return
     */
    public int getChildrenCount(int aGroupPosition)
    {
        return mSourceCollection.get(mDrawerItems.get(aGroupPosition)).size();
    }

    /***
     * TODO..
     *
     * @param aGroupPosition
     * @return
     */
    public Object getGroup(int aGroupPosition)
    {
        return mDrawerItems.get(aGroupPosition);
    }

    /***
     * TODO..
     *
     * @return
     */
    public int getGroupCount()
    {
        return mDrawerItems.size();
    }

    /***
     * TODO..
     *
     * @param aGroupPosition
     * @return
     */
    public long getGroupId(int aGroupPosition)
    {
        return aGroupPosition;
    }

    /***
     * TODO..
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
     * TODO..
     *
     * @return
     */
    public boolean hasStableIds()
    {
        return true;
    }

    /***
     * TODO..
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
