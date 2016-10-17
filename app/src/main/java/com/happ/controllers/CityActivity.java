package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.controllers_drawer.SelectInterestsActivity;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.City;
import com.happ.retrofit.APIService;

/**
 * Created by dante on 9/9/16.
 */
public class CityActivity extends AppCompatActivity {

    private TextView mTVyourcity, mTVnotselected;
    private Button mBtnSelectCity;
    private FloatingActionButton mFab;
    private City selectedCity;

    private BroadcastReceiver setCitiesOKReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        mTVyourcity = (TextView) findViewById(R.id.tv_your_city);
        mTVnotselected = (TextView) findViewById(R.id.tv_not_selected);
        mBtnSelectCity = (Button) findViewById(R.id.btn_select_city);
        mBtnSelectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                SelectCityFragment editNameDialogFragment = SelectCityFragment.newInstance();
                editNameDialogFragment.setOnCitySelectListener(new SelectCityFragment.OnCitySelectListener() {
                    @Override
                    public void onCitySelected(City city) {
                        mFab.show();
                        selectedCity = city;
                        mTVnotselected.setText(selectedCity.getName());
                        mTVnotselected.setTextColor(getResources().getColor(R.color.dark80));
                    }
                });
                editNameDialogFragment.show(fm, "fragment_select_city");
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.select_city_string));

        mFab = (FloatingActionButton) findViewById(R.id.fab_city);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService.setCity(selectedCity.getId());
            }
        });

        if (setCitiesOKReceiver == null) {
            setCitiesOKReceiver = createSetCitiesOKReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(setCitiesOKReceiver, new IntentFilter(BroadcastIntents.SET_CITIES_OK));
        }

    }

    private BroadcastReceiver createSetCitiesOKReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent feedIntent = new Intent(CityActivity.this, SelectInterestsActivity.class);
                feedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CityActivity.this.startActivity(feedIntent);
                CityActivity.this.overridePendingTransition(0,0);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (setCitiesOKReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(setCitiesOKReceiver);
            setCitiesOKReceiver = null;
        }
    }

}
