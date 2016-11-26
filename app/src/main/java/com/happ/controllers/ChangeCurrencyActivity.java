package com.happ.controllers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

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

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    int currenciesPageSize = 13;

    private boolean fromSettings = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSettings = getIntent().getBooleanExtra("from_settings", false);
        setContentView(R.layout.activity_change_currency);

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
        Realm realm = Realm.getDefaultInstance();
        currencies = new ArrayList<>();

        RealmResults<Currency> currencyRealmResults = realm.where(Currency.class).findAll();
        currencies = (ArrayList<Currency>) realm.copyFromRealm(currencyRealmResults);
        realm.close();

        CurrencyListAdapter cla = new CurrencyListAdapter(this, currencies);
        mCurrencyRecyclerView.setAdapter(cla);

        APIService.getCurrencies(1);

        createScrollListener();
    }

    protected void createScrollListener() {
        mCurrencyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mCurrencyRecyclerView.getChildCount();
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
                    // End has been reached

                    Log.i("Yaeye!", "end called");

                    int nextPage = (totalItemCount / currenciesPageSize) + 1;
                    APIService.getCurrencies(nextPage);

                    loading = true;
                }
            }
        });
    }
}
