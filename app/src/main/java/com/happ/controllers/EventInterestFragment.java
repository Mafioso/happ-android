package com.happ.controllers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.happ.R;

public class EventInterestFragment extends DialogFragment implements
        DialogInterface.OnClickListener {

//public class EventInterestFragment extends DialogFragment {

    private View form=null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        form= getActivity().getLayoutInflater()
                .inflate(R.layout.activity_event_interests, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return(builder.setTitle("Select Interest").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create());
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {

        EditText loginBox=(EditText)form.findViewById(R.id.login);
        String login = loginBox.getText().toString();

        EditText loginText = (EditText) getActivity().findViewById(R.id.input_interest);
        loginText.setText(login);
    }
    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);
    }
    @Override
    public void onCancel(DialogInterface unused) {
        super.onCancel(unused);
    }
}