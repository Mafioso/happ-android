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
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.EventImagesSwipeAdapter;
import com.happ.controllers_drawer.EventActivity;
import com.happ.models.Event;
import com.happ.models.EventPhone;
import com.happ.models.GeopointResponse;
import com.happ.models.User;
import com.happ.retrofit.APIService;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;


/**
 * Created by dante on 8/19/16.
 */
public class EditCreateActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button mBtnCreateSave;
    private Event event;
    private String eventId;

    private Date mStartDate, mEndDate, mStartTime, mEndTime;

    private EditText    mEventTitle,
                        mEventInterest,
                        mEventDescription,
                        mEventPlace,
                        mEventMinPrice,
                        mEventMaxPrice,
                        mEventCurrency,
                        mEventStartDate, mEventEndDate,
                        mEventStartTime, mEventEndTime,
                        mEventWebSite,
                        mEventTicketLink,
                        mEventEmail,
                        mEventPhone;

    private ImageButton mImgBtnSelectInterest,
                        mImgBtnSelectStartDate,
                        mImgBtnSelectEndDate,
                        mImgBtnSelectPointMap,
                        mImgBtnSelectCity;

    private View mViewSpaceListenerStartTime, mViewSpaceListenerEndTime;

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
            event = realm.copyFromRealm(event);
            realm.close();

            mEventTitle.setText(event.getTitle());
            mEventDescription.setText(event.getDescription());

            if (event.getPlace() != null) mEventPlace.setText(event.getPlace());
            mEventMinPrice.setText(Integer.toString(event.getLowestPrice()));
            mEventMaxPrice.setText(Integer.toString(event.getHighestPrice()));
            if (event.getCurrency() != null) mEventCurrency.setText(event.getCurrency().getName());

            java.text.DateFormat format = DateFormat.getLongDateFormat(EditCreateActivity.this);
            mEventStartDate.setText(format.format(event.getStartDate()));
            mEventEndDate .setText(format.format(event.getEndDate()));

            java.text.DateFormat timeFormat = DateFormat.getTimeFormat(EditCreateActivity.this);
            mEventStartTime.setText(timeFormat.format(event.getStartDate()));
            mEventEndTime.setText(timeFormat.format(event.getEndDate()));

            if (event.getWebSite() != null) mEventWebSite.setText(event.getWebSite());
            mEventTicketLink.setText("ЗДЕСЬ СЫЛКА НА БИЛЕТ");
            if (event.getEmail() != null)mEventEmail.setText(event.getEmail());
            if (event.getPhones().size() > 0) mEventPhone.setText(event.getPhones().get(0).getPhone());
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
                    hideSoftKeyboard(EditCreateActivity.this, view);
                    finish();
                }
            });
        }


        mImgBtnSelectStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                if (mStartTime != null) {
                                    cal.setTime(mStartTime);
                                    cal.get(Calendar.HOUR_OF_DAY);
                                    cal.get(Calendar.MINUTE);
                                }
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                mStartDate = cal.getTime();

                                //Set Start Date in EditText
                                java.text.DateFormat format = DateFormat.getLongDateFormat(EditCreateActivity.this);
                                mEventStartDate.setText(format.format(mStartDate));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH )
                );

                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        mImgBtnSelectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                if (mEndTime != null) {
                                    cal.setTime(mEndTime);
                                    cal.get(Calendar.HOUR_OF_DAY);
                                    cal.get(Calendar.MINUTE);
                                }
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                mEndDate = cal.getTime();

                                //Set End Date in EditText
                                java.text.DateFormat format = DateFormat.getLongDateFormat(EditCreateActivity.this);
                                mEventEndDate.setText(format.format(mEndDate));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH )
                );

                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        mViewSpaceListenerStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStartDate != null ) {
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(mStartDate);
                            cal.get(Calendar.YEAR);
                            cal.get(Calendar.MONTH);
                            cal.get(Calendar.DAY_OF_MONTH);
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            cal.set(Calendar.MINUTE, minute);
                            mStartTime = cal.getTime();
                            mStartDate = mStartTime;

                            //Set Start Time in EditText
                            java.text.DateFormat format = DateFormat.getTimeFormat(EditCreateActivity.this);
                            mEventStartTime.setText(format.format(mStartTime));

                        }
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                } else {
                    Toast.makeText(EditCreateActivity.this, "Вы не указали старт события.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewSpaceListenerEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStartDate != null) {
                    Calendar now = Calendar.getInstance();

                    TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                            Calendar cal = Calendar.getInstance();
                            if (mEndDate != null) {
                                cal.setTime(mEndDate);
                                cal.get(Calendar.YEAR);
                                cal.get(Calendar.MONTH);
                                cal.get(Calendar.DAY_OF_MONTH);
                            } else {
                                cal.setTime(mStartDate);
                                cal.get(Calendar.YEAR);
                                cal.get(Calendar.MONTH);
                                cal.get(Calendar.DAY_OF_MONTH);
                            }
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            cal.set(Calendar.MINUTE, minute);
                            mEndTime = cal.getTime();
                            mEndDate = mEndTime;

                            //Set End Time in EditText
                            java.text.DateFormat format = DateFormat.getTimeFormat(EditCreateActivity.this);
                            mEventEndTime .setText(format.format(mEndTime));
                        }
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                } else {
                    Toast.makeText(EditCreateActivity.this, "Вы не указали старт события", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mImgBtnSelectPointMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mImgBtnSelectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });




        mBtnCreateSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(EditCreateActivity.this, view);
