package com.happ.controllers_drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.Typefaces;
import com.happ.adapters.EventImagesSwipeAdapter;
import com.happ.adapters.EventPhoneListAdapter;
import com.happ.controllers.EditCreateActivity;
import com.happ.controllers.EventMapActivity;
import com.happ.controllers.UserActivity;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.Event;
import com.happ.models.EventImage;
import com.happ.models.EventPhones;
import com.happ.models.User;
import com.happ.retrofit.APIService;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import io.realm.Realm;
import io.realm.RealmList;


/**
 * Created by dante on 8/8/16.
 */
public class EventActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbarTop;
    private LinearLayout mLLToolbar;
    private String eventId;
    private Event event;
    private Menu menu;

    private ViewPager viewPager;
    private EventImagesSwipeAdapter mEventImagesSwipeAdapter;

    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;

    private TextView mWebSite,
            mEmail,
            mPlace,
            mAuthor,
            mAuthorEmail,
            mDescription,
            mEventDate,
            mEventTime,
            mPrice,
            mTitle,
            mVotesCount;

    private ImageView mFavoritesImage, mUpvoteImage, mCLoseLeftNavigation;

    private LinearLayout mEventWEbSite, mEventEmail, mEventPhone;
    private LinearLayout mCircleLLCalendar, mCircleLLPrice, mCircleLLPlace;
    private ImageView mFlashingImageViewCalendar, mFlashingImageViewPrice, mFlashingImageViewPlace;
    private Animation anim;


    private LinearLayout mLLVote, mLLFav;

    private RelativeLayout mEventAuthor;
    private Typeface tfcs;

    private RecyclerView rvPhones;
    private EventPhoneListAdapter eventPhoneListAdapter;
    private LinearLayoutManager llm;

    private FloatingActionButton mFab;
    private BroadcastReceiver didIsFavReceiver;
    private BroadcastReceiver didUpvoteReceiver;
    private BroadcastReceiver changeCityDoneReceiver;

    private boolean isOrg;
    private boolean inEventActivity;
    private CollapsingToolbarLayout ctl;
    private NavigationView navigationMenu, navigationHeader, navigationView;

    private AppBarLayout appBarLayout;
    private TextView mCurrency, mPhone;

    private CheckBox mDrawerHeaderArrow;
    private TextView mDrawerHeaderTVCity, mDrawerHeaderTVUsername;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        isOrg = intent.getBooleanExtra("is_organizer", false);
        inEventActivity = intent.getBooleanExtra("in_event_activity", false);
        eventId = intent.getStringExtra("event_id");

        setContentView(R.layout.activity_event);
        setTitle("");

        mTitle = (TextView) findViewById(R.id.event_title);
        tfcs = Typefaces.get(App.getContext(), "fonts/WienLight_Normal.otf");
        mTitle.setTypeface(tfcs);

        mEventAuthor = (RelativeLayout) findViewById(R.id.event_author_form);
        mPlace = (TextView) findViewById(R.id.event_place);
        mFavoritesImage = (ImageView) findViewById(R.id.event_iv_favorites);
        mVotesCount = (TextView) findViewById(R.id.event_votes_count);
        mAuthor = (TextView) findViewById(R.id.event_author);
        mAuthorEmail = (TextView) findViewById(R.id.event_author_email);
        mDescription = (TextView) findViewById(R.id.event_description);
        mWebSite = (TextView) findViewById(R.id.event_website);
        mEventWEbSite = (LinearLayout) findViewById(R.id.event_website_form);
        mEventEmail = (LinearLayout) findViewById(R.id.event_email_form);
        mEventPhone = (LinearLayout) findViewById(R.id.event_phone_form);
        mPhone = (TextView) findViewById(R.id.event_phone);
        mPrice = (TextView) findViewById(R.id.event_price);
        mEventDate = (TextView) findViewById(R.id.event_date);
        mEventTime = (TextView) findViewById(R.id.event_time);
        viewPager = (ViewPager) findViewById(R.id.slider_viewpager);
        mEmail = (TextView) findViewById(R.id.event_email);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mToolbarTop = (Toolbar) findViewById(R.id.top_event_toolbar);
        mLLToolbar = (LinearLayout) findViewById(R.id.ll_toolbar);
        ctl = (CollapsingToolbarLayout) findViewById(R.id.event_collapsing_layout);
        mUpvoteImage = (ImageView) findViewById(R.id.event_iv_did_upvote);
        mCircleLLPlace = (LinearLayout) findViewById(R.id.ll_place_image);
        mCircleLLCalendar = (LinearLayout) findViewById(R.id.ll_calendar_image);
        mCircleLLPrice = (LinearLayout) findViewById(R.id.ll_price_image);
        mFlashingImageViewPlace = (ImageView) findViewById(R.id.iv_flashing_place);
        mLLVote = (LinearLayout) findViewById(R.id.ll_upvote_image);
        mLLFav = (LinearLayout) findViewById(R.id.ll_fav_image);
        mCurrency = (TextView) findViewById(R.id.event_currency);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        mCLoseLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);
        rvPhones = (RecyclerView) findViewById(R.id.rv_event_phones);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        mDrawerHeaderArrow = ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow));
        mDrawerHeaderTVCity = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city));
        mDrawerHeaderTVUsername = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username));


        mFab.setVisibility(View.GONE);
        if (isOrg) {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIntent = new Intent(EventActivity.this, EditCreateActivity.class);
                    editIntent.putExtra("event_id", eventId);
                    EventActivity.this.startActivity(editIntent);
                }
            });
        }

        mCircleLLPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventActivity.this, EventMapActivity.class);
                i.putExtra("event_id_for_map", eventId);
                startActivity(i);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }

        mEventImagesSwipeAdapter = new EventImagesSwipeAdapter(getSupportFragmentManager());
        mEventImagesSwipeAdapter.setImageList(new RealmList<EventImage>());
        viewPager.setAdapter(mEventImagesSwipeAdapter);

        setSupportActionBar(mToolbarTop);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_rigth_arrow);
        }

        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    App.doLogout(EventActivity.this);
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    Intent goToFeedIntent = new Intent(EventActivity.this, SettingsActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0, 0);

                }

                if (menuItem.getItemId() == R.id.nav_item_organizer) {
                    Intent goToFeedIntent = new Intent(EventActivity.this, ConfirmEmailActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0, 0);
                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(EventActivity.this, SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }

                if (menuItem.getItemId() == R.id.nav_item_feed) {
                    Intent intent = new Intent(EventActivity.this, FeedActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        navigationMenu.getMenu().findItem(R.id.nav_item_feed).setChecked(true);
        navigationMenu.getMenu().findItem(R.id.nav_item_feed).setIcon(R.drawable.happ_drawer_icon);


        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullName());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());


        mDrawerHeaderTVUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, UserActivity.class);
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

        mCLoseLeftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(navigationView);
            }
        });

        repopulateEvent();


        if (didUpvoteReceiver == null) {
            didUpvoteReceiver = createUpvoteReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didUpvoteReceiver, new IntentFilter(BroadcastIntents.EVENT_UPVOTE_REQUEST_OK));
        }

        if (didIsFavReceiver == null) {
            didIsFavReceiver = createFavReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didIsFavReceiver, new IntentFilter(BroadcastIntents.EVENT_UNFAV_REQUEST_OK));
        }

        if (changeCityDoneReceiver == null) {
            changeCityDoneReceiver = changeCityReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changeCityDoneReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_event_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    EventActivity.this.supportFinishAfterTransition();
                } else {
                    finish();
                    EventActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
                }
                return true;
            case R.id.menu_ea_favorites:
                Toast.makeText(EventActivity.this, "FAVORITES", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void repopulateEvent() {

        if (inEventActivity) {
            Realm realm = Realm.getDefaultInstance();
            event = realm.where(Event.class).equalTo("id", eventId).findFirst();
            event = realm.copyFromRealm(event);
            realm.close();

            mEventImagesSwipeAdapter.setImageList(event.getImages());

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        ctl.setTitle(event.getTitle());
                        ctl.setCollapsedTitleTypeface(tfcs);
                        isShow = true;
                    } else if (isShow) {
                        ctl.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });


            if (event.getColor() != null) {
                mCircleLLCalendar.setBackgroundResource(R.drawable.circle_event);
                mCircleLLPrice.setBackgroundResource(R.drawable.circle_event);
                mCircleLLPlace.setBackgroundResource(R.drawable.circle_event);
                mLLVote.setBackgroundResource(R.drawable.circle_event);
                mFlashingImageViewPlace.setBackgroundResource(R.drawable.circle_event);

                GradientDrawable gdCalendar = (GradientDrawable) mCircleLLCalendar.getBackground().getCurrent();
                GradientDrawable gdPlace = (GradientDrawable) mCircleLLPlace.getBackground().getCurrent();
                GradientDrawable gdPrice = (GradientDrawable) mCircleLLPrice.getBackground().getCurrent();
                GradientDrawable gdVote = (GradientDrawable) mLLVote.getBackground().getCurrent();
                GradientDrawable gdFlashingPlace = (GradientDrawable) mFlashingImageViewPlace.getBackground().getCurrent();

                gdCalendar.setColor(Color.parseColor(event.getColor()));
                gdPlace.setColor(Color.parseColor(event.getColor()));
                gdPrice.setColor(Color.parseColor(event.getColor()));
                gdVote.setColor(Color.parseColor(event.getColor()));
                gdFlashingPlace.setColor(Color.parseColor(event.getColor()));

                mLLToolbar.setBackgroundColor(Color.parseColor(event.getColor()));
            }

            anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animatealpha_infiniti);
            mFlashingImageViewPlace.startAnimation(anim);

            mTitle.setText(event.getTitle());
            mPlace.setText(event.getPlace());
            mPrice.setText(event.getPriceRange());
            mCurrency.setText(event.getCurrency().getName());

            final User author = event.getAuthor();
            if (author != null) {
                if (author.getEmail() != null) {
                    mAuthorEmail.setText(event.getAuthor().getEmail());
                } else {
                    mAuthorEmail.setVisibility(View.GONE);
                }
                mAuthor.setText(event.getAuthor().getFullName());
            } else {
                mEventAuthor.setVisibility(View.GONE);
            }

            if (event.getPhones() != null) {
                llm = new LinearLayoutManager(this);
                rvPhones.setLayoutManager(llm);
                eventPhoneListAdapter = new EventPhoneListAdapter(this, event.getPhones());
                rvPhones.setAdapter(eventPhoneListAdapter);

                eventPhoneListAdapter.setOnSelectEventExploreListener(new EventPhoneListAdapter.SelectEventPhoneItemListener() {
                    @Override
                    public void onEventPhoneItemSelected(EventPhones eventPhones) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", eventPhones.getPhone(), null));
                        startActivity(intent);
                    }
                });
            } else {
                mEventPhone.setVisibility(View.GONE);
            }

            mDescription.setText(event.getDescription());

            if (event.getWebSite() == null || event.getWebSite().equals("")) {
                mEventWEbSite.setVisibility(View.GONE);
            } else {
                final String link = event.getWebSite();
                mWebSite.setText(link);
                mEventWEbSite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent("com.happ.Browser");
                        intent.setData(Uri.parse(link));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.push_to_back);
                    }
                });
            }

            if (event.getEmail() == null || event.getEmail().equals("")) {
                mEventEmail.setVisibility(View.GONE);
            } else {
                final String email = event.getEmail();
                mEmail.setText(email);
                mEventEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_SENDTO);
                        i.setType("message/rfc822");
