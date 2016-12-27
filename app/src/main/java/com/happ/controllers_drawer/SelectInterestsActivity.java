package com.happ.controllers_drawer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.BuildConfig;
import com.happ.R;
import com.happ.adapters.InterestsListAdapter;
import com.happ.controllers.UserActivity;
import com.happ.fragments.InterestChildrenFragment;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by dante on 9/6/16.
 */
public class SelectInterestsActivity extends AppCompatActivity
    implements InterestChildrenFragment.OnInterestChildrenInteractionListener {

    protected RecyclerView mInterestsRecyclerView;
    private GridLayoutManager mInterestsGridLayout;
    private ArrayList<Interest> interests;
    private InterestsListAdapter mInterestsListAdapter;
    protected BroadcastReceiver interestsRequestDoneReceiver;
    protected BroadcastReceiver setInterestsOKReceiver;
    protected BroadcastReceiver getCurrentUserReceiver;
    protected BroadcastReceiver changeCityDoneReceiver;
    protected BroadcastReceiver getSelectedInterestsDoneReceiver;

    private HashMap<String, ArrayList<String>> selectedInterestIds;

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
    private TextView cityName;
    private Button mSaveButton;


    private CheckBox mDrawerHeaderArrow;
    private TextView mDrawerHeaderTVCity, mDrawerHeaderTVUsername;

    private ViewTreeObserver.OnGlobalLayoutListener mKeyboardListener;
    private boolean isKeyboarShown = false;
    private boolean isSingle;
    private LinearLayout mDrawerLLFooter;
    private TextView mDrawerVersionApp;
    private AppBarLayout header;

    InterestChildrenFragment icf;

    private ImageView mDrawerHeaderAvatar;
    private RelativeLayout mDrawerHeaderAvatarPlaceholder;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    public void onBackPressed() {
        if (icf != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(icf)
                    .commit();
            icf = null;
            if (!isSingle) {
                mSaveButton.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }

    public SelectInterestsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        fullActivity = intent.getBooleanExtra("is_full", false);
        isSingle = intent.getBooleanExtra("is_single", false);
        setContentView(R.layout.activity_select_interests);
        final Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
//        int height = display.getHeight();  // deprecated
        titleBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            titleBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        mDrawerCityFragment = (ViewPager) findViewById(R.id.drawer_viewpager);
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
        mDrawerLLFooter = (LinearLayout) findViewById(R.id.ll_drawer_footer);
        selectedRow = (RelativeLayout) findViewById(R.id.selected_row);
        selectedRowContainer = (RelativeLayout) findViewById(R.id.selected_row_container);
        mDrawerVersionApp = (TextView) findViewById(R.id.tv_drawer_version_app);
        cityName = (TextView) findViewById(R.id.header_city_name);
        String versionName = BuildConfig.VERSION_NAME;
        mSaveButton = (Button)findViewById(R.id.save_interests);
        mDrawerVersionApp.setText(getResources().getString(R.string.app_name) + " " + "v" + versionName);
        header = (AppBarLayout) findViewById(R.id.header);
        mDrawerHeaderAvatar = ((ImageView)navigationHeader.getHeaderView(0).findViewById(R.id.dr_iv_user_avatar));
        mDrawerHeaderAvatarPlaceholder = ((RelativeLayout)navigationHeader.getHeaderView(0).findViewById(R.id.dr_avatar_placeholder));

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
        cityName.setText(App.getCurrentCity().getName());

//        if (!fullActivity) {
//            Intent data = new Intent();
//            String interestId = "Result to be returned....";
//            data.putExtra("ID", interestId);
//            setResult(RESULT_OK, data);
//            finish();
//        }

        if (isSingle) {
            mBtnSelectAllInterests.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            mInterestsRecyclerView.setPadding(0,0,0,0);
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedInterests = new ArrayList<String>();

                for (Iterator<String> key = selectedInterestIds.keySet().iterator(); key.hasNext(); ) {
                    String parentId = key.next();
                    if (selectedInterestIds.get(parentId).size() == 0) {
                        selectedInterests.add(parentId);
                    } else {
                        for (Iterator<String> child = selectedInterestIds.get(parentId).iterator(); child.hasNext(); ) {
                            selectedInterests.add(child.next());
                        }
                    }
                }

                APIService.setInterests(selectedInterests);
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
        selectedInterestIds = new HashMap<>();

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

        mInterestsListAdapter = new InterestsListAdapter(SelectInterestsActivity.this, interests, true);
        if (fullActivity && !isSingle) {
            mInterestsListAdapter.setUserAcivityIds(App.getCurrentUser().getInterestIds());
        }
        mInterestsRecyclerView.setAdapter(mInterestsListAdapter);
        mInterestsListAdapter.setSingle(isSingle);
        mInterestsListAdapter.setOnInterestsSelectListener(new InterestsListAdapter.OnInterestsSelectListener() {
            @Override
            public void onParentInterestChanged(String parentId) {
                if (isSingle) {
                    Intent data = new Intent();
                    data.putExtra("ID", parentId);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    //Простой клик. Полносью выбирается интерес
                    if (selectedInterestIds.get(parentId) == null) {
                        selectedInterestIds.put(parentId, new ArrayList<String>());
                    } else {
                        if (selectedInterestIds.get(parentId).size() > 0) {
                            selectedInterestIds.get(parentId).clear();
                        } else {
                            selectedInterestIds.remove(parentId);
                        }
                    }
                    mInterestsListAdapter.updateSelectedInterests(selectedInterestIds);
                }
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


                ArrayList<String> selectedChildren = null;
                if (!isSingle) {
                    selectedChildren =  selectedInterestIds.get(interestId);
                }
                if (selectedChildren == null) selectedChildren = new ArrayList<>();
                icf = InterestChildrenFragment.newInstance(interestId,
                        children, parents, selectedChildren,
                        top-titleBarHeight, height, isSingle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.city_children_fragment_container, icf)
                        .commit();

                mSaveButton.setVisibility(View.GONE);

            }
        });

        toolbar.setBackgroundResource(android.R.color.transparent);
        setSupportActionBar(toolbar);

        if (fullActivity && isSingle) {
            toolbar.setNavigationIcon(R.drawable.ic_close_white);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if (fullActivity && !isSingle) {

            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_gray);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

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
                        if (App.getCurrentUser().getRole() != 0 ) {
                            Intent goToFeedIntent = new Intent(SelectInterestsActivity.this, OrganizerModeActivity.class);
                            goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(goToFeedIntent);
                            overridePendingTransition(0,0);
                        } else {
                            Intent goToFeedIntent = new Intent(SelectInterestsActivity.this, ConfirmEmailActivity.class);
                            goToFeedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(goToFeedIntent);
                            overridePendingTransition(0,0);
                        }
                    }

                    if (menuItem.getItemId() == R.id.nav_item_feed) {
                        Intent intent = new Intent(SelectInterestsActivity.this, FeedActivity.class);
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

            navigationMenu.getMenu().findItem(R.id.nav_item_interests).setChecked(true);
            navigationMenu.getMenu().findItem(R.id.nav_item_interests).setIcon(R.drawable.happ_drawer_icon);

            mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullname());
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
                        mDrawerCityFragment.setAdapter(cityPageAdapter);
                    } else {
                        mDrawerCityFragment.setVisibility(View.GONE);
                    }
                }
            });


        } else {

            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            navigationView.setVisibility(View.GONE);
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
        APIService.getSelectedInterests();
        createScrollListener();

        setListenerToRootView();
        setDrawerLayoutListener();
        setDrawerHeaderAvatar();

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
        if (this.getSelectedInterestsDoneReceiver == null) {
            this.getSelectedInterestsDoneReceiver = selectedInterestsDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(this.getSelectedInterestsDoneReceiver, new IntentFilter(BroadcastIntents.SELECTED_INTERESTS_REQUEST_OK));
        }
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

    private void rebuildSelectedInterests() {
        if (!isSingle) {
            HashMap<String, ArrayList<String>> newSelectedInterests = new HashMap<>();

            RealmList<Interest> selectedInterests = App.getCurrentUser().getInterests();

            for (Iterator<String> key = selectedInterestIds.keySet().iterator(); key.hasNext(); ) {
                String parentId = key.next();
                ArrayList<String> interestsForKey = selectedInterestIds.get(parentId);

                for (int i = selectedInterests.size() - 1; i >= 0; i--) {
                    String selectedParent = selectedInterests.get(i).getParentId();
                    boolean removed = false;
                    if (selectedParent != null && selectedParent.equals(parentId)) {
                        boolean notInInterests = true;
                        for (int j = 0; j < interestsForKey.size(); j++) {
                            if (interestsForKey.get(j).equals(selectedInterests.get(i).getId())) {
                                notInInterests = false;
                                break;
                            }
                        }
                        if (notInInterests) interestsForKey.add(selectedInterests.get(i).getId());
                        selectedInterests.remove(i);
                        removed = true;
                    }
                    if (!removed && selectedInterests.get(i).getId().equals(parentId)) {
                        selectedInterests.remove(i);
                    }
                }
                newSelectedInterests.put(parentId, interestsForKey);
            }

            Realm realm = Realm.getDefaultInstance();
            while (selectedInterests.size() > 0) {
                String id = selectedInterests.get(0).getId();
                boolean isParent = true;
                Interest temp = realm.where(Interest.class).equalTo("id", id).findFirst();
                if (temp == null) {
                    selectedInterests.remove(0);
                    continue;
                }
                if (temp.getParentId() != null) {
                    isParent = false;
                    id = temp.getParentId();
                }

                RealmResults<Interest> children = realm.where(Interest.class).equalTo("parentId", id).findAll();
                ArrayList<String> selectedChildren = new ArrayList<>();

                for (int i = 0; i < children.size(); i++) {
                    for (int j = selectedInterests.size() - 1; j >= 0; j--) {
                        if (children.get(i).getId().equals(selectedInterests.get(j).getId())) {
                            selectedChildren.add(selectedInterests.get(j).getId());
                            selectedInterests.remove(j);
                        }
                    }
                }
                newSelectedInterests.put(id, selectedChildren);
                if (isParent) selectedInterests.remove(0);
            }
            realm.close();

            selectedInterestIds = newSelectedInterests;
            this.mInterestsListAdapter.updateSelectedInterests(selectedInterestIds);
        }
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
                            mDrawerLLFooter.setVisibility(View.GONE);

                        }
                        isKeyboarShown = true;
                    }
                    else {
                        navigationHeader.setVisibility(View.VISIBLE);
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

    @Override
    public void onChildrenUpdated(String parentId, ArrayList<String> childrenIds) {
        if (isSingle) {
            Intent data = new Intent();

            if (childrenIds.size() > 0) {
                data.putExtra("ID", childrenIds.get(0));
            }
            setResult(RESULT_OK, data);
            finish();
        } else {
            ArrayList<String> currentChildren = selectedInterestIds.get(parentId);
            if (currentChildren == null) {
                selectedInterestIds.put(parentId, childrenIds);
            } else {
                if (childrenIds.size() == 0) {
                    selectedInterestIds.remove(parentId);
                } else {
                    selectedInterestIds.put(parentId, childrenIds);
                }
            }
            mInterestsListAdapter.updateSelectedInterests(selectedInterestIds);
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
        setDrawerHeaderAvatar();
        mDrawerHeaderTVUsername.setText(App.getCurrentUser().getFullname());
        mDrawerHeaderTVCity.setText(App.getCurrentCity().getName());
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

    private BroadcastReceiver selectedInterestsDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                rebuildSelectedInterests();
            }
        };
    }



    protected void updateInterestsList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Interest> interestsRealmResults = realm.where(Interest.class).isNull("parentId").findAll();
        interests = (ArrayList<Interest>)realm.copyFromRealm(interestsRealmResults);
        mInterestsListAdapter.updateData(interests);
        realm.close();
    }

    protected void createScrollListener() {
        mInterestsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
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
        if (this.getSelectedInterestsDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(this.getSelectedInterestsDoneReceiver);
            this.getSelectedInterestsDoneReceiver = null;

        }
    }
}
