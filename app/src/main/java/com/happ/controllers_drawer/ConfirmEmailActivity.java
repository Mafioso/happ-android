package com.happ.controllers_drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.BuildConfig;
import com.happ.LockableScrollView;
import com.happ.R;
import com.happ.controllers.HtmlPageAcitivty;
import com.happ.controllers.UserActivity;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.User;
import com.happ.retrofit.HappRestClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Random;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 11/15/16.
 */
public class ConfirmEmailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView, navigationMenu, navigationHeader;
    private ImageView mCLoseLeftNavigation;
    private RelativeLayout mFooterView;
    private ImageView mImageLogo;
    private TextView mTVConfirmEmail;
    private Button mBtnConfirmEmail;
    private RelativeLayout mRLDoneForm;

    private int[] login_bg = {
            R.drawable.login_bg_1,
            R.drawable.login_bg_2,
            R.drawable.login_bg_3,
            R.drawable.login_bg_4,
            R.drawable.login_bg_5,
            R.drawable.login_bg_6,
            R.drawable.login_bg_7,
            R.drawable.login_bg_8,
            R.drawable.login_bg_9,
            R.drawable.login_bg_10,
    };

    int idx = new Random().nextInt(login_bg.length);
    int randomBg = login_bg[idx];

    private boolean isKeyboarShown = false;
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    private CheckBox mDrawerHeaderArrow;
    private TextView mDrawerHeaderTVCity, mDrawerHeaderTVUsername;
    private AppCompatImageView mIVbg;
    private TextView mTVPrivacyPolicy, mTVTermsPolicy;
    private LinearLayout mDrawerLLFooter;
    private MaterialProgressBar mProgressBar;
    private TextView mDrawerVersionApp;
    private EditText mEditTextConfirmEmail;

    private ImageView mDrawerHeaderAvatar;
    private RelativeLayout mDrawerHeaderAvatarPlaceholder;
    private BroadcastReceiver changeCityDoneReceiver;

    private User user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_email);

        user = App.getCurrentUser();

        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        mBtnConfirmEmail = (Button) findViewById(R.id.btn_confirm_email);
        mIVbg = (AppCompatImageView) findViewById(R.id.iv_confirm_email_bg);
        mCLoseLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);
        mFooterView = (RelativeLayout) findViewById(R.id.rl_footer);
        mImageLogo = (ImageView) findViewById(R.id.img_logo);
        mTVPrivacyPolicy = (TextView) findViewById(R.id.tv_privacy_policy);
        mTVTermsPolicy = (TextView) findViewById(R.id.tv_terms_and_policy);
        mTVConfirmEmail = (TextView) findViewById(R.id.tv_confirm_email);
        mDrawerLLFooter = (LinearLayout) findViewById(R.id.ll_drawer_footer);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.circular_progress_confirm_email);
        mEditTextConfirmEmail = (EditText) findViewById(R.id.input_confirm_mail);

        mDrawerHeaderArrow = ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow));
        mDrawerHeaderTVCity = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city));
        mDrawerHeaderTVUsername = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username));
        mDrawerVersionApp = (TextView) findViewById(R.id.tv_drawer_version_app);
        String versionName = BuildConfig.VERSION_NAME;
        mDrawerVersionApp.setText(getResources().getString(R.string.app_name) + " " + "v" + versionName);
        mDrawerHeaderAvatar = ((ImageView)navigationHeader.getHeaderView(0).findViewById(R.id.dr_iv_user_avatar));
        mDrawerHeaderAvatarPlaceholder = ((RelativeLayout)navigationHeader.getHeaderView(0).findViewById(R.id.dr_avatar_placeholder));

        mRLDoneForm = (RelativeLayout) findViewById(R.id.rl_form_response_done);
        mRLDoneForm.setVisibility(View.GONE);

        mIVbg.setImageResource(randomBg);
        LockableScrollView sv = (LockableScrollView)findViewById(R.id.fake_scrollview);
        sv.setScrollingEnabled(false);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        setTitle("");
        if (user.getEmail() != null) {
            mEditTextConfirmEmail.setText(user.getEmail());
        }

        mBtnConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mEditTextConfirmEmail.getText().toString().equals("")) {
                    Toast.makeText(ConfirmEmailActivity.this, getResources().getString(R.string.enter_your_email), Toast.LENGTH_SHORT).show();
                } else {
                    HappRestClient.getInstance().getConfirmEmail();
                    mProgressBar.setVisibility(View.VISIBLE);
                    mRLDoneForm.setVisibility(View.VISIBLE);

                }
            }
        });

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.nav_item_feed) {
                Intent goToFeedIntent = new Intent(ConfirmEmailActivity.this, FeedActivity.class);
                goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToFeedIntent);
                overridePendingTransition(0,0);
            }

            if (menuItem.getItemId() == R.id.nav_item_interests) {
                Intent intent = new Intent(ConfirmEmailActivity.this, SelectInterestsActivity.class);
                intent.putExtra("is_full", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0);
            }

            if (menuItem.getItemId() == R.id.nav_item_settings) {
                Intent goToFeedIntent = new Intent(ConfirmEmailActivity.this, SettingsActivity.class);
                goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToFeedIntent);
                overridePendingTransition(0,0);

            }

            if (menuItem.getItemId() == R.id.nav_item_logout) {
                App.doLogout(ConfirmEmailActivity.this);
            }

            if (menuItem.getItemId() == R.id.nav_item_share_app) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.drawer_share_subject));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.drawer_share_text));
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_happ_to)));
            }
            mDrawerLayout.closeDrawers();
            return true;
            }
        });

        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullname());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());

        mDrawerHeaderTVUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmEmailActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });


        mCLoseLeftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(navigationView);
            }
        });

        cityPageAdapter = new MyCityPageAdapter(getSupportFragmentManager());
        mDrawerCityFragment.setAdapter(cityPageAdapter);

        mDrawerHeaderArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerHeaderArrow.isChecked()) {
                    mDrawerCityFragment.setVisibility(View.VISIBLE);
                    mDrawerCityFragment.setAdapter(cityPageAdapter);
                } else {
                    mDrawerCityFragment.setVisibility(View.GONE);
                }
            }
        });

        mTVTermsPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotToTermsOfService = new Intent(getApplicationContext(), HtmlPageAcitivty.class);
                gotToTermsOfService.putExtra("link_terms_of_service", true);
                startActivity(gotToTermsOfService);
            }
        });

        mTVPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToPrivacyPolicy = new Intent(getApplicationContext(), HtmlPageAcitivty.class);
                goToPrivacyPolicy.putExtra("link_privacy_policy", true);
                startActivity(goToPrivacyPolicy);
            }
        });

        setListenerToRootView();
        setDrawerLayoutListener();
        setSpannableString();
        setDrawerHeaderAvatar();

        if (changeCityDoneReceiver == null) {
            changeCityDoneReceiver = changeCityReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changeCityDoneReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
        }
    }

    private void setDrawerHeaderAvatar() {
        if (App.getCurrentUser().getAvatar() != null) {
            String url = App.getCurrentUser().getAvatar().getUrl();
            mDrawerHeaderAvatar.setVisibility(View.VISIBLE);
            Picasso.with(App.getContext())
                    .load(url)
                    .fit()
                    .centerCrop()
                    .into(mDrawerHeaderAvatar, new Callback() {
                        @Override
                        public void onSuccess() {
                            mDrawerHeaderAvatarPlaceholder.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            mDrawerHeaderAvatarPlaceholder.setVisibility(View.VISIBLE);
                        }
                    });


        } else {
            mDrawerHeaderAvatar.setVisibility(View.GONE);
            mDrawerHeaderAvatarPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    private void setSpannableString() {
        SpannableString spanPrivacyPolicyString = new SpannableString(mTVPrivacyPolicy.getText());
        spanPrivacyPolicyString.setSpan(new UnderlineSpan(), 0, spanPrivacyPolicyString.length(), 0);
        mTVPrivacyPolicy.setText(spanPrivacyPolicyString);

        SpannableString spanTermsPolicyString = new SpannableString(mTVTermsPolicy.getText());
        spanTermsPolicyString.setSpan(new UnderlineSpan(), 0, spanTermsPolicyString.length(), 0);
        mTVTermsPolicy.setText(spanTermsPolicyString);
    }

    public void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (mKeyboardListener == null) {
            mKeyboardListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Rect r = new Rect();
                    activityRootView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = activityRootView.getRootView().getHeight();

                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        if (!isKeyboarShown) {
                            navigationHeader.setVisibility(View.GONE);
                            mFooterView.setVisibility(View.GONE);
                            mImageLogo.setVisibility(View.GONE);
                            mTVConfirmEmail.setVisibility(View.GONE);
                            mDrawerLLFooter.setVisibility(View.GONE);
                        }
                        isKeyboarShown = true;
                    }
                    else {
                        navigationHeader.setVisibility(View.VISIBLE);
                        mFooterView.setVisibility(View.VISIBLE);
                        mImageLogo.setVisibility(View.VISIBLE);
                        mTVConfirmEmail.setVisibility(View.VISIBLE);
                        mDrawerLLFooter.setVisibility(View.VISIBLE);

                        isKeyboarShown = false;
                    }
                }
            };
        } else {
            activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardListener);
        }
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(mKeyboardListener);
    }

    private void setDrawerLayoutListener() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    public class MyCityPageAdapter extends FragmentPagerAdapter {

        public MyCityPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SelectCityFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setDrawerHeaderAvatar();

        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullname());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());
    }

    private BroadcastReceiver changeCityReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());
                mDrawerHeaderArrow.setChecked(false);
                mDrawerCityFragment.setVisibility(View.GONE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        };
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (changeCityDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(changeCityDoneReceiver);
            changeCityDoneReceiver = null;
        }
    }
}
