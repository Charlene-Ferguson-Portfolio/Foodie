package com.gaborbiro.foodie.provider.places.model.places;

import com.gaborbiro.foodie.provider.retrofit.PayloadWrapper;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class PlacesResponse
        implements PayloadWrapper<List<Place>> {

    @SerializedName("next_page_token") public String nextPageToken;
    public List<Place> results = new ArrayList<Place>();
    public String status;

    @Override public List<Place> getPayload() {
        return results;
    }
}
