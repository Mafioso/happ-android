package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.City;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dante on 9/5/16.
 */
public class SelectCityActivity extends AppCompatActivity {

    protected RecyclerView mCityRecyclerView;
    private ArrayList<City> cities;
    private CityListAdapter mCitiesListAdapter;
    private BroadcastReceiver citiesRequestDoneReceiver;
    private LinearLayoutManager citiesListLayoutManager;

    private int interestsPageSize;
    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cities);

        interestsPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));


        mCityRecyclerView = (RecyclerView) findViewById(R.id.activity_cities_rv);
        citiesListLayoutManager = new LinearLayoutManager(this);
        mCityRecyclerView.setLayoutManager(citiesListLayoutManager);
        cities = new ArrayList<>();

        mCitiesListAdapter = new CityListAdapter(this, cities);
        mCityRecyclerView.setAdapter(mCitiesListAdapter);

        citiesRequestDoneReceiver = createCitiesRequestDoneReceiver();
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(citiesRequestDoneReceiver, new IntentFilter(BroadcastIntents.CITY_REQUEST_OK));
        APIService.getCitys();

        createScrollListener();

    }

    private BroadcastReceiver createCitiesRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateCitiesList();
            }
        };
    }

    protected void updateCitiesList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<City> citiesRealmResults = realm.where(City.class).findAll();
        cities = (ArrayList<City>)realm.copyFromRealm(citiesRealmResults);
        mCitiesListAdapter.updateData(cities);
        realm.close();
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

                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        loading = true;
                        int nextPage = (totalItemCount / interestsPageSize) + 1;
                        APIService.getCitys(nextPage);
                    }
                }
                if (dy < 0) {
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
