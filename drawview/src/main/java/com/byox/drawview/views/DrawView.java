package com.byox.drawview.views;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.byox.drawview.R;
import com.byox.drawview.abstracts.DrawViewListener;
import com.byox.drawview.dictionaries.BackgroundImageData;
import com.byox.drawview.dictionaries.DrawCapture;
import com.byox.drawview.dictionaries.DrawData;
import com.byox.drawview.dictionaries.DrawMove;
import com.byox.drawview.enums.BackgroundScale;
import com.byox.drawview.enums.BackgroundType;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingOrientation;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.enums.ImageType;
import com.byox.drawview.interfaces.OnDrawViewCaptureListener;
import com.byox.drawview.interfaces.OnDrawViewListener;
import com.byox.drawview.utils.BitmapUtils;
import com.byox.drawview.utils.ImageLoader;
import com.byox.drawview.utils.MathUtils;
import com.byox.drawview.utils.MatrixUtils;
import com.byox.drawview.utils.SerializablePaint;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ing. Oscar G. Medina Cruz on 06/11/2016.
 * <p>
 * This view was created for draw or paint anything you want.
 * <p>
 * <p>
 * This view can be configured for change draw color, width size, can use tools like pen, line, circle, square.
 * </p>
 *
 * @author Ing. Oscar G. Medina Cruz
 */
public class DrawView extends FrameLayout implements View.OnTouchListener {

    //region CONSTANTS
    private final String TAG = DrawView.class.getSimpleName();
    private final int DEFAULT_BACKGROUND_QUALITY = 50;
    private final int DEFAULT_ZOOM_REGION_SCALE = 4;
    //endregion

    //region LISTENER
    private OnDrawViewListener mOnDrawViewListener;
    private OnDrawViewCaptureListener mOnDrawViewCaptureListener;
    private DrawViewListener mDrawViewListener;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    //endregion

    //region VARS
    private DrawData mDrawData;
    private BackgroundImageData mBackgroundImageData;

    private Rect mCanvasClipBounds;

    private Bitmap mContentBitmap;
    private Canvas mContentCanvas;

    private boolean mZoomEnabled = false;
    private float mZoomScrollStartX = 0f;
    private float mZoomScrollStartY = 0f;
    private float mZoomFactor = 1.0f;
    private float mZoomCenterX = -1.0f;
    private float mZoomCenterY = -1.0f;
    private float mMaxZoomFactor = 8f;
    private float mZoomRegionScale = 4f;
    private float mZoomRegionScaleMin = 2f;
    private float mZoomRegionScaleMax = 5f;
    private boolean mExcludeBackgroundFromErase = false;
    private int mUpTimes = 0;

    private int mLastTouchEvent = -1;

    private RectF mAuxRect;
    private PorterDuffXfermode mEraserXefferMode;
    private SerializablePaint mBackgroundPaint;

    private Rect mInvalidateRect;

    //endregion

    //region VIEWS
    private CardView mZoomRegionCardView;
    private ZoomRegionView mZoomRegionView;
    //endregion

    //region BACKGROUND TARGET
    private Target mBackgroundTarget
            = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            new ProcessBackgroundTask()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mBackgroundImageData, bitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            if (mOnDrawViewListener != null) mOnDrawViewListener.onDrawingError(e);
            if (mDrawViewListener != null) mDrawViewListener.onDrawingError(e);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private Callback mBackgroundCallback
            = new Callback() {
        @Override
        public void onSuccess() {
            Log.i(TAG, "Loading background from " + mBackgroundImageData.toString() + " successful!");
        }

        @Override
        public void onError(Exception e) {
            if (mOnDrawViewListener != null) mOnDrawViewListener.onDrawingError(e);
            if (mDrawViewListener != null) mDrawViewListener.onDrawingError(e);
        }
    };
    //endregion

    //region CONSTRUCTORS

    /**
     * Default constructor
     *
     * @param context     Application context
     * @param willNotDraw Enable it when use {@link DrawView} as a child of {@link DrawCameraView}
     */
    public DrawView(Context context, boolean willNotDraw) {
        super(context);
        initVars();
        setWillNotDraw(willNotDraw);
    }

