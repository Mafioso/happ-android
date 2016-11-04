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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.happ.App;
import com.happ.R;

/**
 ** Created by dante on 11/1/16.
 * */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public MapFragment() {

    }

    public static MapFragment newInstance() {

        return new MapFragment();
    }

    GoogleMap googleMap;
    MapView mapView;

    //координаты для маркера
    private static final double TARGET_LATITUDE = 43.2182859;
    private static final double TARGET_LONGITUDE = 76.9256043;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return view;
    }


//    private void addMarker(){
//
//        double lat = TARGET_LATITUDE;
//        double lng = TARGET_LONGITUDE;
//        //устанавливаем позицию и масштаб отображения карты
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(lat, lng))
//                .zoom(15)
//                .build();
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        googleMap.animateCamera(cameraUpdate);
//
//        if(null != googleMap){
//            googleMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(lat, lng))
//                    .title("Mark")
//                    .draggable(false)
//            );
//        }
//    }

    @Override
    public void onMapReady(GoogleMap map) {
        View markerView = ((LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_marker, null);

//        IconGenerator tc = new IconGenerator(App.getContext());
//        Bitmap bmp = tc.makeIcon("HAPP"); // pass the text you want.

        LatLng sydney = new LatLng(TARGET_LATITUDE, TARGET_LONGITUDE);

        map.addMarker(new MarkerOptions()
                .position(sydney)
//                .title("Marker in Sydney")
//                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                .icon(BitmapDescriptorFactory
                        .fromBitmap(createDrawableFromView(
                                getActivity(),
                                markerView))));

//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney)); //without zoom


        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            Toast.makeText(App.getContext(), "LOL", Toast.LENGTH_SHORT).show();
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
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
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
