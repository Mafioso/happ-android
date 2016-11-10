//package com.happ;
//
//import android.content.Context;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//
//import com.google.android.gms.location.LocationListener;
//
///**
// * Created by dante on 11/10/16.
// */
//public class MyLocationListener implements LocationListener {
//
//    static Location imHere; // здесь будет всегда доступна самая последняя информация о местоположении пользователя.
//
//    public static void SetUpLocationListener(Context context) // это нужно запустить в самом начале работы программы
//    {
//        LocationManager locationManager = (LocationManager)
//                context.getSystemService(Context.LOCATION_SERVICE);
//
//        LocationListener locationListener = new MyLocationListener();
//
//        if (locationManager != null) {
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    5000,
//                    10,
//                    locationListener); // здесь можно указать другие более подходящие вам параметры
//
//            imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location loc) {
//        imHere = loc;
//    }
//    @Override
//    public void onProviderDisabled(String provider) {}
//    @Override
//    public void onProviderEnabled(String provider) {}
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {}
//}
