
package com.gaborbiro.foodie.provider.places.model.place_details;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Review {

    @SerializedName("aspects")
    @Expose
    public List<Aspect> aspects = new ArrayList<Aspect>();
    @SerializedName("author_name")
    @Expose
    public String authorName;
    @SerializedName("author_url")
    @Expose
    public String authorUrl;
    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("rating")
    @Expose
    public int rating;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("time")
    @Expose
    public int time;
    @SerializedName("profile_photo_url")
    @Expose
    public String profilePhotoUrl;

}
