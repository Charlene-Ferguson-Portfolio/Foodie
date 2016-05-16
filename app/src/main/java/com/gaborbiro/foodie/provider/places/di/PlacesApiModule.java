package com.gaborbiro.foodie.provider.places.di;

import com.gaborbiro.foodie.provider.places.PlacesApi;
import com.gaborbiro.foodie.provider.places.PlacesApiImpl;
import com.gaborbiro.foodie.provider.places.PlacesApiRequestInterface;
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
    public PlacesApiRequestInterface provideGooglePlacesApiRequestInterface(
            Retrofit retrofit) {
        return retrofit.create(PlacesApiRequestInterface.class);
    }

    @Provides @Singleton
    public PlacesApi provideApi(PlacesApiRequestInterface api, @Named("server_url") String baseUrl) {
        return new PlacesApiImpl(api, baseUrl);
    }
}
