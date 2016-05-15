package com.gaborbiro.foodie.ui;

import com.gaborbiro.foodie.provider.places.model.Place;

import java.util.List;

public class PlacesModel {

    public interface Listener {
        void onModelUpdated(PlacesModel model);
    }

    private List<Place> mPlaces;
    private Throwable mError;

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void set(List<Place> places, Throwable error) {
        this.mPlaces = places;
        mError = error;
        notifyListener();
    }

    public List<Place> getPlaces() {
        return mPlaces;
    }

    public Throwable getError() {
        return mError;
    }

    private void notifyListener() {
        if (mListener != null) {
            try {
                mListener.onModelUpdated(this);
            } catch (Throwable t) {
            }
        }
    }
}
