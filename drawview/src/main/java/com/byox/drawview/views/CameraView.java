package com.byox.drawview.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.byox.drawview.enums.DrawingCapture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by IngMedina on 29/04/2017.
 */

public class CameraView extends SurfaceView
        implements SurfaceHolder.Callback,
        Camera.PreviewCallback {

    public enum CAMERA_TYPE {
        FRONT_CAMERA,
        BACK_CAMERA
    }

    // CONSTANTS
    private final String TAG = "CameraView";

    private Context mContext;
    private Camera mCamera;
    private SurfaceHolder mCurrentSurface;
    private CAMERA_TYPE mCurrentCameraType;
    private boolean mFlash = false;
    private YuvImage mYuvImage;
    private byte[] byteArray;

    private List<Camera.Size> mCameraSizes;
    private Camera.Size mPreviewSize;

    private OnCameraViewListener mListener;

    public CameraView(Activity activity, Context context) {
        super(context);
        mContext = context;
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        try {
            mListener = (OnCameraViewListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public CameraView(Context context) {
        super(context);

        mContext = context;
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("CAMERA_VIEW", "Creating camera");
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCurrentCameraType = CAMERA_TYPE.BACK_CAMERA;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("CAMERA_VIEW", "Changing camera");
//        mCameraWidth = width;
//        mCameraHeight = height;
        mCurrentSurface = holder;
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("CAMERA_VIEW", "Destroying camera");
        releaseCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            Camera.Parameters p = camera.getParameters();
            int w = p.getPreviewSize().width;
            int h = p.getPreviewSize().height;
            int format = p.getPreviewFormat();
            mYuvImage = new YuvImage(data, format, w, h, null);
            byteArray = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Find size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.i(TAG, "Optimal size: " + optimalSize.width + "x" + optimalSize.height);
        return optimalSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (mCameraSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mCameraSizes, width, height);
        }

        if (mPreviewSize != null) {
            float ratio;
            if (mPreviewSize.height >= mPreviewSize.width) {
                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
                setMeasuredDimension((int) (width * ratio), height);
            } else {
                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;
                setMeasuredDimension(width, (int) (height * ratio));
            }
        } else
            setMeasuredDimension(width, height);
    }

    /**
     * METODOS
     */
    private int getCameraNumber() {
        return Camera.getNumberOfCameras();
    }

    public void changeCamera(CAMERA_TYPE camera_type) {

        if (getCameraNumber() == 1)
            return;

        releaseCamera();

        mCurrentCameraType = camera_type;

        switch (camera_type) {
            case BACK_CAMERA:
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                break;
            case FRONT_CAMERA:
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                break;
            default:
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                break;
        }

        initCamera();
    }

    public void releaseCamera() {
        try {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            getHolder().removeCallback(this);
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initCamera() {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mCamera == null) {
            switch (mCurrentCameraType) {
                case BACK_CAMERA:
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    break;
                case FRONT_CAMERA:
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    break;
                default:
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    break;
            }
            mCamera.setPreviewCallback(this);
        }

        Camera.Parameters p = mCamera.getParameters();
        mCameraSizes = p.getSupportedPreviewSizes();

        if (mPreviewSize == null)
            return;

        p.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(p);

        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            p.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);
        } else {
            p.set("orientation", "landscape");
            mCamera.setDisplayOrientation(180);
        }

        try {
            mCamera.setPreviewCallback(this);
            mCamera.setPreviewDisplay(getHolder());
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mListener.onCameraShow();
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    public Camera getCurrentCamera() {
        return mCamera;
    }

    public CAMERA_TYPE getCurrentCameraType() {
        return mCurrentCameraType;
    }

    public void changeTorchStatus(boolean turnOption) {

        if (mCurrentCameraType == CAMERA_TYPE.FRONT_CAMERA)
            return;

        if (turnOption) {
            turnOffTorch();
        } else {
            turnOnTorch();
        }
    }

    public boolean getFlashStatus() {
        return mFlash;
    }

    private void turnOnTorch() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        Log.i("turnOnTorch", "Flash mode: " + flashMode);
        Log.i("turnOnTorch", "Flash modes: " + flashModes);
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mFlash = true;
            } else {
                Log.e("turnOnTorch", "FLASH_MODE_TORCH not supported");
            }
        }
    }

    private void turnOffTorch() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        Log.i("turnOffTorch", "Flash mode: " + flashMode);
        Log.i("turnOffTorch", "Flash modes: " + flashModes);
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mFlash = false;
            } else {
                Log.e("turnOffTorch", "FLASH_MODE_OFF not supported");
            }
        }
    }

    public Object getCameraFrame(DrawingCapture drawingCapture) {
        Camera.Parameters p = mCamera.getParameters();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Rect area = new Rect(0, 0, p.getPreviewSize().width, p.getPreviewSize().height);
        mYuvImage.compressToJpeg(area, 100, out);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if (drawingCapture == DrawingCapture.BITMAP)
            return bitmap;
        else
            return out.toByteArray();
    }

    /*public Object getCameraFrame(DrawingCapture drawingCapture) {
        Object capturedFrame = null;
        try {
            Camera.Parameters p = mCamera.getParameters();
            if (p == null)
                return null;

            int format = p.getPreviewFormat();
            YuvImage yuvImage = new YuvImage(byteArray, format, p.getPreviewSize().width, p.getPreviewSize().height, null);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            Rect rect = new Rect(0, 0, p.getPreviewSize().width, p.getPreviewSize().height);

            yuvImage.compressToJpeg(rect, 75, byteArrayOutputStream);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inInputShareable = true;

            if (drawingCapture == DrawingCapture.BITMAP)
                capturedFrame = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size(), options);
            else
                capturedFrame = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return capturedFrame;
    }*/

    public void setOnCameraViewListener(OnCameraViewListener onCameraViewListener) {
        mListener = onCameraViewListener;
    }

    /**
     * INTERFACES
     */
    public interface OnCameraViewListener {
        void onCameraShow();
    }
}
