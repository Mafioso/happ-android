package com.happ.controllers_drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
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
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.BuildConfig;
import com.happ.R;
import com.happ.controllers.ChangeCurrencyActivity;
import com.happ.controllers.HtmlPageAcitivty;
import com.happ.controllers.UserActivity;
import com.happ.fragments.SelectCityFragment;

/**
 * Created by dante on 9/22/16.
 */
public class SettingsActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView, navigationHeader, navigationMenu;
    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;

    private Button mBtnProfile,
            mBtnPushNotif,
            mBtnContactHapp,
            mBtnFaqHelp,
            mBtnTermsOfService,
            mBtnPrivacyPolice,
            mBtnChangeCurrency;

    private ImageView mCloseLeftNavigation;
    private boolean isKeyboarShown = false;
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;

    private CheckBox mDrawerHeaderArrow;
    private TextView mDrawerHeaderTVCity, mDrawerHeaderTVUsername;
    protected BroadcastReceiver changeCityDoneReceiver;
    private LinearLayout mDrawerLLFooter;
    private TextView mDrawerVersionApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.ll_toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        mCloseLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);
        mDrawerLLFooter = (LinearLayout) findViewById(R.id.ll_drawer_footer);

        mDrawerHeaderArrow = ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow));
        mDrawerHeaderTVCity = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city));
        mDrawerHeaderTVUsername = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username));

        mBtnProfile = (Button) findViewById(R.id.btn_profile_settings);
        mBtnChangeCurrency = (Button) findViewById(R.id.btn_change_currency);
        mBtnPrivacyPolice = (Button) findViewById(R.id.btn_privacy_policy);
        mBtnTermsOfService = (Button) findViewById(R.id.btn_terms_service);
//        mBtnPushNotif = (Button) findViewById(R.id.btn_push_notif);
        mBtnContactHapp = (Button) findViewById(R.id.btn_contact_happ);
        mBtnFaqHelp = (Button) findViewById(R.id.btn_faq);
        mDrawerVersionApp = (TextView) findViewById(R.id.tv_drawer_version_app);
        String versionName = BuildConfig.VERSION_NAME;
        mDrawerVersionApp.setText(getResources().getString(R.string.app_name) + " " + "v" + versionName);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_gray);
        }

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(SettingsActivity.this);
                }

                if (menuItem.getItemId() == R.id.nav_item_organizer) {
                    Intent goToFeedIntent = new Intent(SettingsActivity.this, ConfirmEmailActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(SettingsActivity.this, SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);

                }

                if (menuItem.getItemId() == R.id.nav_item_feed) {
                    Intent intent = new Intent(SettingsActivity.this, FeedActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
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

        navigationMenu.getMenu().findItem(R.id.nav_item_settings).setChecked(true);
        navigationMenu.getMenu().findItem(R.id.nav_item_settings).setIcon(R.drawable.happ_drawer_icon);

        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullname());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());

        mDrawerHeaderTVUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, UserActivity.class);
                startActivity(intent);
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

        mCloseLeftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(navigationView);
            }
        });

        setDrawerLayoutListener();
        setListenerToRootView();

        if (changeCityDoneReceiver == null) {
            changeCityDoneReceiver = changeCityReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changeCityDoneReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
        }

    }

    public void buttonOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile_settings:
                Intent goToProfile = new Intent(getApplicationContext(), UserActivity.class);
                goToProfile.putExtra("from_settings", true);
                startActivity(goToProfile);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                break;

//            case R.id.btn_push_notif:
//                break;

            case R.id.btn_change_currency:
                Intent goToChangeCurrency = new Intent(getApplicationContext(), ChangeCurrencyActivity.class);
                goToChangeCurrency.putExtra("from_settings", true);
                startActivity(goToChangeCurrency);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                break;

            case R.id.btn_contact_happ:
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.settings_subject_contact)
                        + " " +
                        App.getCurrentUser().getFullname() + App.getCurrentUser().getFn());
                i.setData(Uri.parse("mailto:" + getResources().getString(R.string.happ_email)));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SettingsActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_faq:
                Toast.makeText(SettingsActivity.this, "FAQ Page", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_terms_service:
                Intent gotToTermsOfService = new Intent(getApplicationContext(), HtmlPageAcitivty.class);
                gotToTermsOfService.putExtra("from_settings", true);
                gotToTermsOfService.putExtra("link_terms_of_service", true);
                startActivity(gotToTermsOfService);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                break;

            case R.id.btn_privacy_policy:
                Intent goToPrivacyPolicy = new Intent(getApplicationContext(), HtmlPageAcitivty.class);
                goToPrivacyPolicy.putExtra("from_settings", true);
                goToPrivacyPolicy.putExtra("link_privacy_policy", true);
                startActivity(goToPrivacyPolicy);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                break;
        }
    }

    private void setListenerToRootView() {
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
                            mDrawerLLFooter.setVisibility(View.GONE);
                        }
                        isKeyboarShown = true;
                    }
                    else {
                        navigationHeader.setVisibility(View.VISIBLE);
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
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0,0) {

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
    protected void onResume() {
        super.onResume();
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
