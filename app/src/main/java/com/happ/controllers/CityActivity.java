package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
    private Button mBtnSave;
    private City selectedCity;

    private BroadcastReceiver setCitiesOKReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        mTVyourcity = (TextView) findViewById(R.id.tv_your_city);
        mTVnotselected = (TextView) findViewById(R.id.tv_not_selected);
        mBtnSave = (Button) findViewById(R.id.btn_city_save);

        mTVnotselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getSupportFragmentManager();
                SelectCityFragment scf = new SelectCityFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("from_city_activity", true);
                scf.setArguments(args);

                scf.setOnCitySelectListener(new SelectCityFragment.OnCitySelectListener() {
                    @Override
                    public void onCitySelected(City city) {
                        selectedCity = city;
                        mTVnotselected.setText(selectedCity.getName());
                    }
                });
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fl_select_city, scf)
                        .commit();
            }
        });
        mBtnSave.setVisibility(View.GONE);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService.setCity(selectedCity.getId());
            }
        });

        mTVnotselected.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    mBtnSave.setVisibility(View.VISIBLE);
                } else {
                    mBtnSave.setVisibility(View.GONE);
                }
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
