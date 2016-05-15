package com.gaborbiro.foodie.provider.retrofit;

/**
 * Instead of forcing the user of your communication mApi to go
 * ResponseObject.getUselessWrapper().getUselessWrapper().getUsefulPayload(), make the
 * ResponseObject implement this interface. And make sure that in your ApiImpl class you
 * actually use the payload class in the callbacks.
 */
public interface PayloadWrapper<T> {
    T getPayload();
}
