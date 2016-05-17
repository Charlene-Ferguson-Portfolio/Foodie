package com.gaborbiro.foodie.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.provider.places.PlacesApi;
import com.gaborbiro.foodie.provider.places.model.common.Photo;
import com.gaborbiro.foodie.provider.places.model.place_details.PlaceDetails;
import com.gaborbiro.foodie.provider.places.model.places.Place;
import com.gaborbiro.foodie.provider.retrofit.Callback;
import com.gaborbiro.foodie.ui.adapter.PlaceDetailBodyAdapter;
import com.gaborbiro.foodie.ui.adapter.PlaceDetailHeaderAdapter;
import com.gaborbiro.foodie.ui.adapter.PlaceListAdapter;
import com.gaborbiro.foodie.ui.view.ProgressEvent;
import com.gaborbiro.foodie.util.AndroidLocationUtils;
import com.gaborbiro.foodie.util.LocationUtils;
import com.gaborbiro.foodie.util.Logger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.common.collect.EvictingQueue;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;
import hollowsoft.slidingdrawer.SlidingDrawer;

public class MapPresenterImpl implements MapPresenter, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapPresenterImpl.class.getSimpleName();

    protected Context mAppContext;
    protected PlacesApi mPlacesApi;
    protected Activity mActivity;

    private GoogleMap mMap;
    private Map<Marker, Place> mMarkerMap = new HashMap<>();
    private Location mCurrentBestLocation;
    private LatLng mLastSearchLocation;
    private boolean mIsFollowingMode = true;

    private static final int MAX_PLACE_COUNT = 100;
    /**
     * Keeps a maximum of {@value MAX_PLACE_COUNT} pins on the map
     */
    private Queue<Place> mPlaces = EvictingQueue.create(MAX_PLACE_COUNT);

    /**
     * From the tapped pin
     */
    private Place mSelectedPlace;

    @InjectView(R.id.drawer) SlidingDrawer mDrawer;
    @InjectView(R.id.header) RelativeLayout mHeader;
    @InjectView(R.id.content) RelativeLayout mContent;

    public MapPresenterImpl(Context appContext, PlacesApi placesApi, Activity activity) {
        mAppContext = appContext;
        mPlacesApi = placesApi;
        mActivity = activity;

        ButterKnife.inject(this, activity);
        EventBus.getDefault()
                .unregister(this);

        mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            @Override public void onDrawerOpened() {
                if (mSelectedPlace != null) {
                    loadPlaceDetails(mSelectedPlace);
                }
            }
        });
    }

    /**
     * Invoke this when the map is ready
     */
    public void onScreenStarted(GoogleMap map, SlidingDrawer drawer) {
        mMap = map;
        mDrawer = drawer;

        String[] missingLocationPermissions =
                AndroidLocationUtils.verifyLocationPermissions(mAppContext);

        if (missingLocationPermissions.length > 0) {
            AndroidLocationUtils.askForLocationPermissions(mActivity,
                    missingLocationPermissions);
        } else {
            setupMap(mMap);
            boolean isFirstLocationFetch = mCurrentBestLocation == null;
            Location lastKnownLocation =
                    AndroidLocationUtils.getLastKnownLocation(mAppContext);

            if (LocationUtils.isBetterLocation(lastKnownLocation, mCurrentBestLocation)) {
                mCurrentBestLocation = lastKnownLocation;
            }
            startListeningForLocation();

            if (mCurrentBestLocation != null) {
                centerZoomCamera(mCurrentBestLocation, isFirstLocationFetch);
                loadPlaces(mCurrentBestLocation);
            }
        }
    }

    private void setupMap(GoogleMap map) {
        try {
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(
                    new GoogleMap.OnMyLocationButtonClickListener() {

                        @Override public boolean onMyLocationButtonClick() {
                            if (!mIsFollowingMode) {
                                Toast.makeText(mAppContext,
                                        "The map is now following you",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                            mIsFollowingMode = true;
                            return false;
                        }
                    });
            map.setOnMarkerClickListener(this);
            map.getUiSettings()
                    .setZoomControlsEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override public void onScreenStopped() {
        stopListeningForLocation();
    }

    // LOCATION START

    private void startListeningForLocation() {
        if (mCurrentBestLocation == null) {
            ProgressEvent.sendProgressStartEvent(this);
        }

        LocationManager locationManager =
                (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    LocationUtils.LOCATION_UPDATE_THRESHOLD_TIME_MSEC,
                    LocationUtils.LOCATION_UPDATE_THRESHOLD_METERS, mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LocationUtils.LOCATION_UPDATE_THRESHOLD_TIME_MSEC,
                    LocationUtils.LOCATION_UPDATE_THRESHOLD_METERS, mLocationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopListeningForLocation() {
        if (mCurrentBestLocation != null) {
            ProgressEvent.sendProgressEndEvent(this);
        }
        LocationManager locationManager =
                (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.removeUpdates(mLocationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (mCurrentBestLocation != null) {
                ProgressEvent.sendProgressEndEvent(this);
            }

            // We strip the coordinates by a few decimals. Not too much so that the user
            // doesn't have to pan too much for a new search (we keep search granularity
            // smaller than the search radius) but enough to greatly increase the
            // chance of a (retrofit) cache hit if the user pans back to a previously
            // searched area.
            Location discreteLocation = LocationUtils.roundDown(location);

            if (LocationUtils.isBetterLocation(discreteLocation, mCurrentBestLocation)) {
                Logger.d(TAG, "New location correction: " +
                        (int) LocationUtils.distance(discreteLocation, location));
                boolean isFirstLocationFetch = mCurrentBestLocation == null;
                mCurrentBestLocation = discreteLocation;
                centerZoomCamera(mCurrentBestLocation, isFirstLocationFetch);
                loadPlaces(mCurrentBestLocation);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public void centerZoomCamera(Location location, boolean noAnimation) {
        if (mIsFollowingMode) {
            LatLng newLocation =
                    new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(newLocation, 15);

            if (noAnimation) {
                mMap.moveCamera(update);
            } else {
                mMap.animateCamera(update);
            }
        }
    }
    // LOCATION END

    // PERMISSIONS START

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (AndroidLocationUtils.verifyLocationPermissionsResult(requestCode, permissions,
                grantResults)) {
            onScreenStarted(mMap, mDrawer);
        } else {
            Toast.makeText(mAppContext, "This app cannot work without gps location",
                    Toast.LENGTH_SHORT)
                    .show();
            mActivity.finish();
        }
    }
    // PERMISSIONS END

    @Override public void onMapTouched() {
        if (mIsFollowingMode) {
            Toast.makeText(mAppContext, "The map has stopped following you",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        mIsFollowingMode = false;
        loadPlaces(mMap.getCameraPosition().target, false);
    }

    @Override public boolean onMarkerClick(Marker marker) {
        mSelectedPlace = mMarkerMap.get(marker);
        displayPlaceHeader(mSelectedPlace);
        return true;
    }

    @Override public boolean handleBackPressed() {
        if (mContent.getVisibility() == View.VISIBLE) {
            mDrawer.animateClose();
            return true;
        }
        if (mDrawer.getVisibility() == View.VISIBLE) {
            hideDrawerHandler();
            return true;
        }
        return false;
    }

    // DATA LOADING AND DISPLAY START


    @Override public void onRefreshRequested() {
        if (mMap != null) {
            loadPlaces(mMap.getCameraPosition().target, true);
        }
    }

    private void loadPlaces(Location location) {
        loadPlaces(new LatLng(location.getLatitude(), location.getLongitude()), false);
    }

    private void loadPlaces(LatLng location, boolean force) {
        if (force) {
            mLastSearchLocation = location;
            mPlacesApi.getPlaces(location.latitude, location.longitude,
                    LocationUtils.SEARCH_RADIUS_METERS,
                    PlacesApi.Type.TYPE_RESTAURANT, mPlaceListCallback);
        } else {
            LatLng target = LocationUtils.roundDown(location);
            if (mLastSearchLocation == null ||
                    LocationUtils.distance(target.latitude, target.longitude,
                            mLastSearchLocation.latitude, mLastSearchLocation.longitude) >
                            LocationUtils.LOCATION_UPDATE_THRESHOLD_METERS) {
                mLastSearchLocation = target;
                mPlacesApi.getPlaces(target.latitude, target.longitude,
                        LocationUtils.SEARCH_RADIUS_METERS,
                        PlacesApi.Type.TYPE_RESTAURANT, mPlaceListCallback);
            }
        }
    }

    private final Callback<List<Place>> mPlaceListCallback = new Callback<List<Place>>() {
        @Override public void onResponse(int requestId, List<Place> result) {
            mPlaces.addAll(result);
            displayPlaces(mPlaces.toArray(new Place[mPlaces.size()]));
        }

        @Override public void onFailure(int requestId, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mAppContext, t.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }

        @Override public void onFailure(int requestId, String message) {
            Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private void loadPlaceDetails(Place mSelectedPlace) {
        PlaceDetailBodyAdapter.clear(mContent);
        mPlacesApi.getPlace(mSelectedPlace.placeId, mPlaceDetailsCallback);
    }

    private Callback<PlaceDetails> mPlaceDetailsCallback = new Callback<PlaceDetails>() {
        @Override public void onResponse(int requestId, PlaceDetails result) {
            displayPlaceDetails(result);
        }

        @Override public void onFailure(int requestId, Throwable t) {
            t.printStackTrace();
            Toast.makeText(mAppContext, t.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }

        @Override public void onFailure(int requestId, String message) {
            Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private void displayPlaces(Place[] places) {
        synchronized (mMap) {
            PlaceListAdapter.clear(mMap);
            mMarkerMap = PlaceListAdapter.adapt(places, mMap);
        }
    }

    private void displayPlaceHeader(Place place) {
        Resources res = mAppContext.getResources();
        int thumbWidthPx = (int) res.getDimension(R.dimen.place_thumb_width);
        int thumbHeightPx = (int) res.getDimension(R.dimen.place_thumb_height);
        Photo photo = place.getPhoto(thumbWidthPx, thumbHeightPx);
        String imageUrl = null;

        if (photo != null) {
            imageUrl =
                    mPlacesApi.getPhotoUrlByReference(photo.photoReference, thumbWidthPx,
                            thumbHeightPx);
        }
        PlaceDetailHeaderAdapter.adapt(mAppContext, place, imageUrl, mHeader);

        if (mDrawer.getVisibility() == View.GONE) {
            showDrawerHandler();
        }
    }

    private void displayPlaceDetails(PlaceDetails details) {
        PlaceDetailBodyAdapter.adapt(details, mContent);
    }

    private void showDrawerHandler() {
        Animation bottomUp = AnimationUtils.loadAnimation(mAppContext, R.anim.bottom_up);
        mHeader.startAnimation(bottomUp);
        mDrawer.setVisibility(View.VISIBLE);
    }

    private void hideDrawerHandler() {
        Animation bottomDown =
                AnimationUtils.loadAnimation(mAppContext, R.anim.bottom_down);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {

            }

            @Override public void onAnimationEnd(Animation animation) {
                mDrawer.setVisibility(View.GONE);
            }

            @Override public void onAnimationRepeat(Animation animation) {

            }
        });
        mHeader.startAnimation(bottomDown);
    }
    // DATA LOADING AND DISPLAY END
}
