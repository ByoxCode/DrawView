package com.byox.drawview.interfaces;

import android.graphics.Bitmap;

import com.byox.drawview.dictionaries.DrawCapture;
import com.byox.drawview.enums.BackgroundType;

/**
 * Listener for registering drawing actions of the view
 *
 * @author Ing. Oscar G. Medina Cruz
 */
public interface OnDrawViewListener extends OnDrawViewCaptureListener{
    void onStartDrawing();
    void onEndDrawing();
    void onClearDrawing();
    void onRequestText();
    void onAllMovesPainted();
    void onDrawBackgroundStart();
    void onDrawBackgroundEnds(Bitmap bitmap, BackgroundType originBackgroundType);
    void onDrawBackgroundEnds(byte[] bytes, BackgroundType originBackgroundType);
    void onDrawingError(Exception e);
}
