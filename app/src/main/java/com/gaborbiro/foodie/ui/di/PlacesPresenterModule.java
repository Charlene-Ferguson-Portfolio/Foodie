package com.gaborbiro.foodie.ui.di;

import android.app.Activity;
import android.content.Context;

import com.gaborbiro.foodie.provider.places.PlacesApi;
import com.gaborbiro.foodie.provider.places.di.PlacesApiModule;
import com.gaborbiro.foodie.ui.presenter.MapPresenter;
import com.gaborbiro.foodie.ui.presenter.MapPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module(includes = {PlacesApiModule.class}) public class PlacesPresenterModule {

    private Activity mActivity;

    public PlacesPresenterModule(Activity activity) {
        mActivity = activity;
    }

    @Provides public MapPresenter provideMapPresenter(Context appContext, PlacesApi api) {
        return new MapPresenterImpl(appContext, api, mActivity);
    }
}
