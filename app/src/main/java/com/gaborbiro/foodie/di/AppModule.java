package com.gaborbiro.foodie.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module public class AppModule {
    private final Context mAppContext;

    public AppModule(Context appContext) {
        mAppContext = appContext;
    }

    @Provides @Singleton public Context provideAppContext() {
        return mAppContext;
    }
}
