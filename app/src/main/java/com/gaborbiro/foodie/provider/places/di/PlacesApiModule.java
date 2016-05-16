package com.gaborbiro.foodie.provider.places.di;

import com.gaborbiro.foodie.provider.places.DetailsRequestInterface;
import com.gaborbiro.foodie.provider.places.NearbySearchRequestInterface;
import com.gaborbiro.foodie.provider.places.PlacesApi;
import com.gaborbiro.foodie.provider.places.PlacesApiImpl;
import com.gaborbiro.foodie.provider.retrofit.di.RetrofitModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(includes = {RetrofitModule.class}) public class PlacesApiModule {

    public static String API_BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    @Provides @Singleton @Named("server_url") public String provideServerURL() {
        return API_BASE_URL;
    }

    @Provides @Singleton
    public NearbySearchRequestInterface provideNearbySearchRequestInterface(
            Retrofit retrofit) {
        return retrofit.create(NearbySearchRequestInterface.class);
    }

    @Provides @Singleton
    public DetailsRequestInterface provideDetailsRequestInterface(
            Retrofit retrofit) {
        return retrofit.create(DetailsRequestInterface.class);
    }

    @Provides @Singleton
    public PlacesApi provideApi(NearbySearchRequestInterface nearbySearchRequest,
            DetailsRequestInterface detailsRequest, @Named("server_url") String baseUrl) {
        return new PlacesApiImpl(nearbySearchRequest, detailsRequest, baseUrl);
    }
}
