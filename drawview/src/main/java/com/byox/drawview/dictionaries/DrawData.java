package com.byox.drawview.dictionaries;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.view.MotionEvent;

import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingOrientation;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.utils.SerializablePaint;
import com.byox.drawview.utils.SerializablePath;

import java.io.Serializable;
import java.util.List;

public class DrawData implements Serializable {

    //region CONSTANTS
    public static final String DRAW_DATA = "DRAW_DATA";
    //endregion

    //region VARS
    private DrawMove mDrawMoveForText;
    private List<DrawMove> mDrawMoveHistory;
    private int mDrawMoveHistoryIndex = -1;
    private int mDrawMoveBackgroundIndex = -1;
    private DrawingMode mDrawingMode;
    private DrawingTool mDrawingTool;
    private int mDrawingShapeSides = -1;
    private DrawingOrientation mInitialDrawingOrientation;

    private @ColorInt
    int mDrawColor;
    private int mDrawWidth;
    private int mDrawAlpha;
    private int mBackgroundColor = -1;
    private boolean mAntiAlias;
    private boolean mDither;
    private float mFontSize;
    private SerializablePaint.Style mPaintStyle;
    private SerializablePaint.Cap mLineCap;
    private int mFontFamily;

    private boolean isForCamera = false;
    //endregion

    //region GETTERS & SETTERS


    public DrawMove getDrawMoveForText() {
        return mDrawMoveForText;
    }

    public void setDrawMoveForText(DrawMove drawMoveForText) {
        this.mDrawMoveForText = drawMoveForText;
    }

    public List<DrawMove> getDrawMoveHistory() {
        return mDrawMoveHistory;
    }

    public void setDrawMoveHistory(List<DrawMove> drawMoveHistory) {
        this.mDrawMoveHistory = drawMoveHistory;
    }

    public int getDrawMoveHistoryIndex() {
        return mDrawMoveHistoryIndex;
    }

    public void setDrawMoveHistoryIndex(int drawMoveHistoryIndex) {
        this.mDrawMoveHistoryIndex = drawMoveHistoryIndex;
    }

    public DrawMove getLastMove(){
        if (this.mDrawMoveHistory != null && this.mDrawMoveHistory.size() > 0){
            return this.mDrawMoveHistory.get(this.mDrawMoveHistory.size() - 1);
        }
        return null;
    }

    public int getDrawMoveBackgroundIndex() {
        return mDrawMoveBackgroundIndex;
    }