//                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
//                        i.putExtra(Intent.EXTRA_EMAIL, email);
                        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                        i.putExtra(Intent.EXTRA_TEXT, "body of email");
                        i.setData(Uri.parse("mailto:" + event.getEmail())); // or just "mailto:" for blank
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(EventActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            String startDate = event.getStartDateFormatted("MMM dd").toUpperCase();
            String endDate = event.getEndDateFormatted("MMM dd").toUpperCase();

            if (startDate.equals(endDate)) {
                mEventDate.setText(startDate);
            } else {
                mEventDate.setText(startDate + " — " + endDate);
            }

            String startTime = event.getStartDateFormatted("h:mm a");
            String endTime = event.getEndDateFormatted("h:mm a");
            String rangeTime = startTime + " — " + endTime;

            if (startTime.equals(endTime)) {
                mEventTime.setText(startTime);
            } else {
                mEventTime.setText(rangeTime);
            }

            mVotesCount.setText(String.valueOf(event.getVotesCount()));

            if (event.isInFavorites()) {
                mFavoritesImage.setImageResource(R.drawable.ic_in_favorites_white);
            } else {
                mFavoritesImage.setImageResource(R.drawable.ic_not_in_favorites_white);
            }

            if (event.isDidVote()) {
                mUpvoteImage.setImageResource(R.drawable.ic_did_upvote);
            } else {
                mUpvoteImage.setImageResource(R.drawable.ic_did_not_upvote);
            }


            mLLFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (event.isInFavorites()) {
                        APIService.doUnFav(event.getId());
                    } else {
                        APIService.doFav(event.getId());
                    }
                }
            });

            mLLVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (event.isDidVote()) {
                        APIService.doDownVote(event.getId());
                    } else {
                        APIService.doUpVote(event.getId());
                    }
                }
            });
        }
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
        repopulateEvent();
        if (isOrg && mFab.getVisibility() != View.VISIBLE) {
            mFab.show();
        }
        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullName());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawerLayout = null;
        mToolbarTop = null;
        mLLToolbar = null;
        eventId = null;
        event = null;
        viewPager = null;
        mEventImagesSwipeAdapter = null;
        mWebSite = null;
        mEmail = null;
        mPlace = null;
        mAuthor = null;
        mAuthorEmail = null;
        mDescription = null;
        mEventDate = null;
        mEventTime = null;
        mUpvoteImage = null;
        mFavoritesImage = null;
        mEventAuthor = null;
        mEventWEbSite = null;
        mEventEmail = null;
        mFab = null;
        ctl = null;
        mPrice = null;
        mTitle = null;
        mVotesCount = null;
        mCircleLLCalendar = null;
        mCircleLLPlace = null;
        mCircleLLPrice = null;

        if (changeCityDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(changeCityDoneReceiver);
            changeCityDoneReceiver = null;
        }

        if (didIsFavReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didIsFavReceiver);
            didIsFavReceiver = null;
        }

        if (didUpvoteReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(didUpvoteReceiver);
            didUpvoteReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            EventActivity.this.supportFinishAfterTransition();
        } else {
            finish();
            EventActivity.this.overridePendingTransition(R.anim.pull_from_back, R.anim.slide_out_to_right);
        }
    }

    private BroadcastReceiver createFavReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                repopulateEvent();

            }
        };
    }

    private BroadcastReceiver createUpvoteReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                repopulateEvent();
            }
        };
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

}