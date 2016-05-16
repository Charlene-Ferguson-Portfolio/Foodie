package com.gaborbiro.foodie.provider.places.model.place_details;

import com.gaborbiro.foodie.provider.places.model.common.Geometry;
import com.gaborbiro.foodie.provider.places.model.common.OpeningHours;
import com.gaborbiro.foodie.provider.places.model.common.Photo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class PlaceDetails {

    @SerializedName("address_components") public List<AddressComponent>
            addressComponents = new ArrayList<AddressComponent>();
    @SerializedName("adr_address") public String adrAddress;
    @SerializedName("formatted_address") public String formattedAddress;
    @SerializedName("formatted_phone_number") public String formattedPhoneNumber;
    public Geometry geometry;
    public String icon;
    public String id;
    @SerializedName("international_phone_number") public String internationalPhoneNumber;
    public String name;
    @SerializedName("opening_hours") public OpeningHours openingHours;
    public List<Photo> photos = new ArrayList<Photo>();
    @SerializedName("place_id") public String placeId;
    public double rating;
    public String reference;
    public List<Review> reviews = new ArrayList<Review>();
    public String scope;
    public List<String> types = new ArrayList<String>();
    public String url;
    @SerializedName("user_ratings_total") public int userRatingsTotal;
    @SerializedName("utc_offset") public int utcOffset;
    public String vicinity;
    public String website;

}
