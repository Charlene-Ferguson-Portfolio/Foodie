package com.gaborbiro.foodie.provider.places;

import com.gaborbiro.foodie.provider.places.model.Place;
import com.gaborbiro.foodie.provider.retrofit.Callback;
import com.gaborbiro.foodie.provider.retrofit.RetrofitUtil;

import java.util.List;

import retrofit2.Call;

public class PlacesApiImpl implements PlacesApi {

    private static final String GOOGLE_PLACES_API_KEY =
            "AIzaSyBDwqC3Emv86CJOYxhfC_LYps9caIfucEs";

    private PlacesApiRequestInterface mApi;
    private String mServerUrl;

    private Call mCurrentCall;

    public PlacesApiImpl(PlacesApiRequestInterface api, String serverUrl) {
        mApi = api;
        mServerUrl = serverUrl;
    }

    @Override
    public int getPlaces(double latitude, double longitude, int radius, Type type,
            Callback<List<Place>> callback) {
        if (mCurrentCall != null) {
            mCurrentCall.cancel();
        }
        mCurrentCall = mApi.getPlaces(latitude + "," +
                longitude, radius, type.getValue(), GOOGLE_PLACES_API_KEY);
        RetrofitUtil.executeWithRetry(mCurrentCall, callback);
        return mCurrentCall.request()
                .url()
                .hashCode();
    }

    @Override
    public String getPhotoUrlByReference(String reference, int maxWidth, int maxHeight) {
        String uri =
                String.format("%sphoto?photoreference=%s&key=%s", mServerUrl, reference,
                        GOOGLE_PLACES_API_KEY);

        if (maxHeight != -1) {
            uri += "&maxheight=" + maxHeight;
        }
        if (maxWidth != -1) {
            uri += "&maxwidth=" + maxWidth;
        }
        return uri;
    }
}
