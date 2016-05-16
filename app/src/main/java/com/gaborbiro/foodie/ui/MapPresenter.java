package com.gaborbiro.foodie.ui;

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

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.provider.places.PlacesApi;
import com.gaborbiro.foodie.provider.places.model.Photo;
import com.gaborbiro.foodie.provider.places.model.Place;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MapPresenter extends PlacesPresenter {

    @InjectView(R.id.drawer) hollowsoft.slidingdrawer.SlidingDrawer mDrawer;

    @InjectView(R.id.handle) RelativeLayout mHandle;
    @InjectView(R.id.image) public ImageView mImageView;
    @InjectView(R.id.name) public TextView mNameView;
    @InjectView(R.id.address) public TextView mAddressView;
    @InjectView(R.id.rating) public RatingBar mRatingView;

    @InjectView(R.id.content) TextView mContent;

    public MapPresenter(Context appContext, PlacesApi placesApi, Activity activity) {
        super(appContext, placesApi, activity);
        ButterKnife.inject(this, activity);
    }

    public void onPlaceSelected(Place place) {
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
