package com.gaborbiro.foodie.ui.presenter;

import com.gaborbiro.foodie.ui.TouchableWrapper;
import com.google.android.gms.maps.GoogleMap;

public interface MapPresenter extends PlacesPresenter, TouchableWrapper.MapTouchListener {
    void setMap(GoogleMap googleMap);

    boolean handleBackPressed();
}
