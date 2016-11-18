package com.happ.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.controllers_drawer.SelectInterestsActivity;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.City;
import com.happ.retrofit.APIService;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import codetail.graphics.drawables.DrawableHotspotTouch;
import codetail.graphics.drawables.LollipopDrawable;
import codetail.graphics.drawables.LollipopDrawablesCompat;

/**
 * Created by dante on 9/9/16.
 */
public class CityActivity extends AppCompatActivity {

    private TextView mTVyourcity, mTVnotselected;
    private Button mBtnSave;
    private City selectedCity;

    private FrameLayout citiesListFragment;

    private BroadcastReceiver setCitiesOKReceiver;
    private RelativeLayout citySelectLayout;

    public Drawable getDrawableCompat(int id){
        return LollipopDrawablesCompat.getDrawable(getResources(), id, getTheme());
    }


    private void hideCitiesList(float x, float y) {
        final ViewGroup sceneRoot = (ViewGroup)findViewById(R.id.scene_root);

        TransitionSet set = new TransitionSet();
        set.addTransition(new ChangeBounds());
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set.setDuration(300);
        set.setInterpolator(PathInterpolatorCompat.create(0.4f, 0.0f, 0.6f, 1.0f));
        TransitionManager.beginDelayedTransition(sceneRoot, set);

        RelativeLayout.LayoutParams lparams = (RelativeLayout.LayoutParams) citiesListFragment.getLayoutParams();
        lparams.setMargins((citySelectLayout.getLeft()+citySelectLayout.getWidth()/2), citySelectLayout.getTop(), 0, 0);
        lparams.width = 0;
        lparams.height = 0;
        sceneRoot.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        citiesListFragment.setLayoutParams(lparams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        mTVyourcity = (TextView) findViewById(R.id.tv_your_city);
        mTVnotselected = (TextView) findViewById(R.id.tv_not_selected);
        mBtnSave = (Button) findViewById(R.id.btn_city_save);

        citySelectLayout = (RelativeLayout) findViewById(R.id.city_select_layout);
        citySelectLayout.setBackgroundDrawable(getDrawableCompat(R.drawable.ripple_accent));
        citySelectLayout.setClickable(true);
        citySelectLayout.setOnTouchListener(new DrawableHotspotTouch((LollipopDrawable)citySelectLayout.getBackground()));
        citiesListFragment = (FrameLayout) findViewById(R.id.fl_select_city);


        citySelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                citySelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                RelativeLayout.LayoutParams lparams = (RelativeLayout.LayoutParams) citiesListFragment.getLayoutParams();
                lparams.setMargins((citySelectLayout.getLeft()+citySelectLayout.getWidth()/2), citySelectLayout.getTop(), 0, 0);
                lparams.width = 0;
                lparams.height = 0;

                citiesListFragment.setLayoutParams(lparams);
            }
        });

        citySelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SelectCityFragment scf = new SelectCityFragment();
                Bundle args = new Bundle();
                args.putBoolean("from_city_activity", true);
                scf.setArguments(args);

                scf.setOnCitySelectListener(new SelectCityFragment.OnCitySelectListener() {
                    @Override
                    public void onCitySelected(City city, float x, float y) {
                        selectedCity = city;
                        mTVnotselected.setText(selectedCity.getName());
                        hideCitiesList(x, y);
                    }

                    @Override
                    public void onCancel(float x, float y) {
                        hideCitiesList(x, y);
                    }

                });
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(citiesListFragment.getId(), scf)
                        .commit();

                final ViewGroup sceneRoot = (ViewGroup)findViewById(R.id.scene_root);



                TransitionSet set = new TransitionSet();
                set.addTransition(new ChangeBounds());
                set.setOrdering(TransitionSet.ORDERING_TOGETHER);
                set.setDuration(250);
                set.setInterpolator(PathInterpolatorCompat.create(0.4f, 0.0f, 0.6f, 1.0f));
                TransitionManager.beginDelayedTransition(sceneRoot, set);

                RelativeLayout.LayoutParams  lparams = (RelativeLayout.LayoutParams) citiesListFragment.getLayoutParams();
                lparams.setMargins(0, 0, 0, 0);
                lparams.width = sceneRoot.getWidth();
                lparams.height = sceneRoot.getHeight();
                sceneRoot.setBackgroundColor(getResources().getColor(R.color.dark57));
                citiesListFragment.setLayoutParams(lparams);

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
