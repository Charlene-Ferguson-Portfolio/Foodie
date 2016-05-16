package com.gaborbiro.foodie.provider.places.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class Place implements Parcelable {

    public Geometry geometry;
    public String icon;
    public String id;
    public String name;
    public List<Photo> photos = new ArrayList<Photo>();
    @SerializedName("place_id")
    public String placeId;
    public double rating;
    public String reference;
    public String scope;
    public List<String> types = new ArrayList<String>();
    public String vicinity;
    @SerializedName("opening_hours")
    public OpeningHours openingHours;
    public int priceLevel;

    public Place(Parcel in) {
        geometry = in.readParcelable(Geometry.class.getClassLoader());
        icon = in.readString();
        id = in.readString();
        name = in.readString();
        photos = in.createTypedArrayList(Photo.CREATOR);
        placeId = in.readString();
        rating = in.readDouble();
        reference = in.readString();
        scope = in.readString();
        types = in.createStringArrayList();
        vicinity = in.readString();
        openingHours = in.readParcelable(OpeningHours.class.getClassLoader());
        priceLevel = in.readInt();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (Double.compare(place.rating, rating) != 0) return false;
        if (priceLevel != place.priceLevel) return false;
        if (geometry != null ? !geometry.equals(place.geometry) : place.geometry != null)
            return false;
        if (icon != null ? !icon.equals(place.icon) : place.icon != null) return false;
        if (id != null ? !id.equals(place.id) : place.id != null) return false;
        if (name != null ? !name.equals(place.name) : place.name != null) return false;
        if (photos != null ? !photos.equals(place.photos) : place.photos != null)
            return false;
        if (placeId != null ? !placeId.equals(place.placeId) : place.placeId != null)
            return false;
        if (reference != null ? !reference.equals(place.reference)
                              : place.reference != null) return false;
        if (scope != null ? !scope.equals(place.scope) : place.scope != null)
            return false;
        if (types != null ? !types.equals(place.types) : place.types != null)
            return false;
        if (vicinity != null ? !vicinity.equals(place.vicinity) : place.vicinity != null)
            return false;
        if (openingHours != null ? !openingHours.equals(place.openingHours)
                                 : place.openingHours != null) return false;

        return true;
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = geometry != null ? geometry.hashCode() : 0;
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (photos != null ? photos.hashCode() : 0);
        result = 31 * result + (placeId != null ? placeId.hashCode() : 0);
        temp = Double.doubleToLongBits(rating);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (types != null ? types.hashCode() : 0);
        result = 31 * result + (vicinity != null ? vicinity.hashCode() : 0);
        result = 31 * result + (openingHours != null ? openingHours.hashCode() : 0);
        result = 31 * result + priceLevel;
        return result;
    }

    @Override public String toString() {
        return "{" +
                "geometry=" + geometry +
                ", icon='" + icon + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", photos=" + photos +
                ", placeId='" + placeId + '\'' +
                ", rating=" + rating +
                ", reference='" + reference + '\'' +
                ", scope='" + scope + '\'' +
                ", types=" + types +
                ", vicinity='" + vicinity + '\'' +
                ", openingHours=" + openingHours +
                ", priceLevel=" + priceLevel +
                '}';
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(geometry, 0);
        dest.writeString(icon);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedList(photos);
        dest.writeString(placeId);
        dest.writeDouble(rating);
        dest.writeString(reference);
        dest.writeString(scope);
        dest.writeStringList(types);
        dest.writeString(vicinity);
        dest.writeParcelable(openingHours, 0);
        dest.writeInt(priceLevel);
    }

    public static final Parcelable.Creator<Place> CREATOR =
            new Parcelable.Creator<Place>() {

                @Override public Place createFromParcel(Parcel in) {
                    return new Place(in);
                }

                @Override public Place[] newArray(int size) {
                    return new Place[size];
                }
            };

    public Photo getPhoto(int idealHeight, int idealWidth) {
        if (photos == null) {
            return null;
        }
        int bestAvgDiff = Integer.MAX_VALUE;
        Photo bestPhoto = null;

        for (Photo photo : photos) {
            int avgDiff = (Math.abs(idealHeight - photo.height) +
                    Math.abs(idealWidth - photo.width)) / 2;
            if (avgDiff < bestAvgDiff) {
                bestAvgDiff = avgDiff;
                bestPhoto = photo;
            }
        }
        return bestPhoto;
    }
}
