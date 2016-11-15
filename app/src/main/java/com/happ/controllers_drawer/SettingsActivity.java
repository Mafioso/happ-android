package com.happ.controllers_drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.R;
import com.happ.controllers.PrivacyPolicyActivity;
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
            mBtnCitiesManager,
            mBtnContactHapp,
            mBtnFaqHelp,
            mBtnTermsOfService,
            mBtnPrivacyPolice;
    private ImageView mCloseLeftNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        mCloseLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);

        //Buttons
            mBtnProfile = (Button) findViewById(R.id.btn_profile_settings);
            mBtnPushNotif = (Button) findViewById(R.id.btn_push_notif);
            mBtnCitiesManager = (Button) findViewById(R.id.btn_cities_manager);
            mBtnContactHapp = (Button) findViewById(R.id.btn_contact_happ);
            mBtnFaqHelp = (Button) findViewById(R.id.btn_faq);
            mBtnTermsOfService = (Button) findViewById(R.id.btn_terms_service);
            mBtnPrivacyPolice = (Button) findViewById(R.id.btn_privacy_policy);



        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_grey);
        actionBar.setDisplayHomeAsUpEnabled(true);

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(SettingsActivity.this);
                }
                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);

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

                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        navigationMenu.getMenu().findItem(R.id.nav_item_settings).setChecked(true);
        navigationMenu.getMenu().findItem(R.id.nav_item_settings).setIcon(R.drawable.happ_drawer_icon);

        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());

        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });


        cityPageAdapter = new MyCityPageAdapter(getSupportFragmentManager());
        mDrawerCityFragment.setAdapter(cityPageAdapter);

        ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow)).isChecked()) {
                    mDrawerCityFragment.setVisibility(View.VISIBLE);
                } else {
                    mDrawerCityFragment.setVisibility(View.GONE);
                }
            }
        });

        mBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UserActivity.class);
                i.putExtra("from_settings", true);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
            }
        });

        mBtnPushNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "Push Notifications", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnCitiesManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "Cities Manager", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnContactHapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "Contact Happ", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnFaqHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "Faq / Help", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "Terms of Service", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnPrivacyPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                i.putExtra("from_settings", true);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
            }
        });

        mCloseLeftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(navigationView);
            }
        });



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
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
