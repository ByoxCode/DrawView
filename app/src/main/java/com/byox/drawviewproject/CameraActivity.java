package com.byox.drawviewproject;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.byox.drawview.enums.BackgroundScale;
import com.byox.drawview.enums.BackgroundType;
import com.byox.drawview.enums.DrawingCapture;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.utils.BitmapUtils;
import com.byox.drawview.views.DrawCameraView;
import com.byox.drawview.views.DrawView;
import com.byox.drawviewproject.dialogs.DrawAttribsDialog;
import com.byox.drawviewproject.dialogs.RequestTextDialog;
import com.byox.drawviewproject.dialogs.SaveBitmapDialog;
import com.byox.drawviewproject.dialogs.SelectChoiceDialog;
import com.byox.drawviewproject.dialogs.SelectImageDialog;
import com.byox.drawviewproject.utils.AnimateUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.File;

public class CameraActivity extends AppCompatActivity {

    // CONSTANTS
    private final int STORAGE_PERMISSIONS = 1000;
    private final int CAMERA_PERMISSIONS = 3000;

    // VIEWS
    private Toolbar mToolbar;
    private DrawCameraView mDrawCameraView;
    private DrawView mDrawView;

    private FloatingActionButton mFabClearDraw;

    private MenuItem mMenuItemRedo;
    private MenuItem mMenuItemUndo;

    // ADS
    private NativeExpressAdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mDrawCameraView = (DrawCameraView) findViewById(R.id.draw_cam_view);
        mDrawView = (DrawView) findViewById(R.id.draw_view); 
        mFabClearDraw = (FloatingActionButton) findViewById(R.id.fab_clear);

