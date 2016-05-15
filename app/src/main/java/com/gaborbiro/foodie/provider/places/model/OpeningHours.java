package com.gaborbiro.foodie.provider.places.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class OpeningHours implements Parcelable {

    public boolean openNow;

    public OpeningHours(Parcel in) {
        openNow = in.readByte() == 1;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OpeningHours that = (OpeningHours) o;

        if (openNow != that.openNow) return false;

        return true;
    }

    @Override public int hashCode() {
        return (openNow ? 1 : 0);
    }

    @Override public String toString() {
        return "{openNow=" + openNow + '}';
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (openNow == true ? 1 : 0));
    }

    public static final Parcelable.Creator<OpeningHours> CREATOR =
            new Parcelable.Creator<OpeningHours>() {

                @Override public OpeningHours createFromParcel(Parcel in) {
                    return new OpeningHours(in);
                }

                @Override public OpeningHours[] newArray(int size) {
                    return new OpeningHours[size];
                }
            };
}