package com.gaborbiro.foodie.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * A MapFragment that can detect whether the user touched (and released) the map (as
 * opposed to programmatic panning/zoom)
 */
public class NonProgrammaticTouchDetectorMapFragment extends SupportMapFragment {
    public View mOriginalContentView;
    public TouchableWrapper mTouchView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        try {
            mTouchView = new TouchableWrapper(getActivity(),
                    (TouchableWrapper.MapTouchListener) getActivity());
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass()
                    .getName() + " must implement " +
                    TouchableWrapper.MapTouchListener.class.getName());
        }
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override public View getView() {
        return mOriginalContentView;
    }
}
