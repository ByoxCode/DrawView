package com.byox.drawviewproject;

import android.Manifest;
import android.content.Intent;
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
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.byox.drawview.abstracts.DrawViewListener;
import com.byox.drawview.dictionaries.DrawCapture;
import com.byox.drawview.enums.BackgroundScale;
import com.byox.drawview.enums.BackgroundType;
import com.byox.drawview.enums.DrawingCapture;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.views.DrawView;
import com.byox.drawviewproject.dialogs.DrawAttribsDialog;
import com.byox.drawviewproject.dialogs.SaveBitmapDialog;
import com.byox.drawviewproject.dialogs.SelectImageDialog;
import com.byox.drawviewproject.utils.AnimateUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;

public class MainActivity extends AppCompatActivity {

    //region CONSTANTS
    private final int STORAGE_PERMISSIONS = 1000;
    private final int STORAGE_PERMISSIONS2 = 2000;
    //endregion

    //region VIEWS
    private Toolbar mToolbar;
    private DrawView mDrawView;

    private FloatingActionButton mFabClearDraw;
    private CardView mCardViewLoadingBackground;

    private MenuItem mMenuItemRedo;
    private MenuItem mMenuItemUndo;
    private MenuItem mMenuItemExcludeBackEraser;
    //endregion

    //region ADS
    private AdView mAdView;
    //endregion

    //region EVENTS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFabClearDraw = findViewById(R.id.fab_clear);
        mCardViewLoadingBackground = findViewById(R.id.cv_loading_card);

