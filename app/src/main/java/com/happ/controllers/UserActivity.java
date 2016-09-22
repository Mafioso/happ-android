package com.happ.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.happ.R;
import com.happ.models.User;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import io.realm.Realm;

/**
 * Created by dante on 9/22/16.
 */
public class UserActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener
{

    private Toolbar toolbar;
    private EditText mFirstName, mLastName, mEmail, mPhoneNumber, mBirthday, mPassword, mRepeatPassword, mNewPassword;
    private ImageButton mButtonBirthday, mButtonEditPassword, mButtonCloseFormPw;
    private RelativeLayout mRLeditPasswrod, mRLcloseFormPassword;

    private String eventId;
    private User user;
//    private boolean isOrg;

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
//        isOrg = intent.getBooleanExtra("is_organizer", false);
        eventId = intent.getStringExtra("username_id");
        Realm realm = Realm.getDefaultInstance();
        user = realm.where(User.class).equalTo("username", eventId).findFirst();
        user = realm.copyFromRealm(user);
        realm.close();

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

        mFirstName = (EditText) findViewById(R.id.input__user_firstname);
        mFirstName.setText(user.getFullName());

        mLastName = (EditText) findViewById(R.id.input_user_lastname);
        mLastName.setText(user.getFn());

        mEmail = (EditText) findViewById(R.id.input_user_email);
        mEmail.setText(user.getEmail());

        mPhoneNumber = (EditText) findViewById(R.id.input_user_phonenumber);
        mPhoneNumber.setText(user.getPhone());

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

    }

    public void btn_click_birthday(View view) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                UserActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+" "+(monthOfYear)+" "+year;
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
}
