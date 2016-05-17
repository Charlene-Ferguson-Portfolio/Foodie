package com.gaborbiro.foodie.provider.places.model.common;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class Geometry {

    public Location location;

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
}
