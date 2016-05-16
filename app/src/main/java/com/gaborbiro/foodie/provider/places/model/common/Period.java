
package com.gaborbiro.foodie.provider.places.model.common;

import javax.annotation.Generated;

import com.gaborbiro.foodie.provider.places.model.place_details.Close;
import com.gaborbiro.foodie.provider.places.model.place_details.Open;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Period {

    @SerializedName("close")
    public Close close;
    @SerializedName("open")
    public Open open;

}
