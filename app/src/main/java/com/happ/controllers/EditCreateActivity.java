package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.EventImagesSwipeAdapter;
import com.happ.controllers_drawer.EventActivity;
import com.happ.models.Event;
import com.happ.models.EventPhones;
import com.happ.models.GeopointResponse;
import com.happ.models.User;
import com.happ.retrofit.APIService;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;


/**
 * Created by dante on 8/19/16.
 */
public class    EditCreateActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button mBtnCreateSave;
    private Event event;
    private String eventId;

    private EventImagesSwipeAdapter mEventImagesSwipeAdapter;
    private BroadcastReceiver createEventReceiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");
        setContentView(R.layout.activity_edit_create);
        binds();

        if (eventId == null) {
            setTitle(getString(R.string.create_event));
            event = new Event();
        } else {
            setTitle(getString(R.string.edit_event));
            Realm realm = Realm.getDefaultInstance();
            event = realm.where(Event.class).equalTo("id", eventId).findFirst();
            realm.close();
        }


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_right_arrow_grey);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }


        mBtnCreateSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(EditCreateActivity.this, view);
                saveEvent();
            }
        });

        if (createEventReceiver == null) {
            createEventReceiver = createSaveDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(createEventReceiver, new IntentFilter(BroadcastIntents.EVENTCREATE_REQUEST_OK));
        }
    }

    private void binds() {
        toolbar = (Toolbar) findViewById(R.id.ll_toolbar);
        mBtnCreateSave = (Button) findViewById(R.id.btn_editcreate_save);
        mBtnCreateSave = (Button) findViewById(R.id.btn_editcreate_save);

    }
    private void saveEvent() {
        event.setLocalOnly(true);

        if (eventId != null) {
            event.setLocalId(eventId);
        }
        event.setId("local_event_" + (new Date()).getTime());

        event.setTitle("CS:GO Beta test");
        event.setDescription("Test Description ...");
        event.setCityId(App.getCurrentCity().getId());
        event.setCurrencyId(App.getCurrentUser().getSettings().getCurrency());
        event.setPlace("Esentai Mall");
        event.setStartDate(new Date());
        event.setEndDate(new Date());

        event.setLowestPrice(Integer.parseInt("0"));
        event.setHighestPrice(Integer.parseInt("1000"));
        event.setWebSite("http://vk.com/");
        RealmList<EventPhones> phones = new RealmList<EventPhones>();
        for (int i = 0; i < 3; i++) {
            EventPhones phone1 = new EventPhones();
            phone1.setPhone("+7701774176"+i);
            phones.add(phone1);
        }
        event.setPhones(phones);

        RealmList<GeopointResponse> geopoint = new RealmList<GeopointResponse>();
        GeopointResponse geopoinstResponse = new GeopointResponse();
        geopoinstResponse.setLat((float) 43.239032);
        geopoinstResponse.setLng((float) 76.952740);
        geopoint.add(geopoinstResponse);
        event.setGeopoint(geopoint);

        event.setEmail("dante666lcf@gmail.com");

        User author = new User();
        author.setFullName(App.getCurrentUser().getFn());
        event.setAuthor(author);


        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(event);
        realm.commitTransaction();
        realm.close();

        if (eventId == null) {
            APIService.createEvent(event.getId());
        } else {
            APIService.doEventEdit(event.getId());
        }

    }


    public static void hideSoftKeyboard (Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (createEventReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(createEventReceiver);
            createEventReceiver = null;
        }
    }

    private BroadcastReceiver createSaveDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String evendId = intent.getStringExtra("event_id");
                intent = new Intent(EditCreateActivity.this, EventActivity.class);
                intent.putExtra("in_event_activity", true);
                intent.putExtra("event_id", evendId);
                startActivity(intent);

//                intent = new Intent(context, EventActivity.class);
//                intent.putExtra("event_id", evendId);
//                intent.putExtra("in_event_activity", true);
//                startActivity(intent);

//                final Snackbar snackbar = Snackbar.make(mScrollView, getResources().getString(R.string.event_done), Snackbar.LENGTH_INDEFINITE);
//                snackbar.setAction(getResources().getString(R.string.event_done_action), new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            snackbar.dismiss();
//                        }
//                    });
//                snackbar.show();
            }
        };
    }
}
