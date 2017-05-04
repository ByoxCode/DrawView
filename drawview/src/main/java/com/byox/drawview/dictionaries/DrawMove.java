package com.byox.drawview.dictionaries;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.byox.drawview.enums.BackgroundScale;
import com.byox.drawview.enums.BackgroundType;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.utils.SerializablePaint;
import com.byox.drawview.utils.SerializablePath;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Ing. Oscar G. Medina Cruz on 07/11/2016.
 * <p>
 * Dictionary class that save move for draw in the view, this allow the user to make a history
 * of the user movements in the view and make a redo/undo.
 *
 * @author Ing. Oscar G. Medina Cruz
 */

public class DrawMove implements Serializable {

    private static DrawMove mSingleton;

    private SerializablePaint mPaint;
    private DrawingMode mDrawingMode = null;
    private DrawingTool mDrawingTool = null;
    //private List<SerializablePath> mDrawingPathList;
    private SerializablePath mDrawingPath;
    private float mStartX, mStartY, mEndX, mEndY;
    private String mText;
    private Matrix mBackgroundMatrix;
    private byte[] mBackgroundImage;

    // METHODS
    private DrawMove() {
    }

    public static DrawMove newInstance() {
        mSingleton = new DrawMove();
        return mSingleton;
    }

    // GETTERS

    public SerializablePaint getPaint() {
        return mPaint;
    }

    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }

    public DrawingTool getDrawingTool() {
        return mDrawingTool;
    }

    public SerializablePath getDrawingPath() {
        return mDrawingPath;
    }

    public float getStartX() {
        return mStartX;
    }

    public float getStartY() {
        return mStartY;
    }

    public float getEndX() {
        return mEndX;
    }

    public float getEndY() {
        return mEndY;
    }

    public String getText() {
        return mText;
    }

    public Matrix getBackgroundMatrix(){
        return mBackgroundMatrix;
    }

    public byte[] getBackgroundImage() {
        return mBackgroundImage;
    }

    // SETTERS

    public DrawMove setPaint(SerializablePaint paint) {
        if (mSingleton != null) {
            mSingleton.mPaint = paint;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingMode(DrawingMode drawingMode) {
        if (mSingleton != null) {
            mSingleton.mDrawingMode = drawingMode;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingTool(DrawingTool drawingTool) {
        if (mSingleton != null) {
            mSingleton.mDrawingTool = drawingTool;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingPathList(SerializablePath drawingPath) {
        if (mSingleton != null) {
            mSingleton.mDrawingPath = drawingPath;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setStartX(float startX) {
        if (mSingleton != null) {
            mSingleton.mStartX = startX;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setStartY(float startY) {
        if (mSingleton != null) {
            mSingleton.mStartY = startY;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setEndX(float endX) {
        if (mSingleton != null) {
            mSingleton.mEndX = endX;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setEndY(float endY) {
        if (mSingleton != null) {
            mSingleton.mEndY = endY;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setText(String text) {
        if (mSingleton != null) {
            mSingleton.mText = text;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setBackgroundImage(byte[] backgroundImage, Matrix backgroundMatrix) {
        if (mSingleton != null) {
            mSingleton.mBackgroundImage = backgroundImage;
            mSingleton.mBackgroundMatrix = backgroundMatrix;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }
}
