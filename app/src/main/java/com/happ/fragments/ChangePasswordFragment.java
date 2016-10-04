package com.happ.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.happ.R;
import com.happ.controllers.UserActivity;
import com.happ.retrofit.APIService;

/**
 * Created by dante on 10/3/16.
 */
public class ChangePasswordFragment extends DialogFragment {

    private EditText mOldPw, mNewPw, mRepeatNewPw;


    public ChangePasswordFragment() {
    }

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getActivity()

        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.change_password_fragment, null);
        final Activity activity = getActivity();

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.change_password))
                .setView(contentView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mNewPw.getText().toString().equals(mRepeatNewPw.getText().toString())) {
                            APIService.doChangePassword(mOldPw.getText().toString(), mNewPw.getText().toString());
                            return;
                        } else {
                            String text = getResources().getString(R.string.passwords_mismatch);
                            String ok = getResources().getString(R.string.ok).toUpperCase();
                            final Snackbar snackbar = Snackbar.make(((UserActivity)getActivity()).getRootLayout(), text, Snackbar.LENGTH_LONG);
                            snackbar.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }
                    }
                })
                .create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mOldPw = (EditText) contentView.findViewById(R.id.old_password);
        mNewPw = (EditText) contentView.findViewById(R.id.new_password);
        mRepeatNewPw = (EditText) contentView.findViewById(R.id.repeat_new_password);

        return dialog;
    }

}
