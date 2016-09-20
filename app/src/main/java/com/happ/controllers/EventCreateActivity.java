package com.happ.controllers;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.happ.R;
import com.happ.fragments.EventInterestFragment;
import com.happ.models.Interest;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by dante on 8/22/16.
 */
public class EventCreateActivity extends AppCompatActivity implements
        View.OnClickListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener
{

static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;
    private EditText inputTitle, inputDescription, inputInterest, inputStartDate, inputEndDate;
    private TextInputLayout inputLayoutTitle, inputLayoutDescription, inputLayoutInterests, inputLayoutStartDate, inputLayoutEndDate;
    private ImageButton btnStartDate, btnEndDate;
    private ImageButton editInterestButton;
    private Interest selectedInterest;
//    private FloatingActionButton fab;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventcreate);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inputLayoutTitle = (TextInputLayout) findViewById(R.id.input_layout_title);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.input_layout_description);
        inputLayoutInterests = (TextInputLayout) findViewById(R.id.input_layout_interest);
        inputLayoutStartDate = (TextInputLayout) findViewById(R.id.input_layout_startDate);
        inputLayoutEndDate = (TextInputLayout) findViewById(R.id.input_layout_endDate);

        inputTitle = (EditText) findViewById(R.id.input_title);
        inputDescription = (EditText) findViewById(R.id.input_description);
        inputInterest = (EditText) findViewById(R.id.input_interest);
        inputStartDate = (EditText) findViewById(R.id.input_startDate);
        inputEndDate = (EditText) findViewById(R.id.input_endDate);

//        fab = (FloatingActionButton)findViewById(R.id.fab);
        editInterestButton = (ImageButton) findViewById(R.id.btn_select_interest);


        editInterestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                EventInterestFragment editNameDialogFragment = EventInterestFragment.newInstance();
                editNameDialogFragment.setOnInterestSelectListener(new EventInterestFragment.OnInterestSelectListener() {
                    @Override
                    public void onInterestSelected(Interest interest) {
                        selectedInterest = interest;
                        inputInterest.setText(selectedInterest.getTitle());
                    }
                });
                editNameDialogFragment.show(fm, "fragment_select_interest");
            }
        });

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitForm();
//            }
//        });

        btnStartDate = (ImageButton) findViewById(R.id.btn_choice_startDate);
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EventCreateActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(getResources().getColor(R.color.colorPrimaryDark));
                dpd.show(getFragmentManager(), "Datepickerdialog");


            }
        });

        checkValidation();

        inputTitle.addTextChangedListener(mWatcher);
        inputDescription.addTextChangedListener(mWatcher);
        inputInterest.addTextChangedListener(mWatcher);
        inputStartDate.addTextChangedListener(mWatcher);
        inputEndDate.addTextChangedListener(mWatcher);

    }

//    public void btn_click_selection_interests(View view) {
//        new EventInterestFragment().show(getSupportFragmentManager(), "LoginForm");
//    }


    private void checkValidation() {

        if ((TextUtils.isEmpty(inputTitle.getText()))
                || (TextUtils.isEmpty(inputDescription.getText()))
                || (TextUtils.isEmpty(inputInterest.getText()))
                || (TextUtils.isEmpty(inputStartDate.getText()))
                || (TextUtils.isEmpty(inputEndDate.getText()))
                );
//            fab.setVisibility(View.INVISIBLE);
//        else
//            fab.setVisibility(View.VISIBLE);

    }

    TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            checkValidation();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    private void submitForm() {
        if (!validateTitle()) {
            return;
        }

        if (!validateDescription()) {
            return;
        }
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

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (++monthOfYear) + "/" +year;
        inputStartDate.setText(date);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }
}