    public void setDrawMoveBackgroundIndex(int drawMoveBackgroundIndex) {
        this.mDrawMoveBackgroundIndex = drawMoveBackgroundIndex;
    }

    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }

    public void setDrawingMode(DrawingMode drawingMode) {
        this.mDrawingMode = drawingMode;
    }

    public DrawingTool getDrawingTool() {
        return mDrawingTool;
    }

    public void setDrawingTool(DrawingTool drawingTool) {
        this.mDrawingTool = drawingTool;
    }

    public int getDrawingShapeSides() {
        return this.mDrawingShapeSides;
    }

    public void setDrawingShapeSides(int shapeSides) {
        this.mDrawingShapeSides = shapeSides;
    }

    public DrawingOrientation getInitialDrawingOrientation() {
        return mInitialDrawingOrientation;
    }

    public void setInitialDrawingOrientation(DrawingOrientation initialDrawingOrientation) {
        this.mInitialDrawingOrientation = initialDrawingOrientation;
    }

    public int getDrawColor() {
        return mDrawColor;
    }

    public void setDrawColor(int drawColor) {
        this.mDrawColor = drawColor;
    }

    public int getDrawWidth() {
        return mDrawWidth;
    }

    public void setDrawWidth(int drawWidth) {
        this.mDrawWidth = drawWidth;
    }

    public int getDrawAlpha() {
        return mDrawAlpha;
    }

    public void setDrawAlpha(int drawAlpha) {
        this.mDrawAlpha = drawAlpha;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    public boolean isAntiAlias() {
        return mAntiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        this.mAntiAlias = mAntiAlias;
    }

    public boolean isDither() {
        return mDither;
    }

    public void setDither(boolean dither) {
        this.mDither = dither;
    }

    public float getFontSize() {
        return mFontSize;
    }

    public void setFontSize(float fontSize) {
        this.mFontSize = fontSize;
    }

    public SerializablePaint.Style getPaintStyle() {
        return mPaintStyle;
    }

    public void setPaintStyle(SerializablePaint.Style paintStyle) {
        this.mPaintStyle = paintStyle;
    }

    public SerializablePaint.Cap getLineCap() {
        return mLineCap;
    }

    public void setLineCap(SerializablePaint.Cap lineCap) {
        this.mLineCap = lineCap;
    }

    public int getFontFamilyIndex() {
        return this.mFontFamily;
    }

    public Typeface getFontFamily() {
        switch (mFontFamily) {
            case 0:
            default:
                return Typeface.DEFAULT;
            case 1:
                return Typeface.MONOSPACE;
            case 2:
                return Typeface.SANS_SERIF;
            case 3:
                return Typeface.SERIF;
        }
    }

    public void setFontFamilyIndex(int fontFamily) {
        this.mFontFamily = fontFamily;
    }

    public void setFontFamily(Typeface fontFamily) {
        if (fontFamily == Typeface.DEFAULT){
            this.mFontFamily = 0;
        } else if (fontFamily == Typeface.MONOSPACE){
            this.mFontFamily = 1;
        } else if (fontFamily == Typeface.SANS_SERIF){
            this.mFontFamily = 2;
        } else if (fontFamily == Typeface.SERIF){
            this.mFontFamily = 3;
        }
    }

    public boolean isForCamera() {
        return isForCamera;
    }

    public void setForCamera(boolean forCamera) {
        isForCamera = forCamera;
    }
    //endregion

    //region PUBLIC METHODS

    /**
     * Reset draw moves and his history
     *
     * @return If {@link DrawData} is reset successful
     */
    public boolean reset() {
        if (mDrawMoveHistory != null) {
            mDrawMoveHistory.clear();
            setDrawMoveHistoryIndex(-1);
            setDrawMoveBackgroundIndex(-1);
            return true;
        }
        return false;
    }

    /**
     * Undo last {@link DrawMove}
     *
     * @return If {@link DrawMove} is undo from movements
     */
    public boolean undo() {
        if (getDrawMoveHistoryIndex() > -1 &&
                getDrawMoveHistory().size() > 0) {
            setDrawMoveHistoryIndex(getDrawMoveBackgroundIndex() - 1);

            setDrawMoveBackgroundIndex(-1);
            for (int i = 0; i < getDrawMoveHistoryIndex() + 1; i++) {
                if (getDrawMoveHistory().get(i).getBackgroundImage() != null) {
                    setDrawMoveBackgroundIndex(i);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check if {@link DrawData} can undo
     *
     * @return If {@link DrawData} can undo
     */
    public boolean canUndo() {
        return getDrawMoveHistoryIndex() > -1 &&
                getDrawMoveHistory().size() > 0;
    }

    /**
     * Redo last {@link DrawMove}
     *
     * @return If {@link DrawMove} is redo from movements
     */
    public boolean redo() {
        if (getDrawMoveHistoryIndex() <= getDrawMoveHistory().size() - 1) {
            setDrawMoveHistoryIndex(getDrawMoveHistoryIndex() + 1);

            setDrawMoveBackgroundIndex(-1);
            for (int i = 0; i < getDrawMoveHistoryIndex() + 1; i++) {
                if (getDrawMoveHistory().get(i).getBackgroundImage() != null) {
                    setDrawMoveBackgroundIndex(i);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Check if {@link DrawData} can redo
     *
     * @return If {@link DrawData} can redo
     */
    public boolean canRedo() {
        return getDrawMoveHistoryIndex() < getDrawMoveHistory().size() - 1;
    }

    /**
     * Add text to next movement
     *
     * @param text Text to add
     * @return If text added successfully
     */
    public boolean addText(String text) {
        if (getDrawMoveForText() != null) {
            getDrawMoveHistory().add(getDrawMoveForText());
            setDrawMoveHistoryIndex(getDrawMoveHistoryIndex() + 1);
            setDrawMoveForText(null);
        }

        if (getDrawMoveHistory().get(getDrawMoveHistory().size() - 1).getDrawingMode() == DrawingMode.TEXT) {
            getDrawMoveHistory().get(getDrawMoveHistory().size() - 1).setText(text);
            return true;
        }

        return false;
    }

    /**
     * Add new {@link DrawMove} to movements or handle current move
     *
     * @param drawMove       New {@link DrawMove} instance
     * @param motionEvent    {@link MotionEvent} instance of current move
     * @param lastTouchEvent {@link MotionEvent} event of last touch event
     * @param touchX         Current X position
     * @param touchY         Current Y position
     * @param zoom           Current zoom factor
     * @param bounds         Current drawing bounds
     * @return Created move index
     */
    public int addMove(DrawMove drawMove, MotionEvent motionEvent, int lastTouchEvent, float touchX, float touchY,
                       float zoom, Rect bounds) {
        int moveIndex = -1;

        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN && lastTouchEvent == -1) {
            if (getDrawMoveHistoryIndex() >= -1 &&
                    getDrawMoveHistoryIndex() < getDrawMoveHistory().size() - 1)
                setDrawMoveHistory(getDrawMoveHistory().subList(0, getDrawMoveHistoryIndex() + 1));

            getDrawMoveHistory().add(drawMove);
            moveIndex = getDrawMoveHistory().size() - 1;

            setDrawMoveHistoryIndex(getDrawMoveHistoryIndex() + 1);

            if (getDrawingTool() == DrawingTool.PEN || getDrawingMode() == DrawingMode.ERASER) {
                SerializablePath path = new SerializablePath();
                path.moveTo(touchX, touchY);
                path.lineTo(touchX, touchY);

                getDrawMoveHistory().get(moveIndex).setDrawingPathList(path);
            }
        } else if (motionEvent.getActionMasked() == MotionEvent.ACTION_MOVE && lastTouchEvent == -1) {
            moveIndex = getDrawMoveHistory().size() - 1;

            if (getDrawMoveHistory().size() > 0) {
                getDrawMoveHistory().get(moveIndex).setEndX(touchX).setEndY(touchY);

                if (getDrawingTool() == DrawingTool.PEN || getDrawingMode() == DrawingMode.ERASER) {
                    for (int i = 0; i < motionEvent.getHistorySize(); i++) {
                        float historicalX = motionEvent.getHistoricalX(i) / zoom + bounds.left;
                        float historicalY = motionEvent.getHistoricalY(i) / zoom + bounds.top;
                        getDrawMoveHistory().get(moveIndex).getDrawingPath().lineTo(historicalX, historicalY);
                    }
                    getDrawMoveHistory().get(moveIndex).getDrawingPath().lineTo(touchX, touchY);
                }
            }
        } else if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP && lastTouchEvent != -1) {
            moveIndex = getDrawMoveHistory().size() - 1;
            if (lastTouchEvent == MotionEvent.ACTION_DOWN) {
                if (getDrawMoveHistory().size() > 0) {
                    if (getDrawingMode() == DrawingMode.TEXT) {
                        setDrawMoveForText(mDrawMoveHistory.get(moveIndex));
                    }
                    getDrawMoveHistory().remove(moveIndex);
                    setDrawMoveHistoryIndex(getDrawMoveHistoryIndex() - 1);
                    moveIndex--;
                }
            } else if (lastTouchEvent == MotionEvent.ACTION_MOVE) {
                if (getDrawMoveHistory().size() > 0) {
                    getDrawMoveHistory().get(moveIndex).setEndX(touchX).setEndY(touchY);

                    if (getDrawingTool() == DrawingTool.PEN || getDrawingMode() == DrawingMode.ERASER) {
                        for (int i = 0; i < motionEvent.getHistorySize(); i++) {
                            float historicalX = motionEvent.getHistoricalX(i) / zoom + bounds.left;
                            float historicalY = motionEvent.getHistoricalY(i) / zoom + bounds.top;
                            getDrawMoveHistory().get(moveIndex).getDrawingPath().lineTo(historicalX, historicalY);
                        }
                        getDrawMoveHistory().get(moveIndex).getDrawingPath().lineTo(touchX, touchY);
                    }
                }
            }
        }

        return moveIndex;
    }

    /**
     * Create an instance of {@link SerializablePaint} from default configuration or from existing move
     *
     * @return {@link SerializablePaint} instance
     */
    public SerializablePaint toSerializablePaint() {
        SerializablePaint currentPaint;
        if (getDrawMoveHistory().size() > 0 && getDrawMoveHistoryIndex() >= 0
                && getDrawMoveHistoryIndex() != getDrawMoveBackgroundIndex()) {
            currentPaint = new SerializablePaint();
            currentPaint.setColor(
                    getDrawMoveHistory().get(getDrawMoveBackgroundIndex()).getPaint().getColor());
            currentPaint.setStyle(
                    getDrawMoveHistory().get(getDrawMoveBackgroundIndex()).getPaint().getStyle());
            currentPaint.setDither(
                    getDrawMoveHistory().get(getDrawMoveBackgroundIndex()).getPaint().isDither());
            currentPaint.setStrokeWidth(
                    getDrawMoveHistory().get(getDrawMoveBackgroundIndex()).getPaint().getStrokeWidth());
            currentPaint.setAlpha(
                    getDrawMoveHistory().get(getDrawMoveBackgroundIndex()).getPaint().getAlpha());
            currentPaint.setAntiAlias(
                    getDrawMoveHistory().get(getDrawMoveBackgroundIndex()).getPaint().isAntiAlias());
            currentPaint.setStrokeCap(
                    getDrawMoveHistory().get(getDrawMoveBackgroundIndex()).getPaint().getStrokeCap());
            currentPaint.setTypeface(
                    getDrawMoveHistory().get(getDrawMoveBackgroundIndex()).getPaint().getTypeface());
            currentPaint.setTextSize(getFontSize());
        } else {
            currentPaint = newSerializablePaint();
        }

        return currentPaint;
    }

    /**
     * Create a new instance of {@link SerializablePaint} from default configuration
     *
     * @return {@link SerializablePaint} instance
     */
    public SerializablePaint newSerializablePaint() {
        SerializablePaint serializablePaint = new SerializablePaint();
        serializablePaint.setColor(getDrawColor());
        serializablePaint.setStyle(getPaintStyle());
        serializablePaint.setDither(isDither());
        serializablePaint.setStrokeWidth(getDrawWidth());
        serializablePaint.setAlpha(getDrawAlpha());
        serializablePaint.setAntiAlias(isAntiAlias());
        serializablePaint.setStrokeCap(getLineCap());
        serializablePaint.setTypeface(getFontFamily());
        serializablePaint.setTextSize(getFontSize());

        return serializablePaint;
    }

    /**
     * Initialize class from {@link SerializablePaint} instance
     *
     * @param serializablePaint {@link SerializablePaint} instance
     */
    public void fromSerializablePaint(SerializablePaint serializablePaint) {
        setDrawColor(serializablePaint.getColor());
        setPaintStyle(serializablePaint.getStyle());
        setDither(serializablePaint.isDither());
        setDrawWidth((int) serializablePaint.getStrokeWidth());
        setDrawAlpha(serializablePaint.getAlpha());
        setAntiAlias(serializablePaint.isAntiAlias());
        setLineCap(serializablePaint.getStrokeCap());
        setFontFamily(serializablePaint.getTypeface());
        setFontSize(mFontSize = serializablePaint.getTextSize());
    }

    /**
     * @return If {@link DrawMove} has movements
     */
    public boolean hasMovements() {
        return getDrawMoveHistory() == null || getDrawMoveHistory().size() == 0;
    }
    //endregion
}
