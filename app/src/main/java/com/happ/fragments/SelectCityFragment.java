package com.happ.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.CityListAdapter;
import com.happ.models.City;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by dante on 9/5/16.
 */
public class SelectCityFragment extends Fragment {

    protected RecyclerView mCityRecyclerView;
    private ArrayList<City> cities;
    private CityListAdapter mCitiesListAdapter;
    private String mCity = "";
    private City city;

    private BroadcastReceiver citiesRequestDoneReceiver;
    private BroadcastReceiver changeCityDoneReceiver;

    private LinearLayoutManager citiesListLayoutManager;
    private OnCitySelectListener listener;
    protected OnCitySelectInNavigationListener mNDlistener;
    private MaterialProgressBar mLoadingProgress;
    private Toolbar toolbar;

    private int interestsPageSize;
    private boolean loading = true;
    private boolean dataLoading = false;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;
    private String searchText;
    private City selectedCity;
    private boolean fromCityActivity = false;
    private EditText search;


    public SelectCityFragment() {

    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static SelectCityFragment newInstance() {
        SelectCityFragment fragment = new SelectCityFragment();
        Bundle args = new Bundle();
//        args.putString("title", App.getContext().getString(R.string.select_interest_title));
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnCitySelectListener(OnCitySelectListener listener) {
        this.listener = listener;
    }

    public void setOnCitySelectInNavigationListener(OnCitySelectInNavigationListener listenerNavigationDrawer) {
        mNDlistener = listenerNavigationDrawer;
    }

    public interface OnCitySelectListener {
        void onCitySelected(City city, float x, float y);
        void onCancel(float x, float y);
    }

    public interface OnCitySelectInNavigationListener {
        void onCloseNavigationDrawer();
    }


    
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fromCityActivity = getArguments().getBoolean("from_city_activity");
    final View contentView = inflater.inflate(R.layout.select_city_fragment, container, false);

        toolbar = (Toolbar) contentView.findViewById(R.id.select_city_toolbar);
        search = (EditText)contentView.findViewById( R.id.search);
        mCityRecyclerView = (RecyclerView)contentView.findViewById(R.id.activity_cities_rv);
        mLoadingProgress = (MaterialProgressBar) contentView.findViewById(R.id.cities_progress);


        final AppCompatActivity activity = (AppCompatActivity) getActivity();


        if (fromCityActivity) {
            activity.setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_close_orange);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    clearFragment();
                    float middleX = v.getX() + (v.getWidth()/2);
                    float middleY = v.getY() + (v.getHeight()/2);
                    if (listener != null) listener.onCancel(middleX, middleY);
                }
            });
        } else {
            toolbar.setVisibility(View.GONE);
        }


        interestsPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));


        citiesListLayoutManager = new LinearLayoutManager(activity);
        mCityRecyclerView.setLayoutManager(citiesListLayoutManager);
        cities = new ArrayList<>();

        mCitiesListAdapter = new CityListAdapter(activity, cities);
        mCitiesListAdapter.setOnCityItemSelectListener(new CityListAdapter.SelectCityItemListener() {
            @Override
            public void onCityItemSelected(City city, float x, float y) {
                if (fromCityActivity) {
                    listener.onCitySelected(city, x, y);
//                    clearFragment();
                } else {
                    selectedCity = city;
                    APIService.setCity(selectedCity.getId());
                    mNDlistener.onCloseNavigationDrawer();
                }

            }
        });
        mCityRecyclerView.setAdapter(mCitiesListAdapter);


        APIService.getCities();

        dataLoading = true;
        if (dataLoading) {
//            mLoadingProgress.setVisibility(View.VISIBLE);
        } else {
            mLoadingProgress.setVisibility(View.GONE);
        }
        createScrollListener();
        addTextListener();

        if (citiesRequestDoneReceiver == null) {
            citiesRequestDoneReceiver = createCitiesRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(citiesRequestDoneReceiver, new IntentFilter(BroadcastIntents.CITY_REQUEST_OK));
        }

        if (changeCityDoneReceiver == null) {
            changeCityDoneReceiver = changeCityReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changeCityDoneReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
        }

        return contentView;
    }

    private void clearFragment() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(SelectCityFragment.this);
        trans.commit();
        manager.popBackStack();
    }

    private BroadcastReceiver createCitiesRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dataLoading = false;
                mLoadingProgress.setVisibility(View.GONE);
                if (App.getCurrentUser().getSettings().getCity() != null) {
                    updateCity();
                    updateCitiesList();
                }
            }
        };
    }

    private BroadcastReceiver changeCityReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateCity();
                updateCitiesList();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (App.getCurrentUser().getSettings().getCity() != null) {
            updateCity();
            updateCitiesList();
        }
    }


    protected void updateCity() {
        Realm realm = Realm.getDefaultInstance();
        mCity = App.getCurrentCity().getName();
        City realmCity = realm.where(City.class).equalTo("name", mCity).findFirst();
        if (realmCity != null) city = realm.copyFromRealm(realmCity);
        realm.close();
        mCitiesListAdapter.updateData();
    }

    protected void updateCitiesList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<City> citiesRealmResults;
        if (searchText != null && searchText.length() > 0) {
            citiesRealmResults = realm.where(City.class).contains("name", searchText, Case.INSENSITIVE).findAll();
        } else {
            citiesRealmResults = realm.where(City.class).findAll();
        }
        cities = (ArrayList<City>)realm.copyFromRealm(citiesRealmResults);

        realm.close();

        if (mCityRecyclerView != null && cities.size() > 0 && mCityRecyclerView.getChildAt(0) != null) {
            int itemHeight = mCityRecyclerView.getChildAt(0).getHeight();
            int rvHeight = mCityRecyclerView.getHeight();
            if (itemHeight * cities.size() < rvHeight) {
                int nextPage = (cities.size() / interestsPageSize) + 1;
                APIService.getCities(nextPage, searchText);
            }
        }
    }


    protected void createScrollListener() {
        mCityRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {

                    visibleItemCount = citiesListLayoutManager.getChildCount();
                    totalItemCount = citiesListLayoutManager.getItemCount();
                    firstVisibleItem = citiesListLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (! dataLoading && !loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        loading = true;
                        int nextPage = (totalItemCount / interestsPageSize) + 1;
                        dataLoading = true;
//                        mLoadingProgress.setVisibility(View.VISIBLE);
                        APIService.getCities(nextPage, searchText);
                    }
                }
                if (dy < 0) {
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void addTextListener(){

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                searchText = query.toString();

                Realm realm = Realm.getDefaultInstance();
                ArrayList<City> filteredList = cities;

                try {
                    RealmResults<City> filteredCities = realm.where(City.class).contains("name", searchText, Case.INSENSITIVE).findAll();
                    filteredList.clear();
                    filteredList = (ArrayList<City>) realm.copyFromRealm(filteredCities);
                } catch (Exception ex) {

                } finally {
                    realm.close();
                }

                mCitiesListAdapter.updateData(filteredList);
                dataLoading = true;
//                mLoadingProgress.setVisibility(View.VISIBLE);
                APIService.getCities(searchText);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (citiesRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(citiesRequestDoneReceiver);
            citiesRequestDoneReceiver = null;
        }

        if (changeCityDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(changeCityDoneReceiver);
            changeCityDoneReceiver = null;
        }
        super.onDestroy();
    }
}
