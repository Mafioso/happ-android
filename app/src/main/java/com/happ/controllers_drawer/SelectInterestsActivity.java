package com.happ.controllers_drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.InterestsListAdapter;
import com.happ.controllers.UserActivity;
import com.happ.fragments.InterestChildrenFragment;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dante on 9/6/16.
 */
//public class SelectInterestsActivity extends AppCompatActivity
//    implements InterestChildrenFragment.OnInterestChildrenInteractionListener {
    public class SelectInterestsActivity extends AppCompatActivity {

    protected RecyclerView mInterestsRecyclerView;
    private GridLayoutManager mInterestsGridLayout;
    private ArrayList<Interest> interests;
    private InterestsListAdapter mInterestsListAdapter;
    protected BroadcastReceiver interestsRequestDoneReceiver;
    protected BroadcastReceiver setInterestsOKReceiver;
    protected BroadcastReceiver getCurrentUserReceiver;
    protected BroadcastReceiver changeCityDoneReceiver;

    private LinearLayoutManager interestsListLayoutManager;
    private FloatingActionButton mFab;
    private Button mBtnSelectAllInterests;
    private int interestsPageSize;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private RelativeLayout selectedRow;
    private RelativeLayout selectedRowContainer;
    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;
    private boolean fullActivity = false;
    private NavigationView navigationMenu, navigationHeader, navigationView;
    private ViewPager mDrawerCityFragment;
    private PagerAdapter cityPageAdapter;
    private ImageView mCLoserLeftNavigation;
    private FrameLayout childrenContainer;
    private int titleBarHeight;

    private CheckBox mDrawerHeaderArrow;
    private TextView mDrawerHeaderTVCity, mDrawerHeaderTVUsername;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public SelectInterestsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            Intent intent = getIntent();
            fullActivity = intent.getBooleanExtra("is_full", false);
        setContentView(R.layout.activity_select_interests);

        titleBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            titleBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationMenu = (NavigationView) findViewById(R.id.navigation_menu);
        navigationHeader = (NavigationView) findViewById(R.id.navigation_header);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mInterestsRecyclerView = (RecyclerView) findViewById(R.id.activity_interests_rv);
        childrenContainer = (FrameLayout) findViewById(R.id.city_children_fragment_container);
        mCLoserLeftNavigation = (ImageView) findViewById(R.id.close_left_navigation);
        mBtnSelectAllInterests = (Button) findViewById(R.id.btn_select_interests);
        mDrawerHeaderArrow = ((CheckBox)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_header_arrow));
        mDrawerHeaderTVCity = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_city));
        mDrawerHeaderTVUsername = ((TextView)navigationHeader.getHeaderView(0).findViewById(R.id.drawer_username));

        selectedRow = (RelativeLayout) findViewById(R.id.selected_row);
        selectedRowContainer = (RelativeLayout) findViewById(R.id.selected_row_container);


//        selectedRow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedRow.setVisibility(View.GONE);
//            }
//        });
//        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll);


        interestsPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));

//        setTitle(getResources().getString(R.string.select_interest_title));
        setTitle("");
        setSupportActionBar(toolbar);

        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedInterests = mInterestsListAdapter.getSelectedInterests();
                // SEND DATA TO SERVER
//                selectedInterests.remove(1);
                APIService.setInterests(selectedInterests);
                mFab.setVisibility(View.GONE);
            }
        });

        mBtnSelectAllInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService.setAllInterests(1);
            }
        });

        mInterestsGridLayout = new GridLayoutManager(this, 3);
        mInterestsRecyclerView.setHasFixedSize(true);
        mInterestsRecyclerView.setLayoutManager(mInterestsGridLayout);


