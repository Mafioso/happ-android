package com.happ.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.happ.BuildConfig;
import com.happ.R;

/**
 * Created by dante on 10/3/16.
 */
public class AboutFragment extends DialogFragment {

    private TextView mVersionName;


    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.about_fragment, null);
        final Activity activity = getActivity();

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.version_name))
                .setView(contentView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mVersionName = (TextView) contentView.findViewById(R.id.about_version_name);
        String versionName = BuildConfig.VERSION_NAME;
        mVersionName.setText(versionName);

        return dialog;
    }

}
