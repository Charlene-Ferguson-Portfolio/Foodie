package com.gaborbiro.foodie.provider.places.model.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class Photo implements Parcelable {

    public int height;
    @SerializedName("photo_reference")
    public String photoReference;
    public int width;

    public Photo(Parcel in) {
        height = in.readInt();
        photoReference = in.readString();
        width = in.readInt();
    }

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

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(height);
        dest.writeString(photoReference);
        dest.writeInt(width);
    }

    public static final Parcelable.Creator<Photo> CREATOR =
            new Parcelable.Creator<Photo>() {

                @Override public Photo createFromParcel(Parcel in) {
                    return new Photo(in);
                }

                @Override public Photo[] newArray(int size) {
                    return new Photo[size];
                }
            };
}
