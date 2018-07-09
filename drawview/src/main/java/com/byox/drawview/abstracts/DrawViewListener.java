package com.byox.drawview.abstracts;

import android.graphics.Bitmap;

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
    public void onBackgroundLoaded(Bitmap bitmap) {

    }

    @Override
    public void onBackgroundLoaded(byte[] bytes) {

    }

    @Override
    public void onDrawingError(Exception e) {

    }
}
