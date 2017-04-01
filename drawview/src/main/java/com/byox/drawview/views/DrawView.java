package com.byox.drawview.views;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import com.byox.drawview.dictionaries.DrawMove;
import com.byox.drawview.enums.DrawingCapture;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.utils.BitmapUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ing. Oscar G. Medina Cruz on 06/11/2016.
 * <p>
 * This view was created for draw or paint anything you want.
 * <p>
 * <p>
 * This view can be configurated for change draw color, width size, can use tools like pen, line, circle, square.
 * </p>
 *
 * @author Ing. Oscar G. Medina Cruz
 */
public class DrawView extends FrameLayout implements View.OnTouchListener {

    // CONSTANTS
    final String TAG = "DrawView";

    // LISTENER
    private OnDrawViewListener onDrawViewListener;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    // VARS
    private int mDrawColor;
    private int mDrawWidth;
    private int mDrawAlpha;
    private boolean mAntiAlias;
    private boolean mDither;
    private Paint.Style mPaintStyle;
    private Paint.Cap mLineCap;
    private Typeface mFontFamily;
    private float mFontSize;
    private int mBackgroundColor;
    private File mBackgroundImage;
    private Bitmap mBackgroundImageBitmap;
    private Rect mCanvasClipBounds;

    private Bitmap mContentBitmap;
    private Canvas mContentCanvas;

    private boolean mZoomEnabled = false;
    private float mZoomFactor = 1.0f;
    private float mZoomCenterX = -1.0f;
    private float mZoomCenterY = -1.0f;
    private float mMaxZoomFactor = 8f;
    private float mZoomRegionScale = 4f;
    private float mZoomRegionScaleMin = 2f;
    private float mZoomRegionScaleMax = 5f;
    private boolean mFromZoomRegion = false;

    private int mLastTouchEvent = -1;

    private DrawingMode mDrawingMode;
    private DrawingTool mDrawingTool;

    private List<DrawMove> mDrawMoveHistory;
    private int mDrawMoveHistoryIndex = -1;
    private int mDrawMoveBackgroundIndex = -1;

    private RectF mAuxRect;

    // VIEWS
    private CardView mZoomRegionCardView;
    private ZoomRegionView mZoomRegionView;

    /**
     * Default constructor
     *
     * @param context
     */
    public DrawView(Context context) {
        super(context);
        initVars();
    }

