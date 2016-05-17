package com.gaborbiro.foodie.ui.adapter;

import com.gaborbiro.foodie.provider.places.model.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class PlaceListAdapter {

    public static Map<Marker, Place> adapt(Place[] places, GoogleMap map) {
        Map<Marker, Place> markerMap = new HashMap<>();
        for (Place place : places) {
            LatLng position =
                    new LatLng(place.geometry.location.lat, place.geometry.location.lng);
            StringBuilder snippet = new StringBuilder(place.vicinity);
            if (place.openingHours != null) {
                snippet.append("\n");
                snippet.append(place.openingHours.openNow ? "Open" : "Closed");
            }
            Marker m = map.addMarker(new MarkerOptions().position(position));
            markerMap.put(m, place);
        }
        return markerMap;
    }
}
