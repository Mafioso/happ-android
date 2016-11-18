package com.happ.controllers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
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
import com.happ.fragments.First_EC_Fragment;
import com.happ.fragments.Second_EC_Fragment;
import com.happ.fragments.Third_EC_Fragment;
import com.happ.models.Event;
import com.happ.retrofit.APIService;

import java.util.Date;

import io.realm.Realm;
import me.relex.circleindicator.CircleIndicator;


/**
 * Created by dante on 8/19/16.
 */
public class EditCreateActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager mViewPager;
    private FragmentPagerAdapter adapterViewPager;
    private Button mBtnCreateSave;

    private Event event;
    private String eventId;

//    private DateTime startDate;
//    private DateTime endDate;

    private EventImagesSwipeAdapter mEventImagesSwipeAdapter;
    private BroadcastReceiver saveDoneReceiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_create);

        if (eventId == null) {
            setTitle(getString(R.string.create_event));
            event = new Event();
        } else {
            setTitle(getString(R.string.edit_event));
            Realm realm = Realm.getDefaultInstance();
            event = realm.where(Event.class).equalTo("id", eventId).findFirst();
            realm.close();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.ec_viewpager);
        mBtnCreateSave = (Button) findViewById(R.id.btn_editcreate_save);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.ec_indicator);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapterViewPager);
        indicator.setViewPager(mViewPager);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_right_arrow_grey);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnCreateSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(EditCreateActivity.this, "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        mScrollView = (NestedScrollView) findViewById(R.id.event_edit_srollview);
//        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                Log.d("SCROLL<<<<",  String.valueOf(oldScrollY-scrollY));
//            }
//        });
//
//        mEditTitle = (EditText) findViewById(R.id.edit_input_title);
//        mEditTitle.setText(event.getTitle());
//
//        mEditDescription = (EditText) findViewById(R.id.edit_input_description);
//        mEditDescription.setText(event.getDescription());
//
//        mEditStartDate = (EditText) findViewById(R.id.edit_input_startDate);
//        if (event.getStartDate() == null) {
//            event.setStartDate(new Date());
//        }
//        mEditStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
//
//        mEditFinishDate = (EditText) findViewById(R.id.edit_input_endDate);
//        if (event.getEndDate() == null) {
//            event.setEndDate(new Date());
//        }
//        mEditFinishDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
//
//        mPlace = (EditText) findViewById(R.id.edit_input_place);
//        mMinPrice = (EditText) findViewById(R.id.edit_input_min_price);
//        mMaxPrice = (EditText) findViewById(R.id.edit_input_max_price);
//        mWebsite = (EditText) findViewById(R.id.edit_input_website);
//
//        mPlace.setText(event.getPlace());
//        mMinPrice.setText(String.valueOf(event.getLowestPrice()));
//        mMaxPrice.setText(String.valueOf(event.getHighestPrice()));
//        mWebsite.setText(event.getWebSite());
//
//        mEditInterests = (EditText) findViewById(R.id.edit_input_interest);
//        if (event.getInterest() != null) {
//            String fullText = "";
//            fullText += event.getInterest().getFullTitle().get(0);
//            if (event.getInterest().getFullTitle().size()>1) {
//                fullText += " / " + event.getInterest().getFullTitle().get(1);
//            }
//            mEditInterests.setText(fullText);
//        }
//
//
//        mButtonSelectInterest = (ImageButton) findViewById(R.id.btn_select_interest);
//        mButtonSelectInterest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fm = getSupportFragmentManager();
//                EventInterestFragment editNameDialogFragment = EventInterestFragment.newInstance();
//                editNameDialogFragment.setOnInterestSelectListener(new EventInterestFragment.OnInterestSelectListener() {
//                    @Override
//                    public void onInterestSelected(Interest interest) {
//                        selectedInterest = interest;
//                        RealmList<Interest> interests = new RealmList<Interest>();
//                        interests.add(interest);
//                        event.setInterests(interests);
//                        mEditInterests.setText(selectedInterest.getTitle());
//                    }
//                });
//                editNameDialogFragment.show(fm, "fragment_select_interest");
//            }
//        });
//        mSelectImage = (ViewPager) findViewById(R.id.viewpager_edit);
//        if (event.getImages() != null && event.getImages().size() > 0) {
//            mEventImagesSwipeAdapter = new EventImagesSwipeAdapter(getSupportFragmentManager());
//            mEventImagesSwipeAdapter.setImageList(event.getImages());
//        mSelectImage.setAdapter(mEventImagesSwipeAdapter);
//        }
//
//
//        btnStartDate = (ImageButton) findViewById(R.id.btn_choice_startDate);
//        btnStartDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (event.getStartDate() != null) {
//                    startDate = new DateTime(event.getStartDate().getTime());
//                } else {
//                    startDate = DateTime.now();
//                }
//                DatePickerDialog dpd = DatePickerDialog.newInstance(
//                    createStartDateListener(),
//                    startDate.getYear(),
//                    startDate.getMonthOfYear()-1,
//                    startDate.getDayOfMonth()
//                );
//                dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
//                dpd.show(getFragmentManager(), "StartDatepickerdialog");
//            }
//        });
//
//
//        btnEndDate = (ImageButton) findViewById(R.id.btn_choice_endDate);
//        btnEndDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (event.getEndDate() != null) {
//                    endDate = new DateTime(event.getEndDate().getTime());
//                } else {
//                    if (event.getStartDate() != null) {
//                        endDate = new DateTime(event.getStartDate().getTime());
//                    } else {
//                        endDate = DateTime.now();
//                    }
//                }
//                DatePickerDialog dpd = DatePickerDialog.newInstance(
//                        createEndDateListener(),
//                        endDate.getYear(),
//                        endDate.getMonthOfYear() -1,
//                        endDate.getDayOfMonth()
//                );
//                Calendar mindate = Calendar.getInstance();
//                if (event.getStartDate()!=null) {
//                    mindate.setTime(event.getStartDate());
//                } else {
//                    mindate.setTime(new Date());
//                }
//                dpd.setMinDate(mindate);
//                dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
//                dpd.show(getFragmentManager(), "EndDatepickerdialog");
//            }
//        });
//
//
//        mFab.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                hideSoftKeyboard(EditCreateActivity.this, view);
//                    saveEvent();
//
//            }
//        });


        if (saveDoneReceiver == null) {
            saveDoneReceiver = createSaveDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(saveDoneReceiver, new IntentFilter(BroadcastIntents.EVENTEDIT_REQUEST_OK));
        }

    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return First_EC_Fragment.newInstance();
                case 1:
                    return Second_EC_Fragment.newInstance();
                case 2:
                    return Third_EC_Fragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void saveEvent() {
        event.setLocalOnly(true);

        if (eventId != null) {
            event.setLocalId(eventId);
        }
        event.setId("local_event_" + (new Date()).getTime());

        event.setTitle("CS:GO Beta test");
        event.setDescription("Тест!  Тест!  Тест!  Тест!  Тест!  Тест!  Тест!");
        event.setCityId(App.getCurrentCity().getId());
//        event.setCurrencyId("KZT");
        event.setCurrencyId(App.getCurrentUser().getSettings().getCurrencyObject().getId());
        event.setPlace("l[l[ppl");
        event.setStartDate(new Date());
        event.setEndDate(new Date());

        event.setLowestPrice(Integer.parseInt("1234"));
        event.setHighestPrice(Integer.parseInt("12345"));
        event.setWebSite("http://vk.com/");

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (saveDoneReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(saveDoneReceiver);
        }
    }
//
//    private DatePickerDialog.OnDateSetListener createStartDateListener() {
//        return new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
//                Calendar calendar = Calendar.getInstance();
//                Date startDt = new Date();
//                if (event.getStartDate() != null) startDt = event.getStartDate();
//                calendar.setTime(startDt);
//                calendar.set(year, monthOfYear, dayOfMonth);
//
//                Date newDate = calendar.getTime();
//                startDate = new DateTime(newDate);
//
//                event.setStartDate(newDate);
//                mEditStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
//
//                TimePickerDialog tpd = TimePickerDialog.newInstance(
//                        createStartTimeListener(),
//                        startDate.getHourOfDay(),
//                        startDate.getMinuteOfHour(),
//                        false);
//
//                tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
//                tpd.show(getFragmentManager(), "EndTimePickerDialog");
//            }
//        };
//    }
//
//    private TimePickerDialog.OnTimeSetListener createStartTimeListener() {
//        return new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
//                Calendar calendar = Calendar.getInstance();
//                Date startDt = new Date();
//                if (event.getStartDate() != null) startDt = event.getStartDate();
//                calendar.setTime(startDt);
//                calendar.set(calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH),
//                        hourOfDay, minute);
//
//                event.setStartDate(calendar.getTime());
//                mEditStartDate.setText(event.getStartDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
//            }
//        };
//    }
//
//    private DatePickerDialog.OnDateSetListener createEndDateListener() {
//        return new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
//                Calendar calendar = Calendar.getInstance();
//                Date endDt = new Date();
//                if (event.getEndDate() != null) endDt = event.getEndDate();
//                calendar.setTime(endDt);
//                calendar.set(year, monthOfYear, dayOfMonth);
//
//                Date newDate = calendar.getTime();
//                endDate = new DateTime(newDate);
//
//                event.setEndDate(newDate);
//                mEditFinishDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
//
//                TimePickerDialog tpd = TimePickerDialog.newInstance(
//                        createEndTimeListener(),
//                        endDate.getHourOfDay(),
//                        endDate.getMinuteOfHour(),
//                        false);
//
//                tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
//                tpd.show(getFragmentManager(), "EndTimePickerDialog");
//            }
//        };
//    }
//
//    private TimePickerDialog.OnTimeSetListener createEndTimeListener() {
//        return new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
//                Calendar calendar = Calendar.getInstance();
//                Date startDt = new Date();
//                if (event.getEndDate() != null) startDt = event.getEndDate();
//                calendar.setTime(startDt);
//                calendar.set(calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH),
//                        hourOfDay, minute);
//
//                event.setEndDate(calendar.getTime());
//                mEditFinishDate.setText(event.getEndDateFormatted("MMMM dd, yyyy 'a''t' h:mm a"));
//            }
//        };
//    }

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
