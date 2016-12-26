package com.happ.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.happ.App;
import com.happ.R;
import com.happ.controllers.EventMapActivity;
import com.happ.models.Event;
import com.happ.models.GeopointArrayResponce;
import com.happ.retrofit.HappRestClient;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 ** Created by dante on 11/1/16.
 * */
public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public MapFragment() {

    }

    public static MapFragment newInstance() {

        return new MapFragment();
    }

    private MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<Event> events;
    private FloatingActionButton mFabLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LatLng myLocationLatLng;
    private ChangeColorIconToolbarListener mChangeColorIconToolbarListener;

    public interface ChangeColorIconToolbarListener {
        void onChangeColorIconToolbar(@DrawableRes int drawableHome, @DrawableRes int drawableFilter);
    }

    public void setChangeColorIconToolbarListener(ChangeColorIconToolbarListener listener) {
        mChangeColorIconToolbarListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mFabLocation = (FloatingActionButton) view.findViewById(R.id.fab_my_location);
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

                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(App.getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    buildGoogleApiClient();
                    googleMap.setMyLocationEnabled(true);
                }

                googleMap.getUiSettings().setMyLocationButtonEnabled(false);

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
                        double doubleRadius = results1[0];
                        int intRadius = (int)doubleRadius;

                        if (myLocationLatLng != null ) {

                            googleMap.clear();

//                            Realm deleteFromRealm = Realm.getDefaultInstance();
//                            final RealmResults<Event> results = deleteFromRealm.where(Event.class).findAll();
//                            deleteFromRealm.executeTransaction(new Realm.Transaction() {
//                                @Override
//                                public void execute(Realm realm) {
//                                    results.deleteAllFromRealm();
//                                }
//                            });
//                            deleteFromRealm.close();

                            GeopointArrayResponce geopointArrayResponce = new GeopointArrayResponce();
                            geopointArrayResponce.setLat((float)myLocationLatLng.latitude);
                            geopointArrayResponce.setLng((float)myLocationLatLng.longitude);

                            HappRestClient.getInstance().setEventsMap(geopointArrayResponce, intRadius);

                            Realm realm = Realm.getDefaultInstance();
                            RealmResults<Event> eventRealmResults = realm.where(Event.class).findAll();
                            events = (ArrayList<Event>)realm.copyFromRealm(eventRealmResults);
                            realm.close();


                            for (int p = 0; p < events.size(); p++) {
                                if (events.get(p).getGeopoint() != null) {

                                    double lat = events.get(p).getGeopoint().getLat();
                                    double lng = events.get(p).getGeopoint().getLng();
                                    LatLng location = new LatLng(lat, lng);

                                    googleMap.addMarker(new MarkerOptions()
                                            .position(location)
                                            .icon(BitmapDescriptorFactory.defaultMarker())
                                            .title(events.get(p).getTitle()));

//                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
                                } else {
                                    p++;
                                }
                            }
                        }

                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        for (int p = 0; p < events.size(); p++) {
                            if (marker.getTitle().equals(events.get(p).getTitle())) {

                                    Intent goToEventMapIntent = new Intent(App.getContext(), EventMapActivity.class);
                                    goToEventMapIntent.putExtra("event_id_for_map", events.get(p).getId());
                                    goToEventMapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(goToEventMapIntent);

//                                Toast.makeText(App.getContext(), "marker is  " +
//                                        events.get(p).getTitle() + "// id: " + events.get(p).getId() , Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    }
                });

            }
        });


        mFabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdates();
            }
        });


        return view;
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(App.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocationLatLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(App.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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
        mChangeColorIconToolbarListener.onChangeColorIconToolbar(R.drawable.ic_menu_gray, R.drawable.ic_filter_gray);
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