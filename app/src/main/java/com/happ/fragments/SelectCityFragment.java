package com.happ.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class SelectCityFragment extends DialogFragment {

    protected RecyclerView mCityRecyclerView;
    private ArrayList<City> cities;
    private CityListAdapter mCitiesListAdapter;
    private BroadcastReceiver citiesRequestDoneReceiver;
    private LinearLayoutManager citiesListLayoutManager;
    private OnCitySelectListener listener;
    private MaterialProgressBar mLoadingProgress;

    private int interestsPageSize;
    private boolean loading = true;
    private boolean dataLoading = false;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;
    private String searchText;
    private City selectedCity;


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

    public interface OnCitySelectListener {
        void onCitySelected(City city);
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
//
//        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.select_city_fragment, null);
//        final Activity activity = getActivity();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    final View contentView = inflater.inflate(R.layout.select_city_fragment, container, false);
    final Activity activity = getActivity();

//        final AlertDialog dialog = new AlertDialog.Builder(getContext())
//                .setTitle(getContext().getString(R.string.select_city_string))
//                .setView(contentView)
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .create();
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        interestsPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));

        search = (EditText)contentView.findViewById( R.id.search);

        mCityRecyclerView = (RecyclerView)contentView.findViewById(R.id.activity_cities_rv);
        citiesListLayoutManager = new LinearLayoutManager(activity);
        mCityRecyclerView.setLayoutManager(citiesListLayoutManager);

        cities = new ArrayList<>();

        mCitiesListAdapter = new CityListAdapter(getContext(), cities);
        mCitiesListAdapter.setOnCityItemSelectListener(new CityListAdapter.SelectCityItemListener() {
            @Override
            public void onCityItemSelected(City city) {
//                listener.onCitySelected(city);
//                SelectCityFragment.this.dismiss();
                selectedCity = city;
                APIService.setCity(selectedCity.getId());
            }
        });
        mCityRecyclerView.setAdapter(mCitiesListAdapter);

        if (citiesRequestDoneReceiver == null) {
            citiesRequestDoneReceiver = createCitiesRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(citiesRequestDoneReceiver, new IntentFilter(BroadcastIntents.CITY_REQUEST_OK));
        }

        mLoadingProgress = (MaterialProgressBar) contentView.findViewById(R.id.cities_progress);

        APIService.getCities();

        dataLoading = true;
        if (dataLoading) {
            mLoadingProgress.setVisibility(View.VISIBLE);
        } else {
            mLoadingProgress.setVisibility(View.GONE);
        }
        createScrollListener();

        addTextListener();

        return contentView;
    }

    private BroadcastReceiver createCitiesRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dataLoading = false;
                mLoadingProgress.setVisibility(View.GONE);
                updateCitiesList();
            }
        };
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

                    if (! dataLoading && !loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        loading = true;
                        int nextPage = (totalItemCount / interestsPageSize) + 1;
                        dataLoading = true;
                        mLoadingProgress.setVisibility(View.VISIBLE);
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
                mLoadingProgress.setVisibility(View.VISIBLE);
                APIService.getCities(searchText);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (citiesRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(citiesRequestDoneReceiver);
            citiesRequestDoneReceiver = null;
        }
    }
}
