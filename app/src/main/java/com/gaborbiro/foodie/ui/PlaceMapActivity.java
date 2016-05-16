package com.gaborbiro.foodie.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.di.AppModule;
import com.gaborbiro.foodie.provider.places.model.Place;
import com.gaborbiro.foodie.ui.di.DaggerUIComponent;
import com.gaborbiro.foodie.ui.di.PlacesPresenterModule;
import com.gaborbiro.foodie.ui.model.LocationModel;
import com.gaborbiro.foodie.ui.model.PlacesModel;
import com.gaborbiro.foodie.util.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class PlaceMapActivity extends FragmentActivity
        implements OnMapReadyCallback, TouchableWrapper.MapTouchListener {

    @Inject public PlacesPresenter mPresenter;
    private GoogleMap mMap;
    private boolean mIsFollowingMode = true;
    private LatLng mLastSearchLocation;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_map);

        DaggerUIComponent.builder()
                .appModule(new AppModule(getApplication()))
                .placesPresenterModule(new PlacesPresenterModule(this))
                .build()
                .inject(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(
                        R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(
                    new GoogleMap.OnMyLocationButtonClickListener() {

                        @Override public boolean onMyLocationButtonClick() {
                            if (!mIsFollowingMode) {
                                Toast.makeText(PlaceMapActivity.this,
                                        "The map is now following you",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                            mIsFollowingMode = true;
                            loadRestaurants();
                            return false;
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        EventBus.getDefault()
                .register(this);
        mPresenter.onScreenStarted();
    }

    @Override protected void onStop() {
        super.onStop();
        mPresenter.onScreenStopped();
        EventBus.getDefault()
                .unregister(this);
    }

    @Subscribe public void onEvent(LocationModel.UpdateEvent event) {
        if (mIsFollowingMode) {
            LatLng newLocation = new LatLng(event.currentBestLocation.getLatitude(),
                    event.currentBestLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
        }
    }

    @Subscribe public void onEvent(PlacesModel.UpdateEvent event) {
        mMap.clear();
        for (Place p : event.places) {
            LatLng position =
                    new LatLng(p.geometry.location.lat, p.geometry.location.lng);
            StringBuffer snippet = new StringBuffer(p.vicinity);
            if (p.openingHours != null) {
                snippet.append("\n");
                snippet.append(p.openingHours.openNow ? "Open" : "Closed");
            }
            mMap.addMarker(new MarkerOptions().position(position)
                    .title(p.name)
                    .snippet(snippet.toString()));
        }
    }

    @Subscribe public void onEvent(PlacesModel.UpdateError event) {
        event.error.printStackTrace();
        Toast.makeText(PlaceMapActivity.this, event.error.getMessage(),
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override public void onMapTouched() {
        if (mIsFollowingMode) {
            Toast.makeText(PlaceMapActivity.this, "The map has stopped following you",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        mIsFollowingMode = false;
        loadRestaurants();
    }

    private void loadRestaurants() {
        LatLng target = LocationUtils.roundDown(mMap.getCameraPosition().target);
        if (mLastSearchLocation == null ||
                LocationUtils.distance(target.latitude, target.longitude,
                        mLastSearchLocation.latitude, mLastSearchLocation.longitude) >
                        LocationUtils.LOCATION_UPDATE_THRESHOLD_METERS) {
            mLastSearchLocation = target;
            mPresenter.loadPlaces(target);
        }
    }
}
