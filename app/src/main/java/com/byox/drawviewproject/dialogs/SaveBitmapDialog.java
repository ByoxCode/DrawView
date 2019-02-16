package com.byox.drawviewproject.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.byox.drawview.dictionaries.DrawCapture;
import com.byox.drawviewproject.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ing. Oscar G. Medina Cruz on 09/11/2016.
 */

public class SaveBitmapDialog extends DialogFragment {

    //region CONSTANTS
    private static final String DRAW_CAPTURE = "DRAW_CAPTURE";
    //endregion

    //region LISTENERS
    private OnSaveBitmapListener onSaveBitmapListener;
    //endregion

    //region VARS
    private DrawCapture mDrawCapture;
    private String mCaptureName;
    //endregion

    public SaveBitmapDialog(){}

    public static SaveBitmapDialog newInstance(DrawCapture drawCapture){
        SaveBitmapDialog saveBitmapDialog = new SaveBitmapDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DRAW_CAPTURE, drawCapture);
        saveBitmapDialog.setArguments(bundle);
        return saveBitmapDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mDrawCapture = (DrawCapture) getArguments().getSerializable(DRAW_CAPTURE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_save_bitmap, null);
        ImageView imageView = view.findViewById(R.id.iv_capture_preview);
        final TextInputEditText textInputEditText = view.findViewById(R.id.et_file_name);

        /*final File filePath = Environment.getExternalStorageDirectory();
        final String[] fileName = {"DrawViewCapture." + mPreviewFormat.toLowerCase()};

        if (mPreviewBitmap != null)
            imageView.setImageBitmap(mPreviewBitmap);
        else
            imageView.setImageResource(R.color.colorAccent);*/
        imageView.setImageBitmap(mDrawCapture.getCaptureInBitmap());
        mCaptureName = mDrawCapture.getSuggestedFileName();
        textInputEditText.setText(mCaptureName);

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCaptureName = charSequence.toString();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            /*if (!fileName[0].contains("."))
                                fileName[0] = fileName[0] + "." + mPreviewFormat.toLowerCase();
                            textInputEditText.setText(fileName[0]);

                            File image = new File(filePath + File.separator + fileName[0]);
                            image.createNewFile();

                            FileOutputStream fileOutputStream = new FileOutputStream(image);
                            mPreviewBitmap.compress(
                                    mPreviewFormat.toLowerCase().equals("jpg") ?
                                            Bitmap.CompressFormat.JPEG :
                                            Bitmap.CompressFormat.PNG, 100, fileOutputStream);*/
                            mDrawCapture.save(textInputEditText.getText().toString());

                            if (onSaveBitmapListener != null)
                                onSaveBitmapListener.onSaveBitmapCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onSaveBitmapListener != null)
                            onSaveBitmapListener.onSaveBitmapCanceled();
                        dismiss();
                    }
                });

        return builder.create();
    }

    // METHODS
    /*public void setPreviewBitmap(Bitmap bitmap){
        this.mPreviewBitmap = bitmap;
    }

    public void setPreviewFormat(String previewFormat){
        this.mPreviewFormat = previewFormat;
    }*/

    /**
     * Load preview to save from {@link DrawCapture} instance
     * @param drawCapture {@link DrawCapture} instance
     */
    public void loadDrawCapture(DrawCapture drawCapture){
        this.mDrawCapture = drawCapture;
    }

    // LISTENER
    public void setOnSaveBitmapListener(OnSaveBitmapListener onSaveBitmapListener){
        this.onSaveBitmapListener = onSaveBitmapListener;
    }

    public interface OnSaveBitmapListener{
        void onSaveBitmapCompleted();
        void onSaveBitmapCanceled();
    }
}
