package com.gaborbiro.foodie.ui.model;

import android.location.Location;

import org.greenrobot.eventbus.EventBus;

public class LocationModel {

    public class UpdateEvent {
        public Location currentBestLocation;
    }

    private Location mCurrentBestLocation;

    public Location getCurrentBestLocation() {
        return mCurrentBestLocation;
    }

    public void set(Location currentBestLocation) {
        mCurrentBestLocation = currentBestLocation;
        sendLocationUpdateEvent();
    }

    public void sendLocationUpdateEvent() {
        UpdateEvent event = new UpdateEvent();
        event.currentBestLocation = getCurrentBestLocation();
        EventBus.getDefault()
                .post(event);
    }
}
