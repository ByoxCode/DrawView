package com.byox.drawview.interfaces;

public interface OnDrawCameraViewListener {

    void onDrawCameraViewCaptureStart();
    void onDrawCameraViewCaptureEnd(Object[] data);
    void onDrawCameraViewError(Exception e);
}
