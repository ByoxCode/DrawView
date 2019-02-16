package com.byox.drawview.utils;

import android.graphics.PointF;

public class MathUtils {


    /**
     * Calculate distance between two points
     *
     * @param point1 {@link PointF} first point
     * @param point2 {@link PointF} second point
     * @return Distance between two points
     */
    public static float DistanceBetweenPoints(PointF point1, PointF point2) {
        return DistanceBetweenPoints(point1.x, point1.y, point2.x, point2.y);
    }

    /**
     * Calculate distance between two points
     *
     * @param x1 X coordinate of first point
     * @param y1 Y coordinate of first point
     * @param x2 X coordinate of second point
     * @param y2 Y coordinate of second point
     * @return Distance between two points
     */
    public static float DistanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Calculate middle point between two points
     *
     * @param point1 {@link PointF} first point
     * @param point2 {@link PointF} second point
     * @return Middle point between two points
     */
    public static PointF MiddlePointBetweenPoints(PointF point1, PointF point2) {
        return MiddlePointBetweenPoints(point1.x, point1.y, point2.x, point2.y);
    }

    /**
     * Calculate middle point between two points
     *
     * @param x1 X coordinate of first point
     * @param y1 Y coordinate of first point
     * @param x2 X coordinate of second point
     * @param y2 Y coordinate of second point
     * @return Middle point between two points
     */
    public static PointF MiddlePointBetweenPoints(float x1, float y1, float x2, float y2) {
        return new PointF((x1 + x2) / 2, (y1 + y2) / 2);
    }

    /**
     * Calculate shape side length
     *
     * @param shapeSides Number of sides of shape
     * @param radius     Radius of shape
     * @return Shape side length
     */
    public static float ShapeSideLength(int shapeSides, float radius) {
        float delta = (float) (Math.PI / shapeSides);
        return (float) ((2 * radius) * Math.sin(delta));
    }

    /**
     * Caluclate hypotenuse length from angle and opposite side lenght
     * @param angle Angle to calculate hypotenuse
     * @param oppositeSide Opposite side length
     * @return Hypotenuse length
     */
    public static float Hypotenuse(float angle, float oppositeSide){
        return (float) Math.abs(oppositeSide / Math.sin(angle));
    }

    public static float CalculateInnerStarInnerRadius(float radius, int shapeSides){
        float a = ShapeSideLength(shapeSides, radius);
        float theta = (float) (180 / shapeSides);
        float s = (float) (MathUtils.Cotangent(theta) - (Math.tan(shapeSides - 1) * theta));
        s = (a / 2) * s;
        return s;
    }

    public static float Cotangent(float x){
        return (float) (1 / Math.tan(x));
    }

    public static float CalculateStarSideLength(int starSpikes, float radius){
        float theta = 180 / starSpikes;
        float b = (float) (ShapeSideLength(starSpikes, radius) / (2 * Math.cos((starSpikes - 1) * theta)));
        return Math.abs(b);
    }
}
