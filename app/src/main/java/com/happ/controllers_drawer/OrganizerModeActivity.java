package com.happ.controllers_drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
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
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.BuildConfig;
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

public class OrganizerModeActivity extends AppCompatActivity implements FragNavController.TransactionListener, FragNavController.RootFragmentListener{

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
    private BroadcastReceiver changeCityDoneReceiver;
    private TextView mDrawerVersionApp;
    private EventsOrganizerFragment eventsOrganizerFragment;
    private boolean isKeyboarShown = false;
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    private LinearLayout mDrawerLLFooter;

    private SwitchCompat isActive, isInactive, isOnreview, isRejected, isFinished;
    private boolean
            is_active = false,
            is_inacitve = false,
            is_onreview = false,
            is_rejected = false,
            is_finished = false;

    private final int TAB_MY_EVENTS = FragNavController.TAB1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizermode);
        binds();
        setSupportActionBar(toolbar);
        setTitle(R.string.organizer_title);
        eventsOrganizerFragment = EventsOrganizerFragment.newInstance();
        setAllFunctionNavigationMenu();
        setListenerToRootView();
        setDrawerLayoutListener();

        isActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActive.isChecked()) {
                    is_active = true;
                    APIService.getFilteredOrgEvents(
                            1,
                            getFilterIsActive(),
                            getFilterIsInactive(),
                            getFilterIsOnreview(),
                            getFilterIsRejected(),
                            getFilterIsFinished());
                } else {
                    is_active = false;
                    APIService.getFilteredOrgEvents(
                            1,
                            getFilterIsActive(),
                            getFilterIsInactive(),
                            getFilterIsOnreview(),
                            getFilterIsRejected(),
                            getFilterIsFinished());
                }
            }
        });

//        isInactive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isInactive.isChecked()) {
//                    is_inacitve = true;
//                    APIService.getFilteredOrgEvents(
//                            1,
//                            getFilterIsActive(),
//                            getFilterIsInactive(),
//                            getFilterIsOnreview(),
//                            getFilterIsRejected(),
//                            getFilterIsFinished());
//                } else {
//                    is_inacitve = false;
//                    APIService.getFilteredOrgEvents(
//                            1,
//                            getFilterIsActive(),
//                            getFilterIsInactive(),
//                            getFilterIsOnreview(),
//                            getFilterIsRejected(),
//                            getFilterIsFinished());
//                }
//            }
//        });

        isOnreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnreview.isChecked()) {
                    is_onreview = true;
                    APIService.getFilteredOrgEvents(
                            1,
                            getFilterIsActive(),
                            getFilterIsInactive(),
                            getFilterIsOnreview(),
                            getFilterIsRejected(),
                            getFilterIsFinished());
                } else {
                    is_onreview = false;
                    APIService.getFilteredOrgEvents(
                            1,
                            getFilterIsActive(),
                            getFilterIsInactive(),
                            getFilterIsOnreview(),
                            getFilterIsRejected(),
                            getFilterIsFinished());
                }
            }
        });

//        isRejected.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isRejected.isChecked()) {
//                    is_rejected = true;
//                    APIService.getFilteredOrgEvents(
//                            1,
//                            getFilterIsActive(),
//                            getFilterIsInactive(),
//                            getFilterIsOnreview(),
//                            getFilterIsRejected(),
//                            getFilterIsFinished());
//                } else {
//                    is_rejected = false;
//                    APIService.getFilteredOrgEvents(
//                            1,
//                            getFilterIsActive(),
//                            getFilterIsInactive(),
//                            getFilterIsOnreview(),
//                            getFilterIsRejected(),
//                            getFilterIsFinished());
//                }
//            }
//        });

        isFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFinished.isChecked()) {
                    is_finished = true;
                    APIService.getFilteredOrgEvents(
                            1,
                            getFilterIsActive(),
                            getFilterIsInactive(),
                            getFilterIsOnreview(),
                            getFilterIsRejected(),
                            getFilterIsFinished());
                } else {
                    is_finished = false;
                    APIService.getFilteredOrgEvents(
                            1,
                            getFilterIsActive(),
                            getFilterIsInactive(),
                            getFilterIsOnreview(),
                            getFilterIsRejected(),
                            getFilterIsFinished());
                }
            }
        });

        if (changeCityDoneReceiver == null) {
            changeCityDoneReceiver = changeCityReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changeCityDoneReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
        }

    }


    @Override
    public Fragment getRootFragment(int index) {

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_gray);
        }

        switch (index) {
            case TAB_MY_EVENTS:
                return eventsOrganizerFragment;

        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public void onTabTransaction(Fragment fragment, int i) {
        if (fragNavController.canPop()){
            fragNavController.pop();
        }
    }

    @Override
    public void onFragmentTransaction(Fragment fragment) {
        if (fragNavController.canPop()){
            fragNavController.pop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fragNavController != null) {
            fragNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        this.menu = menu;

        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_filter_gray));

        fragNavController = new FragNavController(null, getSupportFragmentManager(),R.id.org_container,this,1, TAB_MY_EVENTS);
        fragNavController.setTransactionListener(this);
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

                        if (name.equals("") || name.length() == 0) {
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
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                fragNavController.clearStack();
            }
        });

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
                            mBottomBar.setVisibility(View.GONE);
                            mDrawerLLFooter.setVisibility(View.GONE);

                        }
                        isKeyboarShown = true;
                    }
                    else {
                        navigationHeader.setVisibility(View.VISIBLE);
                        mBottomBar.setVisibility(View.VISIBLE);
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

    private void binds() {
        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        navigationViewRightOrg = (NavigationView) findViewById(R.id.navigation_view_right_org);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mBottomBar = (BottomBar) findViewById(R.id.bottombar_org);
        mCloseRightNavigation = (ImageView) findViewById(R.id.close_right_org_navigation);
        mCLoserLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);
        mDrawerHeaderArrow = ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow));
        mDrawerHeaderTVCity = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city));
        mDrawerHeaderTVUsername = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username));
        mDrawerVersionApp = (TextView) findViewById(R.id.tv_drawer_version_app);
        mDrawerLLFooter = (LinearLayout) findViewById(R.id.ll_drawer_footer);

        isActive = (SwitchCompat) findViewById(R.id.filter_activ);
        isInactive = (SwitchCompat) findViewById(R.id.filter_inactive);
        isOnreview = (SwitchCompat) findViewById(R.id.filter_onreview);
        isFinished = (SwitchCompat) findViewById(R.id.filter_finished);
    }

    public boolean getFilterIsActive() {
        return is_active;
    }

    public boolean getFilterIsInactive() {
        return is_inacitve;
    }

    public boolean getFilterIsOnreview() {
        return is_onreview;
    }

    public boolean getFilterIsRejected() {
        return is_rejected;
    }

    public boolean getFilterIsFinished() {
        return is_finished;
    }

    private void setAllFunctionNavigationMenu() {
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
        navigationMenu.getMenu().findItem(R.id.nav_item_organizer).setChecked(true);
        navigationMenu.getMenu().findItem(R.id.nav_item_organizer).setIcon(R.drawable.happ_drawer_icon);

        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullname());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());

        String versionName = BuildConfig.VERSION_NAME;
        mDrawerVersionApp.setText(getResources().getString(R.string.app_name) + " " + "v" + versionName);

        mDrawerHeaderTVUsername.setOnClickListener(new View.OnClickListener() {
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
                    mDrawerCityFragment.setAdapter(cityPageAdapter);
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
    public void onResume() {
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
           
           