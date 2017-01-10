package com.happ.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.FileUtils;
import com.happ.R;
import com.happ.adapters.EcImagesAdapter;
import com.happ.controllers_drawer.EventActivity;
import com.happ.controllers_drawer.SelectInterestsActivity;
import com.happ.fragments.ChangeCurrencyFragment;
import com.happ.fragments.PointMarkerMapFragment;
import com.happ.fragments.SelectCityFragment;
import com.happ.models.City;
import com.happ.models.Currency;
import com.happ.models.Event;
import com.happ.models.EventDateTimes;
import com.happ.models.EventPhone;
import com.happ.models.GeopointResponse;
import com.happ.models.HappImage;
import com.happ.models.Interest;
import com.happ.models.User;
import com.happ.retrofit.APIService;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;

//import android.content.Uri;


/**
 * Created by dante on 8/19/16.
 */
public class EditCreateActivity extends AppCompatActivity {


    public class EventEditImage {
        private boolean isLocal;
        private String imageId;
        private Uri uri;
        private HappImage image;
        private boolean isLast = false;
        private boolean isUploading = false;
        private String path;

        public boolean isLocal() {
            return isLocal;
        }

        public void setLocal(boolean local) {
            this.isLocal = local;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }

        public HappImage getImage() {
            return image;
        }

        public void setImage(HappImage image) {
            this.image = image;
        }

        public boolean isLast() {
            return isLast;
        }

        public void setLast(boolean last) {
            isLast = last;
        }

        public boolean isUploading() {
            return isUploading;
        }

