package com.happ.controllers_drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
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
import com.happ.R;
import com.happ.controllers.UserActivity;
import com.happ.fragments.BaseFeedFragment;
import com.happ.fragments.EverythingFeedFragment;
import com.happ.fragments.ExploreEventsFragment;
import com.happ.fragments.FavoriteFeedFragment;
import com.happ.fragments.MapFragment;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.City;
import com.happ.models.Event;
import com.happ.models.User;
import com.happ.retrofit.APIService;
import com.happ.retrofit.HappRestClient;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

public class FeedActivity extends AppCompatActivity implements FragNavController.TransactionListener, FragNavController.RootFragmentListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private Menu menu;
    protected User user;
    protected City city;
    private String username, mCity;
    private NavigationView navigationView, navigationViewRight, navigationMenu, navigationHeader;
    private CoordinatorLayout rootLayout;
    protected ArrayList<Event> events;
    private boolean dataLoading = false;
    private SelectCityFragment scf;

    private boolean isUnvoting = false;
    private boolean isUnfaving = false;

    private EditText mFilterDate, mFilterTime;

    private SwitchCompat mFilterFree, mFilterPopularity;
    private Date startDate, endDate;
    private String isFree = "";
    private ImageView mCloseRightNavigation, mCLoseLeftNavigation;
    private String searchText = "";
    private boolean popularityEvents = false;
    private EditText mFeedSearchText;
    private BottomBar mBottomBar;
    private FragNavController fragNavController;

    private MapFragment mapFragment;
    private EverythingFeedFragment everythingFeedFragment;
    private FavoriteFeedFragment favoriteFeedFragment;
    private ExploreEventsFragment exploreEventsFragment;

    private final int TAB_EVERYTHING = FragNavController.TAB1;
    private final int TAB_FAVORITES = FragNavController.TAB2;
    private final int TAB_EXPLORE = FragNavController.TAB3;
    private final int TAB_MAP = FragNavController.TAB4;

    private BroadcastReceiver userRequestDoneReceiver;
    private BroadcastReceiver didUpvoteReceiver;
    private BroadcastReceiver didIsFavReceiver;
    private BroadcastReceiver changeCityDoneReceiver;

    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;
    private boolean isKeyboarShown = false;
    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    private View mViewFilterDate;
    private View mViewFilterTime;
    private CheckBox mDrawerHeaderArrow;
    private TextView mDrawerHeaderTVCity, mDrawerHeaderTVUsername;
    private Bundle bundle;
    private TextView mDrawerVersionApp;
    private LinearLayout mDrawerLLFooter, mRightDrawerLLFooter;

    private ImageView mDrawerHeaderAvatar;
    private RelativeLayout mDrawerHeaderAvatarPlaceholder;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        setContentView(R.layout.activity_feed);
        binds();
        setTitle("");

        scf = SelectCityFragment.newInstance();
        everythingFeedFragment = EverythingFeedFragment.newInstance();
        favoriteFeedFragment = FavoriteFeedFragment.newInstance();
        exploreEventsFragment = ExploreEventsFragment.newInstance();
        mapFragment = MapFragment.newInstance();

        setSupportActionBar(toolbar);
        setAllFunctionNavigationMenu();
        setListenerToRootView();
        setDrawerLayoutListener();

        setDrawerHeaderAvatar();

        if (userRequestDoneReceiver == null) {
            userRequestDoneReceiver = createLoginSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(userRequestDoneReceiver, new IntentFilter(BroadcastIntents.USEREDIT_REQUEST_OK));
        }

        if (changeCityDoneReceiver == null) {
            changeCityDoneReceiver = changeCityReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changeCityDoneReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
        }

        if (didUpvoteReceiver == null) {
            didUpvoteReceiver = createUpvoteReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didUpvoteReceiver, new IntentFilter(BroadcastIntents.EVENT_UPVOTE_REQUEST_OK));
        }
        if (didIsFavReceiver == null) {
            didIsFavReceiver = createFavReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didIsFavReceiver, new IntentFilter(BroadcastIntents.EVENT_UNFAV_REQUEST_OK));
        }

    }


    private void binds() {
        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        navigationViewRight = (NavigationView) findViewById(R.id.navigation_view_right);
        mFeedSearchText = (EditText) findViewById(R.id.filter_search);
        mCloseRightNavigation = (ImageView) findViewById(R.id.close_right_navigation);
        mCLoseLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);
        mBottomBar = (BottomBar) findViewById(R.id.bottombar_feed);
        mFilterFree = (SwitchCompat) findViewById(R.id.filter_free);
        mFilterPopularity = (SwitchCompat) findViewById(R.id.filter_popularity_events);
        mViewFilterDate = (View) findViewById(R.id.view_filter_date);
        mViewFilterTime = (View) findViewById(R.id.view_filter_time);
        mFilterDate = (EditText) findViewById(R.id.ff_edittext_filterdate);
        mFilterTime = (EditText) findViewById(R.id.ff_edittext_filtertime);
        mDrawerVersionApp = (TextView) findViewById(R.id.tv_drawer_version_app);
        mDrawerLLFooter = (LinearLayout) findViewById(R.id.ll_drawer_footer);
        mRightDrawerLLFooter = (LinearLayout) findViewById(R.id.right_drawer_ll_footer);
        mDrawerHeaderArrow = ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow));
        mDrawerHeaderTVCity = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city));
        mDrawerHeaderTVUsername = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username));

        mDrawerHeaderAvatar = ((ImageView)navigationHeader.getHeaderView(0).findViewById(R.id.dr_iv_user_avatar));
        mDrawerHeaderAvatarPlaceholder = ((RelativeLayout)navigationHeader.getHeaderView(0).findViewById(R.id.dr_avatar_placeholder));

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

    private void setAllFunctionNavigationMenu() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(FeedActivity.this);
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(FeedActivity.this, SettingsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);

                }
                if (menuItem.getItemId() == R.id.nav_item_organizer) {
                    Intent goToFeedIntent = new Intent(FeedActivity.this, ConfirmEmailActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(FeedActivity.this, SelectInterestsActivity.class);
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

        navigationMenu.getMenu().findItem(R.id.nav_item_feed).setChecked(true);
        navigationMenu.getMenu().findItem(R.id.nav_item_feed).setIcon(R.drawable.happ_drawer_icon);


        mDrawerHeaderTVUsername.setText( App.getCurrentUser().getFullname());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());

        String versionName = BuildConfig.VERSION_NAME;
        mDrawerVersionApp.setText(getResources().getString(R.string.app_name) + " " + "v" + versionName);

        mDrawerHeaderTVUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });


        cityPageAdapter = new MyCityPageAdapter(getSupportFragmentManager());

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
                mDrawerLayout.closeDrawer(navigationViewRight);
            }
        });
        mCLoseLeftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerHeaderArrow.setChecked(false);
                mDrawerCityFragment.setVisibility(View.GONE);
                mDrawerLayout.closeDrawer(GravityCompat.START);

            }
        });

        mFilterFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sD = "";
                String eD = "";
                if (startDate != null) sD = sdf.format(startDate);
                if (endDate != null) eD = sdf.format(endDate);

                if (mFilterFree.isChecked()) {
                    isFree = "0";
                    APIService.getFilteredEvents(1,getFeedSearch(), sD, eD, getMaxFree(),getPopularityEvents(), false);
                } else {
                    isFree = "";
                    APIService.getFilteredEvents(1,getFeedSearch(), sD, eD, getMaxFree(),getPopularityEvents(), false);
                }
            }
        });

        mFilterPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sD = "";
                String eD = "";
                if (startDate != null) sD = sdf.format(startDate);
                if (endDate != null) eD = sdf.format(endDate);

                if (mFilterPopularity.isChecked()) {
                    popularityEvents = true;
                    APIService.getFilteredEvents(1,getFeedSearch(), sD, eD, getMaxFree(),getPopularityEvents(), false);
                } else {
                    popularityEvents = false;
                    APIService.getFilteredEvents(1,getFeedSearch(), sD, eD, getMaxFree(),getPopularityEvents(), false);
                }
            }
        });

        mViewFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startDate == null) startDate = new Date();
                Calendar now = Calendar.getInstance();
                now.setTime(startDate);

                SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                        SmoothDateRangePickerFragment
                                .newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                                    @Override
                                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                               int yearStart, int monthStart,
                                                               int dayStart, int yearEnd,
                                                               int monthEnd, int dayEnd) {
                                        Calendar cal = Calendar.getInstance();
                                        cal.set(Calendar.YEAR, yearStart);
                                        cal.set(Calendar.MONTH, monthStart);
                                        cal.set(Calendar.DAY_OF_MONTH, dayStart);
                                        startDate = cal.getTime();

                                        cal.set(Calendar.YEAR, yearEnd);
                                        cal.set(Calendar.MONTH, monthEnd);
                                        cal.set(Calendar.DAY_OF_MONTH, dayEnd);
                                        endDate = cal.getTime();


                                        DateTimeFormatter dtFormatter = DateTimeFormat.forPattern("MMM dd");
                                        DateTime eventStartDate = new DateTime(getStartD());
                                        DateTime eventEndDate = new DateTime(getEndD());

                                        String filterDate =
                                                eventStartDate.toString(dtFormatter)
                                                        + " - " +
                                                        eventEndDate.toString(dtFormatter);

                                        mFilterDate.setText(filterDate);
                                        String sD = sdf.format(startDate);
                                        String eD = sdf.format(endDate);

                                        APIService.getFilteredEvents(1, getFeedSearch(), sD, eD, getMaxFree(),getPopularityEvents(), false);

                                    }
                                }, now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH));
                smoothDateRangePickerFragment.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        mViewFilterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FeedActivity.this, "Filter Time", Toast.LENGTH_SHORT).show();
            }
        });


        mFeedSearchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchText = mFeedSearchText.getText().toString();
                String sD = "";
                String eD = "";
                if (startDate != null) sD = sdf.format(startDate);
                if (endDate != null) eD = sdf.format(endDate);

                APIService.getFilteredEvents(1,searchText, sD, eD, getMaxFree(),getPopularityEvents(), false);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
                            mRightDrawerLLFooter.setVisibility(View.GONE);

                        }
                        isKeyboarShown = true;
                    }
                    else {
                        navigationHeader.setVisibility(View.VISIBLE);
                        mBottomBar.setVisibility(View.VISIBLE);
                        mDrawerLLFooter.setVisibility(View.VISIBLE);
                        mRightDrawerLLFooter.setVisibility(View.VISIBLE);
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

    @Override
    public Fragment getRootFragment(final int index) {

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        switch (index) {
            case TAB_EVERYTHING:
                everythingFeedFragment.setChangeColorIconToolbarListener(new BaseFeedFragment.ChangeColorIconToolbarListener() {
                    @Override
                    public void onChangeColorIconToolbar(@DrawableRes int drawableHome, @DrawableRes int drawableFilter) {
                        if (actionBar != null) {
                            actionBar.setHomeAsUpIndicator(drawableHome);
                            menu.getItem(0).setIcon(drawableFilter);
                        }
                    }

                    @Override
                    public void onClickButtonEmpty() {

                    }
                });
                return everythingFeedFragment;

            case TAB_FAVORITES:
                favoriteFeedFragment.setChangeColorIconToolbarListener(new BaseFeedFragment.ChangeColorIconToolbarListener() {
                    @Override
                    public void onChangeColorIconToolbar(@DrawableRes int drawableHome, @DrawableRes int drawableFilter) {
                        if (actionBar != null) {
                            actionBar.setHomeAsUpIndicator(drawableHome);
                            menu.getItem(0).setIcon(drawableFilter);
                        }
                    }

                    @Override
                    public void onClickButtonEmpty() {
                        fragNavController.switchTab(TAB_EVERYTHING);
                        mBottomBar.setDefaultTabPosition(0);
                    }
                });
                return favoriteFeedFragment;
            case TAB_EXPLORE:
                exploreEventsFragment.setOnHideShowFilterListener(new ExploreEventsFragment.HideShowFilterListener() {
                    @Override
                    public void onHideFilter() {
                        menu.getItem(0).setVisible(false);
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navigationViewRight);
                    }

                    @Override
                    public void onShowFilter() {
                        menu.getItem(0).setVisible(true);
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, navigationViewRight);
                    }
                });
                return exploreEventsFragment;
            case TAB_MAP:
                return mapFragment;
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fragNavController != null) {
            fragNavController.onSaveInstanceState(outState);
        }
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);

        this.menu = menu;

        fragNavController = new FragNavController(bundle, getSupportFragmentManager(),R.id.feed_container,this,4, TAB_EVERYTHING);
        fragNavController.setTransactionListener(this);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_explore:
                        fragNavController.switchTab(TAB_EXPLORE);
                        break;
                    case R.id.tab_map:
                        fragNavController.switchTab(TAB_MAP);
                        break;
                    case R.id.tab_feed:
                        fragNavController.switchTab(TAB_EVERYTHING);
                        break;
                    case R.id.tab_favorites:
                        fragNavController.switchTab(TAB_FAVORITES);
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
                mDrawerLayout.openDrawer(navigationViewRight);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public Date getStartD() {
        return startDate;
    }
    public Date getEndD() {
        return endDate;
    }
    public String getMaxFree() {
        return isFree;
    }
    public String getFeedSearch() {
        return searchText;
    }
    public String getPopularityEvents() {
        if (popularityEvents) {
            return "popular";
        }
        return "";
    }


    public class MyCityPageAdapter extends FragmentPagerAdapter {

        public MyCityPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return scf;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        HappRestClient.getInstance()
                .setLanguage(getApplicationContext()
                        .getResources()
                        .getConfiguration()
                        .locale.getLanguage());


        setDrawerHeaderAvatar();

        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullname());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());

