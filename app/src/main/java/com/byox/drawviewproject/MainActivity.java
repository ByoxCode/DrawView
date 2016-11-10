package com.byox.drawviewproject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.byox.drawview.enums.DrawingCapture;
import com.byox.drawview.enums.DrawingMode;
import com.byox.drawview.enums.DrawingTool;
import com.byox.drawview.views.DrawView;
import com.byox.drawviewproject.dialogs.DrawAttribsDialog;
import com.byox.drawviewproject.dialogs.SaveBitmapDialog;
import com.byox.drawviewproject.dialogs.SelectChoiceDialog;
import com.byox.drawviewproject.utils.AnimateUtils;

public class MainActivity extends AppCompatActivity {

    // VIEWS
    private Toolbar mToolbar;
    private DrawView mDrawView;

    private View mFadeView;
    private FloatingActionButton mFabActions;
    private FloatingActionButton mFabDrawAttrs;
    private CardView mCardViewDrawAttrs;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFadeView = findViewById(R.id.fade_view);
        mFabActions = (FloatingActionButton) findViewById(R.id.fab_actions);
        mFabDrawAttrs = (FloatingActionButton) findViewById(R.id.fab_draw_attrs);
        mFabDrawTool = (FloatingActionButton) findViewById(R.id.fab_draw_tool);
        mFabDrawMode = (FloatingActionButton) findViewById(R.id.fab_draw_mode);
        mFabSaveDraw = (FloatingActionButton) findViewById(R.id.fab_draw_save);
        mFabClearDraw = (FloatingActionButton) findViewById(R.id.fab_draw_clear);
        mCardViewDrawAttrs = (CardView) findViewById(R.id.cv_draw_attrs);
        mCardViewDrawTool = (CardView) findViewById(R.id.cv_draw_tool);
        mCardViewDrawMode = (CardView) findViewById(R.id.cv_draw_mode);
        mCardViewSaveDraw = (CardView) findViewById(R.id.cv_draw_save);
        mCardViewClearDraw = (CardView) findViewById(R.id.cv_draw_clear);

        setupToolbar();
        setupDrawView();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        if ((int) mFabActions.getTag() == 1)
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
                    AnimateUtils.ScaleInAnimation(mFabDrawTool, 100, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewDrawTool, 100, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mFabDrawMode, 150, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewDrawMode, 150, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mFabSaveDraw, 200, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewSaveDraw, 200, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mFabClearDraw, 250, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleInAnimation(mCardViewClearDraw, 250, 150, new OvershootInterpolator(), true);
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
                    AnimateUtils.ScaleOutAnimation(mFabDrawAttrs, 250, 150, new OvershootInterpolator(), true);
                    AnimateUtils.ScaleOutAnimation(mCardViewDrawAttrs, 250, 150, new OvershootInterpolator(), true);
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
                    saveDraw();
                }
            }
        });

        mCardViewSaveDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility() == View.VISIBLE) {
                    mFabActions.performClick();
                    saveDraw();
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
    }

    private void changeDrawTool(){
        SelectChoiceDialog selectChoiceDialog =
                SelectChoiceDialog.newInstance("Select a draw tool",
                        "PEN", "LINE", "RECTANGLE", "CIRCLE");
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
            public void onRefreshPaint(Paint newPaint, float fontSize) {
                mDrawView.setDrawColor(newPaint.getColor())
                        .setPaintStyle(newPaint.getStyle())
                        .setDither(newPaint.isDither())
                        .setDrawWidth((int) newPaint.getStrokeWidth())
                        .setDrawAlpha(newPaint.getAlpha())
                        .setAntiAlias(newPaint.isAntiAlias())
                        .setLineCap(newPaint.getStrokeCap())
                        .setFontFamily(newPaint.getTypeface())
                        .setFontSize(fontSize);
            }
        });
        drawAttribsDialog.show(getSupportFragmentManager(), "drawAttribs");
    }

    private void saveDraw(){
        SaveBitmapDialog saveBitmapDialog = SaveBitmapDialog.newInstance();
        saveBitmapDialog.setPreviewBitmap((Bitmap) mDrawView.createCapture(DrawingCapture.BITMAP));
        saveBitmapDialog.show(getSupportFragmentManager(), "saveBitmap");
    }

    private void clearDraw(){
        mFabActions.performClick();
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
}
