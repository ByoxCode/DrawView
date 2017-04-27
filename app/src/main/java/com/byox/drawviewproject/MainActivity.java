package com.byox.drawviewproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.byox.drawview.enums.DrawingCapture;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
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

public class MainActivity extends AppCompatActivity {

    // CONSTANTS
    private final int STORAGE_PERMISSIONS = 1000;
    private final int STORAGE_PERMISSIONS2 = 2000;

    // VIEWS
    private Toolbar mToolbar;
    private DrawView mDrawView;

    private View mFadeView;
    private FloatingActionButton mFabActions;
    private FloatingActionButton mFabDrawAttrs;
    private CardView mCardViewDrawAttrs;
    private FloatingActionButton mFabBackgroundImage;
    private CardView mCardViewBackgroundImage;
    private FloatingActionButton mFabDrawTool;
    private CardView mCardViewDrawTool;
    private FloatingActionButton mFabDrawMode;
    private CardView mCardViewDrawMode;
    private FloatingActionButton mFabSaveDraw;
    private CardView mCardViewSaveDraw;
    private FloatingActionButton mFabClearDraw;
    private CardView mCardViewClearDraw;

    private MenuItem mMenuItemRedo;
    private MenuItem mMenuItemUndo;

    // ADS
    private NativeExpressAdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFadeView = findViewById(R.id.fade_view);
        mFabActions = (FloatingActionButton) findViewById(R.id.fab_actions);
        mFabDrawAttrs = (FloatingActionButton) findViewById(R.id.fab_draw_attrs);
        mFabBackgroundImage = (FloatingActionButton) findViewById(R.id.fab_draw_background);
        mFabDrawTool = (FloatingActionButton) findViewById(R.id.fab_draw_tool);
        mFabDrawMode = (FloatingActionButton) findViewById(R.id.fab_draw_mode);
        mFabSaveDraw = (FloatingActionButton) findViewById(R.id.fab_draw_save);
        mFabClearDraw = (FloatingActionButton) findViewById(R.id.fab_draw_clear);
        mCardViewDrawAttrs = (CardView) findViewById(R.id.cv_draw_attrs);
        mCardViewBackgroundImage = (CardView) findViewById(R.id.cv_draw_background);
        mCardViewDrawTool = (CardView) findViewById(R.id.cv_draw_tool);
        mCardViewDrawMode = (CardView) findViewById(R.id.cv_draw_mode);
        mCardViewSaveDraw = (CardView) findViewById(R.id.cv_draw_save);
        mCardViewClearDraw = (CardView) findViewById(R.id.cv_draw_clear);

