package com.gaborbiro.foodie.provider.places;

import com.gaborbiro.foodie.provider.places.model.place_details.PlaceDetailsResponse;
import com.gaborbiro.foodie.provider.places.model.places.PlacesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface for retrofit
 */
public interface PlacesApiRequestInterface {

    @GET("details/json") Call<PlaceDetailsResponse> getPlaceDetails(
            @Query("placeid") String placeId, @Query("key") String apiKey);


    @GET("nearbysearch/json") Call<PlacesResponse> getPlaces(
            @Query("location") String location, @Query("radius") int radius,
            @Query("type") String type, @Query("key") String apiKey);

}
