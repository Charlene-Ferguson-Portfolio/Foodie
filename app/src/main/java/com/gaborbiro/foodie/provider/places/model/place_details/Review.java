package com.gaborbiro.foodie.provider.places.model.place_details;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class Review {

    public List<Aspect> aspects = new ArrayList<Aspect>();
    @SerializedName("author_name") public String authorName;
    @SerializedName("author_url") public String authorUrl;
    public String language;
    public int rating;
    public String text;
    public int time;
    @SerializedName("profile_photo_url") public String profilePhotoUrl;
}
