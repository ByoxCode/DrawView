package com.byox.drawview.abstracts;

import com.byox.drawview.dictionaries.DrawCapture;
import com.byox.drawview.interfaces.OnDrawCameraViewListener;

public abstract class DrawCameraViewListener implements OnDrawCameraViewListener {

    @Override
    public void onDrawCameraViewCaptureStart() {

    }

    @Override
    public void onDrawCameraViewCaptureEnd(DrawCapture capture) {

    }

    @Override
    public void onDrawCameraViewError(Exception e) {

    }
}
