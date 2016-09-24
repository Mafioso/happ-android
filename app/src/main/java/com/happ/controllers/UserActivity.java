package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.User;
import com.happ.retrofit.APIService;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by dante on 9/22/16.
 */
public class UserActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener
{

    private Toolbar toolbar;
    private EditText mFullName, mLastName, mEmail, mPhoneNumber, mBirthday, mPassword, mRepeatPassword, mNewPassword;
    private ImageButton mButtonBirthday, mButtonEditPassword, mButtonCloseFormPw;
    private RelativeLayout mRLeditPasswrod, mRLcloseFormPassword;
    private FloatingActionButton mUserEditFab;

    private String username;
    private User user;
    private Date birthday;
    private BroadcastReceiver setUserEditOKReceiver;

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (user.getBirthDate() != null) {
//            birthday = user.getBirthDate();
//        } else {
//            birthday = new Date();
//        }

        setContentView(R.layout.activity_user);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFullName = (EditText) findViewById(R.id.input__user_fullname);
        mFullName.setText(App.getCurrentUser().getFullName());

        mEmail = (EditText) findViewById(R.id.input_user_email);
        mEmail.setText(App.getCurrentUser().getEmail());

        mPhoneNumber = (EditText) findViewById(R.id.input_user_phonenumber);
        mPhoneNumber.setText(App.getCurrentUser().getPhone());

        mBirthday = (EditText) findViewById(R.id.input_user_birthday);

        mButtonBirthday = (ImageButton) findViewById(R.id.btn_choice_birthday);
        mButtonBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_click_birthday(v);
            }
        });
        mButtonEditPassword = (ImageButton) findViewById(R.id.btn_user_edit_pw);
        mButtonEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_click_user_edit_pw(v);
            }
        });
        mButtonCloseFormPw = (ImageButton) findViewById(R.id.btn_user_close_form_pw);
        mButtonCloseFormPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_click_user_close_form_pw(v);
            }
        });

        mPassword = (EditText) findViewById(R.id.input_user_password);
        mRepeatPassword = (EditText) findViewById(R.id.input_user_rpw);
        mNewPassword = (EditText) findViewById(R.id.input_user_newpw);

        mRLeditPasswrod = (RelativeLayout) findViewById(R.id.user_rl_edit_pw);
        mRLeditPasswrod.setVisibility(View.GONE);
        mRLcloseFormPassword = (RelativeLayout) findViewById(R.id.user_rl_close_form_pw);
        mUserEditFab = (FloatingActionButton) findViewById(R.id.useredit_fab);
        mUserEditFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideSoftKeyboard(UserActivity.this, v);
                APIService.doUserEdit(mFullName.getText().toString(), mEmail.getText().toString(), mPhoneNumber.getText().toString());
            }
        });

        if (setUserEditOKReceiver == null) {
            setUserEditOKReceiver = createSetUserEditOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(setUserEditOKReceiver, new IntentFilter(BroadcastIntents.SET_USEREDIT_OK));
        }

    }

    protected void updateUserList() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Realm realm = Realm.getDefaultInstance();
        user = realm.where(User.class).equalTo("username", username).findFirst();
        user = realm.copyFromRealm(user);
        realm.commitTransaction();
        realm.close();

//        Realm realm = Realm.getDefaultInstance();
//        user = realm.where(User.class).equalTo("username", username).findFirst();
//        realm.beginTransaction();
//            user.setEmail(user.getEmail());
//            user.setFullName(user.getFullName());
//            user.setPhone(user.getPhone());
//        user =  realm.copyToRealmOrUpdate(user);
//        realm.commitTransaction();
//        realm.close();
    }

    @Override
    public void onResume() {
        updateUserList();
        super.onResume();
    }

    private BroadcastReceiver createSetUserEditOKReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUserList();
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
                now.get(Calendar.MONTH)-1,
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+" "+(monthOfYear)+" "+year;
        Calendar cal = Calendar.getInstance();
        cal.setTime(birthday);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        birthday = cal.getTime();
        mBirthday.setText(date);
    }

    public void btn_click_user_close_form_pw(View view) {
        Animation up_anim = null;
        up_anim = AnimationUtils.loadAnimation(this, R.anim.up_animation);

        mRLeditPasswrod.startAnimation(up_anim);

        mButtonCloseFormPw.setVisibility(View.GONE);
        mButtonEditPassword.setVisibility(View.VISIBLE);
        mRLeditPasswrod.setVisibility(View.GONE);

    }

    public void btn_click_user_edit_pw(View view) {
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(this, R.anim.down_animation);

        mRLeditPasswrod.setVisibility(View.VISIBLE);
        mRLeditPasswrod.startAnimation(anim);
        mButtonEditPassword.setVisibility(View.GONE);
        mButtonCloseFormPw.setVisibility(View.VISIBLE);
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
