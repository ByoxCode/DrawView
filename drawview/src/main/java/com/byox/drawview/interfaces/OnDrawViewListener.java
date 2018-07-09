package com.byox.drawview.interfaces;

import android.graphics.Bitmap;

/**
 * Listener for registering drawing actions of the view
 *
 * @author Ing. Oscar G. Medina Cruz
 */
public interface OnDrawViewListener {
    void onStartDrawing();
    void onEndDrawing();
    void onClearDrawing();
    void onRequestText();
    void onAllMovesPainted();
    void onBackgroundLoaded(Bitmap bitmap);
    void onBackgroundLoaded(byte[] bytes);
    void onDrawingError(Exception e);
}
