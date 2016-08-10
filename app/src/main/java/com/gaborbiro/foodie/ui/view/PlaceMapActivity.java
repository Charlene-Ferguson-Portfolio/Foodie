package com.gaborbiro.foodie.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.di.AppModule;
import com.gaborbiro.foodie.ui.di.DaggerUIComponent;
import com.gaborbiro.foodie.ui.di.PlacesPresenterModule;
import com.gaborbiro.foodie.ui.presenter.MapPresenter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import javax.inject.Inject;

import hollowsoft.slidingdrawer.SlidingDrawer;

public class PlaceMapActivity extends AppCompatActivity
        implements OnMapReadyCallback, TouchableWrapper.MapTouchListener {

    @Inject public MapPresenter mPresenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.restaurants);

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
        mPresenter.onScreenStarted(googleMap, (SlidingDrawer) findViewById(R.id.drawer));
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

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mPresenter.onRefreshRequested();
                return true;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onMapPinched() {
        mPresenter.onMapPinched();
    }

    @Override
    public void onMapZoomed(float zoom) {
        mPresenter.onMapZoomed(zoom);
    }

    @Override public void onMapReleased() {
        mPresenter.onMapReleased();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        mPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
