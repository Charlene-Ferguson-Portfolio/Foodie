package com.gaborbiro.foodie.provider.places.model.place_details;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class AddressComponent {

    @SerializedName("long_name") public String longName;
    @SerializedName("short_name") public String shortName;
    public List<String> types = new ArrayList<String>();
}