//                saveEvent();
                Toast.makeText(EditCreateActivity.this, "На стадии разработки Create and Save Button", Toast.LENGTH_SHORT).show();
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

        mEventTitle = (EditText) findViewById(R.id.input_ec_title);
        mEventInterest = (EditText) findViewById(R.id.input_ec_select_interest);
        mEventDescription = (EditText) findViewById(R.id.input_ec_description);
        mEventPlace = (EditText) findViewById(R.id.input_ec_place);
        mEventMinPrice = (EditText) findViewById(R.id.input_ec_min_price);
        mEventMaxPrice = (EditText) findViewById(R.id.input_ec_max_price);
        mEventCurrency = (EditText) findViewById(R.id.input_ec_currency);
        mEventStartDate = (EditText) findViewById(R.id.input_ec_startdate);
        mEventEndDate = (EditText) findViewById(R.id.input_ec_enddate);
        mEventStartTime = (EditText) findViewById(R.id.input_ec_starttime);
        mEventEndTime = (EditText) findViewById(R.id.input_ec_endtime);
        mEventWebSite = (EditText) findViewById(R.id.input_ec_website);
        mEventTicketLink = (EditText) findViewById(R.id.input_ec_ticket_link);
        mEventEmail = (EditText) findViewById(R.id.input_ec_email);
        mEventPhone = (EditText) findViewById(R.id.input_ec_phone);

        mImgBtnSelectInterest = (ImageButton) findViewById(R.id.ibtn_ec_edit_interest);
        mImgBtnSelectStartDate = (ImageButton) findViewById(R.id.ibtn_ec_edit_startdate);
        mImgBtnSelectEndDate = (ImageButton) findViewById(R.id.ibtn_ec_edit_enddate);
        mImgBtnSelectPointMap = (ImageButton) findViewById(R.id.ibtn_ec_edit_map);
        mImgBtnSelectCity = (ImageButton) findViewById(R.id.ibtn_ec_edit_city);

        mViewSpaceListenerStartTime = (View) findViewById(R.id.view_click_starttime);
        mViewSpaceListenerEndTime = (View) findViewById(R.id.view_click_endtime);

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
        RealmList<EventPhone> phones = new RealmList<EventPhone>();
        for (int i = 0; i < 3; i++) {
            EventPhone phone1 = new EventPhone();
            phone1.setPhone("+7701774176"+i);
            phones.add(phone1);
        }
        event.setPhones(phones);


        GeopointResponse geopoinstResponse = new GeopointResponse();
        geopoinstResponse.setLat((float) 43.239032);
        geopoinstResponse.setLng((float) 76.952740);
        event.setGeopoint(geopoinstResponse);

        event.setEmail("dante666lcf@gmail.com");

        User author = new User();
        author.setFullname(App.getCurrentUser().getFn());
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


    private BroadcastReceiver createSaveDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String evendId = intent.getStringExtra("event_id");

                intent = new Intent(EditCreateActivity.this, EventActivity.class);
                intent.putExtra("in_event_activity", true);
                intent.putExtra("is_organizer", true);
                intent.putExtra("event_id", evendId);
                startActivity(intent);

//                final Snackbar snackbar = Snackbar.make(mScrollView, getResources().getString(R.string.event_done), Snackbar.LENGTH_INDEFINITE);
////                snackbar.setAction(getResources().getString(R.string.event_done_action), new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////                            snackbar.dismiss();
////                        }
////                    });
////                snackbar.show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (createEventReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(createEventReceiver);
            createEventReceiver = null;
        }
    }

}
