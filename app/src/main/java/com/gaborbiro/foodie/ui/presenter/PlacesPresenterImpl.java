package com.gaborbiro.foodie.ui.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.gaborbiro.foodie.ProgressEvent;
import com.gaborbiro.foodie.provider.places.PlacesApi;
import com.gaborbiro.foodie.provider.places.model.places.Place;
import com.gaborbiro.foodie.provider.retrofit.Callback;
import com.gaborbiro.foodie.ui.model.LocationModel;
import com.gaborbiro.foodie.ui.model.PlacesModel;
import com.gaborbiro.foodie.util.LocationUtils;
import com.gaborbiro.foodie.util.Logger;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PlacesPresenterImpl implements PlacesPresenter {

    private static final String TAG = PlacesPresenterImpl.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION = 1;

    protected Context mAppContext;
    protected PlacesApi mPlacesApi;
    protected Activity mActivity;

    private final PlacesModel mPlacesModel = new PlacesModel();
    private final LocationModel mLocationModel = new LocationModel();

    private final Callback<List<Place>> mPlaceListCallback = new Callback<List<Place>>() {
        @Override public void onResponse(int requestId, List<Place> result) {
            mPlacesModel.addPlaces(result);
        }

        @Override public void onFailure(int requestId, Throwable t) {
            PlacesModel.sendErrorEvent(t);
        }
    };

    public PlacesPresenterImpl(Context appContext, PlacesApi placesApi, Activity activity) {
        mAppContext = appContext;
        mPlacesApi = placesApi;
        mActivity = activity;
    }

    public void onScreenStarted() {
        startListeningForLocation();

        if (mLocationModel.getCurrentBestLocation() != null) {
            // let's use the last known location instead of waiting for a lock
            // when a more accurate lock happens later, a new request will be made
            loadPlaces();
        }
    }

    public void onScreenStopped() {
        stopListeningForLocation();
    }

    public void loadPlaces() {
        if (mLocationModel.getCurrentBestLocation() != null) {
            Location location = mLocationModel.getCurrentBestLocation();
            mPlacesApi.getPlaces(location.getLatitude(), location.getLongitude(),
                    LocationUtils.SEARCH_RADIUS_METERS, PlacesApi.Type.TYPE_RESTAURANT,
                    mPlaceListCallback);
        } else {
            Toast.makeText(mAppContext, "No location available", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void loadPlaces(LatLng target) {
        mPlacesApi.getPlaces(target.latitude, target.longitude,
                LocationUtils.SEARCH_RADIUS_METERS, PlacesApi.Type.TYPE_RESTAURANT,
                mPlaceListCallback);
    }

    private void startListeningForLocation() {
        if (verifyLocationPermissions()) {
            LocationManager locationManager =
                    (LocationManager) mAppContext.getSystemService(
                            Context.LOCATION_SERVICE);
            try {
                tryLastKnownLocation(locationManager, LocationManager.GPS_PROVIDER);
                tryLastKnownLocation(locationManager, LocationManager.NETWORK_PROVIDER);

                if (mLocationModel.getCurrentBestLocation() == null) {
                    ProgressEvent.sendProgressStartEvent(PlacesPresenterImpl.this);
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LocationUtils.LOCATION_UPDATE_THRESHOLD_TIME_MSEC,
                        LocationUtils.LOCATION_UPDATE_THRESHOLD_METERS,
                        mLocationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LocationUtils.LOCATION_UPDATE_THRESHOLD_TIME_MSEC,
                        LocationUtils.LOCATION_UPDATE_THRESHOLD_METERS,
                        mLocationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void tryLastKnownLocation(LocationManager locationManager, String provider)
            throws SecurityException {
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);

        if (lastKnownLocation == null) {
            return;
        }

        Location discreteLastKnownLocation = LocationUtils.roundDown(lastKnownLocation);

        if (LocationUtils.isBetterLocation(discreteLastKnownLocation,
                mLocationModel.getCurrentBestLocation())) {
            Logger.d(TAG, "Correction: " +
                    (int) LocationUtils.distance(discreteLastKnownLocation,
                            lastKnownLocation) + "m");
            mLocationModel.setCurrentBestLocation(discreteLastKnownLocation);
        }
    }

    private void stopListeningForLocation() {
        if (mLocationModel.getCurrentBestLocation() != null) {
            ProgressEvent.sendProgressEndEvent(PlacesPresenterImpl.this);
        }
        if (hasFineLocationPermission() && hasCoarseLocationPermission()) {
            LocationManager locationManager =
                    (LocationManager) mAppContext.getSystemService(
                            Context.LOCATION_SERVICE);
            try {
                locationManager.removeUpdates(mLocationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public PlacesModel getPlacesModel() {
        return mPlacesModel;
    }

    public LocationModel getLocationModel() {
        return mLocationModel;
    }

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (mLocationModel.getCurrentBestLocation() != null) {
                ProgressEvent.sendProgressEndEvent(PlacesPresenterImpl.this);
            }

            Location discreteLocation = LocationUtils.roundDown(location);

            if (LocationUtils.isBetterLocation(discreteLocation,
                    mLocationModel.getCurrentBestLocation())) {
                Logger.d(TAG, "New location correction: " +
                        (int) LocationUtils.distance(discreteLocation, location));
                mLocationModel.setCurrentBestLocation(discreteLocation);
                loadPlaces();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    // PERMISSIONS START

    /**
     * @return true if no permissions needs to be requested, false otherwise
     */
    private boolean verifyLocationPermissions() {
        List<String> permissionsToAsk = new ArrayList<>();

        if (!hasFineLocationPermission()) {
            permissionsToAsk.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!hasCoarseLocationPermission()) {
            permissionsToAsk.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!permissionsToAsk.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity,
                    permissionsToAsk.toArray(new String[permissionsToAsk.size()]),
                    PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION);
        }
        return permissionsToAsk.isEmpty();
    }

    private boolean hasFineLocationPermission() {
        return ContextCompat.checkSelfPermission(mAppContext,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasCoarseLocationPermission() {
        return ContextCompat.checkSelfPermission(mAppContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startListeningForLocation();
                } else {
                    Toast.makeText(mAppContext,
                            "This app cannot work without gps location",
                            Toast.LENGTH_SHORT)
                            .show();
                    mActivity.finish();
                }
            }
        }
    }
    // PERMISSIONS END
}
