package com.happ.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.happ.R;
import com.happ.fragments.EventInterestFragment;
import com.happ.models.Event;
import com.happ.models.Interest;

import io.realm.Realm;
import retrofit2.http.Url;

/**
 * Created by dante on 8/19/16.
 */
public class EditActivity extends AppCompatActivity {

//    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private EditText mEditTitle, mEditDescription, mEditInterests, mEditStartDate, mEditFinishDate;
    private ImageButton mButtonSelectInterest;
    private ImageView mSelectImage;
    private Event event;
    private Interest selectedInterest;
    private String eventId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");
        Realm realm = Realm.getDefaultInstance();
        event = realm.where(Event.class).equalTo("id", eventId).findFirst();
        event = realm.copyFromRealm(event);
        realm.close();

        setContentView(R.layout.activity_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_rigth_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        mEditInterests.setText(event.getInterest().getTitle());

        mButtonSelectInterest = (ImageButton) findViewById(R.id.btn_select_interest);
        mButtonSelectInterest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                EventInterestFragment editNameDialogFragment = EventInterestFragment.newInstance();
                editNameDialogFragment.setOnInterestSelectListener(new EventInterestFragment.OnInterestSelectListener() {
                    @Override
                    public void onInterestSelected(Interest interest) {
                        selectedInterest = interest;
                        mEditInterests.setText(selectedInterest.getTitle());
                    }
                });
                editNameDialogFragment.show(fm, "fragment_select_interest");
            }
        });
        mSelectImage = (ImageView) findViewById(R.id.edit_imageView);
//        mSelectImage.setImageURI(Url, event.getImages());

//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                menuItem.setChecked(true);
//                mDrawerLayout.closeDrawers();
//                Toast.makeText(EditActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id) {
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                return true;
//            case R.id.action_settings:
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
