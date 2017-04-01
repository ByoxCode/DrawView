package com.byox.drawview.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by IngMedina on 30/03/2017.
 */

public class ZoomRegionView extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {

    // LISTENER
    private OnZoomRegionListener mOnZoomRegionListener;

    // VARS
    private Bitmap mParentContent;
    private Paint mZoomedRegionPaint;

    private Bitmap mZoomOverlay;
    private Canvas mCanvasOverlay;

    private Rect mZoomedRegion;
    private Rect mSourceRect;
    private Rect mDestRect;

    private Paint mClearPaint;

    private int mCurrentMotionEvent = -1;
    private boolean mMoveZoomArea = false;

    private float mStartTouchX, mStartTouchY;

    public ZoomRegionView(@NonNull Context context) {
        super(context);

        initZoomRegion();
    }

    public ZoomRegionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initZoomRegion();
    }

    public ZoomRegionView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initZoomRegion();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mZoomedRegion != null) {
            canvas.drawBitmap(mParentContent, mSourceRect, mDestRect, null);
            canvas.drawRect(mDestRect, mZoomedRegionPaint);

            mZoomOverlay.eraseColor(Color.TRANSPARENT);
            mCanvasOverlay.drawRect(mDestRect, mZoomedRegionPaint);
            mCanvasOverlay.drawRect(mZoomedRegion, mClearPaint);

            canvas.drawBitmap(mZoomOverlay, 0, 0, null);
        }

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mCurrentMotionEvent = MotionEvent.ACTION_DOWN;
                mMoveZoomArea = false;

                if (touchX >= mZoomedRegion.left && touchX <= mZoomedRegion.right
                        && touchY >= mZoomedRegion.top && touchY <= mZoomedRegion.bottom) {
                    mMoveZoomArea = true;
                    mStartTouchX = touchX;
                    mStartTouchY = touchY;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if ((mCurrentMotionEvent == MotionEvent.ACTION_DOWN
                        || mCurrentMotionEvent == MotionEvent.ACTION_MOVE) && mMoveZoomArea){
                    mCurrentMotionEvent = MotionEvent.ACTION_MOVE;
                    Rect preview = new Rect(
                            mZoomedRegion.left + (int) (touchX - mStartTouchX),
                            mZoomedRegion.top + (int) (touchY - mStartTouchY),
                            mZoomedRegion.right + (int) ((touchX - mStartTouchX)),
                            mZoomedRegion.bottom + (int) ((touchY - mStartTouchY)));

                    if (preview.left >= 0 && preview.right <= getWidth()
                            && preview.top >= 0 && preview.bottom <= getHeight()) {
                        mZoomedRegion = preview;
                        invalidate();
                    }

                    mStartTouchX = touchX;
                    mStartTouchY = touchY;

                    if (mOnZoomRegionListener != null)
                        mOnZoomRegionListener.onZoomRegionMoved(mZoomedRegion);
                }
                break;

            case MotionEvent.ACTION_UP:
                mCurrentMotionEvent = MotionEvent.ACTION_UP;
                mMoveZoomArea = false;
                break;
        }
        return true;
    }

    // METHODS
    private void initZoomRegion() {
        mZoomedRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mZoomedRegionPaint.setColor(Color.BLACK);
        mZoomedRegionPaint.setAlpha(60);

        mClearPaint = new Paint();
        mClearPaint.setColor(Color.WHITE);
        mClearPaint.setAlpha(255);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

        setOnTouchListener(this);
    }

    public void drawZoomRegion(Bitmap parentContent, Rect sourceRect, float scaleFactor) {
        this.mParentContent = parentContent;
        mZoomedRegion = new Rect((int) (sourceRect.left / scaleFactor), (int) (sourceRect.top / scaleFactor),
                (int) (sourceRect.right / scaleFactor), (int) (sourceRect.bottom / scaleFactor));
        mSourceRect = new Rect(0, 0, mParentContent.getWidth(), mParentContent.getHeight());
        mDestRect = new Rect(0, 0, getWidth(), getHeight());

        Bitmap init = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mZoomOverlay = init.copy(Bitmap.Config.ARGB_8888, true);
        init.recycle();
        mCanvasOverlay = new Canvas(mZoomOverlay);

        invalidate();
    }

    // GETTERS & SETTERS
    private void setZoomedRegionStrokeWidth(float strokeWidth) {
        this.mZoomedRegionPaint.setStrokeWidth(strokeWidth);
    }

    private void setZoomedRegionStrokeColor(int strokeColor){
        this.mZoomedRegionPaint.setColor(strokeColor);
    }

    public float getZoomedRegionStrokeWidth() {
        return this.mZoomedRegionPaint.getStrokeWidth();
    }

    public int getZoomedRegionStrokeColor(){
        return this.mZoomedRegionPaint.getColor();
    }

    // LISTENER
    public void setOnZoomRegionListener(OnZoomRegionListener onZoomRegionListener){
        mOnZoomRegionListener = onZoomRegionListener;
    }

    public interface OnZoomRegionListener{
        void onZoomRegionMoved(Rect newRect);
    }
}
