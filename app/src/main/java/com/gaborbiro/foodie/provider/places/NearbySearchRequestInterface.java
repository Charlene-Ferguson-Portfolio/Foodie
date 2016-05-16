package com.gaborbiro.foodie.provider.places;

import com.gaborbiro.foodie.provider.places.model.places.PlacesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbySearchRequestInterface {

    @GET("nearbysearch/json")
    Call<PlacesResponse> getPlaces(@Query("location") String location,
            @Query("radius") int radius, @Query("type") String type,
            @Query("key") String apiKey);

}