        requestPermissions(1);
        setupToolbar();
        setupADS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        mMenuItemUndo = menu.getItem(0);
        mMenuItemRedo = menu.getItem(1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_undo:
                if (mDrawView.canUndo()) {
                    mDrawView.undo();
                    canUndoRedo();
                }
                break;
            case R.id.action_redo:
                if (mDrawView.canRedo()) {
                    mDrawView.redo();
                    canUndoRedo();
                }
                break;
            case R.id.action_draw_attrs:
                changeDrawAttribs();
                break;
            case R.id.action_draw_tool:
                changeDrawTool();
                break;
            case R.id.action_draw_mode:
                changeDrawMode();
                break;
            case R.id.action_draw_save:
                requestPermissions(0);
                break;
            case R.id.action_drawview_option:
                Intent i = new Intent(CameraActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveDraw();
                        }
                    }, 600);
                } else {
                    finish();
                }
                break;
            case CAMERA_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDrawCameraView.attachCameraView();
                            setListeners();
                        }
                    }, 600);
                } else {
                    finish();
                }
                break;
        }
    }

    // METHODS
    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void setListeners() {
        mDrawView.setOnDrawViewListener(new DrawView.OnDrawViewListener() {
            @Override
            public void onStartDrawing() {
                canUndoRedo();
            }

            @Override
            public void onEndDrawing() {
                canUndoRedo();

                if (mFabClearDraw.getVisibility() == View.INVISIBLE)
                    AnimateUtils.ScaleInAnimation(mFabClearDraw, 50, 300, new OvershootInterpolator(), true);
            }

            @Override
            public void onClearDrawing() {
                canUndoRedo();

                if (mFabClearDraw.getVisibility() == View.VISIBLE)
                    AnimateUtils.ScaleOutAnimation(mFabClearDraw, 50, 300, new OvershootInterpolator(), true);
            }

            @Override
            public void onRequestText() {
                RequestTextDialog requestTextDialog =
                        RequestTextDialog.newInstance("");
                requestTextDialog.setOnRequestTextListener(new RequestTextDialog.OnRequestTextListener() {
                    @Override
                    public void onRequestTextConfirmed(String requestedText) {
                        mDrawView.refreshLastText(requestedText);
                    }

                    @Override
                    public void onRequestTextCancelled() {
                        mDrawView.cancelTextRequest();
                    }
                });
                requestTextDialog.show(getSupportFragmentManager(), "requestText");
            }

            @Override
            public void onAllMovesPainted() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canUndoRedo();
                        if (!mDrawView.isDrawViewEmpty())
                            mFabClearDraw.setVisibility(View.VISIBLE);
                    }
                }, 300);
            }
        });

        mFabClearDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDraw();
            }
        });
    }

    private void setupADS() {
        mAdView = (NativeExpressAdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FCFD13908AA93E51A1BA390FA8010631")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void changeDrawTool() {
        SelectChoiceDialog selectChoiceDialog =
                SelectChoiceDialog.newInstance("Select a draw tool",
                        "PEN", "LINE", "ARROW", "RECTANGLE", "CIRCLE", "ELLIPSE");
        selectChoiceDialog.setOnChoiceDialogListener(new SelectChoiceDialog.OnChoiceDialogListener() {
            @Override
            public void onChoiceSelected(int position) {
                mDrawView.setDrawingTool(DrawingTool.values()[position]);
            }
        });
        selectChoiceDialog.show(getSupportFragmentManager(), "choiceDialog");
    }

    private void changeDrawMode() {
        SelectChoiceDialog selectChoiceDialog =
                SelectChoiceDialog.newInstance("Select a draw mode",
                        "DRAW", "TEXT", "ERASER");
        selectChoiceDialog.setOnChoiceDialogListener(new SelectChoiceDialog.OnChoiceDialogListener() {
            @Override
            public void onChoiceSelected(int position) {
                mDrawView.setDrawingMode(DrawingMode.values()[position]);
            }
        });
        selectChoiceDialog.show(getSupportFragmentManager(), "choiceDialog");
    }

    private void changeDrawAttribs() {
        DrawAttribsDialog drawAttribsDialog = DrawAttribsDialog.newInstance();
        drawAttribsDialog.setPaint(mDrawView.getCurrentPaintParams());
        drawAttribsDialog.setOnCustomViewDialogListener(new DrawAttribsDialog.OnCustomViewDialogListener() {
            @Override
            public void onRefreshPaint(Paint newPaint) {
                mDrawView.setDrawColor(newPaint.getColor())
                        .setPaintStyle(newPaint.getStyle())
                        .setDither(newPaint.isDither())
                        .setDrawWidth((int) newPaint.getStrokeWidth())
                        .setDrawAlpha(newPaint.getAlpha())
                        .setAntiAlias(newPaint.isAntiAlias())
                        .setLineCap(newPaint.getStrokeCap())
                        .setFontFamily(newPaint.getTypeface())
                        .setFontSize(newPaint.getTextSize());
//                If you prefer, you can easily refresh new attributes using this method
//                mDrawView.refreshAttributes(newPaint);
            }
        });
        drawAttribsDialog.show(getSupportFragmentManager(), "drawAttribs");
    }

    private void saveDraw() {
        SaveBitmapDialog saveBitmapDialog = SaveBitmapDialog.newInstance();
        Object[] createCaptureResponse = mDrawView.createCapture(DrawingCapture.BITMAP, mDrawCameraView.getCameraView());
        saveBitmapDialog.setPreviewBitmap((Bitmap) createCaptureResponse[0]);
        saveBitmapDialog.setPreviewFormat(String.valueOf(createCaptureResponse[1]));
        saveBitmapDialog.setOnSaveBitmapListener(new SaveBitmapDialog.OnSaveBitmapListener() {
            @Override
            public void onSaveBitmapCompleted() {
                Snackbar.make(mFabClearDraw, "Capture saved succesfully!", 2000).show();
            }

            @Override
            public void onSaveBitmapCanceled() {
                Snackbar.make(mFabClearDraw, "Capture saved canceled.", 2000).show();
            }
        });
        saveBitmapDialog.show(getSupportFragmentManager(), "saveBitmap");
    }

    private void clearDraw() {
        mDrawView.restartDrawing();
    }

    private void canUndoRedo() {
        if (!mDrawView.canUndo()) {
            mMenuItemUndo.setEnabled(false);
            mMenuItemUndo.setIcon(R.drawable.ic_action_content_undo_disabled);
        } else {
            mMenuItemUndo.setEnabled(true);
            mMenuItemUndo.setIcon(R.drawable.ic_action_content_undo);
        }
        if (!mDrawView.canRedo()) {
            mMenuItemRedo.setEnabled(false);
            mMenuItemRedo.setIcon(R.drawable.ic_action_content_redo_disabled);
        } else {
            mMenuItemRedo.setEnabled(true);
            mMenuItemRedo.setIcon(R.drawable.ic_action_content_redo);
        }
    }

    private void requestPermissions(int option) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (option == 0) {
                if (ContextCompat.checkSelfPermission(CameraActivity.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(CameraActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(CameraActivity.this,
                            new String[]{
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSIONS);
                } else {
                    saveDraw();
                }
            } else  if (option == 1){
                if (ContextCompat.checkSelfPermission(CameraActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(CameraActivity.this,
                            new String[]{
                                    Manifest.permission.CAMERA},
                            CAMERA_PERMISSIONS);
                } else {
                    mDrawCameraView.attachCameraView();
                    setListeners();
                }
            }
        } else {
            if (option == 0)
                saveDraw();
            if (option == 1) {
                mDrawCameraView.attachCameraView();
                setListeners();
            }
        }
    }

}
