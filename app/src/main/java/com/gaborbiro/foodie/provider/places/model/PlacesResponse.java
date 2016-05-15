package com.gaborbiro.foodie.provider.places.model;

import com.gaborbiro.foodie.provider.retrofit.PayloadWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class PlacesResponse
        implements PayloadWrapper<List<Place>> {

    public String nextPageToken;
    public List<Place> results = new ArrayList<Place>();
    public String status;

    @Override public List<Place> getPayload() {
        return results;
    }
}
