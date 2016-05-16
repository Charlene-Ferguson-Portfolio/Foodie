package com.gaborbiro.foodie.ui.model;

import com.gaborbiro.foodie.provider.places.model.Place;
import com.google.common.collect.EvictingQueue;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Queue;

public class PlacesModel {

    public static class UpdateEvent {
        public Place[] places;
    }

    public static class UpdateError {
        public Throwable error;
    }

    private static final int MAX_PLACE_COUNT = 100;

    private Queue<Place> mPlaces = EvictingQueue.create(MAX_PLACE_COUNT);

    /**
     * Only the last {@value MAX_PLACE_COUNT} places are preserved, the rest is evicted
     */
    public void add(List<Place> places) {
        mPlaces.addAll(places);
        sendPlacesUpdateEvent();
    }

    public Place[] getPlaces() {
        return mPlaces.toArray(new Place[mPlaces.size()]);
    }

    public void sendPlacesUpdateEvent() {
        UpdateEvent event = new UpdateEvent();
        event.places = getPlaces();
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
