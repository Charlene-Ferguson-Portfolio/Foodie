package com.gaborbiro.foodie.provider.retrofit.di;

import android.content.Context;

import com.gaborbiro.foodie.provider.retrofit.RetrofitUtil;
import com.gaborbiro.foodie.util.NetUtils;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Module public class RetrofitModule {

    private static final int CACHE_SIZE = 10 * 1024 * 1024;
    private static final String CACHE_FOLDER_NAME = "retrofit-cache";
    private static final int CACHE_EXPIRY = 60 * 60 * 24 * 1; // tolerate 1-day stale

    @Provides @Singleton public Retrofit provideRetrofit(OkHttpClient client,
            @Named("gson") Converter.Factory factory,
            @Named("server_url") String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(factory)
                .build();
    }


    @Provides @Singleton public OkHttpClient provideOkHttpClient(Cache cache,
            @Named("rewrite_response") Interceptor rewriteResponseInterceptor,
            @Named("offline") Interceptor offlineInterceptor) {
        return new OkHttpClient.Builder().cache(cache)
                .addInterceptor(offlineInterceptor)
                .addNetworkInterceptor(rewriteResponseInterceptor)
                .build();
    }

    @Provides @Singleton @Named("xml") public Converter.Factory provideXmlFactory() {
        return SimpleXmlConverterFactory.create();
    }

    @Provides @Singleton @Named("gson") public Converter.Factory provideJsonFactory() {
        return GsonConverterFactory.create();
    }

    @Provides @Singleton public Cache provideCache(Context appContext) {
        return new Cache(RetrofitUtil.getDiskCacheDir(appContext, CACHE_FOLDER_NAME),
                CACHE_SIZE);
    }

    @Provides @Singleton @Named("rewrite_response")
    public Interceptor provideRewriteResponseInterceptor() {
        return new Interceptor() {

            @Override public okhttp3.Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                String cacheControl = originalResponse.header("Cache-Control");

                if (cacheControl != null) {
                    return originalResponse.newBuilder()
                            .header("Cache-Control",
                                    cacheControl.replace("private", "public")
                                            .replace(", must-revalidate", ""))
                            .build();
                } else {
                    return originalResponse;
                }
            }
        };
    }

    @Provides @Singleton @Named("offline") public Interceptor provideOfflineInterceptor(
            final Context appContext) {
        return new Interceptor() {

            @Override public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!NetUtils.isNetworkAvailable(appContext)) {
                    request = request.newBuilder()
                            .header("Cache-Control",
                                    "public, only-if-cached, max-stale=" + CACHE_EXPIRY)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }
}
