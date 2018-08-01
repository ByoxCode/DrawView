package com.byox.drawview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;
import android.util.Size;
import android.view.View;

import com.byox.drawview.enums.BackgroundScale;
import com.byox.drawview.enums.BackgroundType;
import com.byox.drawview.views.DrawView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IngMedina on 28/03/2017.
 */

public class BitmapUtils {
    public static Bitmap CreateBitmapMatchesViewSize(View imageViewDest, Bitmap bitmapSrc) {
        int currentBitmapWidth = bitmapSrc.getWidth();
        int currentBitmapHeight = bitmapSrc.getHeight();

        int ivWidth = imageViewDest.getWidth();
        int ivHeight = imageViewDest.getHeight();
        int newWidth = ivWidth;
        int newHeight = (int) Math.floor((double) currentBitmapHeight * ((double) newWidth / (double) currentBitmapWidth));

        return Bitmap.createScaledBitmap(bitmapSrc, newWidth, newHeight, true);
    }

    public static Bitmap CreateBitmapMatchesViewSize(int viewWidth, Bitmap bitmapSrc) {
        int currentBitmapWidth = bitmapSrc.getWidth();
        int currentBitmapHeight = bitmapSrc.getHeight();

        int newHeight = (int) Math.floor((double) currentBitmapHeight * ((double) viewWidth / (double) currentBitmapWidth));

        return Bitmap.createScaledBitmap(bitmapSrc, viewWidth, newHeight, true);
    }

    public static byte[] GetCompressedImage(Object image, BackgroundType backgroundType, int compressQuality, boolean recycleSource) {

        Bitmap bmp = null;
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        switch (backgroundType) {
            case FILE:
                bmp = BitmapFactory.decodeFile(((File) image).getAbsolutePath(), options);
                break;
            case BITMAP:
                bmp = (Bitmap) image;
                options.outHeight = bmp.getHeight();
                options.outWidth = bmp.getWidth();
                break;
            case BYTES:
                bmp = BitmapFactory.decodeByteArray((byte[]) image, 0, ((byte[]) image).length, options);
                break;
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = CalculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        switch (backgroundType) {
            case FILE:
                bmp = BitmapFactory.decodeFile(((File) image).getAbsolutePath(), options);
                break;
            case BITMAP:
                bmp = (Bitmap) image;
                break;
            case BYTES:
                bmp = BitmapFactory.decodeByteArray((byte[]) image, 0, ((byte[]) image).length);
                break;
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            Matrix matrix = new Matrix();
            if (backgroundType == BackgroundType.FILE) {
                exif = new ExifInterface(((File) image).getAbsolutePath());

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream stream = null;
        stream = new ByteArrayOutputStream();

//          write the compressed bitmap at the destination specified by filename.
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, compressQuality, stream);

        if (recycleSource) bmp.recycle();

        return stream.toByteArray();

    }

    public static int CalculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static Bitmap GetBitmapForDrawView(DrawView drawView, Object image, BackgroundType backgroundType, int imageQuality, boolean recycleSoruce) {
        byte[] imageInBytes = BitmapUtils.GetCompressedImage(image, backgroundType, imageQuality, recycleSoruce);
        return BitmapUtils.CreateBitmapMatchesViewSize(drawView,
                BitmapFactory.decodeByteArray(imageInBytes, 0, imageInBytes.length));
    }

    public static Bitmap GetBitmapForDrawView(int viewWidth, Object image, BackgroundType backgroundType, int imageQuality, boolean recycleSoruce) {
        byte[] imageInBytes = BitmapUtils.GetCompressedImage(image, backgroundType, imageQuality, recycleSoruce);
        return BitmapUtils.CreateBitmapMatchesViewSize(viewWidth,
                BitmapFactory.decodeByteArray(imageInBytes, 0, imageInBytes.length));
    }

    /*public static Bitmap GetCombinedBitmaps(Bitmap bmp1, Bitmap bmp2, int destWidth, int destHeight) {
        Bitmap bmOverlay = Bitmap.createBitmap(destWidth, destHeight, bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }*/

    /**
     * Combine two bitmap (in byte array format) in same byte array
     *
     * @param baseImage    base byte array bitmap
     * @param overlayImage overlay byte array bitmap
     * @param config       output configuration
     * @param compressFormat    compress format for final image
     * @return combined byte array
     * @throws IOException if the file cannot be created
     */
    public static byte[] CombineBitmapArraysInSameBitmap(byte[] baseImage, byte[] overlayImage, Bitmap.Config config, Bitmap.CompressFormat compressFormat) throws IOException {
        Point frameBitmapSize = GetImageSize(baseImage);
        Point overlayBitmapSize = GetImageSize(overlayImage);
        Bitmap resultBitmap = Bitmap.createBitmap(frameBitmapSize.x, frameBitmapSize.y, config);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, new Matrix(), (Paint)null);
        canvas.drawBitmap(BitmapFactory.decodeByteArray(baseImage, 0, baseImage.length), 0.0F, 0.0F, (Paint)null);
        //byte[] scaledOverlay = GetScaledImageBytesFromBytes(overlayImage, frameBitmapSize.x, frameBitmapSize.y, Bitmap.CompressFormat.PNG, 100);
        canvas.drawBitmap(BitmapFactory.decodeByteArray(overlayImage, 0, overlayImage.length),
                new Rect(0, 0, overlayBitmapSize.x, overlayBitmapSize.y),
                new RectF(0, 0, frameBitmapSize.x, frameBitmapSize.y), (Paint)null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resultBitmap.compress(compressFormat, 100, byteArrayOutputStream);
        byte[] finalBitmap = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        resultBitmap.recycle();
        return finalBitmap;
    }

    /**
     * Get pixel size of image
     *
     * @param imageBytes    Source image file in byte array
     * @return              Point object that contains with and height in x and y properties
     */
    public static Point GetImageSize(byte[] imageBytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
        int width = options.outWidth;
        int height = options.outHeight;

        String type = options.outMimeType;
        return new Point(width, height);
    }

    /**
     * Get scaled image byte array from bitmap source
     * @param imageBytes        Image source bytes to be scaled
     * @param targetW           Image target width
     * @param targetH           Image target height
     * @param compressFormat    Image compress format
     * @param imageQuality      Image compress quality
     * @return                  Byte array of scaled image
     */
    public static byte[] GetScaledImageBytesFromBytes(byte[] imageBytes, int targetW, int targetH, Bitmap.CompressFormat compressFormat, int imageQuality) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, bitmapOptions);

        int photoW = bitmapOptions.outWidth;
        int photoH = bitmapOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;
        bitmapOptions.inPurgeable = true;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, bitmapOptions)
                .compress(compressFormat, imageQuality, stream);

        return stream.toByteArray();
    }
}
