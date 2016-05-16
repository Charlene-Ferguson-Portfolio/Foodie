package com.gaborbiro.foodie.provider.places;

import android.location.Location;

import com.gaborbiro.foodie.provider.places.model.Place;
import com.gaborbiro.foodie.provider.retrofit.Callback;

import java.util.List;

public interface PlacesApi {

    int getPlaces(double latitude, double longitude, int radius, String type,
            Callback<List<Place>> callback);
}