        setupToolbar();
        setupDrawView();
        setListeners();
        setupADS();
    }

    @Override
    public void onBackPressed() {
        if (Integer.parseInt(mFabActions.getTag().toString()) == 1)
            mFabActions.performClick();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenuItemUndo = menu.getItem(0);
        mMenuItemRedo = menu.getItem(1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                }
                break;
            case STORAGE_PERMISSIONS2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chooseBackgroundImage();
                        }
                    }, 600);
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

    private void setupDrawView() {
        mDrawView = (DrawView) findViewById(R.id.draw_view);
    }

    private void setListeners() {
        mDrawView.setOnDrawViewListener(new DrawView.OnDrawViewListener() {
            @Override public void onStartDrawing() { canUndoRedo(); }
            @Override public void onEndDrawing() { canUndoRedo(); }
            @Override public void onClearDrawing() { canUndoRedo(); }

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
        });

        mFadeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE)
                    mFabActions.performClick();
            }
        });

        mFabActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = Integer.parseInt(view.getTag().toString());
                if (status == 0) {
                    status = 1;
                    mFabActions.animate().rotation(45).setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(100).start();
                    AnimateUtils.FadeInAnimation(mFadeView, 0, 100, new AccelerateDecelerateInterpolator(), true);

                    AnimateUtils.ScaleInAnimation(mFabDrawAttrs, 50, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewDrawAttrs, 50, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mFabBackgroundImage, 100, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewBackgroundImage, 100, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mFabDrawTool, 150, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewDrawTool, 150, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mFabDrawMode, 200, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewDrawMode, 200, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mFabSaveDraw, 250, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewSaveDraw, 250, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mFabClearDraw, 300, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewClearDraw, 300, 150, new OvershootInterpolator(), true);
                } else {
                    status = 0;
                    mFabActions.animate().rotation(0).setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(100).start();
                    AnimateUtils.FadeOutAnimation(mFadeView, 0, 100, new AccelerateDecelerateInterpolator(), true);

                    AnimateUtils.ScaleOutAnimation(mFabClearDraw, 50, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mCardViewClearDraw, 50, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mFabSaveDraw, 100, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mCardViewSaveDraw, 100, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mFabDrawMode, 150, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mCardViewDrawMode, 150, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mFabDrawTool, 200, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mCardViewDrawTool, 200, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mFabBackgroundImage, 250, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mCardViewBackgroundImage, 250, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mFabDrawAttrs, 300, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mCardViewDrawAttrs, 300, 150, new OvershootInterpolator(), true);
                }
                view.setTag(status);
            }
        });

        mFabDrawTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    changeDrawTool();
                }
            }
        });

        mCardViewDrawTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    changeDrawTool();
                }
            }
        });

        mFabDrawMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    changeDrawMode();
                }
            }
        });

        mCardViewDrawMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    changeDrawMode();
                }
            }
        });

        mFabDrawAttrs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    changeDrawAttribs();
                }
            }
        });

        mCardViewDrawAttrs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    changeDrawAttribs();
                }
            }
        });

        mFabSaveDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    requestPermissions(0);
                }
            }
        });

        mCardViewSaveDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    requestPermissions(0);
                }
            }
        });

        mFabClearDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    clearDraw();
                }
            }
        });

        mCardViewClearDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    clearDraw();
                }
            }
        });

        mFabBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    requestPermissions(1);
                }
            }
        });

        mCardViewBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    requestPermissions(1);
                }
            }
        });
    }

    private void setupADS(){
        mAdView = (NativeExpressAdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FCFD13908AA93E51A1BA390FA8010631")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void changeDrawTool(){
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

    private void changeDrawMode(){
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

    private void changeDrawAttribs(){
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

    private void saveDraw(){
        SaveBitmapDialog saveBitmapDialog = SaveBitmapDialog.newInstance();
        Object[] createCaptureResponse = mDrawView.createCapture(DrawingCapture.BITMAP);
        saveBitmapDialog.setPreviewBitmap((Bitmap) createCaptureResponse[0]);
        saveBitmapDialog.setPreviewFormat(String.valueOf(createCaptureResponse[1]));
        saveBitmapDialog.setOnSaveBitmapListener(new SaveBitmapDialog.OnSaveBitmapListener() {
            @Override
            public void onSaveBitmapCompleted() {
                Snackbar.make(mFabActions, "Capture saved succesfully!", 2000).show();
            }

            @Override
            public void onSaveBitmapCanceled() {
                Snackbar.make(mFabActions, "Capture saved canceled.", 2000).show();
            }
        });
        saveBitmapDialog.show(getSupportFragmentManager(), "saveBitmap");
    }

    private void chooseBackgroundImage(){
        SelectImageDialog selectImageDialog = SelectImageDialog.newInstance();
        selectImageDialog.setOnImageSelectListener(new SelectImageDialog.OnImageSelectListener() {
            @Override
            public void onSelectImage(File imageFile) {
                mDrawView.setBackgroundImage(imageFile);
            }

            @Override
            public void onSelectImage(byte[] imageBytes) {

            }
        });
        selectImageDialog.show(getSupportFragmentManager(), SelectImageDialog.SELEC_IMAGE_DIALOG);
    }

    private void clearDraw(){
        mDrawView.restartDrawing();
    }

    private void canUndoRedo(){
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

    private void requestPermissions(int option){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (option == 0) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSIONS);
                } else {
                    saveDraw();
                }
            } else if (option == 1){
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSIONS2);
                } else {
                    chooseBackgroundImage();
                }
            }
        } else {
            if (option == 0)
                saveDraw();
            else if (option == 1)
                chooseBackgroundImage();
        }
    }
}
