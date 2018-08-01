package com.byox.drawview.dictionaries;

import android.graphics.Matrix;

import com.byox.drawview.enums.BackgroundScale;
import com.byox.drawview.enums.BackgroundType;
import com.squareup.picasso.Transformation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class BackgroundImageData implements Serializable {

    private static BackgroundImageData mSingleton;

    private Object mBackground;
    private int mDrawViewWidth;
    private int mCompressQuality;
    private Matrix mBackgroundMatrix;
    private BackgroundScale mBackgroundScale;
    private BackgroundType mBackgroundType;
    private List<Transformation> mBackgroundTransformations;

    /**
     * CONSTRUCTOR
     */
    private BackgroundImageData() {
    }

    /**
     * Create a new instance of {@link BackgroundImageData}
     *
     * @return New {@link BackgroundImageData} instance
     */
    public static BackgroundImageData newInstance() {
        if (mSingleton == null) mSingleton = new BackgroundImageData();
        return mSingleton;
    }

    /**
     * Set the background object
     *
     * @param background Background object
     * @return Current {@link BackgroundImageData} instance
     */
    public BackgroundImageData background(Object background) {
        if (mSingleton != null) {
            mSingleton.mBackground = background;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    /**
     * Set the with of the {@link com.byox.drawview.views.DrawView} when the background will be drawn
     *
     * @param drawViewWidth {@link com.byox.drawview.views.DrawView} width
     * @return Current {@link BackgroundImageData} instance
     */
    public BackgroundImageData drawViewWidth(int drawViewWidth) {
        if (mSingleton != null) {
            mSingleton.mDrawViewWidth = drawViewWidth;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    /**
     * Set the compress quality for the background image
     *
     * @param compressQuality Compress quality for background image
     * @return Current {@link BackgroundImageData} instance
     */
    public BackgroundImageData compressQuality(int compressQuality) {
        if (mSingleton != null) {
            mSingleton.mCompressQuality = compressQuality;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    /**
     * Set the matrix for the background image
     *
     * @param matrix Background matrix
     * @return Current {@link BackgroundImageData} instance
     */
    public BackgroundImageData matrix(Matrix matrix) {
        if (mSingleton != null) {
            mSingleton.mBackgroundMatrix = matrix;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    /**
     * Set the {@link BackgroundScale} for the background image
     *
     * @param backgroundScale {@link BackgroundScale} for the image
     * @return Current {@link BackgroundImageData} instance
     */
    public BackgroundImageData backgroundScale(BackgroundScale backgroundScale) {
        if (mSingleton != null) {
            mSingleton.mBackgroundScale = backgroundScale;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    /**
     * Set the {@link BackgroundType} of the background image
     *
     * @param backgroundType {@link BackgroundType} of the background image
     * @return Current {@link BackgroundImageData} instance
     */
    public BackgroundImageData backgroundType(BackgroundType backgroundType) {
        if (mSingleton != null) {
            mSingleton.mBackgroundType = backgroundType;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    /**
     * Set the {@link Transformation} list for the background image
     *
     * @param transformations {@link Transformation} list for the background image
     * @return Current {@link BackgroundImageData} instance
     */
    public BackgroundImageData transformations(Transformation... transformations) {
        if (mSingleton != null) {
            mSingleton.mBackgroundTransformations = Arrays.asList(transformations);
            return mSingleton;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    //region GETTERS

    public Object getBackground() {
        if (mSingleton != null) {
            return mSingleton.mBackground;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    public int getDrawViewWidth() {
        if (mSingleton != null) {
            return mSingleton.mDrawViewWidth;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    public int getCompressQuality() {
        if (mSingleton != null) {
            return mSingleton.mCompressQuality;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    public Matrix getBackgroundMatrix() {
        if (mSingleton != null) {
            return mSingleton.mBackgroundMatrix;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    public BackgroundScale getBackgroundScale() {
        if (mSingleton != null) {
            return mSingleton.mBackgroundScale;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    public BackgroundType getBackgroundType() {
        if (mSingleton != null) {
            return mSingleton.mBackgroundType;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }

    public List<Transformation> getTransformations() {
        if (mSingleton != null) {
            return mSingleton.mBackgroundTransformations;
        } else throw new RuntimeException("Create new instance of BackgroundImageData first!");
    }
    //endregion
}
