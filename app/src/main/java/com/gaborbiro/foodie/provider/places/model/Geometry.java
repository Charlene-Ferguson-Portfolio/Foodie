package com.gaborbiro.foodie.provider.places.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class Geometry implements Parcelable {

    public Location location;

    public Geometry(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Geometry geometry = (Geometry) o;

        if (location != null ? !location.equals(geometry.location)
                             : geometry.location != null) return false;

        return true;
    }

    @Override public int hashCode() {
        return location != null ? location.hashCode() : 0;
    }

    @Override public String toString() {
        return location != null ? location.toString() : "";
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, 0);
    }

    public static final Parcelable.Creator<Geometry> CREATOR =
            new Parcelable.Creator<Geometry>() {

                @Override public Geometry createFromParcel(Parcel in) {
                    return new Geometry(in);
                }

                @Override public Geometry[] newArray(int size) {
                    return new Geometry[size];
                }
            };
}
