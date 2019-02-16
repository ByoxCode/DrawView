package com.byox.drawviewproject;

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.byox.drawview.abstracts.DrawCameraViewListener;
import com.byox.drawview.abstracts.DrawViewListener;
import com.byox.drawview.dictionaries.DrawCapture;
import com.byox.drawview.enums.BackgroundScale;
import com.byox.drawview.enums.BackgroundType;
import com.byox.drawview.enums.DrawingCapture;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.views.DrawCameraView;
import com.byox.drawviewproject.dialogs.DrawAttribsDialog;
import com.byox.drawviewproject.dialogs.RequestTextDialog;
import com.byox.drawviewproject.dialogs.SaveBitmapDialog;
import com.byox.drawviewproject.dialogs.SelectImageDialog;
import com.byox.drawviewproject.utils.AnimateUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;

public class CameraActivity extends AppCompatActivity {

    //region CONSTANTS
    private final int STORAGE_PERMISSIONS = 1000;
    private final int CAMERA_PERMISSIONS = 3000;
    //endregion

    //region VIEWS
    private Toolbar mToolbar;
    private DrawCameraView mDrawCameraView;

    private FloatingActionButton mFabClearDraw;
    private CardView mCardViewLoadingBackground;

    private MenuItem mMenuItemRedo;
    private MenuItem mMenuItemUndo;
    //endregion

    //region ADS
    private NativeExpressAdView mAdView;
    //endregion

    //region EVENTS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mDrawCameraView = findViewById(R.id.draw_camera_view);
        mFabClearDraw = findViewById(R.id.fab_clear);
        mCardViewLoadingBackground = findViewById(R.id.cv_loading_card);