    /**
     * Default constructor
     *
     * @param context
     * @param attrs
     */
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVars();
        initAttributes(context, attrs);
    }

    /**
     * Default constructor
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars();
        initAttributes(context, attrs);
    }

    /**
     * Default constructor
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVars();
        initAttributes(context, attrs);
    }

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

        if (mDrawMoveBackgroundIndex != -1)
            drawBackgroundImage(mDrawMoveHistory.get(mDrawMoveBackgroundIndex), mContentCanvas);

        for (int i = 0; i < mDrawMoveHistoryIndex + 1; i++) {
            DrawMove drawMove = mDrawMoveHistory.get(i);
            if (drawMove.getDrawingMode() != null) {
                switch (drawMove.getDrawingMode()) {
                    case DRAW:
                        switch (drawMove.getDrawingTool()) {
                            case PEN:
                                if (drawMove.getDrawingPathList() != null &&
                                        drawMove.getDrawingPathList().size() > 0)
                                    for (Path path : drawMove.getDrawingPathList()) {
                                        mContentCanvas.drawPath(path, drawMove.getPaint());
                                    }
                                break;
                            case LINE:
                                mContentCanvas.drawLine(drawMove.getStartX(), drawMove.getStartY(),
                                        drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
                                break;
                            case RECTANGLE:
                                mContentCanvas.drawRect(drawMove.getStartX(), drawMove.getStartY(),
                                        drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
                                break;
                            case CIRCLE:
                                if (drawMove.getEndX() > drawMove.getStartX()) {
                                    mContentCanvas.drawCircle(drawMove.getStartX(), drawMove.getStartY(),
                                            drawMove.getEndX() - drawMove.getStartX(), drawMove.getPaint());
                                } else {
                                    mContentCanvas.drawCircle(drawMove.getStartX(), drawMove.getStartY(),
                                            drawMove.getStartX() - drawMove.getEndX(), drawMove.getPaint());
                                }
                                break;
                            case ELLIPSE:
                                mAuxRect.set(drawMove.getEndX() - Math.abs(drawMove.getEndX() - drawMove.getStartX()),
                                        drawMove.getEndY() - Math.abs(drawMove.getEndY() - drawMove.getStartY()),
                                        drawMove.getEndX() + Math.abs(drawMove.getEndX() - drawMove.getStartX()),
                                        drawMove.getEndY() + Math.abs(drawMove.getEndY() - drawMove.getStartY()));
                                mContentCanvas.drawOval(mAuxRect, drawMove.getPaint());
                                break;
                        }
                        break;
                    case TEXT:
                        if (drawMove.getText() != null && !drawMove.getText().equals("")) {
                            mContentCanvas.drawText(drawMove.getText(), drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
                        }
                        break;
                    case ERASER:
                        if (drawMove.getDrawingPathList() != null &&
                                drawMove.getDrawingPathList().size() > 0) {
                            drawMove.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            for (Path path : drawMove.getDrawingPathList()) {
                                mContentCanvas.drawPath(path, drawMove.getPaint());
                            }
                            drawMove.getPaint().setXfermode(null);
                        }
                        break;
                }
            }
        }

        canvas.getClipBounds(mCanvasClipBounds);

        canvas.drawBitmap(mContentBitmap, 0, 0, null);

        if (isZoomEnabled()) {
            canvas.restore();

            if (mZoomRegionView != null && !mFromZoomRegion) {
                mZoomRegionView.drawZoomRegion(mContentBitmap, mCanvasClipBounds, 4);
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

        if (motionEvent.getPointerCount() == 1) {
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchEvent = MotionEvent.ACTION_DOWN;

                    if (onDrawViewListener != null)
                        onDrawViewListener.onStartDrawing();

                    if (mDrawMoveHistoryIndex >= -1 &&
                            mDrawMoveHistoryIndex < mDrawMoveHistory.size() - 1)
                        mDrawMoveHistory = mDrawMoveHistory.subList(0, mDrawMoveHistoryIndex + 1);

                    mDrawMoveHistory.add(DrawMove.newInstance()
                            .setPaint(getNewPaintParams())
                            .setStartX(touchX).setStartY(touchY)
                            .setEndX(touchX).setEndY(touchY)
                            .setDrawingMode(mDrawingMode).setDrawingTool(mDrawingTool));

//                    Paint currentPaint = mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getPaint();
//                    currentPaint.setStrokeWidth(currentPaint.getStrokeWidth() / mZoomFactor);
//                    mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).setPaint(currentPaint);

                    mDrawMoveHistoryIndex++;

                    if (mDrawingTool == DrawingTool.PEN || mDrawingMode == DrawingMode.ERASER) {
                        Path path = new Path();
                        path.moveTo(touchX, touchY);
                        path.lineTo(touchX, touchY);

                        mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).setDrawingPathList(new ArrayList<Path>());
                        mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList().add(path);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((mLastTouchEvent == MotionEvent.ACTION_DOWN ||
                            mLastTouchEvent == MotionEvent.ACTION_MOVE)) {
                        mLastTouchEvent = MotionEvent.ACTION_MOVE;

                        if (mDrawMoveHistory.size() > 0) {
                            mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).setEndX(touchX).setEndY(touchY);

                            if (mDrawingTool == DrawingTool.PEN || mDrawingMode == DrawingMode.ERASER) {
                                mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList()
                                        .get(mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList().size() - 1)
                                        .lineTo(touchX, touchY);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mLastTouchEvent == MotionEvent.ACTION_DOWN) {
                        if (mDrawMoveHistory.size() > 0) {
                            mDrawMoveHistory.remove(mDrawMoveHistory.size() - 1);
                            mDrawMoveHistoryIndex--;
                        }
                    } else if (mLastTouchEvent == MotionEvent.ACTION_MOVE) {
                        mLastTouchEvent = -1;
                        if (mDrawMoveHistory.size() > 0) {
                            mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).setEndX(motionEvent.getX()).setEndY(motionEvent.getY());

                            if (mDrawingTool == DrawingTool.PEN || mDrawingMode == DrawingMode.ERASER) {
                                mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList()
                                        .get(mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList().size() - 1)
                                        .lineTo(touchX, touchY);
                            }
                        }
                    }

                    if (onDrawViewListener != null && mDrawingMode == DrawingMode.TEXT)
                        onDrawViewListener.onRequestText();

                    if (onDrawViewListener != null)
                        onDrawViewListener.onEndDrawing();

                    break;
                default:
                    return false;
            }
        } else {
            mLastTouchEvent = -1;
        }

        invalidate();
        return true;
    }

    // PRIVATE METHODS

    /**
     * Initialize general vars for the view
     */
    private void initVars() {
        mDrawMoveHistory = new ArrayList<>();
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        mCanvasClipBounds = new Rect();
        mAuxRect = new RectF();

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

            FrameLayout.LayoutParams layoutParams = new LayoutParams(getWidth() / 4, getHeight() / 4,
                    Gravity.TOP | Gravity.END);
            layoutParams.setMargins(12, 12, 12, 12);
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
            mZoomRegionView.setOnZoomRegionListener(new ZoomRegionView.OnZoomRegionListener() {
                @Override
                public void onZoomRegionMoved(Rect newRect) {
                    mFromZoomRegion = true;
                    mZoomCenterX = newRect.centerX() * 4;
                    mZoomCenterY = newRect.centerY() * 4;

                    invalidate();
                }
            });

            mZoomRegionCardView.addView(mZoomRegionView);
            addView(mZoomRegionCardView);
        }
    }

    /**
     * Initialize view attributes
     *
     * @param context
     * @param attrs
     */
    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.DrawView, 0, 0);
        try {
            mDrawColor = typedArray.getColor(R.styleable.DrawView_dv_draw_color, Color.BLACK);
            mDrawWidth = typedArray.getInteger(R.styleable.DrawView_dv_draw_width, 3);
            mDrawAlpha = typedArray.getInteger(R.styleable.DrawView_dv_draw_alpha, 255);
            mAntiAlias = typedArray.getBoolean(R.styleable.DrawView_dv_draw_anti_alias, true);
            mDither = typedArray.getBoolean(R.styleable.DrawView_dv_draw_dither, true);
            int paintStyle = typedArray.getInteger(R.styleable.DrawView_dv_draw_style, 2);
            if (paintStyle == 0)
                mPaintStyle = Paint.Style.FILL;
            else if (paintStyle == 1)
                mPaintStyle = Paint.Style.FILL_AND_STROKE;
            else if (paintStyle == 2)
                mPaintStyle = Paint.Style.STROKE;
            int cap = typedArray.getInteger(R.styleable.DrawView_dv_draw_corners, 2);
            if (cap == 0)
                mLineCap = Paint.Cap.BUTT;
            else if (cap == 1)
                mLineCap = Paint.Cap.ROUND;
            else if (cap == 2)
                mLineCap = Paint.Cap.SQUARE;
            int typeface = typedArray.getInteger(R.styleable.DrawView_dv_draw_font_family, 0);
            if (typeface == 0)
                mFontFamily = Typeface.DEFAULT;
            else if (typeface == 1)
                mFontFamily = Typeface.MONOSPACE;
            else if (typeface == 2)
                mFontFamily = Typeface.SANS_SERIF;
            else if (typeface == 3)
                mFontFamily = Typeface.SERIF;
            mFontSize = typedArray.getInteger(R.styleable.DrawView_dv_draw_font_size, 12);
            if (getBackground() != null)
                try {
                    mBackgroundColor = ((ColorDrawable) getBackground()).getColor();
                } catch (Exception e){
                    e.printStackTrace();
                    setBackgroundColor(Color.TRANSPARENT);
                    mBackgroundColor = ((ColorDrawable) getBackground()).getColor();
                    setBackgroundResource(R.drawable.drawable_transparent_pattern);
                }
            else {
                setBackgroundColor(Color.TRANSPARENT);
                mBackgroundColor = ((ColorDrawable) getBackground()).getColor();
                setBackgroundResource(R.drawable.drawable_transparent_pattern);
            }


            mDrawingTool = DrawingTool.values()[typedArray.getInteger(R.styleable.DrawView_dv_draw_tool, 0)];
            mDrawingMode = DrawingMode.values()[typedArray.getInteger(R.styleable.DrawView_dv_draw_mode, 0)];
            mZoomEnabled = typedArray.getBoolean(R.styleable.DrawView_dv_draw_enable_zoom, false);
            mZoomRegionScale = typedArray.getFloat(R.styleable.DrawView_dv_draw_zoomregion_scale, mZoomRegionScale);
            mZoomRegionScaleMin = typedArray.getFloat(R.styleable.DrawView_dv_draw_zoomregion_minscale, mZoomRegionScaleMin);
            mZoomRegionScaleMax = typedArray.getFloat(R.styleable.DrawView_dv_draw_zoomregion_maxscale, mZoomRegionScaleMax);
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * New paint parameters
     *
     * @return new paint parameters for initialize drawing
     */
    private Paint getNewPaintParams() {
        Paint paint = new Paint();

        if (mDrawingMode == DrawingMode.ERASER) {
            if (mDrawingTool != DrawingTool.PEN) {
                Log.i(TAG, "For use eraser drawing mode is necessary to use pen tool");
                mDrawingTool = DrawingTool.PEN;
            }
            paint.setColor(mBackgroundColor);
        } else {
            paint.setColor(mDrawColor);
        }

        paint.setStyle(mPaintStyle);
        paint.setDither(mDither);
        paint.setStrokeWidth(mDrawWidth);
        paint.setAlpha(mDrawAlpha);
        paint.setAntiAlias(mAntiAlias);
        paint.setStrokeCap(mLineCap);
        paint.setTypeface(mFontFamily);
        paint.setTextSize(mFontSize);

        return paint;
    }

    // PUBLIC METHODS

    /**
     * Current paint parameters
     *
     * @return current paint parameters
     */
    public Paint getCurrentPaintParams() {
        Paint currentPaint;
        if (mDrawMoveHistory.size() > 0 && mDrawMoveHistoryIndex >= 0) {
            currentPaint = new Paint();
            currentPaint.setColor(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getColor());
            currentPaint.setStyle(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getStyle());
            currentPaint.setDither(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().isDither());
            currentPaint.setStrokeWidth(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getStrokeWidth());
            currentPaint.setAlpha(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getAlpha());
            currentPaint.setAntiAlias(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().isAntiAlias());
            currentPaint.setStrokeCap(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getStrokeCap());
            currentPaint.setTypeface(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getTypeface());
            currentPaint.setTextSize(mFontSize);
        } else {
            currentPaint = new Paint();
            currentPaint.setColor(mDrawColor);
            currentPaint.setStyle(mPaintStyle);
            currentPaint.setDither(mDither);
            currentPaint.setStrokeWidth(mDrawWidth);
            currentPaint.setAlpha(mDrawAlpha);
            currentPaint.setAntiAlias(mAntiAlias);
            currentPaint.setStrokeCap(mLineCap);
            currentPaint.setTypeface(mFontFamily);
            currentPaint.setTextSize(24f);
        }
        return currentPaint;
    }

    /**
     * Restart all the parameters and drawing history
     *
     * @return if the draw view can be restarted
     */
    public boolean restartDrawing() {
        if (mDrawMoveHistory != null) {
            mDrawMoveHistory.clear();
            mDrawMoveHistoryIndex = -1;
            invalidate();

            if (onDrawViewListener != null)
                onDrawViewListener.onClearDrawing();

            return true;
        }
        invalidate();
        return false;
    }

    /**
     * Undo last drawing action
     *
     * @return if the view can do the undo action
     */
    public boolean undo() {
        if (mDrawMoveHistoryIndex > -1 &&
                mDrawMoveHistory.size() > 0) {
            mDrawMoveHistoryIndex--;

            mDrawMoveBackgroundIndex = -1;
            for (int i = 0; i < mDrawMoveHistoryIndex + 1; i++) {
                if (mDrawMoveHistory.get(i).getBackgroundImage() != null) {
                    mDrawMoveBackgroundIndex = i;
                }
            }

            invalidate();
            return true;
        }
        invalidate();
        return false;
    }

    /**
     * Check if the draw view can do undo action
     *
     * @return if the view can do the undo action
     */
    public boolean canUndo() {
        return mDrawMoveHistoryIndex > -1 &&
                mDrawMoveHistory.size() > 0;
    }

    /**
     * Redo preview action
     *
     * @return if the view can do the redo action
     */
    public boolean redo() {
        if (mDrawMoveHistoryIndex <= mDrawMoveHistory.size() - 1) {
            mDrawMoveHistoryIndex++;

            mDrawMoveBackgroundIndex = -1;
            for (int i = 0; i < mDrawMoveHistoryIndex + 1; i++) {
                if (mDrawMoveHistory.get(i).getBackgroundImage() != null) {
                    mDrawMoveBackgroundIndex = i;
                }
            }

            invalidate();
            return true;
        }
        invalidate();
        return false;
    }

    /**
     * Check if the view can do the redo action
     *
     * @return if the view can do the redo action
     */
    public boolean canRedo() {
        return mDrawMoveHistoryIndex < mDrawMoveHistory.size() - 1;
    }

    /**
     * Create capture of the drawing view as bitmap or as byte array
     *
     * @param drawingCapture
     * @return Object in form of bitmap or byte array
     */
    public Object createCapture(DrawingCapture drawingCapture) {
        switch (drawingCapture) {
            case BITMAP:
                return mContentBitmap;
            case BYTES:
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mContentBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                return stream.toByteArray();
        }
        return null;
    }

    /**
     * Refresh the text of the last movement item
     *
     * @param newText
     */
    public void refreshLastText(String newText) {
        if (mDrawMoveHistory.get(mDrawMoveHistory.size() - 1)
                .getDrawingMode() == DrawingMode.TEXT) {
            mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).setText(newText);
            invalidate();
        } else
            Log.e(TAG, "The last item that you want to refresh text isn't TEXT element.");
    }

    /**
     * Delete las history element, this can help for cancel the text request.
     */
    public void cancelTextRequest() {
        if (mDrawMoveHistory != null && mDrawMoveHistory.size() > 0) {
            mDrawMoveHistory.remove(mDrawMoveHistory.size() - 1);
            mDrawMoveHistoryIndex--;
        }
    }

    // GETTERS
    public int getDrawAlpha() {
        return mDrawAlpha;
    }

    public int getDrawColor() {
        return mDrawColor;
    }

    public int getDrawWidth() {
        return mDrawWidth;
    }

    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }

    public DrawingTool getDrawingTool() {
        return mDrawingTool;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public File getBackgroundImage() {
        return mBackgroundImage;
    }

    public Paint.Style getPaintStyle() {
        return mPaintStyle;
    }

    public Paint.Cap getLineCap() {
        return mLineCap;
    }

    public Typeface getFontFamily() {
        return mFontFamily;
    }

    public float getFontSize() {
        return mFontSize;
    }

    public boolean isAntiAlias() {
        return mAntiAlias;
    }

    public boolean isDither() {
        return mDither;
    }

    public boolean isZoomEnabled() {
        return mZoomEnabled;
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

    // SETTERS

    /**
     * Set the new draw parametters easily
     *
     * @param paint
     * @return this instance of the view
     */
    public DrawView refreshAttributes(Paint paint) {
        mDrawColor = paint.getColor();
        mPaintStyle = paint.getStyle();
        mDither = paint.isDither();
        mDrawWidth = (int) paint.getStrokeWidth();
        mDrawAlpha = paint.getAlpha();
        mAntiAlias = paint.isAntiAlias();
        mLineCap = paint.getStrokeCap();
        mFontFamily = paint.getTypeface();
        mFontSize = paint.getTextSize();
        return this;
    }

    /**
     * Set the current alpha value for the drawing
     *
     * @param drawAlpha
     * @return this instance of the view
     */
    public DrawView setDrawAlpha(int drawAlpha) {
        this.mDrawAlpha = drawAlpha;
        return this;
    }

    /**
     * Set the current draw color for drawing
     *
     * @param drawColor
     * @return this instance of the view
     */
    public DrawView setDrawColor(int drawColor) {
        this.mDrawColor = drawColor;
        return this;
    }

    /**
     * Set the current draw width
     *
     * @param drawWidth
     * @return this instance of the view
     */
    public DrawView setDrawWidth(int drawWidth) {
        this.mDrawWidth = drawWidth;
        return this;
    }

    /**
     * Set the current draw mode like draw, text or eraser
     *
     * @param drawingMode
     * @return this instance of the view
     */
    public DrawView setDrawingMode(DrawingMode drawingMode) {
        this.mDrawingMode = drawingMode;
        return this;
    }

    /**
     * Set the current draw tool like pen, line, circle, rectangle, circle
     *
     * @param drawingTool
     * @return this instance of the view
     */
    public DrawView setDrawingTool(DrawingTool drawingTool) {
        this.mDrawingTool = drawingTool;
        return this;
    }

    /**
     * Set the current background color of draw view
     *
     * @param backgroundColor
     * @return this instance of the view
     */
    public DrawView setBackgroundDrawColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        return this;
    }

    /**
     * Set the current paint style like fill, fill_stroke or stroke
     *
     * @param paintStyle
     * @return this instance of the view
     */
    public DrawView setPaintStyle(Paint.Style paintStyle) {
        this.mPaintStyle = paintStyle;
        return this;
    }

    /**
     * Set the current line cap like round, square or butt
     *
     * @param lineCap
     * @return this instance of the view
     */
    public DrawView setLineCap(Paint.Cap lineCap) {
        this.mLineCap = lineCap;
        return this;
    }

    /**
     * Set the current typeface for the view when we like to draw text
     *
     * @param fontFamily
     * @return this instance of the view
     */
    public DrawView setFontFamily(Typeface fontFamily) {
        this.mFontFamily = fontFamily;
        return this;
    }

    /**
     * Set the current font size for the view when we like to draw text
     *
     * @param fontSize
     * @return this instance of the view
     */
    public DrawView setFontSize(float fontSize) {
        this.mFontSize = fontSize;
        return this;
    }

    /**
     * Set the current anti alias value for the view
     *
     * @param antiAlias
     * @return this instance of the view
     */
    public DrawView setAntiAlias(boolean antiAlias) {
        this.mAntiAlias = antiAlias;
        return this;
    }

    /**
     * Set the current dither value for the view
     *
     * @param dither
     * @return this instance of the view
     */
    public DrawView setDither(boolean dither) {
        this.mDither = dither;
        return this;
    }

    /**
     * Enables the zoom
     *
     * @param zoomEnabled Value that indicates if the Zoom is enabled
     * @return this instance of the view
     */
    public DrawView setZoomEnabled(boolean zoomEnabled) {
        this.mZoomEnabled = zoomEnabled;
        return this;
    }

    /**
     * Set the background image for the DrawView
     *
     * @param backgroundImage File that contains the background image
     * @return this instance of the view
     */
    public DrawView setBackgroundImage(File backgroundImage) {
        if (onDrawViewListener != null)
            onDrawViewListener.onStartDrawing();

        if (mDrawMoveHistoryIndex >= -1 &&
                mDrawMoveHistoryIndex < mDrawMoveHistory.size() - 1)
            mDrawMoveHistory = mDrawMoveHistory.subList(0, mDrawMoveHistoryIndex + 1);

        mDrawMoveHistory.add(DrawMove.newInstance()
                .setBackgroundImage(backgroundImage));
        mDrawMoveHistoryIndex++;

        mDrawMoveBackgroundIndex = mDrawMoveHistoryIndex;

        if (onDrawViewListener != null)
            onDrawViewListener.onEndDrawing();

        invalidate();

        return this;
    }

    /**
     * Set the max zoom factor of the DrawView
     *
     * @param maxZoomFactor The max zoom factor target
     * @return this instance of the view
     */
    public DrawView setMaxZoomFactor(float maxZoomFactor) {
        this.mMaxZoomFactor = maxZoomFactor;
        return this;
    }

    /**
     * Sets the ZoomRegionView scale factor
     *
     * @param zoomRegionScale ZoomRegionView scale factor (DrawView size / scale)
     * @return this instance of the view
     */
    public DrawView setZoomRegionScale(float zoomRegionScale) {
        this.mZoomRegionScale = zoomRegionScale;
        return this;
    }

    /**
     * Sets the ZoomRegionView minimum scale factor
     *
     * @param zoomRegionScaleMin ZoomRegionView scale factor minimum
     * @return this instance of the view
     */
    public DrawView setZoomRegionScaleMin(float zoomRegionScaleMin) {
        this.mZoomRegionScaleMin = zoomRegionScaleMin;
        return this;
    }

    /**
     * Sets the ZoomRegionView maximum scale factor
     *
     * @param zoomRegionScaleMax ZoomRegionView scale factor maximum
     * @return this instance of view
     */
    public DrawView setZoomRegionScaleMax(float zoomRegionScaleMax) {
        this.mZoomRegionScaleMax = zoomRegionScaleMax;
        return this;
    }

    // PRIVATE METHODS

    /**
     * Draw the background image on DrawViewCanvas
     *
     * @param drawMove the DrawMove that contains the background image
     * @param canvas   tha DrawView canvas
     */
    private void drawBackgroundImage(DrawMove drawMove, Canvas canvas) {
        if (this.mBackgroundImage == null) {
            this.mBackgroundImage = drawMove.getBackgroundImage();
            this.mBackgroundImageBitmap = BitmapUtils.GetBitmapForDrawView(this, this.mBackgroundImage, 50, new Matrix());
        } else if (!this.mBackgroundImage.getAbsolutePath().equals(drawMove.getBackgroundImage().getAbsolutePath())) {
            this.mBackgroundImage = drawMove.getBackgroundImage();
            this.mBackgroundImageBitmap = BitmapUtils.GetBitmapForDrawView(this, this.mBackgroundImage, 50, new Matrix());
        }

        int centreX = (getWidth() - mBackgroundImageBitmap.getWidth()) / 2;
        int centreY = (getHeight() - mBackgroundImageBitmap.getHeight()) / 2;
        canvas.drawColor(mBackgroundColor);
        canvas.drawBitmap(this.mBackgroundImageBitmap, centreX, centreY, null);
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

    // SCALE
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (mZoomEnabled) {
                mFromZoomRegion = false;
                mZoomFactor *= detector.getScaleFactor();
                mZoomFactor = Math.max(1f, Math.min(mZoomFactor, mMaxZoomFactor));
                mZoomFactor = mZoomFactor > mMaxZoomFactor ? mMaxZoomFactor : mZoomFactor < 1f ? 1f : mZoomFactor;
                mZoomCenterX = getWidth() - detector.getFocusX();
                mZoomCenterY = getHeight() - detector.getFocusY();

                if (mZoomFactor > 1f)
                    showHideZoomRegionView(VISIBLE);
                else
                    showHideZoomRegionView(INVISIBLE);

                invalidate();
            }

            return false;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(final MotionEvent e) {
            if (mZoomEnabled) {
                mFromZoomRegion = false;
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
            return true;
        }
    }

    // LISTENER

    /**
     * Setting new OnDrawViewListener for this view
     *
     * @param onDrawViewListener
     */
    public void setOnDrawViewListener(OnDrawViewListener onDrawViewListener) {
        this.onDrawViewListener = onDrawViewListener;
    }

    /**
     * Listener for registering drawing actions of the view
     */
    public interface OnDrawViewListener {
        void onStartDrawing();

        void onEndDrawing();

        void onClearDrawing();

        void onRequestText();
    }
}