//        mInterestsRecyclerView.setNestedScrollingEnabled(false);

        Realm realm = Realm.getDefaultInstance();
        interests = new ArrayList<>();

        try {
            RealmResults<Interest> interestsResults = realm.where(Interest.class).isNull("parentId").findAll();
//            RealmResults<Interest> interestsResults = realm.where(Interest.class).equalTo("title", "tenetur").findAll();
            interests = (ArrayList<Interest>) realm.copyFromRealm(interestsResults);
        } catch (Exception ex) {
            Log.e("HAPP", "SelectInterestActivity > onCreate "+ex.getLocalizedMessage());
        } finally {
            realm.close();
        }

        mInterestsListAdapter = new InterestsListAdapter(SelectInterestsActivity.this, interests);
        if (fullActivity) {
            mInterestsListAdapter.setUserAcivityIds(App.getCurrentUser().getInterestIds());
        }
        mInterestsRecyclerView.setAdapter(mInterestsListAdapter);
        mFab.setVisibility(View.VISIBLE);
        mInterestsListAdapter.setOnInterestsSelectListener(new InterestsListAdapter.OnInterestsSelectListener() {
            @Override
            public void onInterestsSelected(ArrayList<String> selectedChildren, String parentId) {
                //Простой клик. Полносью выбирается интерес
            }

            @Override
            public void onInterestExpandRequested(String interestId, int position, int top, int height) {
                final ArrayList<String> children = new ArrayList<String>();
                ArrayList<String> parents = new ArrayList<String>();

                parents.add(interestId);
                int row_pos = position % 3;
                int row = position / 3;
                for (int i=1; i<3; i++) {
                    int pos;
                    if ((position + i) / 3 > row) {
                        pos = position + i - 3;
                        if (interests.size() > pos) parents.add(0, interests.get(pos).getId());
                    } else {
                        pos = position + i;
                        if (interests.size() > pos) parents.add(interests.get(pos).getId());
                    }
                }

                Realm realm = Realm.getDefaultInstance();
                RealmResults<Interest> child_results = realm.where(Interest.class).equalTo("parentId", interestId).findAll();
                if (child_results != null) {
                    ArrayList<Interest> children_interests = (ArrayList<Interest>) realm.copyFromRealm(child_results);

                    for (int i=0; i<children_interests.size(); i++) {
                        children.add(children_interests.get(i).getId());
                    }
                }
                realm.close();


                InterestChildrenFragment icf = InterestChildrenFragment.newInstance(interestId,
                        children, parents, top-titleBarHeight, height);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.city_children_fragment_container, icf)
                        .commit();

            }
        });

        toolbar.setBackgroundResource(android.R.color.transparent);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();


        if (fullActivity) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_grey);
            actionBar.setDisplayHomeAsUpEnabled(true);

            navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {

                    if (menuItem.getItemId() == R.id.nav_item_logout) {
                        App.doLogout(SelectInterestsActivity.this);
                    }

                    if (menuItem.getItemId() == R.id.nav_item_settings) {
                        Intent goToFeedIntent = new Intent(SelectInterestsActivity.this, SettingsActivity.class);
                        goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(goToFeedIntent);
                        overridePendingTransition(0,0);

                    }
                    if (menuItem.getItemId() == R.id.nav_item_organizer) {
                        Intent goToFeedIntent = new Intent(SelectInterestsActivity.this, ConfirmEmailActivity.class);
                        goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(goToFeedIntent);
                        overridePendingTransition(0,0);
                    }

                    if (menuItem.getItemId() == R.id.nav_item_interests) {
                        Intent intent = new Intent(SelectInterestsActivity.this, SelectInterestsActivity.class);
                        intent.putExtra("is_full", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0,0);

                    }

                    if (menuItem.getItemId() == R.id.nav_item_feed) {
                        Intent intent = new Intent(SelectInterestsActivity.this, FeedActivity.class);
                        intent.putExtra("is_full", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            });

            navigationMenu.getMenu().findItem(R.id.nav_item_interests).setChecked(true);
            navigationMenu.getMenu().findItem(R.id.nav_item_interests).setIcon(R.drawable.happ_drawer_icon);

            mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullName());
            mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());

            mDrawerHeaderTVUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectInterestsActivity.this, UserActivity.class);
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


        } else {
//            mDrawerLayout.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            navigationView.setVisibility(View.GONE);
        }

        if (this.interestsRequestDoneReceiver == null) {
            this.interestsRequestDoneReceiver = createInterestsRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(this.interestsRequestDoneReceiver, new IntentFilter(BroadcastIntents.INTERESTS_REQUEST_OK));
        }
        if (this.setInterestsOKReceiver == null) {
            this.setInterestsOKReceiver = createSetInterestsOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(this.setInterestsOKReceiver, new IntentFilter(BroadcastIntents.SET_INTERESTS_OK));
        }
        if (this.getCurrentUserReceiver == null) {
            this.getCurrentUserReceiver = createGetUserDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(this.getCurrentUserReceiver, new IntentFilter(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK));
        }
        if (this.changeCityDoneReceiver == null) {
            this.changeCityDoneReceiver = changeCityReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(this.changeCityDoneReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
        }
        mCLoserLeftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerHeaderArrow.isChecked()) {
                    mDrawerHeaderArrow.setChecked(false);
                    mDrawerCityFragment.setVisibility(View.GONE);
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        APIService.getInterests();
        APIService.getInterests(2);
        createScrollListener();

        mInterestsListAdapter.setOnToastBeforeLongClicked(new InterestsListAdapter.OnToastBeforeLongClicked() {
            @Override
            public void longClickedListener() {
                Toast.makeText(SelectInterestsActivity.this, "LOL", Toast.LENGTH_SHORT).show();
            }
        });


    }

//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }


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
        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullName());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());
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

    private BroadcastReceiver createInterestsRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInterestsList();
            }
        };
    }

    private BroadcastReceiver createSetInterestsOKReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                APIService.getCurrentUser();
            }
        };
    }

    private BroadcastReceiver createGetUserDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent feedIntent = new Intent(SelectInterestsActivity.this, FeedActivity.class);
                feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SelectInterestsActivity.this.startActivity(feedIntent);
                SelectInterestsActivity.this.overridePendingTransition(0,0);
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

    protected void updateInterestsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Interest> interestsRealmResults = realm.where(Interest.class).isNull("parentId").findAll();
//        RealmResults<Interest> interestsRealmResults = realm.where(Interest.class).equalTo("title", "tenetur").findAll();
        interests = (ArrayList<Interest>)realm.copyFromRealm(interestsRealmResults);
        mInterestsListAdapter.updateData(interests);
        realm.close();
    }

    protected void createScrollListener() {
        mInterestsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (mFab.getVisibility() != View.GONE) mFab.hide();
                    visibleItemCount = mInterestsGridLayout.getChildCount();
                    totalItemCount = mInterestsGridLayout.getItemCount();
                    firstVisibleItem = mInterestsGridLayout.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        loading = true;
                        int nextPage = (totalItemCount / interestsPageSize) + 1;
                        APIService.getInterests(nextPage);
                    }
                }
                if (dy < 0) {
                    if (mFab.getVisibility() != View.VISIBLE) mFab.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.interestsRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(this.interestsRequestDoneReceiver);
            this.interestsRequestDoneReceiver = null;
        }
        if (this.setInterestsOKReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(this.setInterestsOKReceiver);
            this.setInterestsOKReceiver = null;
        }
        if (this.getCurrentUserReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(this.getCurrentUserReceiver);
            this.getCurrentUserReceiver = null;
        }
        if (this.changeCityDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(this.changeCityDoneReceiver);
            this.changeCityDoneReceiver = null;
        }
    }
}
