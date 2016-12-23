package com.happ.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.happ.App;
import com.happ.R;
import com.happ.models.Event;

import java.util.ArrayList;

/**
 ** Created by dante on 11/1/16.
 * */
public class MapFragment extends Fragment {

    public MapFragment() {

    }

    private ArrayList<Event> events;

    public static MapFragment newInstance() {

        return new MapFragment();
    }

    private MapView mMapView;
    private GoogleMap googleMap;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);


        mMapView = (MapView) view.findViewById(R.id.mapview_feed);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(App.getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                LatLng eventLocation = new LatLng(43.218282, 76.927793);
                googleMap.addMarker(new MarkerOptions().position(eventLocation).title("Marker Title").snippet("Marker Description"));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(eventLocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(43.218282, 76.927793))
                        .radius(2000)
                        .strokeColor(Color.RED)
                        .fillColor(android.R.color.transparent));

                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        String TAG = "FEED MAP";
                        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                        LatLng target = cameraPosition.target;
                        LatLng northEast = bounds.northeast;
                        LatLng southEast = bounds.southwest;
                        float[] results1 = new float[1];
                        float[] results2 = new float[1];
                        Location.distanceBetween(target.latitude, target.longitude, northEast.latitude, northEast.longitude, results1);
                        Location.distanceBetween(target.latitude, target.longitude, southEast.latitude, southEast.longitude, results2);
                        double distance = results1[0] > results2[0] ? results1[0] : results2[0];
                        Log.d(TAG, "onCameraChange:" + results1[0] + "  " + results2[0]);
                        double radius = results1[0];

                            googleMap.addCircle(new CircleOptions()
                                .center(new LatLng(43.218282, 76.927793))
                                .radius((int)radius)
                                .strokeColor(Color.RED)
                                .fillColor(android.R.color.transparent));
                    }
                });

            }
        });


        return view;
    }


//    @Override
//    public void onMapReady(GoogleMap map) {
//        View markerView = ((LayoutInflater) getActivity()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_marker, null);
//
//        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            map.setMyLocationEnabled(true);
//        } else {
//            Toast.makeText(App.getContext(), "Gps is not enabled.", Toast.LENGTH_SHORT).show();
//        }
//
//        events = new ArrayList<>();
//
//        Realm realm = Realm.getDefaultInstance();
//        RealmResults<Event> results = realm.where(Event.class).findAll();
//        events = (ArrayList<Event>) realm.copyFromRealm(results);
//        realm.close();
//
////        final ArrayList<LatLng> points = new ArrayList<LatLng>();
////        points.add(new LatLng(43.218282, 76.927793));       // Esentai
////        points.add(new LatLng(43.22859709, 76.95256323));   // My home
////        points.add(new LatLng(43.24432741, 76.94549292));   // Work Space
////        points.add(new LatLng(43.2331407, 76.9565731));     // Dostyk Plaza
//
////        for (int p = 0; p < events.size(); p++) {
////            if (events.get(p).getGeopoint() != null) {
////
////                double lat = Double.parseDouble(events.get(p).getGeopoint().get(0).toString());
////                double lng = Double.parseDouble(events.get(p).getGeopoint().get(1).toString());
////                LatLng location = new LatLng(lat, lng);
////
////                map.addMarker(new MarkerOptions()
////                        .position(location)
////                        .icon(BitmapDescriptorFactory
////                                .fromBitmap(createDrawableFromView(
////                                        getActivity(),
////                                        markerView))))
////                        .setAnchor(0.0f, 1.0f);
////
////                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
////            } else {
////                p++;
////            }
////        }
//
////        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
////            @Override
////            public boolean onMarkerClick(Marker marker) {
//////
//////                if (marker.getPosition().equals(events.)){
//////                    Toast.makeText(App.getContext(), "Number 1", Toast.LENGTH_SHORT).show();
//////                }
////
////                return true;
////            }
////        });
//    }

//    public static Bitmap createDrawableFromView(Context context, View view) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay()
//                .getMetrics(displayMetrics);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.layout(0, 0, displayMetrics.widthPixels,
//                displayMetrics.heightPixels);
//        view.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
//                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//
//        return bitmap;
//    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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