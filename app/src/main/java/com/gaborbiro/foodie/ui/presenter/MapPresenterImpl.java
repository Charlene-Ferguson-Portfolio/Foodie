package com.gaborbiro.foodie.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.provider.places.PlacesApi;
import com.gaborbiro.foodie.provider.places.model.common.Photo;
import com.gaborbiro.foodie.provider.places.model.place_details.PlaceDetails;
import com.gaborbiro.foodie.provider.places.model.places.Place;
import com.gaborbiro.foodie.provider.retrofit.Callback;
import com.gaborbiro.foodie.ui.model.LocationModel;
import com.gaborbiro.foodie.ui.model.PlaceDetailsModel;
import com.gaborbiro.foodie.ui.model.PlacesModel;
import com.gaborbiro.foodie.util.LocationUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;

public class MapPresenterImpl extends PlacesPresenterImpl
        implements MapPresenter, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Map<Marker, Place> mMarkerMap = new HashMap<>();
    private LatLng mLastSearchLocation;
    private boolean mIsFollowingMode = true;

    private Place mSelectedPlace;

    @InjectView(R.id.drawer) hollowsoft.slidingdrawer.SlidingDrawer mDrawer;

    @InjectView(R.id.handle) RelativeLayout mHandle;
    @InjectView(R.id.image) public ImageView mImageView;
    @InjectView(R.id.name) public TextView mNameView;
    @InjectView(R.id.address) public TextView mAddressView;
    @InjectView(R.id.rating) public RatingBar mRatingView;

    @InjectView(R.id.content) RelativeLayout mContent;
    @InjectView(R.id.opening_hours) TextView mOpeningHoursView;

    public MapPresenterImpl(Context appContext, PlacesApi placesApi, Activity activity) {
        super(appContext, placesApi, activity);
        ButterKnife.inject(this, activity);
        EventBus.getDefault()
                .unregister(this);

        mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            @Override public void onDrawerOpened() {
                if (mSelectedPlace != null) {
                    mPlacesApi.getPlace(mSelectedPlace.placeId, mPlaceDetailsCallback);
                }
            }
        });
    }

    public void setMap(GoogleMap map) {
        mMap = map;
        try {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(
                    new GoogleMap.OnMyLocationButtonClickListener() {

                        @Override public boolean onMyLocationButtonClick() {
                            if (!mIsFollowingMode) {
                                Toast.makeText(mAppContext,
                                        "The map is now following you",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                            mIsFollowingMode = true;
                            loadPlaces();
                            return false;
                        }
                    });
            mMap.setOnMarkerClickListener(this);
            mMap.getUiSettings()
                    .setZoomControlsEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        EventBus.getDefault()
                .register(this);
        super.onScreenStarted();
    }

    @Override public void onScreenStopped() {
        super.onScreenStopped();
        EventBus.getDefault()
                .unregister(this);
    }

    public void loadPlaces() {
        LatLng target = LocationUtils.roundDown(mMap.getCameraPosition().target);
        if (mLastSearchLocation == null ||
                LocationUtils.distance(target.latitude, target.longitude,
                        mLastSearchLocation.latitude, mLastSearchLocation.longitude) >
                        LocationUtils.LOCATION_UPDATE_THRESHOLD_METERS) {
            mLastSearchLocation = target;
            super.loadPlaces(target);
        }
    }

    @Subscribe public void onEvent(LocationModel.UpdateEvent event) {
        if (mIsFollowingMode) {
            LatLng newLocation = new LatLng(event.currentBestLocation.getLatitude(),
                    event.currentBestLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(newLocation, 15);

            if (event.firstLocationFetch) {
                mMap.moveCamera(update);
            } else {
                mMap.animateCamera(update);
            }
        }
    }

    @Subscribe public void onEvent(PlacesModel.UpdateEvent event) {
        mMap.clear();
        mMarkerMap.clear();
        for (Place p : event.places) {
            LatLng position =
                    new LatLng(p.geometry.location.lat, p.geometry.location.lng);
            StringBuffer snippet = new StringBuffer(p.vicinity);
            if (p.openingHours != null) {
                snippet.append("\n");
                snippet.append(p.openingHours.openNow ? "Open" : "Closed");
            }
            Marker m = mMap.addMarker(new MarkerOptions().position(position));
            mMarkerMap.put(m, p);
        }
    }

    @Subscribe public void onEvent(PlacesModel.UpdateError event) {
        event.error.printStackTrace();
        Toast.makeText(mAppContext, event.error.getMessage(), Toast.LENGTH_SHORT)
                .show();
    }

    @Subscribe public void onEvent(PlaceDetailsModel.UpdateError event) {
        event.error.printStackTrace();
        Toast.makeText(mAppContext, event.error.getMessage(), Toast.LENGTH_SHORT)
                .show();
    }

    @Override public void onMapTouched() {
        if (mIsFollowingMode) {
            Toast.makeText(mAppContext, "The map has stopped following you",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        mIsFollowingMode = false;
        loadPlaces();
    }

    @Override public boolean onMarkerClick(Marker marker) {
        displayPlaceHeader(mMarkerMap.get(marker));
        return true;
    }

    private void displayPlaceHeader(Place place) {
        mSelectedPlace = place;
        int thumbHeightPx = (int) mActivity.getResources()
                .getDimension(R.dimen.place_thumb_height);
        int thumbWidthPx = (int) mActivity.getResources()
                .getDimension(R.dimen.place_thumb_width);
        Photo photo = place.getPhoto(thumbHeightPx, thumbWidthPx);

        if (photo != null) {
            Picasso.with(mAppContext)
                    .load(mPlacesApi.getPhotoUrlByReference(photo.photoReference,
                            thumbHeightPx, thumbWidthPx))
                    .placeholder(R.drawable.ic_restaurant_menu_black_24dp)
                    .into(mImageView);
        } else if (!TextUtils.isEmpty(place.icon)) {
            Picasso.with(mAppContext)
                    .load(place.icon)
                    .placeholder(R.drawable.ic_restaurant_menu_black_24dp)
                    .into(mImageView);
        } else {
            mImageView.setImageResource(R.drawable.ic_restaurant_menu_black_24dp);
        }
        StringBuffer name = new StringBuffer(place.name);
        if (place.openingHours != null) {
            name.append(" (");
            name.append(place.openingHours.openNow ? mAppContext.getString(R.string.open)
                                                   : mAppContext.getString(
                                                           R.string.closed));
            name.append(")");
        }

        mNameView.setText(name.toString());
        mAddressView.setText(place.vicinity);
        mRatingView.setRating((float) place.rating);

        if (mDrawer.getVisibility() == View.GONE) {
            showDrawerHandler();
        }
    }

    private Callback<PlaceDetails> mPlaceDetailsCallback = new Callback<PlaceDetails>() {
        @Override public void onResponse(int requestId, PlaceDetails result) {

        }

        @Override public void onFailure(int requestId, Throwable t) {

        }
    };

    private void showDrawerHandler() {
        Animation bottomUp = AnimationUtils.loadAnimation(mAppContext, R.anim.bottom_up);
        mHandle.startAnimation(bottomUp);
        mDrawer.setVisibility(View.VISIBLE);
    }

    private void hideDrawerHandler() {
        Animation bottomDown =
                AnimationUtils.loadAnimation(mAppContext, R.anim.bottom_down);
        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {

            }

            @Override public void onAnimationEnd(Animation animation) {
                mDrawer.setVisibility(View.GONE);
            }

            @Override public void onAnimationRepeat(Animation animation) {

            }
        });
        mHandle.startAnimation(bottomDown);
    }

    public boolean handleBackPressed() {
        if (mContent.getVisibility() == View.VISIBLE) {
            mDrawer.animateClose();
            return true;
        }
        if (mDrawer.getVisibility() == View.VISIBLE) {
            hideDrawerHandler();
            return true;
        }
        return false;
    }
}
