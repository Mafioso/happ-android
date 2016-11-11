package com.happ.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.happ.R;
import com.happ.adapters.EcImagesAdapter;
import com.happ.models.Event;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by dante on 10/26/16.
 */
public class First_EC_Fragment extends Fragment {

    public static First_EC_Fragment newInstance() {

        return new First_EC_Fragment();
    }

    public First_EC_Fragment() {

    }

    private Event event;
    private String eventId;

    private EditText mEventName,
            mInterests,
            mPlace,
            mPriceMin, mPriceMax,
            mCurrency;

    private RecyclerView mRVImagesView;
    private LinearLayoutManager llm;
    protected EcImagesAdapter mECImagesAdapter;
    private ArrayList<String> imagesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final Intent intent = getActivity().getIntent();
        eventId = intent.getStringExtra("event_id");
        if (eventId != null) {
            Realm realm = Realm.getDefaultInstance();
            event = realm.where(Event.class).equalTo("id", eventId).findFirst();
            event = realm.copyFromRealm(event);
            realm.close();
        } else {
            event = new Event();
        }

        final View view = inflater.inflate(R.layout.fragment_ec_first, container, false);
        final Activity activity = getActivity();

        mEventName = (EditText) view.findViewById(R.id.input_ec_name);
        mInterests = (EditText) view.findViewById(R.id.input_ec_select_interest);
        mPlace = (EditText) view.findViewById(R.id.input_ec_place);
        mPriceMin = (EditText) view.findViewById(R.id.input_ec_min_price);
        mPriceMax = (EditText) view.findViewById(R.id.input_ec_max_price);
        mCurrency = (EditText) view.findViewById(R.id.input_ec_currency);
        mRVImagesView = (RecyclerView) view.findViewById(R.id.ec_image_rv);

        llm = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        mRVImagesView.setLayoutManager(llm);

        imagesList = new ArrayList<>();
        imagesList.add("http://www.freedigitalphotos.net/images/img/homepage/87357.jpg");
        imagesList.add("http://assets.barcroftmedia.com.s3-website-eu-west-1.amazonaws.com/assets/images/recent-images-11.jpg");
        imagesList.add("http://i164.photobucket.com/albums/u8/hemi1hemi/COLOR/COL9-6.jpg");
        imagesList.add("http://www.freedigitalphotos.net/images/img/homepage/87357.jpg");
        imagesList.add("");

        mECImagesAdapter = new EcImagesAdapter(activity, imagesList);
        mRVImagesView.setAdapter(mECImagesAdapter);

        if (eventId != null) {
            mEventName.setText(event.getTitle());
            mInterests.setText(event.getInterest().getTitle());
            mPlace.setText(event.getPlace());

            mPriceMin.setText(String.valueOf(event.getLowestPrice()));
            mPriceMax.setText(String.valueOf(event.getHighestPrice()));
            mCurrency.setText("KZT");
        } else {

        }


        return view;
    }
}
