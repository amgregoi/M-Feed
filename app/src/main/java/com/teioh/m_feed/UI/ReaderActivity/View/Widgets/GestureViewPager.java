package com.teioh.m_feed.UI.ReaderActivity.View.Widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.teioh.m_feed.UI.ReaderActivity.Adapters.ImagePageAdapter;

public class GestureViewPager extends ViewPager implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

    private GestureImageView mGestureImageView;
    private GestureDetector mGestureDetector;
    private OnSingleTapListener mSingleTapListener;

    public GestureViewPager(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    public GestureViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
            fetchGestureImageViewByTag();
            mGestureDetector.onTouchEvent(event);

            if (mGestureImageView != null) {
                if (!mGestureImageView.canScrollParent()) {
                    return false;
                }
            }
            return super.onInterceptTouchEvent(event);
    }

    private void fetchGestureImageViewByTag() {
        mGestureImageView = (GestureImageView) findViewWithTag(ImagePageAdapter.TAG + ":" + getCurrentItem());
    }


    public void setOnSingleTapListener(OnSingleTapListener singleTapListener) {
        mSingleTapListener = singleTapListener;
    }


    @Override
    public boolean onDoubleTap(MotionEvent event) {
        if (mGestureImageView != null) {
            if (mGestureImageView.isInitialized()) {
                if (mGestureImageView.getScale() > mGestureImageView.MIN_SCALE) {
                    mGestureImageView.zoomToPoint(mGestureImageView.MIN_SCALE, getWidth() / 2, getHeight() / 2);
                } else if (mGestureImageView.getScale() < mGestureImageView.MAX_SCALE) {
                    mGestureImageView.zoomToPoint(mGestureImageView.MAX_SCALE, event.getX(), event.getY());
                }
            }
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        if (mGestureImageView != null) {
            if (mGestureImageView.isInitialized()) {
                mGestureImageView.cancelFling();
            }
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent event, MotionEvent event2, float v, float v2) {
        if (mGestureImageView != null) {
            if (mGestureImageView.isInitialized()) {
                mGestureImageView.postTranslate(-v, -v2);
            }
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event, MotionEvent event2, float v, float v2) {
        if (mGestureImageView != null) {
            if (mGestureImageView.isInitialized()) {
                mGestureImageView.startFling(v, v2);
            }
        }
        return true;
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        final float positionX = event.getX();

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

    public void incrementCurrentItem(){
        int position = getCurrentItem();
        if (position != getAdapter().getCount() - 1) {
            setCurrentItem(position + 1, true);
        }
    }

    public void decrememntCurrentItem(){
        int position = getCurrentItem();
        if (position != 0) {
            setCurrentItem(position - 1, true);
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
    }

    @Override
    public void onShowPress(MotionEvent event) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    public interface OnSingleTapListener {
        void onSingleTap();
    }
}