package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.adapters.EventImagesSwipeAdapter;
import com.happ.fragments.EventInterestFragment;
import com.happ.models.Event;
import com.happ.models.Interest;
import com.happ.retrofit.APIService;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;


/**
 * Created by dante on 8/19/16.
 */
public class EditActivity extends AppCompatActivity {

//    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private EditText mEditTitle, mEditDescription, mEditInterests, mEditStartDate, mEditFinishDate,
            mPlace, mMinPrice, mMaxPrice, mWebsite;
    private ImageButton mButtonSelectInterest;
    private ViewPager mSelectImage;
    private Event event;
    private Interest selectedInterest;
    private String eventId;
    private ImageButton btnStartDate, btnEndDate;
    private NestedScrollView mScrollView;
    private FloatingActionButton mFab;
    private CheckBox mCheckOrgRules;
    private TextView mLinkOrgRules;
    private LinearLayout mLLOrgRules;

    private DateTime startDate;
    private DateTime endDate;

    private EventImagesSwipeAdapter mEventImagesSwipeAdapter;
    private BroadcastReceiver saveDoneReceiver;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (saveDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(saveDoneReceiver);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");
        if (eventId != null) {
            Realm realm = Realm.getDefaultInstance();
            event = realm.where(Event.class).equalTo("id", eventId).findFirst();
            event = realm.copyFromRealm(event);
            realm.close();
        } else {
            event = new Event();
        }

        setContentView(R.layout.activity_edit);

        mLLOrgRules = (LinearLayout) findViewById(R.id.ll_org_rules);
        mLinkOrgRules = (TextView) findViewById(R.id.link_org_rules_activity);
        mLinkOrgRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditActivity.this, OrganizerRulesActivity.class);
                startActivity(i);
//                Toast.makeText(EditActivity.this, "LOOK RULES", Toast.LENGTH_LONG).show();
            }
        });

        mCheckOrgRules = (CheckBox) findViewById(R.id.checkbox_for_org_rules);
        mFab = (FloatingActionButton) findViewById(R.id.edit_or_create_fab);
