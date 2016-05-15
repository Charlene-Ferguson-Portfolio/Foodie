package com.gaborbiro.foodie.provider.retrofit;

public interface Callback<T> {
    void onResponse(int requestId, T result);

    void onFailure(int requestId, Throwable t);
}
