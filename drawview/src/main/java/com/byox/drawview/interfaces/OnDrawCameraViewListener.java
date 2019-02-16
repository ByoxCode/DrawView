package com.byox.drawview.interfaces;

import com.byox.drawview.dictionaries.DrawCapture;

public interface OnDrawCameraViewListener {

    void onDrawCameraViewCaptureStart();
    void onDrawCameraViewCaptureEnd(DrawCapture capture);
    void onDrawCameraViewError(Exception e);
}