        public void setUploading(boolean uploading) {
            isUploading = uploading;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 2;
    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_INTEREST = 3;

    private Toolbar toolbar;
    private Button mBtnCreateSave;
    private Event event;
    private String eventId;
    private String selectedInterestId;
    private Interest selectedInterest;

    private Date mStartDate, mEndDate, mStartTime, mEndTime;

    private EditText    mEventTitle,
                        mEventInterest,
                        mEventDescription,
                        mEventCity,
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
                        mImgBtnSelectCity,
                        mImgBtnSelectCurreny;

    private SwitchCompat mSwitchContinuingEvent;

    private BroadcastReceiver imageUploadedReceiver;

    private View mViewSpaceListenerStartTime, mViewSpaceListenerEndTime;

    private RecyclerView mEventImagesList;
    private EcImagesAdapter mEventImagesAdapter;
    private BroadcastReceiver createEventReceiver, eventEditReceiver;

    private City selectedCity = App.getCurrentCity();
    private String selectedCurrency = App.getCurrentUser().getSettings().getCurrency();

    private ArrayList<EventEditImage> imagesList;
    private LatLng saveAddress;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");
        setContentView(R.layout.activity_edit_create);
        binds();

        if (eventId == null) {
            setTitle(getString(R.string.create_event));
            mBtnCreateSave.setText(getResources().getString(R.string.create_event));
//            mEventCurrency.setText(App.getCurrentUser().getSettings().getCurrencyObject().getName());
            mEventCity.setText(selectedCity.getName());
            event = new Event();
            imagesList = new ArrayList<>();
        } else {
            setTitle(getString(R.string.edit_event));
            Realm realm = Realm.getDefaultInstance();
            event = realm.where(Event.class).equalTo("id", eventId).findFirst();
            event = realm.copyFromRealm(event);
            realm.close();

            mBtnCreateSave.setText(getResources().getString(R.string.save));

            mEventTitle.setText(event.getTitle());
            mEventDescription.setText(event.getDescription());

            if (event.getPlace() != null)
                mEventPlace.setText(event.getPlace());

            mEventMinPrice.setText(Integer.toString(event.getLowestPrice()));
            mEventMaxPrice.setText(Integer.toString(event.getHighestPrice()));

            if (event.getCurrency() != null)
                mEventCurrency.setText(event.getCurrency().getName());

            java.text.DateFormat format = DateFormat.getLongDateFormat(EditCreateActivity.this);
            mEventStartDate.setText(format.format(event.getStartDate()));
            mStartDate = event.getStartDate();

            mEventEndDate .setText(format.format(event.getEndDate()));
            mEndDate = event.getEndDate();

            java.text.DateFormat timeFormat = DateFormat.getTimeFormat(EditCreateActivity.this);
            mEventStartTime.setText(timeFormat.format(event.getStartDate()));

            mEventEndTime.setText(timeFormat.format(event.getEndDate()));

            if (event.getWebSite() != null) mEventWebSite.setText(event.getWebSite());
            if (event.getRegistationLink() != null) mEventTicketLink.setText(event.getRegistationLink());
            if (event.getEmail() != null) mEventEmail.setText(event.getEmail());
            if (event.getPhones().size() > 0) mEventPhone.setText(event.getPhones().get(0).getPhone());

            imagesList = new ArrayList<>();
            for (HappImage img: event.getImages()) {
                EventEditImage eei = new EventEditImage();
                eei.setLocal(false);
                eei.setImageId(img.getId());
                eei.setImage(img);
                imagesList.add(eei);
            }

            Interest interest = event.getInterest();
            String interestTitle = interest.getTitle();
            if (interest.getParent() != null) {
                interestTitle = interest.getParent().getTitle() + " / " + interestTitle;
            }
            mEventInterest.setText(interestTitle);
            selectedInterestId = interest.getId();
            selectedInterest = interest;
        }

        EventEditImage lastEei = new EventEditImage();
        lastEei.setLast(true);
        imagesList.add(lastEei);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mEventImagesList.setLayoutManager(llm);

        mEventImagesAdapter = new EcImagesAdapter(this, imagesList);
        mEventImagesList.setAdapter(mEventImagesAdapter);
        mEventImagesAdapter.setItemActionListener(new EcImagesAdapter.EcItemActionLintener() {
            @Override
            public void onImageSelectRequested() {
                openGallery();
            }

            @Override
            public void onImageDeleteRequested(String imageId) {
                int id = -1;
                while (id < imagesList.size()) {
                    if (imageId != null && imagesList.get(id+1).getImageId().equals(imageId)) {
                        id++;
                        break;
                    }
                    id++;
                }
                imagesList.remove(id);
                mEventImagesAdapter.updateList(imagesList);
            }
        });

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
                if (mStartDate != null) {
                    now.setTime(mStartDate);
                } else {
                    now.setTime(new Date());
                }
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
                if (mEndDate != null) {
                    now.setTime(mEndDate);
                } else {
                    now.setTime(new Date());
                }
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
                final PointMarkerMapFragment pmmf = new PointMarkerMapFragment();
                Bundle args = new Bundle();
                pmmf.setArguments(args);
                pmmf.setTakeTheAddressOfThePointListener(new PointMarkerMapFragment.TakeAddressOfThePointListener() {
                    @Override
                    public void setOnAddress(LatLng address, String strAddress) {
                        saveAddress = address;
                        mEventPlace.setText(strAddress);
                    }
                });
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_container, pmmf)
                        .commit();
            }
        });

        mImgBtnSelectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SelectCityFragment scf = new SelectCityFragment();
                Bundle args = new Bundle();
                args.putBoolean("from_edit_create_activity", true);
                args.putString("selectedCity", selectedCity.getId());
                scf.setArguments(args);

                scf.setOnCitySelectListener(new SelectCityFragment.OnCitySelectListener() {
                    @Override
                    public void onCitySelected(City city, float x, float y) {

                    }

                    @Override
                    public void onCancel(float x, float y) {

                    }

                    @Override
                    public void onCitySelectedFromEditCreate(City city) {
                        selectedCity = city;
                        mEventCity.setText(selectedCity.getName().trim());
                    }

                });
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_container, scf)
                        .commit();
            }
        });


        mImgBtnSelectCurreny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ChangeCurrencyFragment ccf = new ChangeCurrencyFragment();
                Bundle args = new Bundle();
