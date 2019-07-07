package com.byox.drawviewproject;

import android.animation.Animator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    // VIEWS
    private ImageView mImageViewLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        /*mImageViewLogo = (ImageView) findViewById(R.id.iv_logo);
        mImageViewLogo.setScaleX(0);
        mImageViewLogo.setScaleY(0);

        initSplashScreen();*/

        // ONLY FOR TESTING
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }

    // METHODS
    private void initSplashScreen() {
        mImageViewLogo.animate().scaleX(1).scaleY(1)
                .setDuration(500).setInterpolator(new OvershootInterpolator())
                .setStartDelay(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                };

                new Timer().schedule(timerTask, 1000);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        }).start();
    }
}
