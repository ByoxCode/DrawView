package com.byox.drawviewproject.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byox.drawview.views.DrawView;
import com.byox.drawviewproject.R;

/**
 * Created by Ing. Oscar G. Medina Cruz on 07/11/2016.
 */

public class DrawAttribsDialog extends DialogFragment {

    // LISTENER
    private OnCustomViewDialogListener onCustomViewDialogListener;

    // VARS
    private Paint mPaint;

    public DrawAttribsDialog() {
    }

    public static DrawAttribsDialog newInstance() {
        return new DrawAttribsDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_draw_attribs, null);

        final View previewColor = view.findViewById(R.id.preview_color);
        final AppCompatSeekBar seekBarRed = view.findViewById(R.id.acsb_red);
        final AppCompatSeekBar seekBarGreen = view.findViewById(R.id.acsb_green);
        final AppCompatSeekBar seekBarBlue = view.findViewById(R.id.acsb_blue);
        final TextView textViewRedValue = view.findViewById(R.id.tv_current_red);
        final TextView textViewGreenValue = view.findViewById(R.id.tv_current_green);
        final TextView textViewBlueValue = view.findViewById(R.id.tv_current_blue);
        AppCompatSeekBar seekBarStrokeWidth = view.findViewById(R.id.acsb_stroke_width);
        final TextView textViewStrokeWidth = view.findViewById(R.id.tv_stroke_width);
        AppCompatSeekBar seekBarOpacity = view.findViewById(R.id.acsb_opacity);
        final TextView textViewOpacity = view.findViewById(R.id.tv_opacity);
        final AppCompatSeekBar seekBarFontSize = view.findViewById(R.id.acsb_font_size);
        final TextView textViewFontSize = view.findViewById(R.id.tv_font_size);
        AppCompatCheckBox appCompatCheckBoxAntiAlias = view.findViewById(R.id.chb_anti_alias);
        AppCompatCheckBox appCompatCheckBoxDither = view.findViewById(R.id.chb_dither);
        AppCompatRadioButton appCompatRadioButtonFill = view.findViewById(R.id.rb_fill);
        AppCompatRadioButton appCompatRadioButtonFillStroke = view.findViewById(R.id.rb_fill_stroke);
        AppCompatRadioButton appCompatRadioButtonStroke = view.findViewById(R.id.rb_stroke);
        AppCompatRadioButton appCompatRadioButtonButt = view.findViewById(R.id.rb_butt);
        AppCompatRadioButton appCompatRadioButtonRound = view.findViewById(R.id.rb_round);
        AppCompatRadioButton appCompatRadioButtonSquare = view.findViewById(R.id.rb_square);
        AppCompatRadioButton appCompatRadioButtonDefault = view.findViewById(R.id.rb_default);
        AppCompatRadioButton appCompatRadioButtonMonospace = view.findViewById(R.id.rb_monospace);
        AppCompatRadioButton appCompatRadioButtonSansSerif = view.findViewById(R.id.rb_sans_serif);
        AppCompatRadioButton appCompatRadioButtonSerif = view.findViewById(R.id.rb_serif);

        previewColor.setBackgroundColor(mPaint.getColor());

        seekBarRed.setProgress(Color.red(mPaint.getColor()));
        seekBarGreen.setProgress(Color.green(mPaint.getColor()));
        seekBarBlue.setProgress(Color.blue(mPaint.getColor()));
        seekBarStrokeWidth.setProgress((int) mPaint.getStrokeWidth());
        seekBarOpacity.setProgress((int) mPaint.getAlpha());
        seekBarFontSize.setProgress((int) mPaint.getTextSize());

        textViewRedValue.setText(String.valueOf(Color.red(mPaint.getColor())));
        textViewGreenValue.setText(String.valueOf(Color.green(mPaint.getColor())));
        textViewBlueValue.setText(String.valueOf(Color.blue(mPaint.getColor())));
        textViewStrokeWidth.setText(getContext().getResources().getString(R.string.stroke_width, (int) mPaint.getStrokeWidth()));
        textViewOpacity.setText(getContext().getResources().getString(R.string.opacity, (int) mPaint.getAlpha()));
        textViewFontSize.setText(getContext().getResources().getString(R.string.font_size, 12));

