package com.byox.drawview.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.byox.drawview.R;
import com.byox.drawview.abstracts.DrawCameraViewListener;
import com.byox.drawview.enums.DrawingCapture;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingOrientation;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.interfaces.OnDrawCameraViewListener;
import com.byox.drawview.utils.BitmapUtils;
import com.byox.drawview.utils.SerializablePaint;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by IngMedina on 29/04/2017.
 */

public class DrawCameraView extends FrameLayout {

    //region VIEWS
    private CameraView mCameraViewInstance;
    private DrawView mDrawViewInstance;
    //endregion

    //region LISTENERS
    private OnDrawCameraViewListener mOnDrawCameraViewListener;
    private DrawCameraViewListener mDrawCameraViewListener;
    //endregion

    //region CONSTRUCTORS
    public DrawCameraView(@NonNull Context context) {
        this(context, null);
    }

    public DrawCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawCameraView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCameraView(context, attrs);
        initDrawView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawCameraView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCameraView(context, attrs);
        initDrawView(context, attrs);
    }
    //endregion

    //region EVENTS
    //endregion

    //region PRIVATE METHODS
    private void initCameraView(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            mCameraViewInstance = new CameraView(context);
            mCameraViewInstance.setFocusable(false);
            mCameraViewInstance.setClickable(false);
            if (attrs != null) initCameraViewAttributes(context, attrs);
            addView(mCameraViewInstance);
        }
    }

    private void initDrawView(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            mDrawViewInstance = new DrawView(context, false);
            if (attrs != null) initDrawViewAttributes(context, attrs);
            addView(mDrawViewInstance);
        }
    }

    /**
     * Initialize {@link DrawView} attributes
     *
     * @param context Application context
     * @param attrs   {@link AttributeSet} of the view
     */
    private void initDrawViewAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.DrawCameraView, 0, 0);
        try {
            if (mDrawViewInstance != null) {
                mDrawViewInstance
                        .color(typedArray.getColor(R.styleable.DrawCameraView_dcv_draw_color, Color.BLACK))
                        .width(typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_width, 3))
                        .alpha(typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_alpha, 255))
                        .antiAlias(typedArray.getBoolean(R.styleable.DrawCameraView_dcv_draw_anti_alias, true))
                        .dither(typedArray.getBoolean(R.styleable.DrawCameraView_dcv_draw_dither, true));

                int paintStyle = typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_style, 2);

                mDrawViewInstance.paintStyle(paintStyle == 0
                        ? SerializablePaint.Style.FILL : paintStyle == 1
                        ? SerializablePaint.Style.FILL_AND_STROKE : SerializablePaint.Style.STROKE);

                int cap = typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_corners, 2);

                mDrawViewInstance.lineCap(cap == 0
                        ? SerializablePaint.Cap.BUTT : cap == 1
                        ? SerializablePaint.Cap.ROUND : SerializablePaint.Cap.SQUARE);

                int typeface = typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_font_family, 0);

                mDrawViewInstance.fontFamily(typeface == 0
                        ? Typeface.DEFAULT : typeface == 1
                        ? Typeface.MONOSPACE : typeface == 2
                        ? Typeface.SANS_SERIF : Typeface.SERIF);

                mDrawViewInstance
                        .fontSize(typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_font_size, 12))
                        .forCamera(true);

                int orientation = typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_orientation,
                        getWidth() > getHeight() ? 1 : 0);

                mDrawViewInstance.initialOrientation(DrawingOrientation.values()[orientation]);

                // The background for draw view when is used for camera is always transparent
                setBackgroundColor(Color.TRANSPARENT);

                SerializablePaint backgroundPaint = new SerializablePaint();
                backgroundPaint.setStyle(SerializablePaint.Style.FILL);
                backgroundPaint.setColor(Color.TRANSPARENT);

                mDrawViewInstance.backgroundPaint(backgroundPaint);

                mDrawViewInstance
                        .tool(DrawingTool.values()[typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_tool, 0)])
                        .mode(DrawingMode.values()[typedArray.getInteger(R.styleable.DrawCameraView_dcv_draw_mode, 0)])
                        .enableZoom(typedArray.getBoolean(R.styleable.DrawCameraView_dcv_draw_enable_zoom, false))
                        .zoomRegionScale(typedArray.getFloat(R.styleable.DrawCameraView_dcv_draw_zoomregion_scale, mDrawViewInstance.getZoomRegionScale()))
                        .zoomRegionScaleMin(typedArray.getFloat(R.styleable.DrawCameraView_dcv_draw_zoomregion_minscale, mDrawViewInstance.getZoomRegionScaleMax()))
                        .zoomRegionScaleMax(typedArray.getFloat(R.styleable.DrawCameraView_dcv_draw_zoomregion_maxscale, mDrawViewInstance.getZoomRegionScaleMin()));
            }
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Initialize {@link CameraView} attributes
     *
     * @param context Application context
     * @param attrs   {@link AttributeSet} of the view
     */
    private void initCameraViewAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.DrawCameraView, 0, 0);
        try {
            if (mCameraViewInstance != null) {
                mCameraViewInstance.setFacing(typedArray.getInteger(R.styleable.DrawCameraView_dcvckFacing, 0));
                mCameraViewInstance.setFlash(typedArray.getInteger(R.styleable.DrawCameraView_dcvckFlash, 0));
                mCameraViewInstance.setFocus(typedArray.getInteger(R.styleable.DrawCameraView_dcvckFocus, 1));
                mCameraViewInstance.setMethod(typedArray.getInteger(R.styleable.DrawCameraView_dcvckMethod, 0));
                mCameraViewInstance.setPermissions(0);
                mCameraViewInstance.setJpegQuality(typedArray.getInteger(R.styleable.DrawCameraView_dcvckJpegQuality, 100));
                mCameraViewInstance.setVideoQuality(typedArray.getInteger(R.styleable.DrawCameraView_dcvckVideoQuality, 4));
                mCameraViewInstance.setCropOutput(true);
                mCameraViewInstance.setVideoBitRate(typedArray.getInteger(R.styleable.DrawCameraView_dcvckVideoBitRate, 0));
                mCameraViewInstance.setLockVideoAspectRatio(typedArray.getBoolean(R.styleable.DrawCameraView_dcvckLockVideoAspectRatio, false));

                // Hot fix to initialize mAdjustViewBounds variable using reflection
                Field field = null;
                try {
                    field = CameraView.class.getDeclaredField("mAdjustViewBounds");
                    field.setAccessible(true);
                    field.set(mCameraViewInstance, typedArray.getBoolean(R.styleable.DrawCameraView_android_adjustViewBounds, false));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            typedArray.recycle();
        }
    }
    //endregion

    //region PUBLIC METHODS
    public void onStart() {
        if (mCameraViewInstance != null) {
            mCameraViewInstance.start();
        }
    }

    public void onStop() {
        if (mCameraViewInstance != null) {
            mCameraViewInstance.stop();
        }
    }

    public void createCapture(final DrawingCapture drawingCapture) {
        if (mOnDrawCameraViewListener != null || mDrawCameraViewListener != null) {
            if (mOnDrawCameraViewListener != null)
                mOnDrawCameraViewListener.onDrawCameraViewCaptureStart();
            if (mDrawCameraViewListener != null)
                mDrawCameraViewListener.onDrawCameraViewCaptureStart();

            mCameraViewInstance.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                @Override
                public void callback(CameraKitImage cameraKitImage) {
                    new ProcessCapture(drawingCapture)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cameraKitImage);
                }
            });
        }
    }
    //endregion

    //region GETTERS
    public CameraView getCamera() {
        return mCameraViewInstance;
    }

    public DrawView getDrawView() {
        return mDrawViewInstance;
    }
    //endregion

    //region LISTENERS

    /**
     * Setting new {@link OnDrawCameraViewListener} interface listener for this view
     *
     * @param onDrawCameraViewListener {@link OnDrawCameraViewListener} interface
     */
    public void addDrawCameraViewListener(OnDrawCameraViewListener onDrawCameraViewListener) {
        this.mOnDrawCameraViewListener = onDrawCameraViewListener;
    }

    /**
     * Setting new {@link DrawCameraViewListener} abstract listener for this view
     *
     * @param drawCameraViewListener {@link DrawCameraViewListener} abstract class
     */
    public void addDrawCameraViewListener(DrawCameraViewListener drawCameraViewListener) {
        this.mDrawCameraViewListener = drawCameraViewListener;
    }
    //endregion

    //region ASYNC TASKS
    @SuppressLint("StaticFieldLeak")
    class ProcessCapture extends AsyncTask<CameraKitImage, Void, Exception> {

        private Object[] result;
        private DrawingCapture drawingCapture;

        public ProcessCapture(DrawingCapture drawingCapture) {
            this.drawingCapture = drawingCapture;
        }

        @Override
        protected Exception doInBackground(CameraKitImage... cameraKitImages) {
            result = mDrawViewInstance.createCapture(DrawingCapture.BYTES);
            try {
                result[0] = BitmapUtils.CombineBitmapArraysInSameBitmap(
                        cameraKitImages[0].getJpeg(), (byte[]) result[0],
                        Bitmap.Config.ARGB_8888, Bitmap.CompressFormat.JPEG);
            } catch (IOException e) {
                return e;
            }
            result[1] = "JPG";

            switch (drawingCapture) {
                case BITMAP:
                    result[0] = BitmapFactory.decodeByteArray((byte[]) result[0], 0, ((byte[]) result[0]).length);
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);

            if (e != null) {
                if (mOnDrawCameraViewListener != null)
                    mOnDrawCameraViewListener.onDrawCameraViewError(e);
                if (mDrawCameraViewListener != null)
                    mDrawCameraViewListener.onDrawCameraViewError(e);
            } else {
                if (mOnDrawCameraViewListener != null)
                    mOnDrawCameraViewListener.onDrawCameraViewCaptureEnd(result);
                if (mDrawCameraViewListener != null)
                    mDrawCameraViewListener.onDrawCameraViewCaptureEnd(result);
            }
        }
    }
    //endregion
}
