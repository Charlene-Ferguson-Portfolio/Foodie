package com.gaborbiro.foodie.provider.places.model.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo") public class OpeningHours implements Parcelable {

    @SerializedName("open_now") public boolean openNow;
    @SerializedName("periods") public List<Period> periods = new ArrayList<Period>();
    @SerializedName("weekday_text") public List<String> weekdayText =
            new ArrayList<String>();


    public OpeningHours(Parcel in) {
        openNow = in.readByte() == 1;
    }

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