    /**
     * Default constructor
     *
     * @param context Application context
     * @param attrs   Layout attributes
     */
    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Default constructor
     *
     * @param context      Application context
     * @param attrs        Layout attributes
     * @param defStyleAttr Layout default style attributes
     */
    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars();
        initAttributes(context, attrs);
    }

    /**
     * Default constructor
     *
     * @param context      Application context
     * @param attrs        Layout attributes
     * @param defStyleAttr Layout default style attributes
     * @param defStyleRes  Layout default style resources
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVars();
        initAttributes(context, attrs);
    }

    //endregion

    //region EVENTS

    /**
     * Draw custom content in the view
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mContentBitmap.eraseColor(Color.TRANSPARENT);

        if (isZoomEnabled()) {
            canvas.save();
            canvas.scale(mZoomFactor, mZoomFactor, mZoomCenterX, mZoomCenterY);
        }

        mContentCanvas.drawRect(0, 0, mContentBitmap.getWidth(), mContentBitmap.getHeight(), mBackgroundPaint);

        // Draw canvas background
        if (mDrawData.getDrawMoveHistory().size() > 0
                && mDrawData.getDrawMoveBackgroundIndex() != -1 && !mExcludeBackgroundFromErase)
            drawBackgroundImage(mContentCanvas, mDrawData.getDrawMoveHistory().get(mDrawData.getDrawMoveBackgroundIndex()));

        // Check all pending moves
        for (int i = 0; mDrawData.getDrawMoveHistory().size() > 0
                && i < mDrawData.getDrawMoveHistoryIndex() + 1; i++) {
            drawMove(mContentCanvas, mDrawData.getDrawMoveHistory().get(i));

            if (i == mDrawData.getDrawMoveHistory().size() - 1) {
                if (mOnDrawViewListener != null) mOnDrawViewListener.onAllMovesPainted();
                if (mDrawViewListener != null) mDrawViewListener.onAllMovesPainted();
            }
        }

        canvas.getClipBounds(mCanvasClipBounds);

        if (mDrawData.getDrawMoveBackgroundIndex() != -1 && mExcludeBackgroundFromErase)
            drawBackgroundImage(canvas, mDrawData.getDrawMoveHistory().get(mDrawData.getDrawMoveBackgroundIndex()));

        canvas.drawBitmap(mContentBitmap, 0, 0, null);

        if (isZoomEnabled()) {
            canvas.restore();

            if (mZoomRegionView != null) {
                mZoomRegionView.drawZoomRegion(mContentBitmap, mCanvasClipBounds, DEFAULT_ZOOM_REGION_SCALE);
            }
        }

        super.onDraw(canvas);
    }

    /**
     * Handle touch events in the view
     *
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mZoomEnabled) {
            mScaleGestureDetector.onTouchEvent(motionEvent);
            mGestureDetector.onTouchEvent(motionEvent);
        }

        float touchX = motionEvent.getX() / mZoomFactor + mCanvasClipBounds.left;
        float touchY = motionEvent.getY() / mZoomFactor + mCanvasClipBounds.top;

        int lastMoveIndex = 0;

        if (motionEvent.getPointerCount() == 1) {
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastMoveIndex = drawTouchDown(motionEvent, touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    lastMoveIndex = drawTouchMove(motionEvent, touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    lastMoveIndex = drawTouchUp(motionEvent, touchX, touchY);
                    break;
                default:
                    return false;
            }
        } else if (motionEvent.getPointerCount() == 2 && mZoomFactor > 1f) {
            mLastTouchEvent = -1;
            touchX = ((motionEvent.getX(0) + motionEvent.getX(1)) / 2)
                    / mZoomFactor + mCanvasClipBounds.left;
            touchY = ((motionEvent.getY(0) + motionEvent.getY(1)) / 2)
                    / mZoomFactor + mCanvasClipBounds.top;

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    mZoomScrollStartX = touchX;
                    mZoomScrollStartY = touchY;

                    // Check if the last move is only a point and delete it
                    DrawMove drawMove = mDrawData.getLastMove();
                    if (drawMove.getStartX() == drawMove.getEndX()
                            && drawMove.getStartY() == drawMove.getEndY() && canUndo()) {
                        undo();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveZoomedView(touchX, touchY);
                    break;
            }
        } else {
            mLastTouchEvent = -1;
        }

        if (lastMoveIndex >= 0 && mDrawData.getDrawMoveHistory().size() > 0
                && mDrawData.getDrawMoveHistory().get(lastMoveIndex) != null
                && mDrawData.getDrawMoveHistory().get(lastMoveIndex).getPaint() != null) {
            float aux = mDrawData.getDrawMoveHistory().get(lastMoveIndex).getPaint().getStrokeWidth() * 2;
            mInvalidateRect = new Rect((int) (touchX - aux), (int) (touchY - aux),
                    (int) (touchX + aux), (int) (touchY + aux));
        }

        if (mInvalidateRect != null) {
            this.invalidate(mInvalidateRect.left, mInvalidateRect.top, mInvalidateRect.right, mInvalidateRect.bottom);
        }
        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putSerializable(DrawData.DRAW_DATA, mDrawData);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mDrawData = (DrawData) bundle.getSerializable(DrawData.DRAW_DATA);
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
    //endregion

    //region PUBLIC METHODS

    /**
     * Restart all the parameters and drawing history
     *
     * @return If the draw view can be restarted
     */
    public boolean restartDrawing() {
        if (mDrawData != null && mDrawData.reset()) {
            invalidate();

            if (mOnDrawViewListener != null) mOnDrawViewListener.onClearDrawing();
            if (mDrawViewListener != null) mDrawViewListener.onClearDrawing();

            return true;
        }
        invalidate();
        return false;
    }

    /**
     * Undo last drawing action
     *
     * @return If the view can do the undo action
     */
    public boolean undo() {
        invalidate();
        return mDrawData.undo();
    }

    /**
     * Check if the draw view can do undo action
     *
     * @return If the view can do the undo action
     */
    public boolean canUndo() {
        return mDrawData.canUndo();
    }

    /**
     * Redo preview action
     *
     * @return If the view can do the redo action
     */
    public boolean redo() {
        invalidate();
        return mDrawData.redo();
    }

    /**
     * Check if the view can do the redo action
     *
     * @return if the view can do the redo action
     */
    public boolean canRedo() {
        return mDrawData.canRedo();
    }

    /**
     * Create capture of the drawing view into {@link DrawCapture} instance
     *
     * @param captureFormat {@link android.graphics.Bitmap.CompressFormat} to save capture
     */
    public void createCapture(Bitmap.CompressFormat captureFormat) {
        new ProcessCapture().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, captureFormat);
    }

    /**
     * Create capture and return the response into {@link OnDrawViewCaptureListener} listener
     *
     * @param captureFormat {@link android.graphics.Bitmap.CompressFormat} of the capture
     * @param onDrawViewCaptureListener {@link OnDrawViewCaptureListener} listener
     */
    public void createCapture(Bitmap.CompressFormat captureFormat, OnDrawViewCaptureListener onDrawViewCaptureListener) {
        mOnDrawViewCaptureListener = onDrawViewCaptureListener;
        new ProcessCapture().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, captureFormat);
    }

    /**
     * Refresh the text of the last movement item
     *
     * @param newText
     */
    public void drawText(String newText) {
        if (mDrawData.addText(newText)) {
            invalidate();
        } else {
            Log.e(TAG, "The last item that you want to refresh text isn't TEXT element.");
        }
    }

    /**
     * Start an async task for process the image for the background
     */
    public void processBackground() {
        if (mBackgroundImageData.getBackgroundType() == BackgroundType.BITMAP ||
                mBackgroundImageData.getBackgroundType() == BackgroundType.BYTES) {
            new ProcessBackgroundTask()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mBackgroundImageData);
        } else {
            ImageLoader.LoadImage(getContext(),
                    mBackgroundTarget,
                    ImageType.valueOf(mBackgroundImageData.getBackgroundType().toString()),
                    mBackgroundImageData.getBackground(),
                    mBackgroundImageData.getTransformations(),
                    mBackgroundCallback);
        }
    }
    //endregion

    //region PRIVATE METHODS

    /**
     * Initialize general vars for the view
     */
    private void initVars() {
        mDrawData = new DrawData();
        mDrawData.setDrawMoveHistory(new ArrayList<DrawMove>());
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        mCanvasClipBounds = new Rect();
        mAuxRect = new RectF();
        mEraserXefferMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        setOnTouchListener(this);

        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @SuppressLint("NewApi")
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        } else {
                            getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                        initZoomRegionView();
                    }
                });
    }

    /**
     * Init the ZoomRegionView for navigate into image when user zoom in
     */
    private void initZoomRegionView() {
        if (mZoomRegionView == null) {

            Bitmap init = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mContentBitmap = init.copy(Bitmap.Config.ARGB_8888, true);
            init.recycle();
            mContentCanvas = new Canvas(mContentBitmap);

            FrameLayout.LayoutParams layoutParams =
                    new LayoutParams(getWidth() / DEFAULT_ZOOM_REGION_SCALE,
                            getHeight() / DEFAULT_ZOOM_REGION_SCALE,
                            Gravity.TOP | Gravity.END);
            layoutParams.setMargins(24, 24, 24, 24);
            mZoomRegionCardView = new CardView(getContext());
            mZoomRegionCardView.setLayoutParams(layoutParams);
            mZoomRegionCardView.setPreventCornerOverlap(true);
            mZoomRegionCardView.setRadius(0f);
            mZoomRegionCardView.setUseCompatPadding(true);
            mZoomRegionCardView.setVisibility(View.INVISIBLE);

            CardView.LayoutParams childLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mZoomRegionView = new ZoomRegionView(getContext());
            mZoomRegionView.setLayoutParams(childLayoutParams);
            mZoomRegionView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            mZoomRegionCardView.addView(mZoomRegionView);
            addView(mZoomRegionCardView);
        }
    }

    /**
     * Initialize view attributes
     *
     * @param context Application context
     * @param attrs   {@link AttributeSet} of the view
     */
    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.DrawView, 0, 0);
        try {
            mDrawData.setDrawColor(typedArray.getColor(R.styleable.DrawView_dv_draw_color, Color.BLACK));
            mDrawData.setDrawWidth(typedArray.getInteger(R.styleable.DrawView_dv_draw_width, 3));
            mDrawData.setDrawAlpha(typedArray.getInteger(R.styleable.DrawView_dv_draw_alpha, 255));
            mDrawData.setAntiAlias(typedArray.getBoolean(R.styleable.DrawView_dv_draw_anti_alias, true));
            mDrawData.setDither(typedArray.getBoolean(R.styleable.DrawView_dv_draw_dither, true));
            switch (typedArray.getInteger(R.styleable.DrawView_dv_draw_style, 2)) {
                case 0:
                    mDrawData.setPaintStyle(SerializablePaint.Style.FILL);
                    break;
                case 1:
                    mDrawData.setPaintStyle(SerializablePaint.Style.FILL_AND_STROKE);
                    break;
                case 2:
                    mDrawData.setPaintStyle(SerializablePaint.Style.STROKE);
                    break;
            }

            switch (typedArray.getInteger(R.styleable.DrawView_dv_draw_corners, 2)) {
                case 0:
                    mDrawData.setLineCap(SerializablePaint.Cap.BUTT);
                    break;
                case 1:
                    mDrawData.setLineCap(SerializablePaint.Cap.ROUND);
                    break;
                case 2:
                    mDrawData.setLineCap(SerializablePaint.Cap.SQUARE);
                    break;
            }

            mDrawData.setFontFamilyIndex(
                    typedArray.getInteger(R.styleable.DrawView_dv_draw_font_family, 0));

            mDrawData.setFontSize(typedArray.getInteger(R.styleable.DrawView_dv_draw_font_size, 12));
            mDrawData.setForCamera(typedArray.getBoolean(R.styleable.DrawView_dv_draw_is_camera, false));
            int orientation = typedArray.getInteger(R.styleable.DrawView_dv_draw_orientation,
                    getWidth() > getHeight() ? 1 : 0);
            mDrawData.setInitialDrawingOrientation(DrawingOrientation.values()[orientation]);
            if (getBackground() != null && !mDrawData.isForCamera())
                try {
                    mDrawData.setBackgroundColor(((ColorDrawable) getBackground()).getColor());
                    setBackgroundColor(Color.TRANSPARENT);
                } catch (Exception e) {
                    e.printStackTrace();
                    setBackgroundColor(Color.TRANSPARENT);
                    mDrawData.setBackgroundColor(((ColorDrawable) getBackground()).getColor());
                    setBackgroundResource(R.drawable.drawable_transparent_pattern);
                }
            else {
                setBackgroundColor(Color.TRANSPARENT);
                mDrawData.setBackgroundColor(((ColorDrawable) getBackground()).getColor());
                if (!mDrawData.isForCamera())
                    setBackgroundResource(R.drawable.drawable_transparent_pattern);
            }

            mBackgroundPaint = new SerializablePaint();
            mBackgroundPaint.setStyle(SerializablePaint.Style.FILL);
            mBackgroundPaint.setColor(mDrawData.getBackgroundColor() != -1
                    ? mDrawData.getBackgroundColor() : Color.TRANSPARENT);

            mDrawData.setDrawingTool(DrawingTool.values()[typedArray.getInteger(R.styleable.DrawView_dv_draw_tool, 0)]);
            mDrawData.setDrawingMode(DrawingMode.values()[typedArray.getInteger(R.styleable.DrawView_dv_draw_mode, 0)]);
            mZoomEnabled = typedArray.getBoolean(R.styleable.DrawView_dv_draw_enable_zoom, false);
            mZoomRegionScale = typedArray.getFloat(R.styleable.DrawView_dv_draw_zoomregion_scale, mZoomRegionScale);
            mZoomRegionScaleMin = typedArray.getFloat(R.styleable.DrawView_dv_draw_zoomregion_minscale, mZoomRegionScaleMin);
            mZoomRegionScaleMax = typedArray.getFloat(R.styleable.DrawView_dv_draw_zoomregion_maxscale, mZoomRegionScaleMax);
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Make a {@link DrawMove} for {@link DrawView}
     *
     * @param drawMove Current {@link DrawMove}
     */
    private void drawMove(Canvas canvas, DrawMove drawMove) {
        if (drawMove.getDrawingMode() != null) {
            switch (drawMove.getDrawingMode()) {
                case DRAW:
                    switch (drawMove.getDrawingTool()) {
                        case PEN:
                            drawWithPen(canvas, drawMove);
                            break;
                        case LINE:
                            drawLine(canvas, drawMove);
                            break;
                        case ARROW:
                            drawArrow(canvas, drawMove);
                            break;
                        case RECTANGLE:
                            drawRectangle(canvas, drawMove);
                            break;
                        case TRIANGLE:
                            drawTriangle(canvas, drawMove);
                            break;
                        case CIRCLE:
                            drawCircle(canvas, drawMove);
                            break;
                        case ELLIPSE:
                            drawEllipse(canvas, drawMove);
                            break;
                        case SHAPE:
                            drawShape(canvas, drawMove);
                            break;
                        /*case STAR:
                            drawStar(canvas, drawMove);
                            break;*/
                    }
                    break;
                case TEXT:
                    drawText(canvas, drawMove);
                    break;
                case ERASER:
                    drawErase(canvas, drawMove);
                    break;
            }
        }
    }

    /**
     * Make touch down action for {@link DrawView}
     *
     * @param touchX X coordinate of touch event
     * @param touchY Y coordinate of touch event
     */
    private int drawTouchDown(MotionEvent motionEvent, float touchX, float touchY) {
        mLastTouchEvent = MotionEvent.ACTION_DOWN;
        DrawMove drawMove = DrawMove.newInstance()
                .setPaint(getNewPaintParams())
                .setStartX(touchX).setStartY(touchY)
                .setEndX(touchX).setEndY(touchY)
                .setDrawingMode(mDrawData.getDrawingMode())
                .setDrawingTool(mDrawData.getDrawingTool())
                .setDrawingShapeSides(mDrawData.getDrawingShapeSides());
        return mDrawData.addMove(drawMove, motionEvent, -1, touchX, touchY, mZoomFactor, mCanvasClipBounds);
    }

    /**
     * Make a touch down action for {@link DrawView}
     *
     * @param motionEvent Current {@link MotionEvent} of {@link DrawView}
     * @param touchX      X coordinate of touch event
     * @param touchY      Y coordinate of touch event
     */
    private int drawTouchMove(MotionEvent motionEvent, float touchX, float touchY) {
        int lastMoveIndex = 0;

        if (mLastTouchEvent == MotionEvent.ACTION_DOWN) {
            if (mOnDrawViewListener != null) mOnDrawViewListener.onStartDrawing();
            if (mDrawViewListener != null) mDrawViewListener.onStartDrawing();
        }

        if ((mLastTouchEvent == MotionEvent.ACTION_DOWN ||
                mLastTouchEvent == MotionEvent.ACTION_MOVE)) {
            mLastTouchEvent = MotionEvent.ACTION_MOVE;

            lastMoveIndex = mDrawData.addMove(null, motionEvent, -1, touchX, touchY,
                    mZoomFactor, mCanvasClipBounds);
        }

        return lastMoveIndex;
    }

    /**
     * Make a touch up action for {@link DrawView}
     *
     * @param motionEvent Current {@link MotionEvent} of {@link DrawView}
     * @param touchX      X coordinate of touch event
     * @param touchY      Y coordinate of touch event
     */
    private int drawTouchUp(final MotionEvent motionEvent, float touchX, float touchY) {
        int lastMoveIndex = mDrawData.addMove(null, motionEvent, mLastTouchEvent, touchX, touchY,
                mZoomFactor, mCanvasClipBounds);
        mUpTimes++;

        if (mLastTouchEvent == MotionEvent.ACTION_MOVE) {
            mLastTouchEvent = -1;
        }

        if (mDrawData.getDrawingMode() == DrawingMode.TEXT) {
            if (mOnDrawViewListener != null) mOnDrawViewListener.onRequestText();
            if (mDrawViewListener != null) mDrawViewListener.onRequestText();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "mUpTimes = " + mUpTimes);
                    if (mUpTimes == 1){
                        if (mOnDrawViewListener != null) mOnDrawViewListener.onEndDrawing();
                        if (mDrawViewListener != null) mDrawViewListener.onEndDrawing();
                        mUpTimes = 0;
                    } else if (mUpTimes == 2){
                        mUpTimes = 0;
                    }
                }
            }, 200);
        }

        return lastMoveIndex;
    }

    /**
     * Move the zoomed view with 2 fingers
     *
     * @param touchX The calculated X point between 2 fingers
     * @param touchY The calculated Y point between 2 fingers
     */
    private void moveZoomedView(float touchX, float touchY) {
        //Log.i(TAG, "------------------------------------------------------------");

        //private boolean mZoomScrollEnabled = false;
        float mZoomScrollAmountX = mZoomCenterX + (touchX - mZoomScrollStartX);

        if (mZoomScrollAmountX > 0) {
            if (mZoomScrollAmountX > mCanvasClipBounds.right) {
                mZoomScrollAmountX = mCanvasClipBounds.right;
            }
            mZoomCenterX = mZoomScrollAmountX;
        } else if (mZoomScrollAmountX < 0) {
            mZoomCenterX = 0f;
        }

        float mZoomScrollAmountY = mZoomCenterY + (touchY - mZoomScrollStartY);

        if (mZoomScrollAmountY > 0) {
            if (mZoomScrollAmountY > mCanvasClipBounds.bottom) {
                mZoomScrollAmountY = mCanvasClipBounds.bottom;
            }
            mZoomCenterY = mZoomScrollAmountY;
        } else if (mZoomScrollAmountY < 0) {
            mZoomCenterY = 0f;
        }


        /*Log.i(TAG, "mZoomScrollStartX: " + mZoomScrollStartX);
        Log.i(TAG, "mZoomScrollStartY: " + mZoomScrollStartY);
        Log.i(TAG, "mZoomCenterX: " + mZoomCenterX + " += " + mZoomScrollAmountX + " = " + (mZoomCenterX + mZoomScrollAmountX));
        Log.i(TAG, "mZoomCenterY: " + mZoomCenterY + " += " + mZoomScrollAmountY + " = " + (mZoomCenterY + mZoomScrollAmountY));*/

        mZoomScrollStartX = touchX;
        mZoomScrollStartY = touchY;

        invalidate();
    }

    /**
     * New paint parameters
     *
     * @return new paint parameters for initialize drawing
     */
    private SerializablePaint getNewPaintParams() {
        SerializablePaint paint = mDrawData.newSerializablePaint();

        if (mDrawData.getDrawingMode() == DrawingMode.ERASER) {
            if (mDrawData.getDrawingTool() != DrawingTool.PEN) {
                Log.i(TAG, "For use eraser drawing mode is necessary to use pen tool");
                mDrawData.setDrawingTool(DrawingTool.PEN);
            }
            paint.setColor(mDrawData.getBackgroundColor());
        } else {
            paint.setColor(mDrawData.getDrawColor());
        }

        return paint;
    }

    /**
     * Draw the background image on DrawViewCanvas
     *
     * @param drawMove the DrawMove that contains the background image
     * @param canvas   tha DrawView canvas
     */
    private void drawBackgroundImage(Canvas canvas, DrawMove drawMove) {
        if (drawMove.getBackgroundImage() != null) {
            canvas.drawBitmap(BitmapFactory.decodeByteArray(drawMove.getBackgroundImage(), 0,
                    drawMove.getBackgroundImage().length), drawMove.getBackgroundMatrix(), null);
        }
    }

    /**
     * Shows or hides ZoomRegionView
     *
     * @param visibility the ZoomRegionView visibility target
     */
    private void showHideZoomRegionView(final int visibility) {
        if (mZoomRegionCardView.getAnimation() == null) {
            AlphaAnimation alphaAnimation = null;

            if (visibility == INVISIBLE && mZoomRegionCardView.getVisibility() == VISIBLE)
                alphaAnimation = new AlphaAnimation(1f, 0f);
            else if (visibility == VISIBLE && mZoomRegionCardView.getVisibility() == INVISIBLE)
                alphaAnimation = new AlphaAnimation(0f, 1f);

            if (alphaAnimation != null) {
                alphaAnimation.setDuration(300);
                alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (visibility == VISIBLE)
                            mZoomRegionCardView.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (visibility == INVISIBLE)
                            mZoomRegionCardView.setVisibility(INVISIBLE);

                        mZoomRegionCardView.setAnimation(null);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mZoomRegionCardView.startAnimation(alphaAnimation);
            }
        }
    }

    /**
     * @param imageWidth
     * @param imageHeight
     * @param backgroundScale
     * @return
     */
    private Matrix getMatrixFromBackgroundScale(int imageWidth, int imageHeight, BackgroundScale backgroundScale) {
        Matrix matrix = new Matrix();

        switch (backgroundScale) {
            case CENTER_CROP:
                matrix = MatrixUtils.GetCenterCropMatrix(new RectF(0, 0,
                                imageWidth, imageHeight),
                        new RectF(0, 0, getWidth(), getHeight()));
                break;
            case CENTER_INSIDE:
                matrix.setRectToRect(new RectF(0, 0,
                                imageWidth, imageHeight),
                        new RectF(0, 0, getWidth(), getHeight()), Matrix.ScaleToFit.CENTER);
                break;
            case FIT_XY:
                matrix.setRectToRect(new RectF(0, 0,
                                imageWidth, imageHeight),
                        new RectF(0, 0, getWidth(), getHeight()), Matrix.ScaleToFit.FILL);
                break;
            case FIT_START:
                matrix.setRectToRect(new RectF(0, 0,
                                imageWidth, imageHeight),
                        new RectF(0, 0, getWidth(), getHeight()), Matrix.ScaleToFit.START);
                break;
            case FIT_END:
                matrix.setRectToRect(new RectF(0, 0,
                                imageWidth, imageHeight),
                        new RectF(0, 0, getWidth(), getHeight()), Matrix.ScaleToFit.END);
                break;
        }

        return matrix;
    }

    //region DRAWING METHODS
    private void drawWithPen(Canvas canvas, DrawMove drawMove) {
        if (drawMove.getDrawingPath() != null) {
            canvas.drawPath(drawMove.getDrawingPath(), drawMove.getPaint());
        }
    }

    private void drawLine(Canvas canvas, DrawMove drawMove) {
        canvas.drawLine(drawMove.getStartX(), drawMove.getStartY(),
                drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
    }

    private void drawArrow(Canvas canvas, DrawMove drawMove) {
        canvas.drawLine(drawMove.getStartX(), drawMove.getStartY(),
                drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
        float angle = (float) Math.toDegrees(Math.atan2(drawMove.getEndY() - drawMove.getStartY(),
                drawMove.getEndX() - drawMove.getStartX())) - 90;
        angle = angle < 0 ? angle + 360 : angle;
        float middleWidth = 8f + drawMove.getPaint().getStrokeWidth();
        float arrowHeadLarge = 30f + drawMove.getPaint().getStrokeWidth();

        canvas.save();
        canvas.translate(drawMove.getEndX(), drawMove.getEndY());
        canvas.rotate(angle);
        canvas.drawLine(0f, 0f, middleWidth, 0f, drawMove.getPaint());
        canvas.drawLine(middleWidth, 0f, 0f, arrowHeadLarge, drawMove.getPaint());
        canvas.drawLine(0f, arrowHeadLarge, -middleWidth, 0f, drawMove.getPaint());
        canvas.drawLine(-middleWidth, 0f, 0f, 0f, drawMove.getPaint());
        canvas.restore();
    }

    private void drawRectangle(Canvas canvas, DrawMove drawMove) {
        canvas.drawRect(drawMove.getStartX(), drawMove.getStartY(),
                drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
    }

    private void drawTriangle(Canvas canvas, DrawMove drawMove) {
        float radius = MathUtils.DistanceBetweenPoints(
                drawMove.getStartX(), drawMove.getStartY(),
                drawMove.getEndX(), drawMove.getEndY());

        canvas.save();
        canvas.translate(drawMove.getStartX(), drawMove.getStartY());

        float internalAngle = 180 - (360 / 3);
        canvas.translate(0, -radius);
        canvas.rotate(-180 + (-internalAngle / 2));

        radius = MathUtils.ShapeSideLength(3, radius);

        for (int side = 0; side < 3; side++) {
            canvas.drawLine(0, 0, 0, -radius, drawMove.getPaint());
            canvas.translate(0, -radius);
            canvas.rotate(-180 - internalAngle);
        }
        canvas.restore();
    }

    private void drawCircle(Canvas canvas, DrawMove drawMove) {
        float radius = MathUtils.DistanceBetweenPoints(
                drawMove.getStartX(), drawMove.getStartY(),
                drawMove.getEndX(), drawMove.getEndY());

        canvas.drawCircle(drawMove.getStartX(), drawMove.getStartY(), radius, drawMove.getPaint());
    }

    private void drawEllipse(Canvas canvas, DrawMove drawMove) {
        mAuxRect.set(drawMove.getEndX() - Math.abs(drawMove.getEndX() - drawMove.getStartX()),
                drawMove.getEndY() - Math.abs(drawMove.getEndY() - drawMove.getStartY()),
                drawMove.getEndX() + Math.abs(drawMove.getEndX() - drawMove.getStartX()),
                drawMove.getEndY() + Math.abs(drawMove.getEndY() - drawMove.getStartY()));
        canvas.drawOval(mAuxRect, drawMove.getPaint());
    }

    private void drawShape(Canvas canvas, DrawMove drawMove) {
        float radius = MathUtils.DistanceBetweenPoints(
                drawMove.getStartX(), drawMove.getStartY(),
                drawMove.getEndX(), drawMove.getEndY());

        canvas.save();
        canvas.translate(drawMove.getStartX(), drawMove.getStartY());

        if (drawMove.getDrawingShapeSides() < 5) {
            drawMove.setDrawingShapeSides(5);
            Log.i(TAG, "The minimum number of sides for a shape is 5");
        }

        float internalAngle = 180 - (360 / drawMove.getDrawingShapeSides());
        canvas.translate(0, -radius);
        canvas.rotate(-180 + (-internalAngle / 2));

        radius = MathUtils.ShapeSideLength(drawMove.getDrawingShapeSides(), radius);

        for (int side = 0; side < drawMove.getDrawingShapeSides(); side++) {
            canvas.drawLine(0, 0, 0, -radius, drawMove.getPaint());
            canvas.translate(0, -radius);
            canvas.rotate(-180 - internalAngle);
        }
        canvas.restore();
    }

    /*private void drawStar(Canvas canvas, DrawMove drawMove) {
        if (drawMove.getDrawingShapeSides() < 4) {
            drawMove.setDrawingShapeSides(4);
            Log.i(TAG, "The minimum number of sides for a shape is 4");
        }

        float radius = MathUtils.DistanceBetweenPoints(
                drawMove.getStartX(), drawMove.getStartY(),
                drawMove.getEndX(), drawMove.getEndY());
        //float innerRadius = radius / 4.25f;//radius * ((1f / drawMove.getDrawingShapeSides()) - 0.015f);// this works for 4 sided star 0.235f;// radius / 2.085f;

        //float innerRadius = MathUtils.CalculateInnerStarInnerRadius(radius, drawMove.getDrawingShapeSides());

        canvas.drawCircle(drawMove.getStartX(), drawMove.getStartY(), radius, drawMove.getPaint());
        //canvas.drawCircle(drawMove.getStartX(), drawMove.getStartY(), innerRadius, drawMove.getPaint());

        canvas.save();
        canvas.translate(drawMove.getStartX(), drawMove.getStartY());

        //region INNER SHAPE TESTING
        /*float internalAngle = 180 - (360 / drawMove.getDrawingShapeSides());
        canvas.translate(0, -radius);
        canvas.rotate(-180 + (-internalAngle / 2));

        float otherRadius = MathUtils.ShapeSideLength(drawMove.getDrawingShapeSides(), radius);

        for (int side = 0; side < drawMove.getDrawingShapeSides(); side++) {
            canvas.drawLine(0, 0, 0, -otherRadius, drawMove.getPaint());
            canvas.translate(0, -otherRadius);
            canvas.rotate(-180 - internalAngle);
        }

        canvas.rotate(internalAngle / 2);
        canvas.translate(0, -radius);*/
    //endregion

        /*float spikeAngle = 180 / drawMove.getDrawingShapeSides();
        float baseAngle = 360 / drawMove.getDrawingShapeSides();
        float sideLength = MathUtils.CalculateStarSideLength(drawMove.getDrawingShapeSides(), radius);//MathUtils.Hypotenuse(spikeAngle / 2, innerRadius);//

        for (int sides = 0; sides < drawMove.getDrawingShapeSides(); sides++) {
            //canvas.drawLine(0, 0, 0, -radius, drawMove.getPaint());
            canvas.translate(0, -radius);

            canvas.rotate(-spikeAngle / 2);
            canvas.drawLine(0, 0, 0, sideLength, drawMove.getPaint());

            canvas.rotate(spikeAngle);
            canvas.drawLine(0, 0, 0, sideLength, drawMove.getPaint());

            canvas.rotate(-spikeAngle/2);
            canvas.translate(0, radius);
            canvas.rotate(baseAngle);
        }
        canvas.restore();
    }*/

    private void drawText(Canvas canvas, DrawMove drawMove) {
        if (drawMove.getText() != null && !drawMove.getText().equals("")) {
            canvas.drawText(drawMove.getText(), drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
        }
    }

    private void drawErase(Canvas canvas, DrawMove drawMove) {
        if (drawMove.getDrawingPath() != null) {
            drawMove.getPaint().setXfermode(mEraserXefferMode);
            canvas.drawPath(drawMove.getDrawingPath(), drawMove.getPaint());
            drawMove.getPaint().setXfermode(null);
        }
    }
    //endregion

    //endregion

    //region GETTERS

    /**
     * Current paint parameters
     *
     * @return current paint parameters
     */
    public SerializablePaint getCurrentPaintParams() {
        return mDrawData.toSerializablePaint();
    }

    public int getDrawAlpha() {
        return mDrawData.getDrawAlpha();
    }

    public int getDrawColor() {
        return mDrawData.getDrawColor();
    }

    public int getDrawWidth() {
        return mDrawData.getDrawWidth();
    }

    public DrawingMode getDrawingMode() {
        return mDrawData.getDrawingMode();
    }

    public DrawingTool getDrawingTool() {
        return mDrawData.getDrawingTool();
    }

    public int getBackgroundColor() {
        return mDrawData.getBackgroundColor();
    }

    public SerializablePaint.Style getPaintStyle() {
        return mDrawData.getPaintStyle();
    }

    public SerializablePaint.Cap getLineCap() {
        return mDrawData.getLineCap();
    }

    public Typeface getFontFamily() {
        return mDrawData.getFontFamily();
    }

    public float getFontSize() {
        return mDrawData.getFontSize();
    }

    public boolean isAntiAlias() {
        return mDrawData.isAntiAlias();
    }

    public boolean isDither() {
        return mDrawData.isDither();
    }

    public boolean isZoomEnabled() {
        return mZoomEnabled;
    }

    public boolean isDrawViewEmpty() {
        return mDrawData.hasMovements();
    }

    public float getMaxZoomFactor() {
        return mMaxZoomFactor;
    }

    public float getZoomRegionScale() {
        return mZoomRegionScale;
    }

    public float getZoomRegionScaleMin() {
        return mZoomRegionScaleMin;
    }

    public float getZoomRegionScaleMax() {
        return mZoomRegionScaleMax;
    }

    public boolean isExcludingBackgroundFromEraser() {
        return mExcludeBackgroundFromErase;
    }

    public boolean isForCamera() {
        return this.mDrawData.isForCamera();
    }
    //endregion

    //region SETTERS

    /**
     * Set if the draw view is used for camera
     *
     * @param isForCamera Value that indicates if the draw view is for camera
     * @return {@link DrawView} instance
     */
    public DrawView forCamera(boolean isForCamera) {
        this.mDrawData.setForCamera(isForCamera);
        return this;
    }

    /**
     * Set the new draw parametters easily
     *
     * @param paint
     * @return {@link DrawView} instance
     */
    public DrawView refreshAttributes(SerializablePaint paint) {
        mDrawData.fromSerializablePaint(paint);
        return this;
    }

    /**
     * Set the current alpha value for the drawing
     *
     * @param drawAlpha
     * @return {@link DrawView} instance
     */
    public DrawView alpha(int drawAlpha) {
        this.mDrawData.setDrawAlpha(drawAlpha);
        return this;
    }

    /**
     * Set the current draw color for drawing
     *
     * @param drawColor
     * @return {@link DrawView} instance
     */
    public DrawView color(int drawColor) {
        this.mDrawData.setDrawColor(drawColor);
        return this;
    }

    /**
     * Set the current draw width
     *
     * @param drawWidth
     * @return {@link DrawView} instance
     */
    public DrawView width(int drawWidth) {
        this.mDrawData.setDrawWidth(drawWidth);
        return this;
    }

    /**
     * Set the current draw mode like draw, text or eraser
     *
     * @param drawingMode
     * @return {@link DrawView} instance
     */
    public DrawView mode(DrawingMode drawingMode) {
        this.mDrawData.setDrawingMode(drawingMode);
        return this;
    }

    /**
     * Set the current {@link DrawingTool}
     *
     * @param drawingTool {@link DrawingTool} instance
     * @return {@link DrawView} instance
     */
    public DrawView tool(DrawingTool drawingTool) {
        this.mDrawData.setDrawingTool(drawingTool);
        return this;
    }

    /**
     * Set the current {@link DrawingTool}
     *
     * @param drawingTool {@link DrawingTool} instance
     * @param shapeSides  Shape sides if {@link DrawingTool} is SHAPE
     * @return {@link DrawView} instance
     */
    public DrawView tool(DrawingTool drawingTool, int shapeSides) {
        if (drawingTool == DrawingTool.SHAPE) {// || drawingTool == DrawingTool.STAR) {
            this.mDrawData.setDrawingTool(drawingTool);
            this.mDrawData.setDrawingShapeSides(shapeSides);
        }
        return this;
    }

    /**
     * Set the current background color of draw view
     *
     * @param backgroundColor
     * @return {@link DrawView} instance
     */
    public DrawView backgroundColor(int backgroundColor) {
        this.mDrawData.setBackgroundColor(backgroundColor);
        return this;
    }

    /**
     * Set the current paint style like fill, fill_stroke or stroke
     *
     * @param paintStyle
     * @return {@link DrawView} instance
     */
    public DrawView paintStyle(SerializablePaint.Style paintStyle) {
        this.mDrawData.setPaintStyle(paintStyle);
        return this;
    }

    /**
     * Set the current line cap like round, square or butt
     *
     * @param lineCap
     * @return {@link DrawView} instance
     */
    public DrawView lineCap(SerializablePaint.Cap lineCap) {
        this.mDrawData.setLineCap(lineCap);
        return this;
    }

    /**
     * Set the current typeface for the view when we like to draw text
     *
     * @param fontFamily
     * @return {@link DrawView} instance
     */
    public DrawView fontFamily(Typeface fontFamily) {
        this.mDrawData.setFontFamily(fontFamily);
        return this;
    }

    /**
     * Set the current font size for the view when we like to draw text
     *
     * @param fontSize
     * @return {@link DrawView} instance
     */
    public DrawView fontSize(float fontSize) {
        this.mDrawData.setFontSize(fontSize);
        return this;
    }

    /**
     * Set the current anti alias value for the view
     *
     * @param antiAlias
     * @return {@link DrawView} instance
     */
    public DrawView antiAlias(boolean antiAlias) {
        this.mDrawData.setAntiAlias(antiAlias);
        return this;
    }

    /**
     * Set the current dither value for the view
     *
     * @param dither
     * @return {@link DrawView} instance
     */
    public DrawView dither(boolean dither) {
        this.mDrawData.setDither(dither);
        return this;
    }

    /**
     * Enables the zoom
     *
     * @param zoomEnabled Value that indicates if the Zoom is enabled
     * @return {@link DrawView} instance
     */
    public DrawView enableZoom(boolean zoomEnabled) {
        this.mZoomEnabled = zoomEnabled;
        return this;
    }

    /**
     * Set the background paint for the view
     *
     * @param backgroundPaint The background paint for the view
     * @return {@link DrawView} instance
     */
    public DrawView backgroundPaint(SerializablePaint backgroundPaint) {
        this.mBackgroundPaint = backgroundPaint;
        return this;
    }

    /**
     * Set the background image for the DrawView. This image can be a File, Bitmap or ByteArray
     *
     * @param backgroundImage File that contains the background image
     * @param backgroundType  Background image type (File, Bitmap or ByteArray)
     * @param backgroundScale Background scale (Center crop, center inside, fit xy, fit top or fit bottom)
     * @return {@link DrawView} instance
     */
    public DrawView backgroundImage(@NonNull final Object backgroundImage,
                                    @NonNull final BackgroundType backgroundType,
                                    @NonNull final BackgroundScale backgroundScale,
                                    int backgroundCompressQuality,
                                    @Nullable Transformation... backgroundTransformations) {
        if (!(backgroundImage instanceof File)
                && !(backgroundImage instanceof Bitmap)
                && !(backgroundImage instanceof byte[])
                && !(backgroundImage instanceof Integer)
                && !(backgroundImage instanceof String)) {
            throw new RuntimeException("Background image must be File, Bitmap, ByteArray, Integer (drawable) or String (Asset or URL).");
        }

        if (mDrawData.isForCamera()) {
            Log.i(TAG, "You can't set a background image if your draw view is for camera");
            return this;
        }

        if (mDrawData.getDrawMoveHistoryIndex() >= -1 &&
                mDrawData.getDrawMoveHistoryIndex() < mDrawData.getDrawMoveHistory().size() - 1)
            mDrawData.setDrawMoveHistory(mDrawData.getDrawMoveHistory().subList(0, mDrawData.getDrawMoveHistoryIndex() + 1));

        final int drawViewWidth = this.getWidth();

        if (backgroundCompressQuality <= 0 || backgroundCompressQuality > 100) {
            Log.i(TAG, "Your compress quality must be between 1 and 100");
            backgroundCompressQuality = DEFAULT_BACKGROUND_QUALITY;
        }
        final int compressQuality = backgroundCompressQuality;

        mBackgroundImageData
                = BackgroundImageData.newInstance()
                .background(backgroundImage)
                .drawViewWidth(drawViewWidth)
                .compressQuality(compressQuality)
                .backgroundScale(backgroundScale)
                .backgroundType(backgroundType)
                .transformations(backgroundTransformations);

        return this;
    }

    /**
     * Set the background image for the DrawView. This image can be a File, Bitmap or ByteArray
     *
     * @param backgroundImage  File that contains the background image
     * @param backgroundType   Background image type (File, Bitmap or ByteArray)
     * @param backgroundMatrix Background matrix for the image
     * @return {@link DrawView} instance
     */
    public DrawView backgroundImage(@NonNull final Object backgroundImage,
                                    @NonNull final BackgroundType backgroundType,
                                    @NonNull final Matrix backgroundMatrix,
                                    int backgroundCompressQuality,
                                    @Nullable Transformation... backgroundTransformations) {
        if (!(backgroundImage instanceof File)
                && !(backgroundImage instanceof Bitmap)
                && !(backgroundImage instanceof byte[])
                && !(backgroundImage instanceof Integer)
                && !(backgroundImage instanceof String)) {
            throw new RuntimeException("Background image must be File, Bitmap, ByteArray, Integer (drawable) or String (Asset or URL).");
        }

        if (mDrawData.isForCamera()) {
            Log.i(TAG, "You can't set a background image if your draw view is for camera");
            return this;
        }

        if (mDrawData.getDrawMoveHistoryIndex() >= -1 &&
                mDrawData.getDrawMoveHistoryIndex() < mDrawData.getDrawMoveHistory().size() - 1)
            mDrawData.setDrawMoveHistory(mDrawData.getDrawMoveHistory().subList(0, mDrawData.getDrawMoveHistoryIndex() + 1));

        final int drawViewWidth = this.getWidth();

        if (backgroundCompressQuality <= 0 || backgroundCompressQuality > 100) {
            Log.i(TAG, "Your compress quality must be between 1 and 100");
            backgroundCompressQuality = DEFAULT_BACKGROUND_QUALITY;
        }
        final int compressQuality = backgroundCompressQuality;

        mBackgroundImageData
                = BackgroundImageData.newInstance()
                .background(backgroundImage)
                .drawViewWidth(drawViewWidth)
                .compressQuality(compressQuality)
                .matrix(backgroundMatrix)
                .backgroundType(backgroundType)
                .transformations(backgroundTransformations);

        return this;
    }

    /**
     * Set the max zoom factor of the DrawView
     *
     * @param maxZoomFactor The max zoom factor target
     * @return {@link DrawView} instance
     */
    public DrawView maxZoomFactor(float maxZoomFactor) {
        this.mMaxZoomFactor = maxZoomFactor;
        return this;
    }

    /**
     * Sets the ZoomRegionView scale factor
     *
     * @param zoomRegionScale ZoomRegionView scale factor (DrawView size / scale)
     * @return {@link DrawView} instance
     */
    public DrawView zoomRegionScale(float zoomRegionScale) {
        this.mZoomRegionScale = zoomRegionScale;
        return this;
    }

    /**
     * Sets the ZoomRegionView minimum scale factor
     *
     * @param zoomRegionScaleMin ZoomRegionView scale factor minimum
     * @return {@link DrawView} instance
     */
    public DrawView zoomRegionScaleMin(float zoomRegionScaleMin) {
        this.mZoomRegionScaleMin = zoomRegionScaleMin;
        return this;
    }

    /**
     * Sets the ZoomRegionView maximum scale factor
     *
     * @param zoomRegionScaleMax ZoomRegionView scale factor maximum
     * @return {@link DrawView} instance
     */
    public DrawView zoomRegionScaleMax(float zoomRegionScaleMax) {
        this.mZoomRegionScaleMax = zoomRegionScaleMax;
        return this;
    }

    /**
     * Excludes background when the user select {@link DrawingMode} ERASER mode
     *
     * @param exclude If this option was excluded
     * @return {@link DrawView} instance
     */
    public DrawView excludeBackgroundFromEraser(boolean exclude) {
        this.mExcludeBackgroundFromErase = exclude;
        return this;
    }

    /**
     * Set the initial {@link DrawingOrientation} for the {@link DrawView}
     *
     * @param drawingOrientation {@link DrawingOrientation} instance
     * @return {@link DrawView} instance
     */
    public DrawView initialOrientation(DrawingOrientation drawingOrientation) {
        this.mDrawData.setInitialDrawingOrientation(drawingOrientation);
        return this;
    }
    //endregion

    //region LISTENERS

    /**
     * Setting new {@link OnDrawViewListener} interface listener for this view
     *
     * @param onDrawViewListener {@link OnDrawViewListener} interface
     */
    public void addDrawViewListener(OnDrawViewListener onDrawViewListener) {
        this.mOnDrawViewListener = onDrawViewListener;
    }

    /**
     * Setting new {@link DrawViewListener} abstract listener for this view
     *
     * @param drawViewListener {@link DrawViewListener} abstract class
     */
    public void addDrawViewListener(DrawViewListener drawViewListener) {
        this.mDrawViewListener = drawViewListener;
    }

    /**
     * Listener to detect zoom gestures in {@link DrawView}
     */
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (mZoomEnabled) {
                mZoomFactor *= detector.getScaleFactor();
                mZoomFactor = Math.max(1f, Math.min(mZoomFactor, mMaxZoomFactor));
                mZoomFactor = mZoomFactor > mMaxZoomFactor ? mMaxZoomFactor : mZoomFactor < 1f ? 1f : mZoomFactor;
                mZoomCenterX = detector.getFocusX() / mZoomFactor + mCanvasClipBounds.left;
                mZoomCenterY = detector.getFocusY() / mZoomFactor + mCanvasClipBounds.top;

                mZoomScrollStartX = mZoomCenterX;
                mZoomScrollStartY = mZoomCenterY;

                if (mZoomFactor > 1f)
                    showHideZoomRegionView(VISIBLE);
                else
                    showHideZoomRegionView(INVISIBLE);

                invalidate();
            }

            return false;
        }
    }

    /**
     * Listener to detect double tap to maze zoom in {@link DrawView}
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(final MotionEvent e) {
            if (mZoomEnabled) {
                int animationOption = -1;

                if (mZoomFactor >= 1f && mZoomFactor < mMaxZoomFactor)
                    animationOption = 0;
                else if (mZoomFactor <= mMaxZoomFactor && mZoomFactor > 1f)
                    animationOption = 1;

                if (animationOption != -1) {
                    ValueAnimator valueAnimator = null;

                    if (animationOption == 0)
                        valueAnimator = ValueAnimator.ofFloat(mZoomFactor, mMaxZoomFactor);
                    else {
                        float distance = mMaxZoomFactor - mZoomFactor;
                        valueAnimator = ValueAnimator.ofFloat(mZoomFactor, distance);
                    }

                    valueAnimator.setDuration(300);
                    valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mZoomFactor = (float) animation.getAnimatedValue();
//                            Log.i(TAG, "Current Zoom: " + mZoomFactor);
                            mZoomFactor = mZoomFactor < 1f ? 1 : mZoomFactor;
                            mZoomCenterX = e.getX() / mZoomFactor + mCanvasClipBounds.left;
                            mZoomCenterY = e.getY() / mZoomFactor + mCanvasClipBounds.top;

                            mZoomScrollStartX = mZoomCenterX;
                            mZoomScrollStartY = mZoomCenterY;

                            if (mZoomFactor > 1f)
                                mZoomRegionCardView.setVisibility(VISIBLE);
                            else
                                mZoomRegionCardView.setVisibility(INVISIBLE);

                            invalidate();
                        }
                    });
                    valueAnimator.start();
                }
            }

            /*if (mZoomScrollEnabled) {
                mZoomScrollEnabled = false;
                mZoomScrollStartX = 0;
                mZoomScrollStartY = 0;
                mZoomScrollAmountX = 0;
                mZoomScrollAmountY = 0;
            }*/

            return true;
        }

        /*@Override
        public void onLongPress(MotionEvent e) {

            if (mZoomFactor > 1f) {
                if (!mZoomScrollEnabled) {
                    mZoomScrollEnabled = true;

                    Log.i(TAG, "Now you can scroll into zoomed view");

                    mZoomScrollStartX = e.getX() / mZoomFactor + mCanvasClipBounds.left;
                    mZoomScrollStartY = e.getY() / mZoomFactor + mCanvasClipBounds.top;

                    Log.i(TAG, "------------ onLongPress ----------------");
                    Log.i(TAG, "mZoomScrollStartX: " + mZoomScrollStartX);
                    Log.i(TAG, "mZoomScrollStartY: " + mZoomScrollStartY);

                    mLastTouchEvent = MotionEvent.ACTION_DOWN;
                } else {
                    mZoomScrollEnabled = false;
                    mZoomScrollStartX = 0;
                    mZoomScrollStartY = 0;
                    mZoomScrollAmountX = 0;
                    mZoomScrollAmountY = 0;

                    Log.i(TAG, "Now you can't scroll into zoomed view");
                }
            }
            super.onLongPress(e);
        }*/
    }
    //endregion

    //region ASYNC TASKS
    @SuppressLint("StaticFieldLeak")
    class ProcessBackgroundTask extends AsyncTask<Object, Void, BackgroundImageData> {

        private Bitmap background;
        private byte[] backgroundBytes;
        Matrix backgroundMatrix = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mOnDrawViewListener != null) {
                mOnDrawViewListener.onStartDrawing();
                mOnDrawViewListener.onDrawBackgroundStart();
            }
            if (mDrawViewListener != null) {
                mDrawViewListener.onStartDrawing();
                mDrawViewListener.onDrawBackgroundStart();
            }
        }

        @Override
        protected BackgroundImageData doInBackground(Object... objects) {
            BackgroundImageData instance = (BackgroundImageData) objects[0];

            if (instance.getBackgroundType() == BackgroundType.BITMAP
                    || instance.getBackgroundType() == BackgroundType.BYTES) {
                background = BitmapUtils
                        .GetBitmapForDrawView(
                                instance.getDrawViewWidth(),
                                instance.getBackground(),
                                instance.getBackgroundType(),
                                instance.getCompressQuality(),
                                true);
            } else {
                background = BitmapUtils
                        .GetBitmapForDrawView(
                                instance.getDrawViewWidth(),
                                objects[1],
                                BackgroundType.BITMAP,
                                instance.getCompressQuality(),
                                false);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            background.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            backgroundBytes = byteArrayOutputStream.toByteArray();

            publishProgress();

            while (backgroundMatrix == null) {
            }

            mDrawData.getDrawMoveHistory().add(DrawMove.newInstance()
                    .setBackgroundImage(backgroundBytes, backgroundMatrix)
                    .setPaint(new SerializablePaint())
                    .setDrawingShapeSides(mDrawData.getDrawingShapeSides()));

            mDrawData.setDrawMoveHistoryIndex(mDrawData.getDrawMoveHistoryIndex() + 1);

            mDrawData.setDrawMoveBackgroundIndex(mDrawData.getDrawMoveHistoryIndex());

            return instance;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            backgroundMatrix = getMatrixFromBackgroundScale(
                    background.getWidth(),
                    background.getHeight(),
                    mBackgroundImageData.getBackgroundScale());
        }

        @Override
        protected void onPostExecute(BackgroundImageData backgroundImageData) {
            super.onPostExecute(backgroundImageData);

            if (mDrawViewListener != null) {
                mDrawViewListener.onDrawBackgroundEnds(background, backgroundImageData.getBackgroundType());
                mDrawViewListener.onDrawBackgroundEnds(backgroundBytes, backgroundImageData.getBackgroundType());
                mDrawViewListener.onEndDrawing();
            }
            if (mOnDrawViewListener != null) {
                mOnDrawViewListener.onDrawBackgroundEnds(background, backgroundImageData.getBackgroundType());
                mOnDrawViewListener.onDrawBackgroundEnds(backgroundBytes, backgroundImageData.getBackgroundType());
                mOnDrawViewListener.onEndDrawing();
            }

            invalidate();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ProcessCapture extends AsyncTask<Bitmap.CompressFormat, Void, DrawCapture> {
        @Override
        protected DrawCapture doInBackground(Bitmap.CompressFormat... formats) {

            if (mBackgroundPaint.getColor() == Color.TRANSPARENT)
                formats[0] = Bitmap.CompressFormat.PNG;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mContentBitmap.compress(formats[0], 100, stream);

            DrawCapture drawCapture = DrawCapture.newInstance();
            drawCapture.setCaptureInBytes(stream.toByteArray());
            drawCapture.setCaptureFormat(formats[0]);

            return drawCapture;
        }

        @Override
        protected void onPostExecute(DrawCapture drawCapture) {
            super.onPostExecute(drawCapture);

            if (drawCapture != null) {
                if (mDrawViewListener != null) {
                    mDrawViewListener.onCaptureCreated(drawCapture);
                }

                if (mOnDrawViewCaptureListener != null) {
                    mOnDrawViewCaptureListener.onCaptureCreated(drawCapture);
                }
            }
        }
    }
    //endregion
}
