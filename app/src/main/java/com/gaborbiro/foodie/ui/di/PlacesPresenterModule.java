package com.gaborbiro.foodie.ui.di;

import android.app.Activity;
import android.content.Context;

import com.gaborbiro.foodie.provider.places.PlacesApi;
import com.gaborbiro.foodie.provider.places.di.PlacesApiModule;
import com.gaborbiro.foodie.ui.PlacesPresenter;
import com.gaborbiro.foodie.ui.PlacesModel;

import dagger.Module;
import dagger.Provides;

@Module(includes = {PlacesApiModule.class}) public class PlacesPresenterModule {

    private Activity mActivity;
    private PlacesModel.Listener mModelListener;

    public PlacesPresenterModule(Activity activity, PlacesModel.Listener modelListener) {
        mActivity = activity;
        mModelListener = modelListener;
    }

    @Provides public PlacesPresenter providePresenter(Context appContext, PlacesApi api) {
        return new PlacesPresenter(appContext, api, mActivity, mModelListener);
    }
}
