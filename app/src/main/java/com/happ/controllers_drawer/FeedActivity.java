package com.happ.controllers_drawer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.EventsListAdapter;
import com.happ.controllers.UserActivity;
import com.happ.fragments.EverythingFeedFragment;
import com.happ.fragments.ExploreEventsFragment;
import com.happ.fragments.FavoriteFeedFragment;
import com.happ.fragments.MapFragment;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.City;
import com.happ.models.Event;
import com.happ.models.User;
import com.happ.retrofit.APIService;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

public class FeedActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private Menu menu;
    protected User user;
    protected City city;
    protected ArrayList<City> cities;
    private String username, mCity;
    private NavigationView navigationView, navigationViewRight, navigationMenu, navigationHeader;
    private CoordinatorLayout rootLayout;
    private EventsListAdapter mEventAdapter;
    protected ArrayList<Event> events;
    private boolean dataLoading = false;

    private boolean isUnvoting = false;
    private boolean isUnfaving = false;


    private EditText mDateText;

    private SwitchCompat mFilterFree;
    private Date startDate, endDate;
    private String isFree = "";
    private FloatingActionButton mFabFilterDone;
    private ImageView mCloseRightNavigation, mCLoseLeftNavigation;
    private String searchText = "";
    private EditText mFeedSearchText;
    private BottomBar mBottomBar;
    private FragNavController fragNavController;

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


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

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


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_grey);
        }
        setTitle(App.getCurrentCity().getName());


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
                    Intent goToFeedIntent = new Intent(FeedActivity.this, OrganizerModeActivity.class);
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
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        navigationMenu.getMenu().findItem(R.id.nav_item_feed).setChecked(true);
        navigationMenu.getMenu().findItem(R.id.nav_item_feed).setIcon(R.drawable.happ_drawer_icon);

        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());

        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });


        cityPageAdapter = new MyCityPageAdapter(getSupportFragmentManager());
        mDrawerCityFragment.setAdapter(cityPageAdapter);

        ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow)).isChecked()) {
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

        mCloseRightNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(navigationViewRight);
            }
        });
        mCLoseLeftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(navigationView);
            }
        });

//        mFabFilterDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (mFilterFree.isChecked()) {
//                    isFree = "0";
//                } else {
//                    isFree = "";
//                }
//
////                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//
//                String max_free = "";
//                String sD = "";
//                String eD = "";
//                String feedSearch = "";
//
//                if (mFeedSearchText != null && mFeedSearchText.length() > 0) {
//                    feedSearch = mFeedSearchText.getText().toString();
//                    searchText = mFeedSearchText.getText().toString();
//                }
//
////                if (startDate != null) {
////                    sD = sdf.format(startDate);
////                }
////                if (endDate != null) {
////                    eD = sdf.format(endDate);
////                }
//                if (isFree != null) {
//                    max_free = isFree;
//                }
//
//                APIService.getFilteredEvents(1,feedSearch, sD, eD, max_free, false);
//            }
//        });


        ArrayList<Fragment> fragments = new ArrayList<>(4);

        //add fragments to list
        fragments.add(EverythingFeedFragment.newInstance());
        fragments.add(FavoriteFeedFragment.newInstance());
        fragments.add(ExploreEventsFragment.newInstance());
        fragments.add(MapFragment.newInstance());

        fragNavController = new FragNavController(getSupportFragmentManager(),R.id.feed_container,fragments);

        mBottomBar.setDefaultTabPosition(2);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_explore:
                        fragNavController.switchTab(TAB_EXPLORE);
                        break;
                    case R.id.tab_map:
                        fragNavController.switchTab(TAB_MAP);
//                        Toast.makeText(FeedActivity.this, "TAB_MAP", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tab_feed:
                        fragNavController.switchTab(TAB_EVERYTHING);
                        break;
                    case R.id.tab_favorites:
                        fragNavController.switchTab(TAB_FAVORITES);
                        break;
                    case R.id.tab_chat:

                        Toast.makeText(FeedActivity.this, "TAB_CHAT", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_explore) {
                    fragNavController.clearStack();
                }
                if (tabId == R.id.tab_map) {
                    fragNavController.clearStack();
                }
                if (tabId == R.id.tab_feed) {
                    fragNavController.clearStack();
                }
                if (tabId == R.id.tab_favorites) {
                    fragNavController.clearStack();
                }
                if (tabId == R.id.tab_chat) {
                    fragNavController.clearStack();
                }
            }
        });