        setupToolbar();
        setListeners();
        setupADS();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawCameraView.onStart();
    }

    @Override
    protected void onPause() {
        mDrawCameraView.onStop();
        super.onPause();
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
                if (mDrawCameraView.getDrawView().canUndo()) {
                    mDrawCameraView.getDrawView().undo();
                    canUndoRedo();
                }
                break;
            case R.id.action_redo:
                if (mDrawCameraView.getDrawView().canRedo()) {
                    mDrawCameraView.getDrawView().redo();
                    canUndoRedo();
                }
                break;
            case R.id.action_draw_attrs:
                changeDrawAttributes();
                break;
            case R.id.action_draw_tool:
                changeDrawTool();
                break;
            case R.id.action_draw_mode:
                changeDrawMode();
                break;
            case R.id.action_draw_save:
                requestPermissions();
                break;
            case R.id.action_drawview_option:
                Intent i = new Intent(CameraActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
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
                            mDrawCameraView.createCapture(Bitmap.CompressFormat.PNG);
                        }
                    }, 300);
                }
                break;
        }
    }
    //endregion

    //region PRIVATE METHODS
    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void setListeners() {
        mDrawCameraView.getDrawView().addDrawViewListener(new DrawViewListener() {
            @Override
            public void onStartDrawing() {
                super.onStartDrawing();
            }

            @Override
            public void onEndDrawing() {
                super.onEndDrawing();

                canUndoRedo();

                if (mFabClearDraw.getVisibility() == View.INVISIBLE)
                    AnimateUtils.ScaleInAnimation(mFabClearDraw, 50, 300, new OvershootInterpolator(), true);
            }

            @Override
            public void onClearDrawing() {
                super.onClearDrawing();

                canUndoRedo();

                if (mFabClearDraw.getVisibility() == View.VISIBLE)
                    AnimateUtils.ScaleOutAnimation(mFabClearDraw, 50, 300, new OvershootInterpolator(), true);
            }

            @Override
            public void onRequestText() {
                super.onRequestText();
                requestText();
            }

            @Override
            public void onAllMovesPainted() {
                super.onAllMovesPainted();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canUndoRedo();
                        if (!mDrawCameraView.getDrawView().isDrawViewEmpty())
                            mFabClearDraw.setVisibility(View.VISIBLE);
                    }
                }, 200);
            }

            @Override
            public void onDrawingError(Exception e) {
                super.onDrawingError(e);

                e.printStackTrace();
            }
        });
        mDrawCameraView.addDrawCameraViewListener(new DrawCameraViewListener() {
            @Override
            public void onDrawCameraViewCaptureStart() {
                super.onDrawCameraViewCaptureStart();

                if (mCardViewLoadingBackground.getVisibility() == View.INVISIBLE)
                    AnimateUtils.ScaleInAnimation(mCardViewLoadingBackground, 50, 300, new OvershootInterpolator(), true);
            }

            @Override
            public void onDrawCameraViewCaptureEnd(DrawCapture capture) {
                super.onDrawCameraViewCaptureEnd(capture);

                mDrawCameraView.onStop();
                mDrawCameraView.onStart();

                if (mCardViewLoadingBackground.getVisibility() == View.VISIBLE)
                    AnimateUtils.ScaleOutAnimation(mCardViewLoadingBackground, 50, 300, new OvershootInterpolator(), true);

                saveDraw(capture);
            }

            @Override
            public void onDrawCameraViewError(Exception e) {
                super.onDrawCameraViewError(e);

                mDrawCameraView.onStop();
                mDrawCameraView.onStart();

                if (mCardViewLoadingBackground.getVisibility() == View.VISIBLE)
                    AnimateUtils.ScaleOutAnimation(mCardViewLoadingBackground, 50, 300, new OvershootInterpolator(), true);

                e.printStackTrace();
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
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FCFD13908AA93E51A1BA390FA8010631")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void changeDrawTool() {
        int currentTool = 0;
        List<String> tools = new ArrayList<>();

        for (int i = 0; i < DrawingTool.values().length; i++){
            tools.add(DrawingTool.values()[i].toString());
            if (DrawingTool.values()[i] == mDrawCameraView.getDrawView().getDrawingTool()) currentTool = i;
        }

        new MaterialDialog.Builder(this)
                .title(R.string.choose_draw_tool_tile)
                .items(tools)
                .itemsCallbackSingleChoice(currentTool, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mDrawCameraView.getDrawView().tool(DrawingTool.values()[which]);
                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .show();
    }

    private void changeDrawMode() {
        int currentMode = 0;
        List<String> modes = new ArrayList<>();

        for (int i = 0; i < DrawingMode.values().length; i++){
            modes.add(DrawingMode.values()[i].toString());
            if (DrawingMode.values()[i] == mDrawCameraView.getDrawView().getDrawingMode()) currentMode = i;
        }

        new MaterialDialog.Builder(this)
                .title(R.string.choose_draw_mode_tile)
                .items(modes)
                .itemsCallbackSingleChoice(currentMode, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mDrawCameraView.getDrawView().mode(DrawingMode.values()[which]);
                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .show();
    }

    private void changeDrawAttributes() {
        DrawAttribsDialog drawAttribsDialog = DrawAttribsDialog.newInstance();
        drawAttribsDialog.setPaint(mDrawCameraView.getDrawView().getCurrentPaintParams());
        drawAttribsDialog.setOnCustomViewDialogListener(new DrawAttribsDialog.OnCustomViewDialogListener() {
            @Override
            public void onRefreshPaint(Paint newPaint) {
                mDrawCameraView.getDrawView().color(newPaint.getColor())
                        .paintStyle(newPaint.getStyle())
                        .dither(newPaint.isDither())
                        .width((int) newPaint.getStrokeWidth())
                        .alpha(newPaint.getAlpha())
                        .antiAlias(newPaint.isAntiAlias())
                        .lineCap(newPaint.getStrokeCap())
                        .fontFamily(newPaint.getTypeface())
                        .fontSize(newPaint.getTextSize());
//                If you prefer, you can easily refresh new attributes using this method
//                mDrawView.refreshAttributes(newPaint);
            }
        });
        drawAttribsDialog.show(getSupportFragmentManager(), "drawAttribs");
    }

    private void saveDraw(DrawCapture drawCapture) {
        SaveBitmapDialog saveBitmapDialog = SaveBitmapDialog.newInstance(drawCapture);
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

    private void requestText(){
        new MaterialDialog.Builder(this)
                .title(R.string.request_text_title)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.request_text), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                mDrawCameraView.getDrawView().drawText(input.toString());
                            }
                        }).show();
    }

    private void clearDraw() {
        mDrawCameraView.getDrawView().restartDrawing();
    }

    private void canUndoRedo() {
        if (!mDrawCameraView.getDrawView().canUndo()) {
            mMenuItemUndo.setEnabled(false);
            mMenuItemUndo.setIcon(R.drawable.ic_action_content_undo_disabled);
        } else {
            mMenuItemUndo.setEnabled(true);
            mMenuItemUndo.setIcon(R.drawable.ic_action_content_undo);
        }
        if (!mDrawCameraView.getDrawView().canRedo()) {
            mMenuItemRedo.setEnabled(false);
            mMenuItemRedo.setIcon(R.drawable.ic_action_content_redo_disabled);
        } else {
            mMenuItemRedo.setEnabled(true);
            mMenuItemRedo.setIcon(R.drawable.ic_action_content_redo);
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                mDrawCameraView.createCapture(Bitmap.CompressFormat.PNG);
            }
        } else {
            mDrawCameraView.createCapture(Bitmap.CompressFormat.PNG);
        }
    }
    //endregion
/*


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mDrawCameraView = findViewById(R.id.draw_cam_view);
        mDrawView = findViewById(R.id.draw_view);
        mFabClearDraw = findViewById(R.id.fab_clear);

        requestPermissions(1);
        setupToolbar();
        setupADS();
    }





    // METHODS



    */

}
