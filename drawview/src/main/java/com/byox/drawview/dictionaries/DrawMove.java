package com.byox.drawview.dictionaries;

import android.graphics.Matrix;

import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.utils.SerializablePaint;
import com.byox.drawview.utils.SerializablePath;

import java.io.Serializable;

/**
 * Created by Ing. Oscar G. Medina Cruz on 07/11/2016.
 * <p>
 * Dictionary class that save move for draw in the view, this allow the user to make a history
 * of the user movements in the view and make a redo/undo.
 *
 * @author Ing. Oscar G. Medina Cruz
 */

public class DrawMove implements Serializable {

    //region VARS
    private static DrawMove mSingleton;

    private SerializablePaint mPaint;
    private DrawingMode mDrawingMode = null;
    private DrawingTool mDrawingTool = null;
    private int mDrawingShapeSides;
    //private List<SerializablePath> mDrawingPathList;
    private SerializablePath mDrawingPath;
    private float mStartX, mStartY, mEndX, mEndY;
    private String mText;
    private Matrix mBackgroundMatrix;
    private byte[] mBackgroundImage;
    //endregion

    //region CONSTRUCTORS
    private DrawMove() {
    }

    public static DrawMove newInstance() {
        mSingleton = new DrawMove();
        return mSingleton;
    }
    //endregion

    //region GETTERS

    public SerializablePaint getPaint() {
        if (mSingleton != null)
            return mPaint;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawingMode getDrawingMode() {
        if (mSingleton != null)
            return mDrawingMode;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawingTool getDrawingTool() {
        if (mSingleton != null)
            return mDrawingTool;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public SerializablePath getDrawingPath() {
        if (mSingleton != null)
            return mDrawingPath;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public float getStartX() {
        if (mSingleton != null)
            return mStartX;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public float getStartY() {
        if (mSingleton != null)
            return mStartY;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public float getEndX() {
        if (mSingleton != null)
            return mEndX;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public float getEndY() {
        if (mSingleton != null)
            return mEndY;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public String getText() {
        if (mSingleton != null)
            return mText;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public Matrix getBackgroundMatrix() {
        if (mSingleton != null)
            return mBackgroundMatrix;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public byte[] getBackgroundImage() {
        if (mSingleton != null)
            return mBackgroundImage;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public int getDrawingShapeSides() {
        return mDrawingShapeSides;
    }

    //endregion

    //region SETTERS

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

    public DrawMove setDrawingShapeSides(int drawingShapeSides) {
        this.mDrawingShapeSides = drawingShapeSides;
        return this;
    }

    //endregion
}
