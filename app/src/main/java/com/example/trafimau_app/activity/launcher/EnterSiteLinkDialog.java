package com.example.trafimau_app.activity.launcher;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class EnterSiteLinkDialog extends DialogFragment {

    public interface EnterSiteLinkDialogListener {
        void onSiteLinkReceived(String URL);
    }

    Context context;
    private EnterSiteLinkDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            Fragment targetFragment = getTargetFragment();
            listener = (EnterSiteLinkDialogListener) targetFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EnterSiteLinkDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Pass null as the parent view because its going in the dialog layout
        final View layout = inflater.inflate(R.layout.dialog_enter_site_link, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.enter_the_site_link)
                .setView(layout)
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    EditText et = layout.findViewById(R.id.addSiteDialogEditText);
                    String URL = et.getText().toString();
                    listener.onSiteLinkReceived(URL);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    Log.d(MyApplication.LOG_TAG, "EnterSiteLinkDialog cancelled");
                    EnterSiteLinkDialog.this.getDialog().cancel();
                });
        return builder.create();
    }
}