//package com.teioh.m_feed.UI.ReaderActivity.Adapters;
//
//import android.content.Context;
//import android.support.v4.view.PagerAdapter;
//import android.text.method.ScrollingMovementMethod;
//import android.util.SparseArray;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.teioh.m_feed.R;
//import com.teioh.m_feed.UI.ReaderActivity.Widgets.GestureImageView;
//
//import java.util.List;
//
//
//public class NovelPageAdapter extends PagerAdapter
//{
//    final public static String TAG = ImagePageAdapter.class.getSimpleName();
//
//    private Context mContext;
//    private List<String> mTextPages;
//    private LayoutInflater mInflater;
//
//    private SparseArray<View> mTextViews = new SparseArray<>();
//
//    /***
//     * This is the constructor for the Image Page Adapter.
//     *
//     * @param aContext
//     * @param aTextPages
//     */
//    public NovelPageAdapter(Context aContext, List<String> aTextPages)
//    {
//        this.mContext = aContext;
//        this.mTextPages = aTextPages;
//    }
//
//    /***
//     * This function returns the count of pages in the chapter.
//     *
//     * @return
//     */
//    @Override
//    public int getCount()
//    {
//        return this.mTextPages.size();
//    }
//
//    /***
//     * This function instantiates the item specified by its position.
//     *
//     * @param aContainer
//     * @param aPosition
//     * @return
//     */
//    @Override
//    public Object instantiateItem(ViewGroup aContainer, int aPosition)
//    {
//        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View lView = mInflater.inflate(R.layout.reader_chapter_item, aContainer, false);
//
//        TextView mTextView = (TextView) lView.findViewById(R.id.novel_page_item);
//        GestureImageView mImageView = (GestureImageView) lView.findViewById(R.id.chapter_page_image_view);
//
//        mTextView.setText(mTextPages.get(aPosition));
//        mTextView.setMovementMethod(new ScrollingMovementMethod());
//        mImageView.setVisibility(View.GONE);
//
//        (aContainer).addView(lView);
//        mTextViews.put(aPosition, lView);
//        return lView;
//    }
//
//    /***
//     * This function destroys the item specified by its position.
//     *
//     * @param aContainer
//     * @param aPosition
//     * @param aObject
//     */
//    @Override
//    public void destroyItem(ViewGroup aContainer, int aPosition, Object aObject)
//    {
//        (aContainer).removeView((RelativeLayout) aObject);
//        mTextViews.remove(aPosition);
//    }
//
//    /***
//     *
//     *
//     * @param aView
//     * @param aObject
//     * @return
//     */
//    @Override
//    public boolean isViewFromObject(View aView, Object aObject)
//    {
//        return aView == (aObject);
//    }
//
//    /***
//     * This function adds an item to the adapter with the specified text.
//     *
//     */
//    public void addItem(String aPageText)
//    {
//        mTextPages.add(aPageText);
//        notifyDataSetChanged();
//    }
//}