//        mStartDateText = (EditText) findViewById(R.id.filter_input_start_date);
//        mDateText = (EditText) findViewById(R.id.filter_input_end_date);
//        mDateText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (endDate == null) endDate = new Date();
//                Calendar now = Calendar.getInstance();
//                now.setTime(endDate);
//
//                DatePickerDialog dpd = DatePickerDialog.newInstance(
//                        FeedActivity.this,
//                        now.get(Calendar.YEAR),
//                        now.get(Calendar.MONTH),
//                        now.get(Calendar.DAY_OF_MONTH)
//                );
//                dpd.show(getFragmentManager(), "Datepickerdialog");
//            }
//        });
//
//
////        RLFeedFilter = (RelativeLayout) findViewById(R.id.filter_feed_ll);
//        RLFeedFilter.setVisibility(View.GONE);
//        mFabFilterDone.setVisibility(View.GONE);

        setListenerToRootView();

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

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

    @Override
    public void onBackPressed() {
        if (fragNavController.getCurrentStack().size() > 1) {
            fragNavController.pop();
        } else {
            super.onBackPressed();
        }
    }


//    @Override
//    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//
//        java.text.DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
//
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.YEAR, year);
//        cal.set(Calendar.MONTH, monthOfYear);
//        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//        String endDate = dateFormat.format(cal.getTime());
//
//        cal.set(Calendar.YEAR, yearEnd);
//        cal.set(Calendar.MONTH, monthOfYearEnd);
//        cal.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
//        endDate = endDate = " – " + dateFormat.format(cal.getTime());
//
////        mStartDateText.setText(startDate);
//        mDateText.setText(endDate);
//    }


//    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
//
//        Calendar calendar = Calendar.getInstance();
//        if (endDate == null) endDate = new Date();
//        calendar.setTime(endDate);
//        calendar.set(year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd);
//
//        endDate = calendar.getTime();
//        java.text.DateFormat format = DateFormat.getLongDateFormat(App.getContext());
//
////                mStartDateText.setText(format.format(startDate));
//        mDateText.setText(format.format(endDate));
//
//    }

//    private DatePickerDialog.OnDateSetListener createStartDateListener() {
//        return new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar calendar = Calendar.getInstance();
//                if (startDate == null) startDate = new Date();
//                calendar.setTime(startDate);
//                calendar.set(year, monthOfYear, dayOfMonth);
//
//                startDate = calendar.getTime();
//                java.text.DateFormat format = DateFormat.getLongDateFormat(App.getContext());
//                mStartDateText.setText(format.format(startDate));
//            }
//        };
//    }


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
//                Intent i = new Intent(FeedActivity.this, CityActivity.class);
//                startActivity(i);
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


    @Override
    public void onResume() {
        super.onResume();

        updateUser();
        updateCity();

        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());

//        java.text.DateFormat format = DateFormat.getLongDateFormat(App.getContext());
//        mStartDateText.setText(format.format(startDate));
//        mDateText.setText(format.format(endDate));
//        if (isFree != null && isFree.length() > 0) {
//            mFilterFree.setChecked(true);
//        } else {
//            mFilterFree.setChecked(false);
//        }
    }

    protected void updateUser() {
        Realm realm = Realm.getDefaultInstance();
        User realmUser = realm.where(User.class).equalTo("username", username).findFirst();
        if (realmUser != null) user = realm.copyFromRealm(realmUser);
        realm.close();
    }
    protected void updateCity() {
        Realm realm = Realm.getDefaultInstance();
        mCity = App.getCurrentCity().getName();
        City realmCity = realm.where(City.class).equalTo("name", mCity).findFirst();
        if (realmCity != null) city = realm.copyFromRealm(realmCity);
        realm.close();
    }

    @Override
    protected void onDestroy() {

        if (userRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(userRequestDoneReceiver);
        }
        if (didUpvoteReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didUpvoteReceiver);
        }
        if (didIsFavReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didIsFavReceiver);
        }
        if (changeCityDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(changeCityDoneReceiver);
            changeCityDoneReceiver = null;
        }

        super.onDestroy();
    }

    private BroadcastReceiver changeCityReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateCity();
                ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());
            }
        };
    }

    private BroadcastReceiver createLoginSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUser();
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
                    snackbar.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (didUpvote == 1) {
                                APIService.doDownVote(eventId);
                            } else {
                                APIService.doUpVote(eventId);
                            }
//                            snackbar.dismiss();
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
                    snackbar.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
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

}

