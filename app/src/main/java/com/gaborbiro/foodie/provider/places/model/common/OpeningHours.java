package com.gaborbiro.foodie.provider.places.model.common;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class OpeningHours {

    @SerializedName("open_now") public boolean openNow;
    public List<Period> periods = new ArrayList<Period>();
    @SerializedName("weekday_text") public List<String> weekdayText =
            new ArrayList<String>();

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OpeningHours that = (OpeningHours) o;

        if (openNow != that.openNow) return false;
        if (periods != null ? !periods.equals(that.periods) : that.periods != null)
            return false;
        if (weekdayText != null ? !weekdayText.equals(that.weekdayText)
                                : that.weekdayText != null) return false;

        return true;
    }

    @Override public int hashCode() {
        int result = (openNow ? 1 : 0);
        result = 31 * result + (periods != null ? periods.hashCode() : 0);
        result = 31 * result + (weekdayText != null ? weekdayText.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append('{');
        buffer.append("openNow=" + openNow);
        if (periods != null) {
            buffer.append(", periods=" + periods);
        }
        buffer.append('}');
        return buffer.toString();
    }
}