        setupToolbar();
        setupADS();
        setupDrawView();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenuItemUndo = menu.getItem(0);
        mMenuItemRedo = menu.getItem(1);
        mMenuItemExcludeBackEraser = menu.findItem(R.id.action_view_exclude_background_from_eraser);
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
            case R.id.action_draw_attrs:
                changeDrawAttributes();
                break;
            case R.id.action_draw_background:
                requestPermissions(1);
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
            case R.id.action_view_camera_option:
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                break;
            case R.id.action_view_exclude_background_from_eraser:
                mMenuItemExcludeBackEraser.setChecked(!item.isChecked());
                mDrawView.excludeBackgroundFromEraser(mMenuItemExcludeBackEraser.isChecked());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == grantResults.length) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (requestCode) {
                        case STORAGE_PERMISSIONS:
                            saveDraw();
                            break;
                        case STORAGE_PERMISSIONS2:
                            chooseBackgroundImage();
                            break;
                    }
                }
            }, 300);
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

    private void setupDrawView() {
        mDrawView = findViewById(R.id.draw_view);
    }

    private void setListeners() {
        mDrawView.addDrawViewListener(new DrawViewListener() {
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
                        if (!mDrawView.isDrawViewEmpty())
                            mFabClearDraw.setVisibility(View.VISIBLE);
                    }
                }, 200);
            }

            @Override
            public void onDrawBackgroundStart() {
                super.onDrawBackgroundStart();

                if (mCardViewLoadingBackground.getVisibility() == View.INVISIBLE)
                    AnimateUtils.ScaleInAnimation(mCardViewLoadingBackground, 50, 300, new OvershootInterpolator(), true);
            }

            @Override
            public void onDrawBackgroundEnds(byte[] bytes, BackgroundType originBackgroundType) {
                super.onDrawBackgroundEnds(bytes, originBackgroundType);

                if (mCardViewLoadingBackground.getVisibility() == View.VISIBLE)
                    AnimateUtils.ScaleOutAnimation(mCardViewLoadingBackground, 50, 300, new OvershootInterpolator(), true);
            }

            @Override
            public void onDrawingError(Exception e) {
                super.onDrawingError(e);

                e.printStackTrace();
            }

            @Override
            public void onCaptureCreated(DrawCapture drawCapture) {
                super.onCaptureCreated(drawCapture);

                SaveBitmapDialog saveBitmapDialog
                        = SaveBitmapDialog.newInstance(drawCapture);
                saveBitmapDialog.setOnSaveBitmapListener(new SaveBitmapDialog.OnSaveBitmapListener() {
                    @Override
                    public void onSaveBitmapCompleted() {
                        Snackbar.make(mFabClearDraw, "Capture saved successfully!", 2000).show();
                    }

                    @Override
                    public void onSaveBitmapCanceled() {
                        Snackbar.make(mFabClearDraw, "Capture saved canceled.", 2000).show();
                    }
                });
                saveBitmapDialog.show(getSupportFragmentManager(), "saveBitmap");
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
        MobileAds.initialize(this,
                "ca-app-pub-6238951090454835~5923556703");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("790BE5F246F1F9AA4391D5B1F2E57E68")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void changeDrawTool() {
        int currentTool = 0;
        List<String> tools = new ArrayList<>();

        for (int i = 0; i < DrawingTool.values().length; i++) {
            tools.add(DrawingTool.values()[i].toString());
            if (DrawingTool.values()[i] == mDrawView.getDrawingTool()) currentTool = i;
        }

        new MaterialDialog.Builder(this)
                .title(R.string.choose_draw_tool_tile)
                .items(tools)
                .itemsCallbackSingleChoice(currentTool, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (DrawingTool.values()[which] == DrawingTool.SHAPE){// ||
                                //DrawingTool.values()[which] == DrawingTool.STAR) {
                            final DrawingTool tool = DrawingTool.SHAPE;// DrawingTool.values()[which];
                            final String[] items = new String[]{"5", "6", "7", "8", "9", "10"};
                            /*if (tool == DrawingTool.SHAPE) items = new String[]{"5", "6", "7", "8", "9", "10"};
                            else items = new String[]{"4", "5", "6", "7", "8", "9", "10"};*/
                            new MaterialDialog.Builder(MainActivity.this)
                                    .title(R.string.choose_draw_tool_sides_title)
                                    .items(items)
                                    .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            mDrawView.tool(tool, Integer.parseInt(items[which]));
                                            return true;
                                        }
                                    })
                                    .positiveText(android.R.string.ok)
                                    .show();
                        } else {
                            mDrawView.tool(DrawingTool.values()[which]);
                        }
                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .show();
    }

    private void changeDrawMode() {
        int currentMode = 0;
        List<String> modes = new ArrayList<>();

        for (int i = 0; i < DrawingMode.values().length; i++) {
            modes.add(DrawingMode.values()[i].toString());
            if (DrawingMode.values()[i] == mDrawView.getDrawingMode()) currentMode = i;
        }

        new MaterialDialog.Builder(this)
                .title(R.string.choose_draw_mode_tile)
                .items(modes)
                .itemsCallbackSingleChoice(currentMode, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mDrawView.mode(DrawingMode.values()[which]);
                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .show();
    }

    private void changeDrawAttributes() {
        DrawAttribsDialog drawAttribsDialog = DrawAttribsDialog.newInstance();
        drawAttribsDialog.setPaint(mDrawView.getCurrentPaintParams());
        drawAttribsDialog.setOnCustomViewDialogListener(new DrawAttribsDialog.OnCustomViewDialogListener() {
            @Override
            public void onRefreshPaint(Paint newPaint) {
                mDrawView.color(newPaint.getColor())
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

    private void saveDraw() {
        mDrawView.createCapture(Bitmap.CompressFormat.JPEG);
    }

    private void chooseBackgroundImage() {
        new MaterialDialog.Builder(this)
                .title(R.string.choose_background_title)
                .items(R.array.image_source)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            chooseBackgroundImageFile();
                        } else if (which == 1) {
                            chooseBackgroundImageURL();
                        }
                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .show();
    }

    private void chooseBackgroundImageFile() {
        SelectImageDialog selectImageDialog = SelectImageDialog.newInstance();
        selectImageDialog.setOnImageSelectListener(new SelectImageDialog.OnImageSelectListener() {
            @Override
            public void onSelectImage(File imageFile) {
                mDrawView
                        .backgroundImage(
                                imageFile,
                                BackgroundType.FILE,
                                BackgroundScale.CENTER_CROP,
                                60,
                                new BlurTransformation(getApplicationContext(), 20, 20),
                                new GrayscaleTransformation())
                        .processBackground();
            }

            @Override
            public void onSelectImage(byte[] imageBytes) {
                //mDrawView.backgroundImage(imageBytes, BackgroundType.BYTES, BackgroundScale.FIT_START);
            }
        });
        selectImageDialog.show(getSupportFragmentManager(), SelectImageDialog.SELEC_IMAGE_DIALOG);
    }

    private void chooseBackgroundImageURL() {
        new MaterialDialog.Builder(this)
                .title(R.string.choose_background_title)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI)
                .input(R.string.choose_background_url, R.string.choose_background_url_default,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                mDrawView
                                        .backgroundImage(
                                                input.toString(),
                                                BackgroundType.URL,
                                                BackgroundScale.CENTER_CROP,
                                                60)
                                        .processBackground();
                            }
                        }).show();
    }

    private void requestText() {
        new MaterialDialog.Builder(this)
                .title(R.string.request_text_title)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.request_text), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                mDrawView.drawText(input.toString());
                            }
                        }).show();
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
            if (option == 0 || option == 1) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            option == 1 ? STORAGE_PERMISSIONS : STORAGE_PERMISSIONS2);
                } else {
                    if (option == 0) saveDraw();
                    else chooseBackgroundImage();
                }
            }
        } else {
            switch (option) {
                case 0:
                    saveDraw();
                    break;
                case 1:
                    chooseBackgroundImage();
                    break;
            }
        }
    }
    //endregion
}
