package com.byox.drawview.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.byox.drawview.R;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingOrientation;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.utils.SerializablePaint;
import com.byox.drawview.utils.ViewUtils;

/**
 * Created by IngMedina on 29/04/2017.
 */

public class DrawCameraView extends FrameLayout {

    private CameraView mCameraView;

    public DrawCameraView(@NonNull Context context) {
        super(context);
        initCameraView();
    }

    public DrawCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initCameraView();
    }

    public DrawCameraView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCameraView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawCameraView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCameraView();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mCameraView != null)
            mCameraView.releaseCamera();

        super.onDetachedFromWindow();
    }

    // METHODS
    private void initCameraView() {
        try {
            if (mCameraView == null) {
                mCameraView = new CameraView(getContext());
                mCameraView.setOnCameraViewListener(new CameraView.OnCameraViewListener() {
                    @Override
                    public void onCameraShow() {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void attachCameraView() {
        if (getChildCount() == 0)
            addView(mCameraView);
    }

    public CameraView getCameraView() {
        return mCameraView;
    }
}
