package com.gaborbiro.foodie.provider.places;

import android.location.Location;

import com.gaborbiro.foodie.provider.places.model.Place;
import com.gaborbiro.foodie.provider.retrofit.Callback;
import com.gaborbiro.foodie.provider.retrofit.RetrofitUtil;

import java.util.List;

import retrofit2.Call;

public class PlacesApiImpl implements PlacesApi {

    private static final String GOOGLE_PLACES_API_KEY =
            "AIzaSyBDwqC3Emv86CJOYxhfC_LYps9caIfucEs";

    private PlacesApiRequestInterface mApi;

    private Call mCurrentCall;

    public PlacesApiImpl(PlacesApiRequestInterface api) {
        mApi = api;
    }

    @Override public int getPlaces(Location location, int radius, String type,
            Callback<List<Place>> callback) {
        if (mCurrentCall != null) {
            mCurrentCall.cancel();
        }
        mCurrentCall = mApi.getPlaces(location.getLatitude() + "," +
                location.getLongitude(), radius, type, GOOGLE_PLACES_API_KEY);
        RetrofitUtil.executeWithRetry(mCurrentCall, callback);
        return mCurrentCall.request()
                .url()
                .hashCode();
    }
}
