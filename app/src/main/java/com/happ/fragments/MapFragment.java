package com.happ.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.happ.App;
import com.happ.R;
import com.happ.models.Event;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 ** Created by dante on 11/1/16.
 * */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public MapFragment() {

    }

    private ArrayList<Event> events;
    public static MapFragment newInstance() {

        return new MapFragment();
    }

//    private ClusterManager<AbstractMarker> clusterManager;

    GoogleMap googleMap;
    MapView mapView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);


        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

//        clusterManager = new ClusterManager<AbstractMarker>(this, getMap());


        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        View markerView = ((LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_marker, null);

        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            Toast.makeText(App.getContext(), "Gps is not enabled.", Toast.LENGTH_SHORT).show();
        }

        events = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Event> results = realm.where(Event.class).findAll();
        events = (ArrayList<Event>) realm.copyFromRealm(results);
        realm.close();

//        final ArrayList<LatLng> points = new ArrayList<LatLng>();
//        points.add(new LatLng(43.218282, 76.927793));       // Esentai
//        points.add(new LatLng(43.22859709, 76.95256323));   // My home
//        points.add(new LatLng(43.24432741, 76.94549292));   // Work Space
//        points.add(new LatLng(43.2331407, 76.9565731));     // Dostyk Plaza

//        for (int p = 0; p < events.size(); p++) {
//            if (events.get(p).getGeopoint() != null) {
//
//                double lat = Double.parseDouble(events.get(p).getGeopoint().get(0).toString());
//                double lng = Double.parseDouble(events.get(p).getGeopoint().get(1).toString());
//                LatLng location = new LatLng(lat, lng);
//
//                map.addMarker(new MarkerOptions()
//                        .position(location)
//                        .icon(BitmapDescriptorFactory
//                                .fromBitmap(createDrawableFromView(
//                                        getActivity(),
//                                        markerView))))
//                        .setAnchor(0.0f, 1.0f);
//
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
//            } else {
//                p++;
//            }
//        }

//        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
////
////                if (marker.getPosition().equals(events.)){
////                    Toast.makeText(App.getContext(), "Number 1", Toast.LENGTH_SHORT).show();
////                }
//
//                return true;
//            }
//        });
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    @Override
    public void onResume() {
        super.onResume();
//        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        mapView.onLowMemory();
    }

}

//abstract class AbstractMarker implements ClusterItem {
//    protected double latitude;
//    protected double longitude;
//
//    protected MarkerOptions marker;
//
//    @Override
//    public LatLng getPosition() {
//        return new LatLng(latitude, longitude);
//    }
//
//    protected AbstractMarker(double latitude, double longitude) {
//        setLatitude(latitude);
//        setLongitude(longitude);
//    }
//
//    @Override
//    public abstract String toString();
//
//    public MarkerOptions getMarker() {
//        return marker;
//    }
//
//    public void setMarker(MarkerOptions marker) {
//        this.marker = marker;
//    }
//
//    public void setLatitude(double latitude) {
//        this.latitude = latitude;
//    }
//
//    public void setLongitude(double longitude) {
//        this.longitude = longitude;
//    }
//    //others getters & setters
//}