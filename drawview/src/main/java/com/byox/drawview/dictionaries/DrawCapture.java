package com.byox.drawview.dictionaries;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class DrawCapture implements Serializable {

    //region VARS
    private static DrawCapture mSingleton;

    private byte[] mCaptureInBytes;
    private Bitmap.CompressFormat mCaptureFormat;
    //endregion

    //region CONSTRUCTORS
    private DrawCapture() {
    }

    public static DrawCapture newInstance() {
        mSingleton = new DrawCapture();
        return mSingleton;
    }
    //endregion

    //region GETTERS & SETTERS
    public byte[] getCaptureInBytes() {
        return mCaptureInBytes;
    }

    public void setCaptureInBytes(byte[] captureInBytes) {
        this.mCaptureInBytes = captureInBytes;
    }

    public Bitmap.CompressFormat getCaptureFormat() {
        return mCaptureFormat;
    }

    public void setCaptureFormat(Bitmap.CompressFormat captureFormat) {
        this.mCaptureFormat = captureFormat;
    }

    //endregion

    //region PUBLIC METHODS

    /**
     * @return Instance of capture in {@link Bitmap} instance
     */
    public Bitmap getCaptureInBitmap() {
        return BitmapFactory.decodeByteArray(getCaptureInBytes(), 0, getCaptureInBytes().length);
    }

    /**
     * @return Save capture in file
     * @throws IOException If save process failed
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File save() throws IOException {
        return save(getSuggestedFileName());
    }

    /**
     * @param fileName File name to save capture
     * @return Save capture in file
     * @throws IOException If save process failed
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File save(String fileName) throws IOException {
        final File filePath = Environment.getExternalStorageDirectory();

        if (!fileName.contains("."))
            fileName = fileName + "." + mCaptureFormat.name().toLowerCase();

        File image = new File(filePath + File.separator + fileName);
        image.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(image);
        getCaptureInBitmap().compress(getCaptureFormat(), 100, fileOutputStream);

        return image;
    }

    /**
     * @return Suggested name for file
     */
    public String getSuggestedFileName() {
        return "DrawViewCapture." + mCaptureFormat.name().toLowerCase();
    }
    //endregion
}
