package com.byox.drawviewproject.behaviors;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Ing. Oscar G. Medina Cruz on 22/02/2017.
 */

public class CustomBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {
    private boolean mLocked = false;

    public CustomBottomSheetBehavior() {}

    public CustomBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLocked(boolean locked) {
        mLocked = locked;
    }

    public boolean isLocked(){
        return mLocked;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onInterceptTouchEvent(parent, child, event);
        }

        return handled;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onTouchEvent(parent, child, event);
        }

        return handled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        }

        return handled;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {
        if (!mLocked) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        if (!mLocked) {
            super.onStopNestedScroll(coordinatorLayout, child, target);
        }
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
        }

        return handled;

    }
//    private boolean mAllowUserDragging = true;
//    /**
//     * Default constructor for instantiating BottomSheetBehaviors.
//     */
//    public CustomBottomSheetBehavior() {
//        super();
//    }
//
//    /**
//     * Default constructor for inflating BottomSheetBehaviors from layout.
//     *
//     * @param context The {@link Context}.
//     * @param attrs   The {@link AttributeSet}.
//     */
//    public CustomBottomSheetBehavior(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public void setAllowUserDragging(boolean allowUserDragging) {
//        mAllowUserDragging = allowUserDragging;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
//        if (!mAllowUserDragging) {
//            return false;
//        }
//        return super.onInterceptTouchEvent(parent, child, event);
//    }
}
