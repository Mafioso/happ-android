package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.fragments.ChangePasswordFragment;
import com.happ.retrofit.APIService;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dante on 9/22/16.
 */
public class UserActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener
{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;
    private EditText mFullName, mEmail, mPhoneNumber, mBirthday;
    private Button mButtonBirthday, mButtonChangePW;
    private FloatingActionButton mUserEditFab;
    private NestedScrollView mScrollView;
    private SwitchCompat mGenderSwitch;
    private Date birthday;
    private BroadcastReceiver setUserEditOKReceiver;
    private boolean fromSettings = false;
    private CoordinatorLayout mRootLayout;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (fromSettings) {
            UserActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSettings = getIntent().getBooleanExtra("from_settings", false);

        setContentView(R.layout.activity_user);

        mRootLayout = (CoordinatorLayout)findViewById(R.id.root_layout);

        ((CircleImageView)findViewById(R.id.avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (fromSettings) {
            toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_close_white);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (fromSettings) {
                    UserActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
                }
            }
        });

        mFullName = (EditText) findViewById(R.id.input__user_fullname);
        mFullName.setText(App.getCurrentUser().getFullName());

        mEmail = (EditText) findViewById(R.id.input_user_email);
        mEmail.setText(App.getCurrentUser().getEmail());

        mPhoneNumber = (EditText) findViewById(R.id.input_user_phonenumber);
        mPhoneNumber.setText(App.getCurrentUser().getPhone());

        mBirthday = (EditText) findViewById(R.id.input_user_birthday);

        birthday = App.getCurrentUser().getBirthDate();
        if (birthday == null) {
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            cal.set(Calendar.YEAR, currentYear-18);
            birthday = cal.getTime();
        }

        java.text.DateFormat format = DateFormat.getLongDateFormat(this);
        mBirthday.setText(format.format(birthday));

        mButtonBirthday = (Button) findViewById(R.id.btn_choice_birthday);
        mButtonBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_click_birthday(v);
            }
        });

        mScrollView = (NestedScrollView) findViewById(R.id.event_edit_srollview);

        mGenderSwitch = (SwitchCompat) findViewById(R.id.user_gender);
        mGenderSwitch.setChecked(App.getCurrentUser().getGender()>0);

        mUserEditFab = (FloatingActionButton) findViewById(R.id.useredit_fab);
        mUserEditFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideSoftKeyboard(UserActivity.this, v);
                int gender = 0;
                if (mGenderSwitch.isChecked()) gender = 1;
                APIService.doUserEdit(mFullName.getText().toString(), mEmail.getText().toString(), mPhoneNumber.getText().toString(), birthday, gender);
            }
        });

        if (setUserEditOKReceiver == null) {
            setUserEditOKReceiver = createSetUserEditOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(setUserEditOKReceiver, new IntentFilter(BroadcastIntents.USEREDIT_REQUEST_OK));
        }

        mButtonChangePW = (Button) findViewById(R.id.btn_user_change_password);
        mButtonChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ChangePasswordFragment changePasswordFragment = ChangePasswordFragment.newInstance();
                changePasswordFragment.show(fm, "fragment_change_password");
            }
        });

    }

    public View getRootLayout() {
        return mRootLayout;
    }

    protected void updateUserData() {
//        user = App.getCurrentUser();
        final Snackbar snackbar = Snackbar.make(mScrollView, getResources().getString(R.string.user_done), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getResources().getString(R.string.event_done_action), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private BroadcastReceiver createSetUserEditOKReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUserData();
            }
        };
    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public void btn_click_birthday(View view) {
        Calendar now = Calendar.getInstance();
        now.setTime(birthday);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                UserActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        now.setTime(new Date());
        dpd.setMaxDate(now);

        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String date = dayOfMonth+" "+(monthOfYear)+" "+year;
        Calendar cal = Calendar.getInstance();
        cal.setTime(birthday);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        birthday = cal.getTime();

        java.text.DateFormat format = DateFormat.getLongDateFormat(this);
        mBirthday.setText(format.format(birthday));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (setUserEditOKReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(setUserEditOKReceiver);
            setUserEditOKReceiver = null;
        }
    }

}
