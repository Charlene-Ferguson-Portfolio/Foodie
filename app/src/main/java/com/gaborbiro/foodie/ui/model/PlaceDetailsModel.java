package com.gaborbiro.foodie.ui.model;

import com.gaborbiro.foodie.provider.places.model.place_details.PlaceDetails;

import org.greenrobot.eventbus.EventBus;

public class PlaceDetailsModel {
    public static class UpdateEvent {
        public PlaceDetails placeDetails;
    }

    public static class UpdateError {
        public Throwable error;
    }

    private PlaceDetails mPlaceDetails;

    public PlaceDetails getPlaceDetails() {
        return mPlaceDetails;
    }

    public void setPlaceDetails(PlaceDetails placeDetails) {
        mPlaceDetails = placeDetails;
    }

    public void sendPlaceDetailUpdateEvent() {
        UpdateEvent event = new UpdateEvent();
        event.placeDetails = getPlaceDetails();
        EventBus.getDefault()
                .post(event);
    }

    public static void sendErrorEvent(Throwable t) {
        UpdateError event = new UpdateError();
        event.error = t;
        EventBus.getDefault()
                .post(event);
    }
}
