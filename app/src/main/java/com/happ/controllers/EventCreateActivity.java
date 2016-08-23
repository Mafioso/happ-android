package com.happ.controllers;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.happ.R;
import com.happ.models.Event;
import com.happ.models.Interest;

/**
 * Created by dante on 8/22/16.
 */
public class EventCreateActivity extends AppCompatActivity {

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;
    private EditText inputTitle, inputDescription, inputInterests, inputStartDate, inputEndDate;
    private TextInputLayout inputLayoutTitle, inputLayoutDescription, inputLayoutInterests, inputLayoutStartDate, inputLayoutEndDate;
//    private ImageButton btnInterests, btnStartDate, btnEndDate;
    private ImageButton buttonClick;

    private Event event;
    protected Interest eventInterest;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventcreate);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        inputLayoutTitle = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.input_layout_description);
        inputLayoutInterests = (TextInputLayout) findViewById(R.id.input_layout_interest);
        inputLayoutStartDate = (TextInputLayout) findViewById(R.id.input_layout_startDate);
        inputLayoutEndDate = (TextInputLayout) findViewById(R.id.input_layout_endDate);

        inputTitle = (EditText) findViewById(R.id.input_title);
        inputDescription = (EditText) findViewById(R.id.input_description);
        inputInterests = (EditText) findViewById(R.id.input_interest);
        inputStartDate = (EditText) findViewById(R.id.input_startDate);
        inputEndDate = (EditText) findViewById(R.id.input_endDate);

        buttonClick = (ImageButton) findViewById(R.id.btn_choice_interests);

        //FLoating Action Button
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });





    }

    public void btn_click (View v) {
        new EventInterestFragment().show(getSupportFragmentManager(), "LoginForm");
        }

    private void submitForm() {
        if (!validateTitle()) {
            return;
        }

        if (!validateDescription()) {
            return;
        }

        event = new Event();
        event.setTitle(inputTitle.getText().toString());
        event.setInterest(eventInterest);



        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateTitle() {
        if (inputTitle.getText().toString().trim().isEmpty()) {
            inputLayoutTitle.setError(getString(R.string.err_msg));
            requestFocus(inputTitle);
            return false;
        } else {
            inputLayoutTitle.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDescription() {
        if (inputDescription.getText().toString().trim().isEmpty()) {
            inputLayoutDescription.setError(getString(R.string.err_msg));
            requestFocus(inputDescription);
            return false;
        } else {
            inputLayoutDescription.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_title:
                    validateTitle();
                    break;
                case R.id.input_description:
                    validateDescription();
                    break;
            }
        }
    }
}
