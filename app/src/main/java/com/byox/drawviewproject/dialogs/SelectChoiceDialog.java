package com.byox.drawviewproject.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Ing. Oscar G. Medina Cruz on 07/11/2016.
 */

public class SelectChoiceDialog extends DialogFragment {

    private OnChoiceDialogListener onChoiceDialogListener;

    private static final String CHOICES_TITLE ="CHOICES_TITLE";
    private static final String CHOICES_ARRAY ="CHOICES_ARRAY";

    public SelectChoiceDialog() {}

    public static SelectChoiceDialog newInstance(String title, String... choices){
        if (choices.length == 0)
            throw new RuntimeException("Be sure to add at least one choise to the dialog!");

        SelectChoiceDialog selectChoiceDialog = new SelectChoiceDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CHOICES_TITLE, title);
        bundle.putStringArray(CHOICES_ARRAY, choices);
        selectChoiceDialog.setArguments(bundle);
        return selectChoiceDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getArguments().getString(CHOICES_TITLE))
                .setItems(getArguments().getStringArray(CHOICES_ARRAY), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onChoiceDialogListener != null)
                            onChoiceDialogListener.onChoiceSelected(i);
                        dismiss();

                    }
                });
        return builder.create();
    }

    // INTERFACE
    public void setOnChoiceDialogListener(OnChoiceDialogListener onChoiceDialogListener){
        this.onChoiceDialogListener = onChoiceDialogListener;
    }

    public interface OnChoiceDialogListener{
        void onChoiceSelected(int position);
    }
}