//        mFab.setEnabled(false);
        mFab.setVisibility(View.GONE);

        if (eventId == null) {
            setTitle(getString(R.string.create_event));
        } else {
            mLLOrgRules.setVisibility(View.GONE);
            mCheckOrgRules.setEnabled(true);
            mFab.setVisibility(View.VISIBLE);
            setTitle(getString(R.string.edit_event));
        }



        mCheckOrgRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCheckOrgRules.isChecked()) {
                    mFab.setVisibility(View.VISIBLE);
                } else {
                    mFab.setVisibility(View.GONE);
                }
            }
        });


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mScrollView = (NestedScrollView) findViewById(R.id.event_edit_srollview);
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("SCROLL<<<<",  String.valueOf(oldScrollY-scrollY));
            }
        });

        mEditTitle = (EditText) findViewById(R.id.edit_input_title);
        mEditTitle.setText(event.getTitle());

        mEditDescription = (EditText) findViewById(R.id.edit_input_description);
        mEditDescription.setText(event.getDescription());

        mEditStartDate = (EditText) findViewById(R.id.edit_input_startDate);
        if (event.getStartDate() == null) {
            event.setStartDate(new Date());
        }
        mEditStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));

        mEditFinishDate = (EditText) findViewById(R.id.edit_input_endDate);
        if (event.getEndDate() == null) {
            event.setEndDate(new Date());
        }
        mEditFinishDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));

        mPlace = (EditText) findViewById(R.id.edit_input_place);
        mMinPrice = (EditText) findViewById(R.id.edit_input_min_price);
        mMaxPrice = (EditText) findViewById(R.id.edit_input_max_price);
        mWebsite = (EditText) findViewById(R.id.edit_input_website);

        mPlace.setText(event.getPlace());
        mMinPrice.setText(String.valueOf(event.getLowestPrice()));
        mMaxPrice.setText(String.valueOf(event.getHighestPrice()));
        mWebsite.setText(event.getWebSite());

        mEditInterests = (EditText) findViewById(R.id.edit_input_interest);
        if (event.getInterest() != null) {
            String fullText = "";
            fullText += event.getInterest().getFullTitle().get(0);
            if (event.getInterest().getFullTitle().size()>1) {
                fullText += " / " + event.getInterest().getFullTitle().get(1);
            }
            mEditInterests.setText(fullText);
        }


        mButtonSelectInterest = (ImageButton) findViewById(R.id.btn_select_interest);
        mButtonSelectInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                EventInterestFragment editNameDialogFragment = EventInterestFragment.newInstance();
                editNameDialogFragment.setOnInterestSelectListener(new EventInterestFragment.OnInterestSelectListener() {
                    @Override
                    public void onInterestSelected(Interest interest) {
                        selectedInterest = interest;
                        RealmList<Interest> interests = new RealmList<Interest>();
                        interests.add(interest);
                        event.setInterests(interests);
                        mEditInterests.setText(selectedInterest.getTitle());
                    }
                });
                editNameDialogFragment.show(fm, "fragment_select_interest");
            }
        });
        mSelectImage = (ViewPager) findViewById(R.id.viewpager_edit);
        if (event.getImages() != null && event.getImages().size() > 0) {
            mEventImagesSwipeAdapter = new EventImagesSwipeAdapter(getSupportFragmentManager());
            mEventImagesSwipeAdapter.setImageList(event.getImages());
        mSelectImage.setAdapter(mEventImagesSwipeAdapter);
        }


        btnStartDate = (ImageButton) findViewById(R.id.btn_choice_startDate);
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getStartDate() != null) {
                    startDate = new DateTime(event.getStartDate().getTime());
                } else {
                    startDate = DateTime.now();
                }
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                    createStartDateListener(),
                    startDate.getYear(),
                    startDate.getMonthOfYear()-1,
                    startDate.getDayOfMonth()
                );
                dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dpd.show(getFragmentManager(), "StartDatepickerdialog");
            }
        });


        btnEndDate = (ImageButton) findViewById(R.id.btn_choice_endDate);
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getEndDate() != null) {
                    endDate = new DateTime(event.getEndDate().getTime());
                } else {
                    if (event.getStartDate() != null) {
                        endDate = new DateTime(event.getStartDate().getTime());
                    } else {
                        endDate = DateTime.now();
                    }
                }
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        createEndDateListener(),
                        endDate.getYear(),
                        endDate.getMonthOfYear() -1,
                        endDate.getDayOfMonth()
                );
                Calendar mindate = Calendar.getInstance();
                if (event.getStartDate()!=null) {
                    mindate.setTime(event.getStartDate());
                } else {
                    mindate.setTime(new Date());
                }
                dpd.setMinDate(mindate);
                dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dpd.show(getFragmentManager(), "EndDatepickerdialog");
            }
        });


        mFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hideSoftKeyboard(EditActivity.this, view);
                    saveEvent();

            }
        });


        if (saveDoneReceiver == null) {
            saveDoneReceiver = createSaveDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(saveDoneReceiver, new IntentFilter(BroadcastIntents.EVENTEDIT_REQUEST_OK));
        }

    }

    private void saveEvent() {
        event.setTitle(mEditTitle.getText().toString());
        event.setDescription(mEditDescription.getText().toString());
        event.setLocalOnly(true);

        if (eventId != null) {
            event.setLocalId(eventId);
        }
        event.setId("local_event_" + (new Date()).getTime());

        event.setCityId(App.getCurrentCity().getId());
        event.setCurrencyId(App.getCurrentUser().getSettings().getCurrency());
        event.setPlace(mPlace.getText().toString());
        if (mMinPrice.getText().toString().length() == 0) mMinPrice.setText("0");
        if (mMaxPrice.getText().toString().length() == 0) mMaxPrice.setText("0");
        event.setLowestPrice(Integer.parseInt(mMinPrice.getText().toString()));
        event.setHighestPrice(Integer.parseInt(mMaxPrice.getText().toString()));
        event.setWebSite(mWebsite.getText().toString());

        if (event.getCurrency() != null) {
            event.setCurrencyId(event.getCurrency().getId());
        }

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


    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private DatePickerDialog.OnDateSetListener createStartDateListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                Date startDt = new Date();
                if (event.getStartDate() != null) startDt = event.getStartDate();
                calendar.setTime(startDt);
                calendar.set(year, monthOfYear, dayOfMonth);

                Date newDate = calendar.getTime();
                startDate = new DateTime(newDate);

                event.setStartDate(newDate);
                mEditStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));

                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        createStartTimeListener(),
                        startDate.getHourOfDay(),
                        startDate.getMinuteOfHour(),
                        false);

                tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                tpd.show(getFragmentManager(), "EndTimePickerDialog");
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener createStartTimeListener() {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                Date startDt = new Date();
                if (event.getStartDate() != null) startDt = event.getStartDate();
                calendar.setTime(startDt);
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay, minute, second);

                event.setStartDate(calendar.getTime());
                mEditStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
            }
        };
    }

    private DatePickerDialog.OnDateSetListener createEndDateListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                Date endDt = new Date();
                if (event.getEndDate() != null) endDt = event.getEndDate();
                calendar.setTime(endDt);
                calendar.set(year, monthOfYear, dayOfMonth);

                Date newDate = calendar.getTime();
                endDate = new DateTime(newDate);

                event.setEndDate(newDate);
                mEditFinishDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));

                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        createEndTimeListener(),
                        endDate.getHourOfDay(),
                        endDate.getMinuteOfHour(),
                        false);

                tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                tpd.show(getFragmentManager(), "EndTimePickerDialog");
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener createEndTimeListener() {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                Date startDt = new Date();
                if (event.getEndDate() != null) startDt = event.getEndDate();
                calendar.setTime(startDt);
                calendar.set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay, minute, second);

                event.setEndDate(calendar.getTime());
                mEditFinishDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
            }
        };
    }

    private BroadcastReceiver createSaveDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String evendId = intent.getStringExtra("event_id");
                intent = new Intent(App.getContext(), EventActivity.class);
                intent.putExtra("event_id", evendId);
                startActivity(intent);

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
