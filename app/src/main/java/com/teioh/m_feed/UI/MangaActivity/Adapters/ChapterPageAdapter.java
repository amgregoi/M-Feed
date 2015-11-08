package com.teioh.m_feed.UI.MangaActivity.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import com.teioh.m_feed.R;

import java.util.List;


public class ChapterPageAdapter extends PagerAdapter {
    private Context context;
    private List<String> imageUrls;
    private LayoutInflater inflater;

    public ChapterPageAdapter(Context c, List<String> imagePaths) {
        this.context = c;
        this.imageUrls = imagePaths;
    }

    @Override public int getCount() {
        return this.imageUrls.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.chapter_page_image, container, false);

//        Picasso.with(context).load(imageUrls.get(position)).into(imgDisplay);

        WebView webView = (WebView) viewLayout.findViewById(R.id.web);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        String page = "<html><body> <img src =\""+imageUrls.get(position)+"\" width=\"100%\"> </body></html>";
        webView.loadData(page, "text/html", null);

        (container).addView(viewLayout);
        return viewLayout;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((RelativeLayout) object);
    }



}