//        java.text.DateFormat format = DateFormat.getLongDateFormat(App.getContext());
//        mStartDateText.setText(format.format(startDate));
//        mDateText.setText(format.format(endDate));
//        if (isFree != null && isFree.length() > 0) {
//            mFilterFree.setChecked(true);
//        } else {
//            mFilterFree.setChecked(false);
//        }
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

    private BroadcastReceiver createLoginSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullname());

            }
        };
    }

    private BroadcastReceiver createUpvoteReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isUnvoting) {
                    isUnvoting = false;
                    return;
                }
                final int didUpvote = intent.getIntExtra(BroadcastIntents.EXTRA_DID_UPVOTE, -1);
                final String eventId = intent.getStringExtra(BroadcastIntents.EXTRA_EVENT_ID);

                if (didUpvote >= 0 && eventId != null) {
                    isUnvoting = true;
                    Realm realm = Realm.getDefaultInstance();
                    Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    String eventTitle = event.getTitle();
                    String text = "";
                    if (didUpvote == 1) {
                        text = getResources().getString(R.string.did_upvote) + " \"" + eventTitle+"\"";
                    } else {
                        text = getResources().getString(R.string.did_downvote) + " \"" + eventTitle+"\"";
                    }
                    String undo = getResources().getString(R.string.undo);

                    final Snackbar snackbar = Snackbar.make(rootLayout, text, Snackbar.LENGTH_LONG);
                    snackbar.setAction(undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (didUpvote == 1) {
                                APIService.doDownVote(eventId);
                            } else {
                                APIService.doUpVote(eventId);
                            }
                        }
                    });
                    snackbar.setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                isUnvoting = false;
                            }
                            super.onDismissed(snackbar, event);
                        }
                    });
                    snackbar.show();
                }
            }
        };
    }

    private BroadcastReceiver createFavReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isUnfaving) {
                    isUnfaving = false;
                    return;
                }
                final int didFav = intent.getIntExtra(BroadcastIntents.EXTRA_DID_FAV, -1);
                final String eventId = intent.getStringExtra(BroadcastIntents.EXTRA_EVENT_ID);
                if (didFav >= 0 && eventId != null) {
                    isUnfaving = true;
                    Realm realm = Realm.getDefaultInstance();
                    Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    String eventTitle = event.getTitle();
                    String text = "";
                    if (didFav == 1) {
                        text = getResources().getString(R.string.did_fav) + " \"" + eventTitle + "\" " + getResources().getString(R.string.did_fav_2);
                    } else {
                        text = getResources().getString(R.string.did_unfav) + " \"" + eventTitle + "\" " + getResources().getString(R.string.did_unfav_2);
                    }
                    String undo = getResources().getString(R.string.undo);

                    final Snackbar snackbar = Snackbar.make(rootLayout, text, Snackbar.LENGTH_LONG);
                    snackbar.setAction(undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (didFav == 1) {
                                APIService.doUnFav(eventId);
                            } else {
                                APIService.doFav(eventId);
                            }
//                            snackbar.dismiss();
                        }
                    });
                    snackbar.setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                isUnfaving = false;
                            }
                            super.onDismissed(snackbar, event);
                        }
                    });
                    snackbar.show();
                }
            }
        };
    }

    @Override
    protected void onDestroy() {

        if (userRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(userRequestDoneReceiver);
            userRequestDoneReceiver = null;
        }
        if (didUpvoteReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didUpvoteReceiver);
            didUpvoteReceiver = null;
        }
        if (didIsFavReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didIsFavReceiver);
            didIsFavReceiver = null;
        }
        if (changeCityDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(changeCityDoneReceiver);
            changeCityDoneReceiver = null;
        }

        super.onDestroy();
    }

}

