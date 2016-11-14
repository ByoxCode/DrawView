package com.byox.drawview.dictionaries;

import android.graphics.Paint;
import android.graphics.Path;

import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;

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
    private DrawingMode mDrawingMode;
    private DrawingTool mDrawingTool;
    private List<Path> mDrawingPathList;
    private float mStartX, mStartY, mEndX, mEndY;
    private String mText;

    // METHODS
    public DrawMove(){}

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

    // SETTERS
    public DrawMove setPaint(Paint paint) {
        this.mPaint = paint;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingMode(DrawingMode drawingMode) {
        this.mDrawingMode = drawingMode;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingTool(DrawingTool drawingTool) {
        this.mDrawingTool = drawingTool;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingPathList(List<Path> drawingPathList) {
        this.mDrawingPathList = drawingPathList;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setStartX(float startX) {
        this.mStartX = startX;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setStartY(float startY) {
        this.mStartY = startY;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setEndX(float endX) {
        this.mEndX = endX;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setEndY(float endY) {
        this.mEndY = endY;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setText(String text) {
        this.mText = text;
        if (mSingleton != null)
            return mSingleton;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }
}
