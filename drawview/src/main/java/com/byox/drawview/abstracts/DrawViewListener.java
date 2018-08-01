package com.byox.drawview.abstracts;

import android.graphics.Bitmap;

import com.byox.drawview.enums.BackgroundType;
import com.byox.drawview.interfaces.OnDrawViewListener;

public abstract class DrawViewListener implements OnDrawViewListener{

    @Override
    public void onStartDrawing() {

    }

    @Override
    public void onEndDrawing() {

    }

    @Override
    public void onClearDrawing() {

    }

    @Override
    public void onRequestText() {

    }

    @Override
    public void onAllMovesPainted() {

    }

    @Override
    public void onDrawBackgroundStart() {

    }

    @Override
    public void onDrawBackgroundEnds(Bitmap bitmap, BackgroundType originBackgroundType) {

    }

    @Override
    public void onDrawBackgroundEnds(byte[] bytes, BackgroundType originBackgroundType) {

    }

    @Override
    public void onDrawingError(Exception e) {

    }
}
