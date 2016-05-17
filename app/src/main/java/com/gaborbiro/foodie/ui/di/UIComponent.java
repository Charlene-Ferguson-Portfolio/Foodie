package com.gaborbiro.foodie.ui.di;

import com.gaborbiro.foodie.di.AppModule;
import com.gaborbiro.foodie.ui.view.PlaceMapActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component(modules = {PlacesPresenterModule.class}, dependencies = {AppModule.class})
public interface UIComponent {
    void inject(PlaceMapActivity activity);
}
