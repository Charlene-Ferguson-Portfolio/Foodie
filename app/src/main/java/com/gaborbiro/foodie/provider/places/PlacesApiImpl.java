package com.gaborbiro.foodie.provider.places;

import com.gaborbiro.foodie.provider.places.model.place_details.PlaceDetails;
import com.gaborbiro.foodie.provider.places.model.places.Place;
import com.gaborbiro.foodie.provider.retrofit.Callback;
import com.gaborbiro.foodie.provider.retrofit.RetrofitUtil;

import java.util.List;

import retrofit2.Call;

public class PlacesApiImpl implements PlacesApi {

    private static final String GOOGLE_PLACES_API_KEY =
            "AIzaSyBDwqC3Emv86CJOYxhfC_LYps9caIfucEs";

    private PlacesApiRequestInterface mPlacesApiRequestInterface;

    private String mServerUrl;

    private Call mCurrentDetailsCall;
    private Call mCurrentPlacesCall;

    public PlacesApiImpl(PlacesApiRequestInterface placesApiRequestInterface,
            String serverUrl) {
        mPlacesApiRequestInterface = placesApiRequestInterface;
        mServerUrl = serverUrl;
    }

    @Override public int getPlace(String placeId, Callback<PlaceDetails> callback) {
        if (mCurrentDetailsCall != null) {
            mCurrentDetailsCall.cancel();
        }
        mCurrentDetailsCall = mPlacesApiRequestInterface.getPlaceDetails(placeId,
                GOOGLE_PLACES_API_KEY);
        RetrofitUtil.executeWithRetry(mCurrentDetailsCall, callback);
        return mCurrentDetailsCall.request()
                .url()
                .hashCode();
    }

    @Override
    public int getPlaces(double latitude, double longitude, int radius, Type type,
            Callback<List<Place>> callback) {
        if (mCurrentPlacesCall != null) {
            mCurrentPlacesCall.cancel();
        }
        mCurrentPlacesCall = mPlacesApiRequestInterface.getPlaces(latitude + "," +
                longitude, radius, type.getValue(), GOOGLE_PLACES_API_KEY);
        RetrofitUtil.executeWithRetry(mCurrentPlacesCall, callback);
        return mCurrentPlacesCall.request()
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