//                args.putBoolean("from_edit_create_activity", true);
                args.putString("selectedCurrency", selectedCurrency);
                ccf.setArguments(args);

                ccf.setOnCurrencyListener(new ChangeCurrencyFragment.OnCurrencySelectListener() {
                    @Override
                    public void onSelectedCurrency(Currency currency) {
                        selectedCurrency = currency.getId();
                        mEventCurrency.setText(currency.getName());
                    }
                });

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_container, ccf)
                        .commit();
            }
        });


        mBtnCreateSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(EditCreateActivity.this, view);
                saveEvent();
            }
        });

        mImgBtnSelectInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditCreateActivity.this, SelectInterestsActivity.class);
                intent.putExtra("is_single", true);
                intent.putExtra("is_full", true);
                EditCreateActivity.this.startActivityForResult(intent, SELECT_INTEREST);
            }
        });

        if (createEventReceiver == null) {
            createEventReceiver = createSaveDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(createEventReceiver, new IntentFilter(BroadcastIntents.EVENTCREATE_REQUEST_OK));
        }
        if (eventEditReceiver == null) {
            eventEditReceiver = eventEditSaveDoneReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(eventEditReceiver, new IntentFilter(BroadcastIntents.EVENTEDIT_REQUEST_OK));
        }

        if (imageUploadedReceiver == null) {
            imageUploadedReceiver = createImageUploadedReceiver();
            LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(imageUploadedReceiver, new IntentFilter(BroadcastIntents.IMAGE_UPLOAD_OK));
        }


    }

    private void binds() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        mEventCity = (EditText) findViewById(R.id.input_ec_city);

        mImgBtnSelectInterest = (ImageButton) findViewById(R.id.ibtn_ec_edit_interest);
        mImgBtnSelectStartDate = (ImageButton) findViewById(R.id.ibtn_ec_edit_startdate);
        mImgBtnSelectEndDate = (ImageButton) findViewById(R.id.ibtn_ec_edit_enddate);
        mImgBtnSelectPointMap = (ImageButton) findViewById(R.id.ibtn_ec_edit_map);
        mImgBtnSelectCity = (ImageButton) findViewById(R.id.ibtn_ec_edit_city);
        mImgBtnSelectCurreny = (ImageButton) findViewById(R.id.ibtn_ec_edit_currency);

        mViewSpaceListenerStartTime = (View) findViewById(R.id.view_click_starttime);
        mViewSpaceListenerEndTime = (View) findViewById(R.id.view_click_endtime);

        mEventImagesList = (RecyclerView) findViewById(R.id.ec_image_rv);
        mSwitchContinuingEvent = (SwitchCompat) findViewById(R.id.switch_ec_continuing);


    }

    private void saveEvent() {

        event.setLocalOnly(true);

        if (eventId != null) {
            event.setLocalId(eventId);
        }

        event.setId("local_event_" + (new Date()).getTime());

        event.setTitle(mEventTitle.getText().toString());
        event.setDescription(mEventDescription.getText().toString());
        event.setCityId(selectedCity.getId());

//        event.setCloseOnStart(false);

        event.setCurrencyId(selectedCurrency);

        if (!mEventPlace.getText().toString().equals(""))
            event.setPlace(mEventPlace.getText().toString());

        if (event.getGeopoint() != null) {
            // Сохранение точек при редактировании.
        }

        if (saveAddress != null) {
            GeopointResponse geopoinstResponse = new GeopointResponse();
            geopoinstResponse.setLat((float) saveAddress.latitude);
            geopoinstResponse.setLng((float) saveAddress.longitude);
            event.setGeopoint(geopoinstResponse);
        }


        RealmList<HappImage> images = new RealmList<>();
        for (EventEditImage img: imagesList) {
            if (img.getImageId() != null) {
                if (!img.isLocal()) {
                    images.add(img.getImage());
                } else {
                    HappImage image = new HappImage();
                    image.setId(img.getImageId());
                    images.add(image);
                }
            }
        }

        event.setImages(images);


//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//
//        event.setStartDate(mStartDate);
//        event.setEndDate(mEndDate);

        if (mStartDate != null && mEndDate != null) {
            Calendar gcal = Calendar.getInstance();
            Date start = mStartDate;
            Date end = mEndDate;
            gcal.setTime(start);
            RealmList<EventDateTimes> eventDateTimes = new RealmList<EventDateTimes>();
            do {
                Date nextDate = gcal.getTime();
                EventDateTimes eventDate = new EventDateTimes();
                eventDate.setDate(nextDate);
                eventDate.setStartTime(mStartDate);
                eventDate.setEndTime(mEndDate);
                eventDateTimes.add(eventDate);
                gcal.add(Calendar.DAY_OF_MONTH, 1);
            } while (gcal.getTime().before(end));

            event.setDatetimes(eventDateTimes);
        }

        if (mEventMinPrice.getText().toString().equals("")) {
            event.setLowestPrice(0);
        } else {
            event.setLowestPrice(Integer.parseInt(mEventMinPrice.getText().toString()));
        }

        if (mEventMaxPrice.getText().toString().equals("")) {
            event.setHighestPrice(0);
        } else {
            event.setHighestPrice(Integer.parseInt(mEventMaxPrice.getText().toString()));
        }

        if (!mEventWebSite.getText().toString().equals(""))
            event.setWebSite(mEventWebSite.getText().toString());

        if (!mEventEmail.getText().toString().equals(""))
            event.setEmail(mEventEmail.getText().toString());

        if (!mEventPhone.getText().toString().equals("")) {
            RealmList<EventPhone> phones = new RealmList<EventPhone>();
                EventPhone phone = new EventPhone();
                phone.setPhone(mEventPhone.getText().toString());
                phones.add(phone);
            event.setPhones(phones);
        } else {
            if (App.getCurrentUser().getPhone() != null) {
                RealmList<EventPhone> phones = new RealmList<EventPhone>();
                    EventPhone phone = new EventPhone();
                    phone.setPhone(App.getCurrentUser().getPhone());
                    phones.add(phone);
                event.setPhones(phones);
            }
        }

        if (!mEventTicketLink.getText().toString().equals("")){
            event.setRegistationLink(mEventTicketLink.getText().toString());
        }

        if (mSwitchContinuingEvent.isChecked()) {
            event.setCloseOnStart(true);
        } else {
            event.setCloseOnStart(false);
        }
        User author = new User();
        author.setFullname(App.getCurrentUser().getFn());
        event.setAuthor(author);

        if (selectedInterestId != null) {
            RealmList<Interest> interests = new RealmList<>();
            interests.add(selectedInterest);
            event.setInterests(interests);
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(event);
        realm.commitTransaction();
        realm.close();

        if (mStartDate != null && mEndDate != null
                && !mEventPlace.getText().toString().equals("")
                && !mEventTitle.getText().toString().equals("")
                && !mEventDescription.getText().toString().equals("")
                && !mEventInterest.getText().toString().equals("")
                && !mEventCurrency.getText().toString().equals("")) {

            if (eventId == null) {
                APIService.createEvent(event.getId());
            } else {
                APIService.doEventEdit(event.getId());
            }

        } else {
            Toast.makeText(EditCreateActivity.this, R.string.not_all_fields_are_filled, Toast.LENGTH_SHORT).show();
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        };
    }

    private BroadcastReceiver eventEditSaveDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String evendId = intent.getStringExtra("event_id");

                intent = new Intent(EditCreateActivity.this, EventActivity.class);
                intent.putExtra("in_event_activity", true);
                intent.putExtra("is_organizer", true);
                intent.putExtra("event_id", evendId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
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

        if (eventEditReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(eventEditReceiver);
            eventEditReceiver = null;
        }


        if (imageUploadedReceiver != null) {
            LocalBroadcastManager.getInstance(App.getContext()).unregisterReceiver(imageUploadedReceiver);
            imageUploadedReceiver = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String path = FileUtils.getPath(App.getContext(), selectedImageUri);

                EventEditImage eei = imagesList.get(imagesList.size()-1);
                eei.setLast(false);
                eei.setPath(path);
                eei.setUri(selectedImageUri);
                eei.setUploading(true);
                eei.setLocal(true);


                EventEditImage last = new EventEditImage();
                last.setLast(true);
                imagesList.add(last);

                mEventImagesAdapter.updateList(imagesList);


                APIService.uploadImage(selectedImageUri);

            } else if (requestCode == SELECT_INTEREST) {
                String interestId = data.getStringExtra("ID");
                selectedInterestId = interestId;

                Realm realm = Realm.getDefaultInstance();
                Interest interest = realm.where(Interest.class).equalTo("id", selectedInterestId).findFirst();
                interest = realm.copyFromRealm(interest);
                realm.close();
                if (interest != null) {
                    String interestTitle = interest.getTitle();
                    if (interest.getParent() != null) {
                        interestTitle = interest.getParent().getTitle() + " / " + interestTitle;
                    }
                    mEventInterest.setText(interestTitle);
                    selectedInterest = interest;
                }
            }
        }
    }

    private BroadcastReceiver createImageUploadedReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String savedId = intent.getStringExtra(BroadcastIntents.IMAGE_UPLOAD_EXTRA_ID);
                String savedPath = intent.getStringExtra(BroadcastIntents.IMAGE_UPLOAD_EXTRA_URI);

                EventEditImage eei = null;
                for (int i=0; i < imagesList.size(); i++) {
                    if (imagesList.get(i).isLocal() && imagesList.get(i).getPath().equals(savedPath)) {
                        eei = imagesList.get(i);
                        break;
                    }
                }

                if (eei != null) {
                    if (savedId != null) {
                        eei.setUploading(false);
                    }
                    eei.setImageId(savedId);
                }

                mEventImagesAdapter.updateList(imagesList);

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    readDataExternal();
                }
                break;

            default:
                break;
        }
    }

    private void readDataExternal() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    private void openGallery() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
        } else {
            readDataExternal();
        }
    }

}
