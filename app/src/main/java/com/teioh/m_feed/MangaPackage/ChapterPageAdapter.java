package com.teioh.m_feed.MangaPackage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.squareup.picasso.Picasso;
import com.teioh.m_feed.R;

import java.util.List;

/**
 * Created by Asus1 on 11/4/2015.
 */
public class ChapterPageAdapter extends PagerAdapter {
    private Context context;
    private List<String> imageUrls;
    private LayoutInflater inflater;

    public ChapterPageAdapter(Context c, List<String> imagePaths) {
        this.context = c;
        this.imageUrls = imagePaths;
    }

    @Override
    public int getCount() {
        return this.imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.chapter_pager_item, container,
                false);


//        imgDisplay = (CustomImageView) viewLayout.findViewById(R.id.chapter_image);
//        Picasso.with(context).load(imageUrls.get(position)).into(imgDisplay);

        WebView webView = (WebView) viewLayout.findViewById(R.id.web);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        String summary = "<html><body> <img src =\""+imageUrls.get(position)+"\" width=\"100%\"> </body></html>";
        webView.loadData(summary, "text/html", null);


        (container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((RelativeLayout) object);
    }



}
