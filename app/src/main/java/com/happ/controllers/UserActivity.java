package com.happ.controllers;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.retrofit.APIService;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dante on 9/22/16.
 */
public class UserActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;
    private NestedScrollView mScrollView;
    private CoordinatorLayout mRootLayout;
    private CollapsingToolbarLayout ctl;
    private AppBarLayout mAppBarLayout;

    private EditText mUsername, mEmail, mPhoneNumber, mBirthday;
    private Date mDateBirthday;
    private Button mUserSave;
    private ImageView mBtnEditBirthday, mBtnEditPhoto;
    private ImageView mUserPhoto;
    private RadioButton mMale, mFemale;

    private BroadcastReceiver setUserEditOKReceiver;
    private boolean fromSettings = false;


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

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
//        int height = display.getHeight();  // deprecated

        mRootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        mScrollView = (NestedScrollView) findViewById(R.id.event_edit_srollview);
        toolbar = (Toolbar) findViewById(R.id.ll_toolbar);
        setSupportActionBar(toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        ctl = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        mUsername = (EditText) findViewById(R.id.input__user_username);
        mPhoneNumber = (EditText) findViewById(R.id.input_user_phone);
        mEmail = (EditText) findViewById(R.id.input_user_email);
        mBirthday = (EditText) findViewById(R.id.input_user_birthday);
        mUserSave = (Button) findViewById(R.id.btn_user_save);
        mBtnEditBirthday = (ImageView) findViewById(R.id.iv_edit_birthday);
        mBtnEditPhoto = (ImageView) findViewById(R.id.edit_user_photo);
        mMale = (RadioButton) findViewById(R.id.btn_user_male);
        mFemale = (RadioButton) findViewById(R.id.btn_user_female);
        mUserPhoto = (ImageView) findViewById(R.id.iv_user_avatar);

        mUserPhoto.setMaxHeight(width);
        mUserPhoto.setMinimumHeight(width);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (fromSettings) {
                    UserActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
                }
            }
        });

        if (App.getCurrentUser() != null) {

            mUsername.setText(App.getCurrentUser().getFullName());
            mEmail.setText(App.getCurrentUser().getEmail());
            mPhoneNumber.setText(App.getCurrentUser().getPhone());
            mDateBirthday = App.getCurrentUser().getBirthDate();
//            if (mDateBirthday == null) {
//                Calendar cal = Calendar.getInstance();
//                int currentYear = cal.get(Calendar.YEAR);
//                cal.set(Calendar.YEAR, currentYear-18);
//                mDateBirthday = cal.getTime();
//            }
//            java.text.DateFormat format = DateFormat.getLongDateFormat(this);
//            mBirthday.setText(format.format(mDateBirthday));

            mBtnEditBirthday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (mDateBirthday == null) mDateBirthday = new Date();
//                    Calendar now = Calendar.getInstance();
//                    now.setTime(mDateBirthday);

                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(UserActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, year);
                            cal.set(Calendar.MONTH, monthOfYear);
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mDateBirthday = cal.getTime();

                            //Set DateBirthday in EditText
                            java.text.DateFormat format = DateFormat.getLongDateFormat(UserActivity.this);
                            mBirthday.setText(format.format(mDateBirthday));
                        }
                    }, year, month, day);
//                    Calendar cc = Calendar.getInstance();
//                    cc.set(2014, 4, 22);
//                    datePickerDialog.getDatePicker().setMinDate(cc.getTimeInMillis());
                    datePickerDialog.show();
                }
            });

            mMale.setChecked(App.getCurrentUser().getGender() == 0);
            mFemale.setChecked(App.getCurrentUser().getGender() > 0);

            mBtnEditPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(UserActivity.this, "Change Photo", Toast.LENGTH_SHORT).show();
                }
            });


            mUserSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int colorFrom = ContextCompat.getColor(App.getContext(), R.color.light_gray);
                    int colorTo = ContextCompat.getColor(App.getContext(), R.color.bg_light);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(250); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            mUserSave.setBackgroundColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();

                    hideSoftKeyboard(UserActivity.this, v);
                    int gender = 0;
                    if (mFemale.isChecked()) gender = 1;
                    APIService.doUserEdit(mUsername.getText().toString(), mEmail.getText().toString(), mPhoneNumber.getText().toString(), mDateBirthday, gender);
                }
            });
        }

        if (setUserEditOKReceiver == null) {
            setUserEditOKReceiver = createSetUserEditOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(setUserEditOKReceiver, new IntentFilter(BroadcastIntents.USEREDIT_REQUEST_OK));
        }


        initViews();

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (setUserEditOKReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(setUserEditOKReceiver);
            setUserEditOKReceiver = null;
        }
    }

    private enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private void initViews() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private State state;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != State.EXPANDED) {
                        if (fromSettings) {
                            ctl.setExpandedTitleColor(Color.TRANSPARENT);
                            toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
                        } else {
                            ctl.setExpandedTitleColor(Color.TRANSPARENT);
                            toolbar.setNavigationIcon(R.drawable.ic_close_white);
                        }
                    }
                    state = State.EXPANDED;
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != State.COLLAPSED) {
                        if (fromSettings) {
                            ctl.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorElementsToolbar));
                            toolbar.setNavigationIcon(R.drawable.ic_right_arrow_grey);
                        } else {
                            ctl.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorElementsToolbar));
                            toolbar.setNavigationIcon(R.drawable.ic_close_grey);
                        }
                    }
                    state = State.COLLAPSED;
                } else {
                    if (state != State.IDLE) {
                        if (fromSettings) {
                            ctl.setExpandedTitleColor(Color.TRANSPARENT);
                            toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
                        } else {
                            ctl.setExpandedTitleColor(Color.TRANSPARENT);
                            toolbar.setNavigationIcon(R.drawable.ic_close_white);
                        }
                    }
                    state = State.IDLE;
                }
            }
        });
    }

}
