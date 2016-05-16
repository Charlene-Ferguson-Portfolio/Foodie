package com.gaborbiro.foodie.ui.model;

import android.location.Location;

import org.greenrobot.eventbus.EventBus;

public class LocationModel {

    public class UpdateEvent {
        public Location currentBestLocation;
        public boolean firstLocationFetch;
    }

    private Location mCurrentBestLocation;

    public Location getCurrentBestLocation() {
        return mCurrentBestLocation;
    }

    public void setCurrentBestLocation(Location currentBestLocation) {
        // TODO mCurrentBestLocation can be set to null (for whatever reason). Handle
        // that situation too.
        boolean firstLocationFetch = mCurrentBestLocation == null;
        mCurrentBestLocation = currentBestLocation;
        sendLocationUpdateEvent(firstLocationFetch);
    }

    public void sendLocationUpdateEvent(boolean firstLocationFetch) {
        UpdateEvent event = new UpdateEvent();
        event.currentBestLocation = getCurrentBestLocation();
        event.firstLocationFetch = firstLocationFetch;
        EventBus.getDefault()
                .post(event);
    }
}
