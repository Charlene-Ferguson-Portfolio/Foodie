
package com.gaborbiro.foodie.provider.places.model.common;

import com.gaborbiro.foodie.provider.places.model.place_details.Close;
import com.gaborbiro.foodie.provider.places.model.place_details.Open;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Period {

    @SerializedName("close")
    public Close close;
    @SerializedName("open")
    public Open open;

}
