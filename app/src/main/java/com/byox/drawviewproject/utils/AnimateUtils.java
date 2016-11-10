package com.byox.drawviewproject.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by Ing. Oscar G. Medina Cruz on 18/06/2016.
 */
public class AnimateUtils {

    public static void ScaleInAnimation(final View view, int startOffset, int duration,
                                        Interpolator interpolator, final boolean isInvisible) {
        ScaleAnimation scaleInAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleInAnimation.setInterpolator(interpolator);
        scaleInAnimation.setDuration(duration);
        scaleInAnimation.setStartOffset(startOffset);
        scaleInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (isInvisible) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(scaleInAnimation);
    }

    public static void ScaleInAnimation(final View view, int startOffset, int duration,
                                        Interpolator interpolator, Animation.AnimationListener animationListener) {
        ScaleAnimation scaleInAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleInAnimation.setInterpolator(interpolator);
        scaleInAnimation.setDuration(duration);
        scaleInAnimation.setStartOffset(startOffset);
        if (animationListener != null)
            scaleInAnimation.setAnimationListener(animationListener);
        view.startAnimation(scaleInAnimation);
    }

    public static void ScaleOutAnimation(final View view, int startOffset, int duration,
                                         Interpolator interpolator, final boolean invisibleAtEnd) {
        ScaleAnimation scaleInAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleInAnimation.setInterpolator(interpolator);
        scaleInAnimation.setDuration(duration);
        scaleInAnimation.setStartOffset(startOffset);
        scaleInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (invisibleAtEnd) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(scaleInAnimation);
    }

    public static void ScaleOutAnimation(final View view, int startOffset, int duration,
                                         Interpolator interpolator, Animation.AnimationListener animationListener) {
        ScaleAnimation scaleInAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleInAnimation.setInterpolator(interpolator);
        scaleInAnimation.setDuration(duration);
        scaleInAnimation.setStartOffset(startOffset);
        if (animationListener != null)
            scaleInAnimation.setAnimationListener(animationListener);
        view.startAnimation(scaleInAnimation);
    }

    public static void FlipOutHorizontalAnimation(final View view, int startOffset, int duration,
                                                  Interpolator interpolator, final boolean invisibleAtEnd) {
        ScaleAnimation flipOutAnimation = new ScaleAnimation(1f, 0f, 1f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipOutAnimation.setInterpolator(interpolator);
        flipOutAnimation.setDuration(duration);
        flipOutAnimation.setStartOffset(startOffset);
        flipOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (invisibleAtEnd) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(flipOutAnimation);
    }

    public static void FlipOutHorizontalAnimation(final View view, int startOffset, int duration,
                                                  Interpolator interpolator, Animation.AnimationListener animationListener){
        ScaleAnimation flipOutAnimation = new ScaleAnimation(1f, 0f, 1f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipOutAnimation.setInterpolator(interpolator);
        flipOutAnimation.setDuration(duration);
        flipOutAnimation.setStartOffset(startOffset);
        if (animationListener != null)
            flipOutAnimation.setAnimationListener(animationListener);
        view.startAnimation(flipOutAnimation);
    }

    public static void FlipInHorizontalAnimation(final View view, int startOffset, int duration,
                                                 Interpolator interpolator, final boolean visibleAtEnd) {
        ScaleAnimation flipInAnimation = new ScaleAnimation(0f, 1f, 1f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipInAnimation.setInterpolator(interpolator);
        flipInAnimation.setDuration(duration);
        flipInAnimation.setStartOffset(startOffset);
        flipInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (visibleAtEnd) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(flipInAnimation);
    }

    public static void FlipInHorizontalAnimation(final View view, int startOffset, int duration,
                                                 Interpolator interpolator, Animation.AnimationListener animationListener){
        ScaleAnimation flipInAnimation = new ScaleAnimation(0f, 1f, 1f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipInAnimation.setInterpolator(interpolator);
        flipInAnimation.setDuration(duration);
        flipInAnimation.setStartOffset(startOffset);
        if (animationListener != null)
            flipInAnimation.setAnimationListener(animationListener);
        view.startAnimation(flipInAnimation);
    }

    public static void FlipOutVerticalAnimation(final View view, int startOffset, int duration,
                                                  Interpolator interpolator, final boolean invisibleAtEnd) {
        ScaleAnimation flipOutAnimation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipOutAnimation.setInterpolator(interpolator);
        flipOutAnimation.setDuration(duration);
        flipOutAnimation.setStartOffset(startOffset);
        flipOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (invisibleAtEnd) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(flipOutAnimation);
    }

    public static void FlipOutVerticalAnimation(final View view, int startOffset, int duration,
                                                  Interpolator interpolator, Animation.AnimationListener animationListener){
        ScaleAnimation flipOutAnimation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipOutAnimation.setInterpolator(interpolator);
        flipOutAnimation.setDuration(duration);
        flipOutAnimation.setStartOffset(startOffset);
        if (animationListener != null)
            flipOutAnimation.setAnimationListener(animationListener);
        view.startAnimation(flipOutAnimation);
    }

    public static void FlipInVerticalAnimation(final View view, int startOffset, int duration,
                                                Interpolator interpolator, final boolean visibleAtEnd) {
        ScaleAnimation flipOutAnimation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipOutAnimation.setInterpolator(interpolator);
        flipOutAnimation.setDuration(duration);
        flipOutAnimation.setStartOffset(startOffset);
        flipOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (visibleAtEnd) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(flipOutAnimation);
    }

    public static void FlipInVerticalAnimation(final View view, int startOffset, int duration,
                                                Interpolator interpolator, Animation.AnimationListener animationListener){
        ScaleAnimation flipOutAnimation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipOutAnimation.setInterpolator(interpolator);
        flipOutAnimation.setDuration(duration);
        flipOutAnimation.setStartOffset(startOffset);
        if (animationListener != null)
            flipOutAnimation.setAnimationListener(animationListener);
        view.startAnimation(flipOutAnimation);
    }

    public static void FadeInAnimation(final View view, int startOffset, int duration,
                                               Interpolator interpolator, final boolean visibleAtEnd) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setInterpolator(interpolator);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(startOffset);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (visibleAtEnd) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(alphaAnimation);
    }

    public static void FadeInAnimation(final View view, int startOffset, int duration,
                                               Interpolator interpolator, Animation.AnimationListener animationListener){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setInterpolator(interpolator);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(startOffset);
        if (animationListener != null)
            alphaAnimation.setAnimationListener(animationListener);
        view.startAnimation(alphaAnimation);
    }

    public static void FadeOutAnimation(final View view, int startOffset, int duration,
                                       Interpolator interpolator, final boolean invisibleAtEnd) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setInterpolator(interpolator);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(startOffset);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (invisibleAtEnd) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(alphaAnimation);
    }

    public static void FadeOutAnimation(final View view, int startOffset, int duration,
                                       Interpolator interpolator, Animation.AnimationListener animationListener){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setInterpolator(interpolator);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(startOffset);
        if (animationListener != null)
            alphaAnimation.setAnimationListener(animationListener);
        view.startAnimation(alphaAnimation);
    }
}
