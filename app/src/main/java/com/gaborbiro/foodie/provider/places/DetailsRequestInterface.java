package com.gaborbiro.foodie.provider.places;

import com.gaborbiro.foodie.provider.places.model.place_details.PlaceDetailsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DetailsRequestInterface {

    @GET("details/json") Call<PlaceDetailsResponse> getPlaceDetails(
            @Query("placeid") String placeId, @Query("key") String apiKey);

}
