package com.happ.controllers;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.User;
import com.happ.retrofit.APIService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 9/22/16.
 */
public class UserActivity extends AppCompatActivity implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 2;
    private static final int SELECT_PICTURE = 1;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;
    private NestedScrollView mScrollView;
    private CoordinatorLayout mRootLayout;
    private CollapsingToolbarLayout ctl;
    private AppBarLayout mAppBarLayout;

    private EditText mUsername, mEmail, mPhoneNumber, mBirthday;
    private EditText mOldPassword, mNewPassword, mReapeatNewPassword;
    private Date mDateBirthday;
    private Button mUserSave;
    private ImageView mBtnEditBirthday;
    private ImageButton mBtnEditPhoto;
    private ImageView mUserPhoto;
    private RadioButton mMale, mFemale;

    private BroadcastReceiver setUserEditOKReceiver;
    private BroadcastReceiver imageUploadedReceiver;
    private BroadcastReceiver createCurrentUserReceiver;
    private BroadcastReceiver changePasswordUserOKReceiver;
    private BroadcastReceiver changePasswordUserFAILReceiver;

    private boolean fromSettings = false;


    private User user;
    private RelativeLayout mAvatarPlaceholder;

    private int arrowDrawable;
    private int closeDrawable;
    private AppBarLayout.OnOffsetChangedListener offsetChangedListener;
    private String imageId;
    private MaterialProgressBar mUserAvatarProgress;


    private Bitmap newImageBitmap;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (fromSettings) {
            UserActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
        }
        if (offsetChangedListener != null) {
            mAppBarLayout.removeOnOffsetChangedListener(offsetChangedListener);
            offsetChangedListener = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSettings = getIntent().getBooleanExtra("from_settings", false);
        user = App.getCurrentUser();
        setContentView(R.layout.activity_user);

        Realm realm = Realm.getDefaultInstance();


        final Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
//        int height = display.getHeight();  // deprecated

        mRootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        mScrollView = (NestedScrollView) findViewById(R.id.event_edit_srollview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        ctl = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        mUsername = (EditText) findViewById(R.id.input__user_username);
        mPhoneNumber = (EditText) findViewById(R.id.input_user_phone);
        mEmail = (EditText) findViewById(R.id.input_user_email);
        mBirthday = (EditText) findViewById(R.id.input_user_birthday);
        mUserSave = (Button) findViewById(R.id.btn_user_save);
        mBtnEditBirthday = (ImageView) findViewById(R.id.iv_edit_birthday);
        mBtnEditPhoto = (ImageButton) findViewById(R.id.add_image_button);
        mMale = (RadioButton) findViewById(R.id.btn_user_male);
        mFemale = (RadioButton) findViewById(R.id.btn_user_female);
        mUserPhoto = (ImageView) findViewById(R.id.iv_user_avatar);
        mAvatarPlaceholder = (RelativeLayout) findViewById(R.id.avatar_placeholder);

        mOldPassword = (EditText) findViewById(R.id.old_password);
        mNewPassword = (EditText) findViewById(R.id.new_password);
        mReapeatNewPassword = (EditText) findViewById(R.id.repeat_new_password);
        mUserAvatarProgress = (MaterialProgressBar) findViewById(R.id.user_avatar_progress);

        if (user.getAvatar() != null) {
            imageId = user.getAvatar().getPath();
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



        if (user != null) {

            mUsername.setText(user.getFullname());

            if (user.getEmail() != null) mEmail.setText(user.getEmail());

            if (user.getPhone() != null) mPhoneNumber.setText(user.getPhone());

            if (user.getDate_of_birth() != null) {
                mDateBirthday = user.getDate_of_birth();
                java.text.DateFormat format = DateFormat.getLongDateFormat(this);
                mBirthday.setText(format.format(mDateBirthday));
            }


            mBtnEditBirthday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Calendar now = Calendar.getInstance();
                    if (mDateBirthday != null) {
                        now.setTime(mDateBirthday);
                    } else {
                        now.setTime(new Date());
                    }

                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            UserActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH )
                    );

                    dpd.show(getFragmentManager(), "Datepickerdialog");

                }
            });

            mMale.setChecked(user.getGender() == 0);
            mFemale.setChecked(user.getGender() > 0);

            arrowDrawable = R.drawable.ic_right_arrow_grey;
            closeDrawable = R.drawable.ic_close_grey;

            if (user.getAvatar() == null) {
                mUserPhoto.setVisibility(View.GONE);
                mAvatarPlaceholder.setVisibility(View.VISIBLE);
            } else {
                Picasso.with(App.getContext())
                        .load(user.getAvatar().getPath())
                        .fit()
                        .centerCrop()
                        .into(mUserPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                photoDidSet();
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }


            View.OnClickListener openGalleryListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            };

            mBtnEditPhoto.setOnClickListener(openGalleryListener);
            mAvatarPlaceholder.setOnClickListener(openGalleryListener);


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
                    if (!mOldPassword.getText().toString().equals("") && !mNewPassword.getText().toString().equals("")) {
                        if (mNewPassword.getText().toString().equals(mReapeatNewPassword.getText().toString())) {
                            APIService.doChangePassword(mOldPassword.getText().toString(), mNewPassword.getText().toString());
                        } else {
                            Toast.makeText(UserActivity.this, R.string.password_dont_match, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        int gender = 0;
                        if (mFemale.isChecked()) gender = 1;

                        if (mBirthday.getText().toString().equals("")
                                || mPhoneNumber.getText().toString().equals("")
                                || mEmail.getText().toString().equals("")) {

                            final Snackbar snackbar = Snackbar.make(mScrollView, getResources().getString(R.string.fields_not_filled), Snackbar.LENGTH_SHORT);
                            snackbar.setAction(getResources().getString(R.string.event_done_action), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();

                        } else {
                            APIService.doUserEdit(
                                    mUsername.getText().toString(),
                                    mEmail.getText().toString(),
                                    mPhoneNumber.getText().toString(),
                                    mDateBirthday,
                                    gender,
                                    imageId
                            );
                        }
                    }

                }
            });
        }

        if (setUserEditOKReceiver == null) {
            setUserEditOKReceiver = createSetUserEditOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(setUserEditOKReceiver, new IntentFilter(BroadcastIntents.USEREDIT_REQUEST_OK));
        }
        if (imageUploadedReceiver == null) {
            imageUploadedReceiver = createImageUploadedReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(imageUploadedReceiver, new IntentFilter(BroadcastIntents.IMAGE_UPLOAD_OK));
        }
        if (createCurrentUserReceiver == null) {
            createCurrentUserReceiver = createCurrentUserOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(createCurrentUserReceiver, new IntentFilter(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK));
        }
        if (changePasswordUserOKReceiver == null) {
            changePasswordUserOKReceiver = changePasswordOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changePasswordUserOKReceiver, new IntentFilter(BroadcastIntents.CHANGE_PW_REQUEST_OK));
        }
        if (changePasswordUserFAILReceiver == null) {
            changePasswordUserFAILReceiver = changePasswordFAILReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changePasswordUserFAILReceiver, new IntentFilter(BroadcastIntents.CHANGE_PW_REQUEST_FAIL));
        }

        initViews();
        APIService.getCurrentUser();

        mNewPassword.addTextChangedListener(mWatcher);
        mOldPassword.addTextChangedListener(mWatcher);

    }


    TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!mNewPassword.getText().toString().equals("") && !mOldPassword.getText().toString().equals("")) {
                mUserSave.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.colorAccentDark));
                mUserSave.setTextColor(ContextCompat.getColor(App.getContext(), R.color.bg_light));
                mUserSave.setText(getResources().getString(R.string.change_password));
            } else {
                mUserSave.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.bg_light));
                mUserSave.setTextColor(ContextCompat.getColor(App.getContext(), R.color.colorAccentDark));
                mUserSave.setText(getResources().getString(R.string.save));
            }
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

    private BroadcastReceiver createImageUploadedReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String savedId = intent.getStringExtra(BroadcastIntents.IMAGE_UPLOAD_EXTRA_ID);
                if (savedId != null) {
                    imageId = savedId;
                    mUserPhoto.setImageBitmap(newImageBitmap);
                    photoDidSet();
                    mUserAvatarProgress.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    private BroadcastReceiver createCurrentUserOKReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
    }
    private BroadcastReceiver changePasswordOKReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mOldPassword.setText("");
                mNewPassword.setText("");
                mReapeatNewPassword.setText("");

                final Snackbar snackbar = Snackbar.make(mScrollView, getResources().getString(R.string.user_password_updated), Snackbar.LENGTH_SHORT);
                snackbar.setAction(getResources().getString(R.string.event_done_action), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        };
    }
    private BroadcastReceiver changePasswordFAILReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final Snackbar snackbar = Snackbar.make(mScrollView, getResources().getString(R.string.error_400), Snackbar.LENGTH_SHORT);
                snackbar.setAction(getResources().getString(R.string.event_done_action), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        };
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, year);
                            cal.set(Calendar.MONTH, monthOfYear);
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mDateBirthday = cal.getTime();

                            //Set DateBirthday in EditText
                            java.text.DateFormat format = DateFormat.getLongDateFormat(UserActivity.this);
                            mBirthday.setText(format.format(mDateBirthday));
    }

    private void openGallery() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
        } else {
            readDataExternal();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    readDataExternal();
                }
                break;

            default:
                break;
        }
    }

    private void readDataExternal() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    public View getRootLayout() {
        return mRootLayout;
    }

    protected void updateUserData() {
        final Snackbar snackbar = Snackbar.make(mScrollView, getResources().getString(R.string.user_done), Snackbar.LENGTH_SHORT);
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
        user = App.getCurrentUser();

        if (user.getAvatar() == null) {
            mUserPhoto.setVisibility(View.GONE);
            mAvatarPlaceholder.setVisibility(View.VISIBLE);
        } else {
            photoDidSet();

            Picasso.with(App.getContext())
                    .load(user.getAvatar().getUrl())
                    .fit()
                    .centerCrop()
                    .into(mUserPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            photoDidSet();
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mUserAvatarProgress.setVisibility(View.VISIBLE);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
//                selectedImagePath = getPath(selectedImageUri);
                updateImage(selectedImageUri);
                APIService.uploadImage(selectedImageUri);
            }
        }
    }

    private void photoDidSet() {
        mUserPhoto.setVisibility(View.VISIBLE);
        mAvatarPlaceholder.setVisibility(View.GONE);
        arrowDrawable = R.drawable.ic_rigth_arrow;
        closeDrawable = R.drawable.ic_close_white;
        initViews();
    }

    private void updateImage(Uri uri) {
        if (uri == null) return;

        Bitmap bmp = null;
        try {
            bmp = getBitmapFromUri(uri);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newImageBitmap = bmp;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media
                .getBitmap(this.getContentResolver(), uri);
        return bitmap;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (setUserEditOKReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(setUserEditOKReceiver);
            setUserEditOKReceiver = null;
        }
        if (imageUploadedReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(imageUploadedReceiver);
            imageUploadedReceiver = null;
        }
    }


    private enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private void initViews() {
        if (offsetChangedListener != null) {
            mAppBarLayout.removeOnOffsetChangedListener(offsetChangedListener);
        }
        offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
            private State state;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != State.EXPANDED) {
                        if (fromSettings) {
                            ctl.setExpandedTitleColor(Color.TRANSPARENT);
                            toolbar.setNavigationIcon(arrowDrawable);
                        } else {
                            ctl.setExpandedTitleColor(Color.TRANSPARENT);
                            toolbar.setNavigationIcon(closeDrawable);
                        }
                    }
                    state = State.EXPANDED;
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != State.COLLAPSED) {
                        if (fromSettings) {
                            ctl.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorElementsToolbar));
                            toolbar.setNavigationIcon(arrowDrawable);
                        } else {
                            ctl.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorElementsToolbar));
                            toolbar.setNavigationIcon(closeDrawable);
                        }
                    }
                    state = State.COLLAPSED;
                } else {
                    if (state != State.IDLE) {
                        if (fromSettings) {
                            ctl.setExpandedTitleColor(Color.TRANSPARENT);
                            toolbar.setNavigationIcon(arrowDrawable);
                        } else {
                            ctl.setExpandedTitleColor(Color.TRANSPARENT);
                            toolbar.setNavigationIcon(closeDrawable);
                        }
                    }
                    state = State.IDLE;
                }
            }
        };

        mAppBarLayout.addOnOffsetChangedListener(offsetChangedListener);
    }

}
