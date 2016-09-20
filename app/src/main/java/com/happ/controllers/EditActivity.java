package com.happ.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.happ.R;
import com.happ.fragments.EventInterestFragment;
import com.happ.models.Event;
import com.happ.models.Interest;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
    private EditText mEditTitle, mEditDescription, mEditInterests, mEditStartDate, mEditFinishDate;
    private ImageButton mButtonSelectInterest;
    private ViewPager mSelectImage;
    private Event event;
    private Interest selectedInterest;
    private String eventId;
    private ImageButton btnStartDate, btnEndDate;
    private NestedScrollView mScrollView;

    private DateTime startDate;
    private DateTime endDate;

    EventImagesSwipeAdapter mEventImagesSwipeAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
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
        if (eventId == null) {
            setTitle(getString(R.string.create_event));
        } else {
            setTitle(getString(R.string.edit_event));
        }

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
        mEditStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));

        mEditFinishDate = (EditText) findViewById(R.id.edit_input_endDate);
        mEditFinishDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));

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
                        endDate.getMonthOfYear()-1,
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
}
