package com.teioh.m_feed.UI.ReaderActivity.Widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;
import com.teioh.m_feed.Utils.SharedPrefs;

public class GestureViewPager extends ViewPager implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private GestureImageView mGestureImageView;
    private GestureDetector mGestureDetector;
    private OnSingleTapListener mSingleTapListener;

    private boolean mVertical;

    public GestureViewPager(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(getContext(), this);
        setScrollerType();
    }

    public GestureViewPager(Context aContext, AttributeSet aAttributeSet) {
        super(aContext, aAttributeSet);
        mGestureDetector = new GestureDetector(getContext(), this);
        setScrollerType();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent aEvent) {
        fetchGestureImageViewByTag();
        mGestureDetector.onTouchEvent(aEvent);

        if (mGestureImageView != null) {
            if (!mGestureImageView.canScrollParent(mVertical)) {
                return false;
            }
        }
        if (mVertical) {
            boolean lResult = super.onInterceptTouchEvent(swapXY(aEvent));
            swapXY(aEvent); // return touch coordinates to original reference frame for any child views
            return lResult;
        } else {
            return super.onInterceptTouchEvent(aEvent);
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent aEvent) {
        if (mGestureImageView != null) {
            if (mGestureImageView.isInitialized()) {
                if (mGestureImageView.getScale() > mGestureImageView.MIN_SCALE) {
                    mGestureImageView.zoomToPoint(mGestureImageView.MIN_SCALE, getWidth() / 2, getHeight() / 2);
                } else if (mGestureImageView.getScale() < mGestureImageView.MED_SCALE) {
                    mGestureImageView.zoomToPoint(mGestureImageView.MED_SCALE, aEvent.getX(), aEvent.getY());
                }
            }
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent aEvent) {
        if (mGestureImageView != null) {
            if (mGestureImageView.isInitialized()) {
                mGestureImageView.cancelFling();
            }
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent aEvent, MotionEvent aEvent2, float aXDistance, float aYDistance) {
        if (mGestureImageView != null) {
            if (mGestureImageView.isInitialized()) {
                mGestureImageView.postTranslate(-aXDistance, -aYDistance);
            }
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent aEvent, MotionEvent aEvent2, float aXDistance, float aYDistance) {
        if (mGestureImageView != null) {
            if (mGestureImageView.isInitialized()) {
                mGestureImageView.startFling(aXDistance, aYDistance);
            }
        }
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent aEvent) {
        final float positionX = aEvent.getX();

        if (positionX < getWidth() * 0.2f) {
            decrememntCurrentItem();
        } else if (positionX > getWidth() * 0.8f) {
            incrementCurrentItem();
        } else {
            if (mSingleTapListener != null) {
                mSingleTapListener.onSingleTap();
            }
        }

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent aEvent) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent aEvent) {
    }

    @Override
    public void onShowPress(MotionEvent aEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent aEvent) {
        return false;
    }

    private void fetchGestureImageViewByTag() {
        mGestureImageView = (GestureImageView) findViewWithTag(ImagePageAdapter.TAG + ":" + getCurrentItem());
    }

    public void setOnSingleTapListener(OnSingleTapListener singleTapListener) {
        mSingleTapListener = singleTapListener;
    }

    public void incrementCurrentItem() {
        int position = getCurrentItem();
        if (getAdapter() != null) {
            if (position != getAdapter().getCount() - 1) {
                setCurrentItem(position + 1, true);
            }
        }
    }

    public void decrememntCurrentItem() {
        int position = getCurrentItem();
        if (position != 0) {
            setCurrentItem(position - 1, true);
        }
    }

    public interface OnSingleTapListener {
        void onSingleTap();
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVertical) return super.onTouchEvent(swapXY(ev));
        else return super.onTouchEvent(ev);
    }

    public boolean toggleVerticalScroller() {
        return setScrollerType();
    }

    public boolean setScrollerType() {
        mVertical = SharedPrefs.getChapterScrollVertical();

        if (mVertical) {
            setPageTransformer(true, new VerticalPageTransformer());
            setOverScrollMode(OVER_SCROLL_IF_CONTENT_SCROLLS);
            return true;
        } else {
            setPageTransformer(true, null);
            return false;
        }
    }
}

class VerticalPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            view.setAlpha(1);

            // Counteract the default slide transition
            view.setTranslationX(view.getWidth() * -position);

            //set Y position to swipe in from top
            float yPosition = position * view.getHeight();
            view.setTranslationY(yPosition);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}