package com.byox.drawview.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.byox.drawview.R;
import com.byox.drawview.dictionaries.DrawMove;
import com.byox.drawview.enums.DrawingCapture;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;

import java.io.ByteArrayOutputStream;
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

    // FINAL VARS
    final String TAG = "DrawView";

    // LISTENER
    private OnDrawViewListener onDrawViewListener;

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

    private DrawingMode mDrawingMode;
    private DrawingTool mDrawingTool;

    private List<DrawMove> mDrawMoveHistory;
    private int mDrawMoveHistoryIndex = -1;

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
        for (int i = 0; i < mDrawMoveHistoryIndex + 1; i++) {
            DrawMove drawMove = mDrawMoveHistory.get(i);
            switch (drawMove.getDrawingMode()) {
                case DRAW:
                    switch (drawMove.getDrawingTool()) {
                        case PEN:
                            if (drawMove.getDrawingPathList() != null &&
                                    drawMove.getDrawingPathList().size() > 0)
                                for (Path path : drawMove.getDrawingPathList())
                                    canvas.drawPath(path, drawMove.getPaint());
                            break;
                        case LINE:
                            canvas.drawLine(drawMove.getStartX(), drawMove.getStartY(),
                                    drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
                            break;
                        case RECTANGLE:
                            canvas.drawRect(drawMove.getStartX(), drawMove.getStartY(),
                                    drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
                            break;
                        case CIRCLE:
                            if (drawMove.getEndX() > drawMove.getStartX())
                                canvas.drawCircle(drawMove.getStartX(), drawMove.getStartY(),
                                        drawMove.getEndX() - drawMove.getStartX(), drawMove.getPaint());
                            else
                                canvas.drawCircle(drawMove.getStartX(), drawMove.getStartY(),
                                        drawMove.getStartX() - drawMove.getEndX(), drawMove.getPaint());
                            break;
                    }
                    break;
                case TEXT:
                    if (drawMove.getText() != null && !drawMove.getText().equals(""))
                        canvas.drawText(drawMove.getText(), drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
                    break;
                case ERASER:
                    if (drawMove.getDrawingPathList() != null &&
                            drawMove.getDrawingPathList().size() > 0)
                        for (Path path : drawMove.getDrawingPathList())
                            canvas.drawPath(path, drawMove.getPaint());
                    break;
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
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (onDrawViewListener != null)
                    onDrawViewListener.onStartDrawing();

                if (mDrawMoveHistoryIndex >= -1 &&
                        mDrawMoveHistoryIndex < mDrawMoveHistory.size() - 1)
                    mDrawMoveHistory = mDrawMoveHistory.subList(0, mDrawMoveHistoryIndex + 1);

                mDrawMoveHistory.add(DrawMove.newInstance()
                        .setPaint(getNewPaintParams())
                        .setStartX(motionEvent.getX()).setStartY(motionEvent.getY())
                        .setEndX(motionEvent.getX()).setEndY(motionEvent.getY())
                        .setDrawingMode(mDrawingMode).setDrawingTool(mDrawingTool));
                mDrawMoveHistoryIndex++;

                if (mDrawingTool == DrawingTool.PEN || mDrawingMode == DrawingMode.ERASER) {
                    Path path = new Path();
                    path.moveTo(motionEvent.getX(), motionEvent.getY());
                    path.lineTo(motionEvent.getX(), motionEvent.getY());

                    mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).setDrawingPathList(new ArrayList<Path>());
                    mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList().add(path);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).setEndX(motionEvent.getX()).setEndY(motionEvent.getY());

                if (mDrawingTool == DrawingTool.PEN || mDrawingMode == DrawingMode.ERASER) {
                    mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList()
                            .get(mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList().size() - 1)
                            .lineTo(motionEvent.getX(), motionEvent.getY());
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).setEndX(motionEvent.getX()).setEndY(motionEvent.getY());

                if (mDrawingTool == DrawingTool.PEN || mDrawingMode == DrawingMode.ERASER) {
                    mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList()
                            .get(mDrawMoveHistory.get(mDrawMoveHistory.size() - 1).getDrawingPathList().size() - 1)
                            .lineTo(motionEvent.getX(), motionEvent.getY());
                }

                if (onDrawViewListener != null && mDrawingMode == DrawingMode.TEXT)
                    onDrawViewListener.onRequestText();

                if (onDrawViewListener != null)
                    onDrawViewListener.onEndDrawing();

                invalidate();
                break;
        }
        return true;
    }

    // PRIVATE METHODS

    /**
     * Initialize general vars for the view
     */
    private void initVars() {
        mDrawMoveHistory = new ArrayList<>();
        setOnTouchListener(this);
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
                mBackgroundColor = ((ColorDrawable) getBackground()).getColor();
            else {
                setBackgroundColor(Color.WHITE);
                mBackgroundColor = ((ColorDrawable) getBackground()).getColor();
            }


            mDrawingTool = DrawingTool.values()[typedArray.getInteger(R.styleable.DrawView_dv_draw_tool, 0)];
            mDrawingMode = DrawingMode.values()[typedArray.getInteger(R.styleable.DrawView_dv_draw_mode, 0)];
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
        if (mDrawMoveHistory.size() > 0) {
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
        setDrawingCacheEnabled(false);
        setDrawingCacheEnabled(true);

        switch (drawingCapture) {
            case BITMAP:
                return getDrawingCache(true);
            case BYTES:
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                getDrawingCache(true).compress(Bitmap.CompressFormat.PNG, 100, stream);
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

    // SETTERS

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
