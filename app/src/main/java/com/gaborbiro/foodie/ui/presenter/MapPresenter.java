package com.gaborbiro.foodie.ui.presenter;

import android.support.annotation.NonNull;

import com.gaborbiro.foodie.ui.view.TouchableWrapper;
import com.google.android.gms.maps.GoogleMap;

import hollowsoft.slidingdrawer.SlidingDrawer;

/**
 * Presenter in the MVP pattern
 */
public interface MapPresenter extends TouchableWrapper.MapTouchListener {
    void onScreenStarted(GoogleMap googleMap, SlidingDrawer drawer);

    boolean handleBackPressed();

    void onScreenStopped();

    void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults);

    void onRefreshRequested();
}
