package com.byox.drawview.dictionaries;

import android.graphics.Paint;
import android.graphics.Path;

import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;

import java.util.List;

/**
 * Created by Ing. Oscar G. Medina Cruz on 07/11/2016.
 *
 * Dictionary class that save move for draw in the view, this allow the user to make a history
 * of the user movements in the view and make a redo/undo.
 *
 * @author Ing. Oscar G. Medina Cruz
 */

public class DrawMove {

    private Paint mPaint;
    private DrawingMode mDrawingMode;
    private DrawingTool mDrawingTool;
    private List<Path> mDrawingPathList;
    private float mStartX, mStartY, mEndX, mEndY;

    // GETTERS

    public Paint getPaint() {
        return mPaint;
    }
    public DrawingMode getDrawingMode(){
        return mDrawingMode;
    }
    public DrawingTool getDrawingTool(){
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

    // SETTERS
    public DrawMove setPaint(Paint paint) {
        this.mPaint = paint;
        return this;
    }

    public DrawMove setDrawingMode(DrawingMode drawingMode){
        this.mDrawingMode = drawingMode;
        return this;
    }

    public DrawMove setDrawingTool(DrawingTool drawingTool){
        this.mDrawingTool = drawingTool;
        return this;
    }

    public DrawMove setDrawingPathList(List<Path> drawingPathList) {
        this.mDrawingPathList = drawingPathList;
        return this;
    }

    public DrawMove setStartX(float startX) {
        this.mStartX = startX;
        return this;
    }

    public DrawMove setStartY(float startY) {
        this.mStartY = startY;
        return this;
    }

    public DrawMove setEndX(float endX) {
        this.mEndX = endX;
        return this;
    }

    public DrawMove setEndY(float endY) {
        this.mEndY = endY;
        return this;
    }
}
