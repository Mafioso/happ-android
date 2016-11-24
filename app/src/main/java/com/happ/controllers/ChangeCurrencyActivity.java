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
        try {
            RealmResults<Currency> currencyRealmResults = realm.where(Currency.class).findAll();
            currencies = (ArrayList<Currency>) realm.copyFromRealm(currencyRealmResults);
        } catch (Exception ex) {
            Log.e("HAPP", "ChangeCurrencyActivity> onCreate "+ex.getLocalizedMessage());
        } finally {
            realm.close();
        }

        CurrencyListAdapter cla = new CurrencyListAdapter(this, currencies);
        mCurrencyRecyclerView.setAdapter(cla);

        APIService.getCurrencies();
    }
}