        appCompatCheckBoxAntiAlias.setChecked(mPaint.isAntiAlias());
        appCompatCheckBoxDither.setChecked(mPaint.isDither());
        appCompatRadioButtonFill.setChecked(mPaint.getStyle() == Paint.Style.FILL);
        appCompatRadioButtonFillStroke.setChecked(mPaint.getStyle() == Paint.Style.FILL_AND_STROKE);
        appCompatRadioButtonStroke.setChecked(mPaint.getStyle() == Paint.Style.STROKE);
        appCompatRadioButtonButt.setChecked(mPaint.getStrokeCap() == Paint.Cap.BUTT);
        appCompatRadioButtonRound.setChecked(mPaint.getStrokeCap() == Paint.Cap.ROUND);
        appCompatRadioButtonSquare.setChecked(mPaint.getStrokeCap() == Paint.Cap.SQUARE);
        appCompatRadioButtonDefault.setChecked(mPaint.getTypeface() == Typeface.DEFAULT);
        appCompatRadioButtonMonospace.setChecked(mPaint.getTypeface() == Typeface.MONOSPACE);
        appCompatRadioButtonSansSerif.setChecked(mPaint.getTypeface() == Typeface.SANS_SERIF);
        appCompatRadioButtonSerif.setChecked(mPaint.getTypeface() == Typeface.SERIF);

        AppCompatSeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mPaint.setColor(Color.rgb(
                        seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                previewColor.setBackgroundColor(mPaint.getColor());

                textViewRedValue.setText(String.valueOf(seekBarRed.getProgress()));
                textViewGreenValue.setText(String.valueOf(seekBarGreen.getProgress()));
                textViewBlueValue.setText(String.valueOf(seekBarBlue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        seekBarRed.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarGreen.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarBlue.setOnSeekBarChangeListener(onSeekBarChangeListener);

        seekBarStrokeWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mPaint.setStrokeWidth(i);
                textViewStrokeWidth.setText(getContext().getResources().getString(R.string.stroke_width, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mPaint.setAlpha(i);
                textViewOpacity.setText(getContext().getResources().getString(R.string.opacity, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mPaint.setTextSize(i);
                textViewFontSize.setText(getContext().getResources().getString(R.string.font_size, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        appCompatCheckBoxAntiAlias.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setAntiAlias(b);
            }
        });

        appCompatCheckBoxDither.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setDither(b);
            }
        });

        appCompatRadioButtonFill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setStyle(Paint.Style.FILL);
            }
        });

        appCompatRadioButtonFillStroke.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            }
        });

        appCompatRadioButtonStroke.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setStyle(Paint.Style.STROKE);
            }
        });

        appCompatRadioButtonButt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setStrokeCap(Paint.Cap.BUTT);
            }
        });

        appCompatRadioButtonRound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setStrokeCap(Paint.Cap.ROUND);
            }
        });

        appCompatRadioButtonSquare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setStrokeCap(Paint.Cap.SQUARE);
            }
        });

        appCompatRadioButtonDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setTypeface(Typeface.DEFAULT);
            }
        });

        appCompatRadioButtonMonospace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setTypeface(Typeface.MONOSPACE);
            }
        });

        appCompatRadioButtonSansSerif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setTypeface(Typeface.SANS_SERIF);
            }
        });

        appCompatRadioButtonSerif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    mPaint.setTypeface(Typeface.SERIF);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onCustomViewDialogListener != null)
                            onCustomViewDialogListener.onRefreshPaint(mPaint);
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    // METHODS
    public void setPaint(Paint paint) {
        this.mPaint = paint;
    }

    // INTERFACE
    public void setOnCustomViewDialogListener(OnCustomViewDialogListener onCustomViewDialogListener) {
        this.onCustomViewDialogListener = onCustomViewDialogListener;
    }

    public interface OnCustomViewDialogListener {
        void onRefreshPaint(Paint newPaint);
    }
}

