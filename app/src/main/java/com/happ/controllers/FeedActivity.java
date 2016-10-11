package com.happ.controllers;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.fragments.EverythingFeedFragment;
import com.happ.fragments.FavoriteFeedFragment;
import com.happ.models.Event;
import com.happ.models.User;
import com.happ.retrofit.APIService;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class FeedActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

//    protected final String[] mTabNames = {"Everything", "Favorites"};
//    protected ArrayList<Fragment> mTabFragments;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private Menu menu;
    protected User user;
    private String username;
    private NavigationView navigationView;
    private CoordinatorLayout rootLayout;

    private boolean isUnvoting = false;
    private boolean isUnfaving = false;


    private EditText mStartDateText, mDateText;
    private SwitchCompat mFilterFree;
    private Date startDate, endDate;
    private String isFree = "";
    private RelativeLayout RLFeedFilter;
    private FloatingActionButton mFabFilterDone;

    private FrameLayout container;
    private FragmentManager myFragmentManager;
    private FavoriteFeedFragment favoritesFeedFragment;
    private EverythingFeedFragment everythingFeedFragment;
    private BottomBar mBottomBar;
    private FragNavController fragNavController;

    private final int TAB_EVERYTHING = FragNavController.TAB1;
    private final int TAB_FAVORITES = FragNavController.TAB2;


    private BroadcastReceiver userRequestDoneReceiver;
    private BroadcastReceiver didUpvoteReceiver;
    private BroadcastReceiver didIsFavReceiver;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        if (userRequestDoneReceiver == null) {
            userRequestDoneReceiver = createLoginSuccessReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(userRequestDoneReceiver, new IntentFilter(BroadcastIntents.USEREDIT_REQUEST_OK));
        }

        if (didUpvoteReceiver == null) {
            didUpvoteReceiver = createUpvoteReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didUpvoteReceiver, new IntentFilter(BroadcastIntents.EVENT_UPVOTE_REQUEST_OK));
        }
        if (didIsFavReceiver == null) {
            didIsFavReceiver = createFavReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didIsFavReceiver, new IntentFilter(BroadcastIntents.EVENT_UNFAV_REQUEST_OK));
        }

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(App.getCurrentCity().getName());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                if (menuItem.getItemId() == R.id.nav_item_privacy_policy) {
                    Intent intent = new Intent(FeedActivity.this, PrivacyPolicyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }
                if (menuItem.getItemId() == R.id.nav_item_org_rules) {
                    Intent intent = new Intent(FeedActivity.this, OrganizerRulesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        navigationView.getMenu().findItem(R.id.nav_item_feed).setChecked(true);

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

//        mTabFragments = new ArrayList<>();
//        for (int i=0; i<mTabNames.length; i++) {
//            mTabFragments.add(null);
//        }
//        FeedPagerAdapter adapter = new FeedPagerAdapter(getSupportFragmentManager());
//        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
//        viewPager.setAdapter(adapter);

        ArrayList<Fragment> fragments = new ArrayList<>(2);

        //add fragments to list
        fragments.add(EverythingFeedFragment.newInstance());
        fragments.add(FavoriteFeedFragment.newInstance());

        fragNavController = new FragNavController(getSupportFragmentManager(),R.id.feed_container,fragments);

        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        mBottomBar.setDefaultTabPosition(2);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_explore:
                        Toast.makeText(FeedActivity.this, "TAB_EXPLORE", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tab_map:

                        Toast.makeText(FeedActivity.this, "TAB_MAP", Toast.LENGTH_SHORT).show();
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




//        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
//        tabLayout.setupWithViewPager(viewPager);


        mStartDateText = (EditText) findViewById(R.id.filter_input_start_date);

        mDateText = (EditText) findViewById(R.id.filter_input_end_date);
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endDate == null) endDate = new Date();
                Calendar now = Calendar.getInstance();
                now.setTime(endDate);

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        FeedActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });


        mFilterFree = (SwitchCompat) findViewById(R.id.filter_free);
        RLFeedFilter = (RelativeLayout) findViewById(R.id.filter_feed_ll);
        RLFeedFilter.setVisibility(View.GONE);
        mFabFilterDone = (FloatingActionButton) findViewById(R.id.fab_filter_done);
        mFabFilterDone.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        if (fragNavController.getCurrentStack().size() > 1) {
            fragNavController.pop();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String startDate = year +""+ (monthOfYear++)+""+ (dayOfMonth++);
        String endDate = yearEnd +""+ (monthOfYearEnd)+ "" + dayOfMonthEnd;

        mStartDateText.setText(startDate);
        mDateText.setText(endDate);
    }



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

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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

                if (RLFeedFilter.getVisibility() == View.GONE ) {
                    RLFeedFilter.setVisibility(View.VISIBLE);

                    if (startDate == null) {
                        startDate = new Date();
                    }
                    if (endDate == null) {
                        endDate = new Date();
                    }



                    if (RLFeedFilter.getVisibility() == View.VISIBLE) {
                        mFabFilterDone.setVisibility(View.VISIBLE);
                        mFabFilterDone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mFilterFree.isChecked()) {
                                    isFree = "0";
                                } else {
                                    isFree = "";
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

                                String max_free = "";
                                String sD = "";
                                String eD = "";

                                if (startDate != null) {
                                    sD = sdf.format(startDate);
                                }
                                if (endDate != null) {
                                    eD = sdf.format(endDate);
                                }
                                if (isFree != null) {
                                    max_free = isFree;
                                }

                                APIService.getFilteredEvents(1, sD, eD, max_free, false);
                            }
                        });
                    }
                } else {
                    RLFeedFilter.setVisibility(View.GONE);
                }
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

//
//    protected class FeedPagerAdapter extends FragmentStatePagerAdapter {
//
//        public FeedPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            if (mTabFragments.get(position) == null) {
//                switch (position) {
//                    case 1:
//                        mTabFragments.set(position, FavoriteFeedFragment.newInstance());
//                        break;
//                    default:
//                        mTabFragments.set(position, EverythingFeedFragment.newInstance());
//                        break;
//                }
//            }
//            return mTabFragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mTabNames.length;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mTabNames[position];
//        }
//    }

    private BroadcastReceiver createLoginSuccessReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                updateUserList();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateUserList();
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.avatar));
        java.text.DateFormat format = DateFormat.getLongDateFormat(App.getContext());
//        mStartDateText.setText(format.format(startDate));
//        mDateText.setText(format.format(endDate));
//        if (isFree != null && isFree.length() > 0) {
//            mFilterFree.setChecked(true);
//        } else {
//            mFilterFree.setChecked(false);
//        }
    }

    protected void updateUserList() {
        Realm realm = Realm.getDefaultInstance();
        User realmUser = realm.where(User.class).equalTo("username", username).findFirst();
        user = realm.copyFromRealm(realmUser);
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
        super.onDestroy();
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

