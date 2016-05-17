package com.gaborbiro.foodie.provider.places.model.common;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class Photo {

    public int height;
    @SerializedName("photo_reference") public String photoReference;
    public int width;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (height != photo.height) return false;
        if (width != photo.width) return false;
        if (photoReference != null ? !photoReference.equals(photo.photoReference)
                                   : photo.photoReference != null) return false;

        return true;
    }

    @Override public int hashCode() {
        int result = height;
        result = 31 * result + (photoReference != null ? photoReference.hashCode() : 0);
        result = 31 * result + width;
        return result;
    }

    @Override public String toString() {
        return "{" +
                "height=" + height +
                ", photoReference='" + photoReference + '\'' +
                ", width=" + width +
                '}';
    }
}
