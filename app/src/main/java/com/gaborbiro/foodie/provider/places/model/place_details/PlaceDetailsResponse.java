package com.gaborbiro.foodie.provider.places.model.place_details;

import com.gaborbiro.foodie.provider.retrofit.PayloadWrapper;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class PlaceDetailsResponse
        implements PayloadWrapper<PlaceDetails> {
    public PlaceDetails result;
    public String status;

    @Override public PlaceDetails getPayload() {
        return result;
    }
}
