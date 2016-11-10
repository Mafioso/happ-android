package com.happ.controllers_drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.happ.controllers.EditCreateActivity;
import com.happ.controllers.EventMapActivity;
import com.happ.controllers.UserActivity;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.Event;
import com.happ.models.EventImage;
import com.happ.models.User;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by dante on 8/8/16.
 */
public class EventActivity extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private String eventId;
    private Event event;
    private Menu menu;

    private ViewPager viewPager;
    private EventImagesSwipeAdapter mEventImagesSwipeAdapter;

    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;

    private TextView    mWebSite,
                        mEmail,
                        mPlace,
                        mAuthor,
                        mDescription,
                        mStartDate,
                        mEndDate,
                        mPrice,
                        mTitle,
                        mVotesCount;

    private ImageView mFavoritesImage, mCLoseLeftNavigation;

    private LinearLayout mEventWEbSite, mEventEmail;
    private LinearLayout mCirclePlace;

    private RelativeLayout mEventAuthor;
    private Typeface tfcs;

    private FloatingActionButton mFab;
    private BroadcastReceiver didIsFavReceiver;

    private boolean isOrg;
    private CollapsingToolbarLayout ctl;
    private NavigationView navigationMenu, navigationHeader, navigationView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        isOrg = intent.getBooleanExtra("is_organizer", false);
        eventId = intent.getStringExtra("event_id");
        setContentView(R.layout.activity_event);

        mTitle = (TextView) findViewById(R.id.event_title);
        tfcs = Typefaces.get(App.getContext(), "fonts/WienLight_Normal.ttf");
        mTitle.setTypeface(tfcs);

        mEventAuthor = (RelativeLayout) findViewById(R.id.event_author_form);
        mPlace = (TextView)findViewById(R.id.event_place);
        mFavoritesImage = (ImageView) findViewById(R.id.event_iv_favorites);
        mVotesCount = (TextView) findViewById(R.id.event_votes_count);
        mAuthor = (TextView)findViewById(R.id.event_author);
        mDescription = (TextView)findViewById(R.id.event_description);
        mWebSite = (TextView) findViewById(R.id.event_website);
        mEventWEbSite = (LinearLayout) findViewById(R.id.event_website_form);
        mEventEmail = (LinearLayout) findViewById(R.id.event_email_form);
        mCirclePlace = (LinearLayout) findViewById(R.id.ll_place);
        mPrice = (TextView) findViewById(R.id.event_price);
        mStartDate = (TextView)findViewById(R.id.event_start_date);
        mEndDate = (TextView)findViewById(R.id.event_end_date);
        viewPager=(ViewPager)findViewById(R.id.slider_viewpager);
        mEmail = (TextView) findViewById(R.id.event_email);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ctl = (CollapsingToolbarLayout)findViewById(R.id.event_collapsing_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        mCLoseLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);


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

        mCirclePlace.setOnClickListener(new View.OnClickListener() {
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

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    overridePendingTransition(0,0);

                }

                if (menuItem.getItemId() == R.id.nav_item_organizer) {
                    Intent goToFeedIntent = new Intent(EventActivity.this, OrganizerModeActivity.class);
                    goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToFeedIntent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_interests) {
                    Intent intent = new Intent(EventActivity.this, SelectInterestsActivity.class);
                    intent.putExtra("is_full", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }

                if (menuItem.getItemId() == R.id.nav_item_feed) {
                    Intent intent = new Intent(EventActivity.this, FeedActivity.class);
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

        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, UserActivity.class);
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

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
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
                } else if(isShow) {
                    ctl.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
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

        if (didIsFavReceiver == null) {
            didIsFavReceiver = createFavReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(didIsFavReceiver, new IntentFilter(BroadcastIntents.EVENT_UNFAV_REQUEST_OK));
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
        Realm realm = Realm.getDefaultInstance();
        event = realm.where(Event.class).equalTo("id", eventId).findFirst();
        event = realm.copyFromRealm(event);
        realm.close();

        mEventImagesSwipeAdapter.setImageList(event.getImages());

//        Interest interest = event.getInterest();
//        if (interest != null) {
//            if (interest.getColor() != null) {
//                String action_navigation_item_text_color = interest.getColor();
//                if (action_navigation_item_text_color.length() > 6) {
//                    action_navigation_item_text_color = action_navigation_item_text_color.substring(action_navigation_item_text_color.length()-6);
//                }
//                mEventInterestBg.setBackgroundColor(Color.parseColor("#" + action_navigation_item_text_color));
//            } else {
//                mEventInterestBg.setBackgroundColor(Color.parseColor("#FF1493"));
//            }
//
//            ArrayList<String> fullTitle = event.getInterest().getFullTitle();
//            String fullTitleString = fullTitle.get(0);
//            if (fullTitle.size() > 1) {
//                fullTitleString = fullTitleString + " / " + fullTitle.get(1);
//            }
//            mEventInterestTitle.setText(fullTitleString.toUpperCase());
//        } else {
//            mEventInterestTitle.setText("Null");
//        }

        mTitle.setText(event.getTitle());
        mPlace.setText(event.getPlace());
        mPrice.setText(event.getPriceRange());

        User author = event.getAuthor();
        if (author != null) {
            mAuthor.setText(event.getAuthor().getFullName());
        } else {
            mEventAuthor.setVisibility(View.GONE);
        }

        mDescription.setText(event.getDescription());
        if (event.getWebSite() == null || event.getWebSite().equals("") ) {
            mEventWEbSite.setVisibility(View.GONE);
        } else {
            mWebSite.setText(event.getWebSite());
        }

        if (event.getEmail() == null || event.getEmail().equals("") ) {
            mEventEmail.setVisibility(View.GONE);
        } else {
            mEmail.setText(event.getEmail());
        }
        mStartDate.setText(event.getStartDateFormatted("dd MMM"));
        mEndDate.setText(event.getEndDateFormatted("dd MMM"));
        mVotesCount.setText(String.valueOf(event.getVotesCount()));

        mFavoritesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.isInFavorites()) {
                    mFavoritesImage.setImageResource(R.drawable.ic_in_favorites_white);
//                    APIService.doUnFav(event.getId());
                } else {
                    mFavoritesImage.setImageResource(R.drawable.ic_not_in_favorites_white);
//                    APIService.doFav(event.getId());
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


    @Override
    protected void onResume() {
        super.onResume();
        repopulateEvent();
        if (isOrg && mFab.getVisibility() != View.VISIBLE) {
            mFab.show();
        }
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username)).setText(App.getCurrentUser().getFullName());
        ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city)).setText(App.getCurrentCity().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawerLayout = null;
        mToolbar = null;
        eventId = null;
        event = null;
        viewPager = null;
        mEventImagesSwipeAdapter = null;
        mWebSite = null;
        mEmail = null;
        mPlace = null;
        mAuthor = null;
        mDescription = null;
        mStartDate = null;
        mEndDate = null;
        mEventAuthor = null;
        mEventWEbSite = null;
        mEventEmail = null;
        mFab = null;
        ctl = null;
        mPrice = null;
        mTitle = null;
        mVotesCount = null;
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

            }
        };
    }

}
