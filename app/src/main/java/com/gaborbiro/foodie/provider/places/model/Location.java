package com.gaborbiro.foodie.provider.places.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class Location implements Parcelable {

    public double lat;
    public double lng;

    public Location(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Double.compare(location.lat, lat) != 0) return false;
        if (Double.compare(location.lng, lng) != 0) return false;

        return true;
    }

    @Override public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lng);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override public String toString() {
        return "{lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    public static final Parcelable.Creator<Location> CREATOR =
            new Parcelable.Creator<Location>() {

                @Override public Location createFromParcel(Parcel in) {
                    return new Location(in);
                }

                @Override public Location[] newArray(int size) {
                    return new Location[size];
                }
            };
}
