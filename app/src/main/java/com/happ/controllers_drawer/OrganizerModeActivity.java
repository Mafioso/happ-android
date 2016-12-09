package com.happ.controllers_drawer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.happ.App;
import com.happ.R;
import com.happ.controllers.EditCreateActivity;
import com.happ.controllers.HtmlPageAcitivty;
import com.happ.controllers.UserActivity;
import com.happ.fragments.EventsOrganizerFragment;
import com.happ.fragments.SelectCityFragment;
import com.happ.retrofit.APIService;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

public class OrganizerModeActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;
    private Menu menu;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationMenu, navigationHeader, navigationViewRightOrg, navigationView;
    private SharedPreferences sPref;
    final String FIRST_CREATE_EVENT = "first_create_event";
    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;
    private BottomBar mBottomBar;
    private FragNavController fragNavController;
    private ImageView mCloseRightNavigation, mCLoserLeftNavigation;

    private CheckBox mDrawerHeaderArrow;
    private TextView mDrawerHeaderTVCity, mDrawerHeaderTVUsername;

    private final int TAB_MY_EVENTS = FragNavController.TAB1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizermode);


        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        navigationViewRightOrg = (NavigationView) findViewById(R.id.navigation_view_right_org);
        toolbar = (Toolbar) findViewById(R.id.ll_toolbar);
        mBottomBar = (BottomBar) findViewById(R.id.bottombar_org);
        mCloseRightNavigation = (ImageView) findViewById(R.id.close_right_org_navigation);
        mCLoserLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);
        mDrawerHeaderArrow = ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow));
        mDrawerHeaderTVCity = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city));
        mDrawerHeaderTVUsername = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username));


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        setTitle(R.string.organizer_title);

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(OrganizerModeActivity.this);
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(OrganizerModeActivity.this, SettingsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0, 0);
                }

//                if (menuItem.getItemId() == R.id.nav_item_organizer) {
//                    Intent goToFeedIntent = new Intent(OrganizerModeActivity.this, OrganizerModeActivity.class);
//                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(goToFeedIntent);
//                    overridePendingTransition(0, 0);
//                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(OrganizerModeActivity.this, SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }

                if (menuItem.getItemId() == R.id.nav_item_feed) {
                    Intent intent = new Intent(OrganizerModeActivity.this, FeedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }


                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        navigationMenu.getMenu().findItem(R.id.nav_item_organizer).setChecked(true);
        navigationMenu.getMenu().findItem(R.id.nav_item_organizer).setIcon(R.drawable.happ_drawer_icon);

        ((TextView) navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView) navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());

        ((TextView) navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerModeActivity.this, UserActivity.class);
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
                } else {
                    mDrawerCityFragment.setVisibility(View.GONE);
                }
            }
        });

        mCloseRightNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(navigationViewRightOrg);
            }
        });
        mCLoserLeftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(navigationView);
            }
        });


        ArrayList<Fragment> fragments = new ArrayList<>(1);
        fragments.add(EventsOrganizerFragment.newInstance());
        fragNavController = new FragNavController(getSupportFragmentManager(),R.id.org_container,fragments);

        mBottomBar.setDefaultTabPosition(2);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_analytics:
                        Toast.makeText(OrganizerModeActivity.this, "Analytics", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.tab_pro_function:
                        Toast.makeText(OrganizerModeActivity.this, "Pro-functions", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.tab_my_events:
                        fragNavController.switchTab(TAB_MY_EVENTS);
                        break;

                    case R.id.tab_add_event:

                        sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
                        String name = sPref.getString("first_create_event", "");

                        if (name == null || name.length() == 0) {
                            sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
                            sPref.edit().putString("first_create_event", FIRST_CREATE_EVENT).apply();

                            Intent i = new Intent(OrganizerModeActivity.this, HtmlPageAcitivty.class);
                            i.putExtra("from_organizermode", true);
                            startActivity(i);

                        } else {
                            Intent i = new Intent(getApplicationContext(), EditCreateActivity.class);
                            startActivity(i);
                        }

                        break;
                    case R.id.tab_chat:
                        Toast.makeText(OrganizerModeActivity.this, "TAB_CHAT", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_analytics) {
                    fragNavController.clearStack();
                }
                if (tabId == R.id.tab_pro_function) {
                    fragNavController.clearStack();
                }
                if (tabId == R.id.tab_my_events) {
                    fragNavController.clearStack();
                }
                if (tabId == R.id.tab_add_event) {
                    fragNavController.clearStack();
                }
                if (tabId == R.id.tab_chat) {
                    fragNavController.clearStack();
                }
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


    public void functionToRun(final String eventId) {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.delete_confirm_title))
                .content(getResources().getString(R.string.delete_confirm_description))
                .positiveText(getResources().getString(R.string.agree))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        APIService.doEventDelete(eventId);
                    }
                })
                .negativeText(getResources().getString(R.string.cancel))
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_feed, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_filter:
                mDrawerLayout.openDrawer(navigationViewRightOrg);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView) navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
           
           