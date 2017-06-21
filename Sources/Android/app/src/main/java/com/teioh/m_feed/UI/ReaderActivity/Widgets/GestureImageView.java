/*
 * https://github.com/jasonpolites/gesture-imageview
 * http://developer.android.com/reference/android/graphics/Matrix.html
 * forgot to copy other useful links
 */
package com.teioh.m_feed.UI.ReaderActivity.Widgets;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.OverScroller;

public class GestureImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener
{
    public static final float MIN_SCALE = 1.00f;
    public static final float MED_SCALE = 2.00f;
    public static final float MAX_SCALE = 3.00f;

    private static final float ZOOM_DURATION = 200f;
    private static final long RUNNABLE_DELAY_MS = 1000 / 60;

    private Matrix mBaseMatrix = new Matrix();
    private Matrix mSupplementaryMatrix = new Matrix();
    private Matrix mDisplayMatrix = new Matrix();
    private float[] mMatrixValues = new float[9];

    private float mBitmapWidth;
    private float mBitmapHeight;

    private Zoomable mZoomable;
    private Flingable mFlingable;
    private ScaleGestureDetector mScaleGestureDetector;

    private boolean mInitialized;

    public GestureImageView(Context aContext)
    {
        super(aContext);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    public GestureImageView(Context aContext, AttributeSet aAttributeSet)
    {
        super(aContext, aAttributeSet);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    public GestureImageView(Context aContext, AttributeSet aAttributeSet, int aDefinitionStyle)
    {
        super(aContext, aAttributeSet, aDefinitionStyle);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent aEvent)
    {
        mScaleGestureDetector.onTouchEvent(aEvent);
        return true;
    }

    @Override
    public void setImageDrawable(Drawable aDrawable)
    {
        super.setImageDrawable(aDrawable);

        if (aDrawable != null)
        {
            mBitmapWidth = aDrawable.getIntrinsicWidth();
            mBitmapHeight = aDrawable.getIntrinsicHeight();
        }
    }

    @Override
    public void setImageBitmap(Bitmap aBitmap)
    {
        super.setImageBitmap(aBitmap);

        if (aBitmap != null)
        {
            mBitmapWidth = aBitmap.getWidth();
            mBitmapHeight = aBitmap.getHeight();
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector aScaleGestureDetector)
    {
        if (mInitialized)
        {
            float lScale = getScale() * aScaleGestureDetector.getScaleFactor();
            zoomTo(lScale, aScaleGestureDetector.getFocusX(), aScaleGestureDetector.getFocusY());
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector aScaleGestureDetector)
    {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector aScaleGestureDetector)
    {
    }

    public float getScale()
    {
        return getScaleX(mSupplementaryMatrix);
    }

    public void zoomTo(float aScale, float aCenterX, float aCenterY)
    {
        if (aScale > MAX_SCALE)
        {
            aScale = MAX_SCALE;
        }
        if (aScale < MIN_SCALE)
        {
            aScale = MIN_SCALE;
        }

        float lOldScale = getScaleX(mSupplementaryMatrix);
        float lDeltaScale = aScale / lOldScale;

        mSupplementaryMatrix.postScale(lDeltaScale, lDeltaScale, aCenterX, aCenterY);
        setImageMatrix(getImageViewMatrix());
        center(true, true);
    }

    private float getScaleX(Matrix aMatrix)
    {
        aMatrix.getValues(mMatrixValues);
        return mMatrixValues[Matrix.MSCALE_X];
    }

    private Matrix getImageViewMatrix()
    {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSupplementaryMatrix);
        return mDisplayMatrix;
    }

    private void center(boolean aCenterHorizontal, boolean aCenterVertical)
    {
        Matrix lCurrentImageViewMatrix = getImageViewMatrix();

        RectF lDrawableRectangle = new RectF(0.00f, 0.00f, mBitmapWidth, mBitmapHeight);
        lCurrentImageViewMatrix.mapRect(lDrawableRectangle);

        float lHeight = lDrawableRectangle.height();
        float lWidth = lDrawableRectangle.width();

        float lDeltaX = 0, lDeltaY = 0;

        if (aCenterHorizontal)
        {
            int lViewWidth = getWidth();

            if (lWidth < lViewWidth) lDeltaX = (lViewWidth - lWidth) / 2 - lDrawableRectangle.left;
            else if (lDrawableRectangle.left > 0) lDeltaX = -lDrawableRectangle.left;
            else if (lDrawableRectangle.right < lViewWidth) lDeltaX = lViewWidth - lDrawableRectangle.right;
        }

        if (aCenterVertical)
        {
            int viewHeight = getHeight();

            if (lHeight < viewHeight) lDeltaY = (viewHeight - lHeight) / 2 - lDrawableRectangle.top;
            else if (lDrawableRectangle.top > 0) lDeltaY = -lDrawableRectangle.top;
            else if (lDrawableRectangle.bottom < viewHeight) lDeltaY = getHeight() - lDrawableRectangle.bottom;
        }

        mSupplementaryMatrix.postTranslate(lDeltaX, lDeltaY);
        setImageMatrix(getImageViewMatrix());
    }

    public void initializeView()
    {
        if (!mInitialized)
        {
            setScaleType(ScaleType.MATRIX);
            initializeBaseMatrix();
            setImageMatrix(getImageViewMatrix());
            mInitialized = true;
        }
    }

    private void initializeBaseMatrix()
    {
        mBaseMatrix.reset();
        float lWidthScale = Math.min(getWidth() / mBitmapWidth, 2.00f);

        mBaseMatrix.postScale(lWidthScale, lWidthScale);
        mBaseMatrix.postTranslate((getWidth() - mBitmapWidth * lWidthScale) / 2.00f, (getHeight() - mBitmapHeight * lWidthScale) / 2.00f);
        mInitialized = true;
    }

    public boolean isInitialized()
    {
        return mInitialized;
    }

    public void startFling(float aVelocityX, float aVelocityY)
    {
        if (mFlingable != null)
        {
            mFlingable.cancel();
        }

        mFlingable = new Flingable(aVelocityX, aVelocityY);
        postDelayed(mFlingable, RUNNABLE_DELAY_MS);
    }

    public void cancelFling()
    {
        if (mFlingable != null)
        {
            mFlingable.cancel();
        }
    }

    public void postTranslate(float aDeltaX, float aDeltaY)
    {
        mSupplementaryMatrix.postTranslate(aDeltaX, aDeltaY);
        center(true, true);
    }

    public void zoomToPoint(float aScale, float aPointX, float aPointY)
    {
        zoomTo(aScale, aPointX, aPointY, ZOOM_DURATION);
    }

    private void zoomTo(float aScale, float aCenterX, float aCenterY, float aDurationMs)
    {
        mZoomable = new Zoomable(aScale, aCenterX, aCenterY, aDurationMs);
        postDelayed(mZoomable, RUNNABLE_DELAY_MS);
    }

    public boolean canScrollParent(boolean aVertical)
    {
        if (mInitialized)
        {
            if (!aVertical)
            {
                if (getTransitionX(mDisplayMatrix) == 0)
                {
                    return true;
                }
                else if (mBitmapWidth * getScaleX(mDisplayMatrix) + getTransitionX(mDisplayMatrix) <= getWidth())
                {
                    return true;
                }
                return false;
            }
            else
            {
                if (getTransitionY(mDisplayMatrix) == 0)
                {
                    return true;
                }
                else if (mBitmapHeight * getScaleY(mDisplayMatrix) + getTransitionY(mDisplayMatrix) <= getHeight())
                {
                    return true;
                }
                return false;

            }
        }
        return true;
    }

    private float getTransitionX(Matrix aMatrix)
    {
        aMatrix.getValues(mMatrixValues);
        return mMatrixValues[Matrix.MTRANS_X];
    }

    private float getTransitionY(Matrix aMatrix)
    {
        aMatrix.getValues(mMatrixValues);
        return mMatrixValues[Matrix.MTRANS_Y];
    }

    private float getScaleY(Matrix aMatrix)
    {
        aMatrix.getValues(mMatrixValues);
        return mMatrixValues[Matrix.MSCALE_Y];
    }

    private class Flingable implements Runnable
    {
        private OverScroller mOverScroller;

        private int mCurrentX;
        private int mCurrentY;

        public Flingable(float aInputVelocityX, float aInputVelocityY)
        {
            mOverScroller = new OverScroller(getContext());

            int lStartX = (int) getTransitionX(mDisplayMatrix);
            int lStartY = (int) getTransitionY(mDisplayMatrix);

            int lVelocityX = (int) aInputVelocityX;
            int lVelocityY = (int) aInputVelocityY;

            int lViewWidth = getMeasuredWidth();
            int lViewHeight = getMeasuredHeight();

            int lMinX, lMaxX, lMinY, lMaxY;

            float lWidthScale = Math.min(getWidth() / mBitmapWidth, 2.00f);
            float lHeightScale = Math.min(getHeight() / mBitmapHeight, 2.00f);
            float lActualScale = Math.min(lWidthScale, lHeightScale);

            float lRedundantSpaceX = lViewWidth - (lActualScale * mBitmapWidth);
            float lRedundantSpaceY = lViewHeight - (lActualScale * mBitmapHeight);

            Rect lDrawableRectangle = new Rect(0, 0, (int) (mBitmapWidth * getScaleX(mDisplayMatrix)), (int) (mBitmapHeight * getScaleY(mDisplayMatrix)));
            int lDrawableWidth = lDrawableRectangle.width();
            int lDrawableHeight = lDrawableRectangle.height();


            if (lDrawableWidth > lViewWidth)
            {
                lMinX = lViewWidth - (int) lRedundantSpaceX - lDrawableWidth;
                lMaxX = 0;
            }
            else
            {
                lMinX = lStartX;
                lMaxX = lStartX;
            }

            if (lDrawableHeight > lViewHeight)
            {
                lMinY = lViewHeight - (int) lRedundantSpaceY - lDrawableHeight;
                lMaxY = 0;
            }
            else
            {
                lMinY = lStartY;
                lMaxY = lStartY;
            }

            mCurrentX = lStartX;
            mCurrentY = lStartY;

            mOverScroller.fling(lStartX, lStartY, lVelocityX, lVelocityY, lMinX, lMaxX, lMinY, lMaxY);
        }

        @Override
        public void run()
        {
            if (mOverScroller.isFinished())
            {
                return;
            }

            if (mOverScroller.computeScrollOffset())
            {
                int lNewX = mOverScroller.getCurrX();
                int lNewY = mOverScroller.getCurrY();

                int lTransX = lNewX - mCurrentX;
                int lTransY = lNewY - mCurrentY;

                mCurrentX = lNewX;
                mCurrentY = lNewY;

                postTranslate(lTransX, lTransY);
                postDelayed(this, RUNNABLE_DELAY_MS);
            }
        }

        public void cancel()
        {
            if (mOverScroller != null)
            {
                mOverScroller.forceFinished(true);
            }
        }
    }

    private class Zoomable implements Runnable
    {
        private float mOldScale;
        private float mCenterX, mCenterY;
        private float mZoomDuration; //MS
        private float mZoomRate;    //MS

        private long mStartTime;

        public Zoomable(float aScale, float aCenterX, float aCentery, float aDurationMs)
        {
            mOldScale = getScaleX(mSupplementaryMatrix);

            mCenterX = aCenterX;
            mCenterY = aCentery;

            mZoomDuration = aDurationMs;
            mZoomRate = (aScale - mOldScale) / mZoomDuration;

            mStartTime = System.currentTimeMillis();
        }

        @Override
        public void run()
        {
            float lCurrentMs = Math.min(mZoomDuration, System.currentTimeMillis() - mStartTime);
            float lTargetScale = mOldScale + (mZoomRate * lCurrentMs);
            zoomTo(lTargetScale, mCenterX, mCenterY);

            if (lCurrentMs < mZoomDuration)
            {
                postDelayed(this, RUNNABLE_DELAY_MS);
            }
        }
    }
}
