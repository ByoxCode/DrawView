package com.byox.drawview.dictionaries;

import android.graphics.Paint;
import android.graphics.Path;

import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;

import java.io.File;
import java.util.List;

/**
 * Created by Ing. Oscar G. Medina Cruz on 07/11/2016.
 * <p>
 * Dictionary class that save move for draw in the view, this allow the user to make a history
 * of the user movements in the view and make a redo/undo.
 *
 * @author Ing. Oscar G. Medina Cruz
 */

public class DrawMove {

    private static DrawMove mSingleton;

    private Paint mPaint;
    private DrawingMode mDrawingMode = null;
    private DrawingTool mDrawingTool = null;
    private List<Path> mDrawingPathList;
    private float mStartX, mStartY, mEndX, mEndY;
    private String mText;
    private File mBackgroundImage;

    // METHODS
    public DrawMove() {
    }

    public static DrawMove newInstance() {
        mSingleton = new DrawMove();
        return mSingleton;
    }

    // GETTERS

    public Paint getPaint() {
        return mPaint;
    }

    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }

    public DrawingTool getDrawingTool() {
        return mDrawingTool;
    }

    public List<Path> getDrawingPathList() {
        return mDrawingPathList;
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

    public File getBackgroundImage() {
        return mBackgroundImage;
    }

    // SETTERS

    public DrawMove setPaint(Paint paint) {
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

    public DrawMove setDrawingPathList(List<Path> drawingPathList) {
        if (mSingleton != null) {
            mSingleton.mDrawingPathList = drawingPathList;
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

    public DrawMove setBackgroundImage(File backgroundImage) {
        if (mSingleton != null) {
            mSingleton.mBackgroundImage = backgroundImage;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }
}
