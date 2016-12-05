package com.happ.controllers_drawer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.R;
import com.happ.controllers.UserActivity;
import com.happ.fragments.SelectCityFragment;

import java.util.Random;

/**
 * Created by dante on 11/15/16.
 */
public class ConfirmEmailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView, navigationMenu, navigationHeader;
    private RelativeLayout mRLbg;

    private Button mBtnConfirmEmail;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_email);


        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        mBtnConfirmEmail = (Button) findViewById(R.id.btn_confirm_email);
        mRLbg = (RelativeLayout) findViewById(R.id.rl_confirm_email_bg);

        mRLbg.setBackground(ContextCompat.getDrawable(App.getContext(), randomBg));

        mDrawerHeaderArrow = ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow));
        mDrawerHeaderTVCity = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city));
        mDrawerHeaderTVUsername = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username));

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_grey);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
        setTitle("");

        mBtnConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToOrgEvent = new Intent(ConfirmEmailActivity.this, OrganizerModeActivity.class);
                goToOrgEvent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToOrgEvent);
                overridePendingTransition(0,0);
            }
        });

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(ConfirmEmailActivity.this);
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(ConfirmEmailActivity.this, SettingsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);

                }
//                if (menuItem.getItemId() == R.id.nav_item_organizer) {
//                    Intent goToFeedIntent = new Intent(ConfirmEmailActivity.this, OrganizerModeActivity.class);
//                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(goToFeedIntent);
//                    overridePendingTransition(0,0);
//                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(ConfirmEmailActivity.this, SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);

                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

//        navigationMenu.getMenu().findItem(R.id.nav_item_feed).setChecked(true);
//        navigationMenu.getMenu().findItem(R.id.nav_item_feed).setIcon(R.drawable.happ_drawer_icon);

        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullName());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());

        mDrawerHeaderTVUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmEmailActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });


        cityPageAdapter = new MyCityPageAdapter(getSupportFragmentManager());
        mDrawerCityFragment.setAdapter(cityPageAdapter);

        mDrawerHeaderArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerHeaderArrow.isChecked()) {
                    mDrawerCityFragment.animate()
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    mDrawerCityFragment.setVisibility(View.VISIBLE);
                                }
                            })
                            .alpha(1.0f)
                            .translationY(0.0f)
                            .setDuration(2000);
                } else {
                    mDrawerCityFragment.animate()
                            .alpha(0.0f)
                            .translationY(700.0f)
                            .setDuration(2000)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mDrawerCityFragment.setVisibility(View.GONE);
                                    navigationMenu.animate()
                                            .alpha(1.0f)
                                            .setDuration(2000)
                                            .translationX(0.0f)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);
                                                    navigationMenu.setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            });
                }
            }
        });

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
                        }
                        isKeyboarShown = true;
                    }
                    else {
                        navigationHeader.setVisibility(View.VISIBLE);
                        isKeyboarShown = false;
                    }
                }
            };
        } else {
            activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardListener);
        }
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(mKeyboardListener);
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
}
