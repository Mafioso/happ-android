package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.CurrencyListAdapter;
import com.happ.models.Currency;
import com.happ.retrofit.APIService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dante on 11/24/16.
 */
public class ChangeCurrencyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mCurrencyRecyclerView;
    private LinearLayoutManager llm;
    private ArrayList<Currency> currencies;

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private int visibleThreshold;
    private int currencyPageSize;
    private BroadcastReceiver currenciesRequestDoneReceiver;
    private BroadcastReceiver changeCurrencyRequestDoneReceiver;

    private boolean fromSettings = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSettings = getIntent().getBooleanExtra("from_settings", false);
        setContentView(R.layout.activity_change_currency);
        setTitle(R.string.change_currency);

        currencyPageSize = Integer.parseInt(this.getString(R.string.event_feeds_page_size));
//        visibleThreshold = Integer.parseInt(this.getString(R.string.event_feeds_visible_treshold_for_loading_next_items));
        visibleThreshold = 10;
        toolbar = (Toolbar) findViewById(R.id.select_currency_toolbar);
        mCurrencyRecyclerView = (RecyclerView) findViewById(R.id.activity_currency_rv);


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_right_arrow_grey);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        llm = new LinearLayoutManager(ChangeCurrencyActivity.this);
        mCurrencyRecyclerView.setLayoutManager(llm);
        currencies = new ArrayList<>();

        CurrencyListAdapter cla = new CurrencyListAdapter(this, currencies);
        cla.setOnCurrencyItemSelectListener(new CurrencyListAdapter.SelectCurrencyItemListener() {
            @Override
            public void onCurrencyItemSelected(Currency currency) {
                APIService.setCurrency(currency.getId());
            }
        });
        mCurrencyRecyclerView.setAdapter(cla);

        APIService.getCurrencies(1);
        APIService.getCurrencies(2);

        createScrollListener();

        if (currenciesRequestDoneReceiver == null) {
            currenciesRequestDoneReceiver = createCurrenciesRequestDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(currenciesRequestDoneReceiver, new IntentFilter(BroadcastIntents.CURRENCY_REQUEST_OK));
        }

        if (changeCurrencyRequestDoneReceiver == null) {
            changeCurrencyRequestDoneReceiver = changeCurrencyReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(changeCurrencyRequestDoneReceiver, new IntentFilter(BroadcastIntents.SET_CURRENCY_OK));
        }


    }


    protected void updateCurrenciesList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Currency> currencyRealmResults = realm.where(Currency.class).findAll();
        currencies = (ArrayList<Currency>) realm.copyFromRealm(currencyRealmResults);
        ((CurrencyListAdapter)mCurrencyRecyclerView.getAdapter()).updateData(currencies);
        realm.close();
    }
    protected void createScrollListener() {
        mCurrencyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    firstVisibleItem = llm.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        loading = true;
                        int nextPage = (totalItemCount / currencyPageSize) + 1;
                        APIService.getCurrencies(nextPage);
                    }
                }
                if (dy < 0) {

                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private BroadcastReceiver createCurrenciesRequestDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateCurrenciesList();
            }
        };
    }

    private BroadcastReceiver changeCurrencyReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateCurrenciesList();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (currenciesRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(currenciesRequestDoneReceiver);
            currenciesRequestDoneReceiver = null;
        }

        if (changeCurrencyRequestDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(changeCurrencyRequestDoneReceiver);
            changeCurrencyRequestDoneReceiver = null;
        }

    }
}
