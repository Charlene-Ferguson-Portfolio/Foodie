package com.gaborbiro.foodie.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.di.AppModule;
import com.gaborbiro.foodie.ui.di.DaggerUIComponent;
import com.gaborbiro.foodie.ui.di.PlacesPresenterModule;
import com.gaborbiro.foodie.ui.presenter.MapPresenter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import javax.inject.Inject;

public class PlaceMapActivity extends FragmentActivity
        implements OnMapReadyCallback, TouchableWrapper.MapTouchListener {

    @Inject public MapPresenter mPresenter;

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
        mPresenter.setMap(googleMap);
    }

    @Override protected void onStop() {
        super.onStop();
        mPresenter.onScreenStopped();
    }

    @Override public void onBackPressed() {
        if (!mPresenter.handleBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override public void onMapTouched() {
        mPresenter.onMapTouched();
    }
}
