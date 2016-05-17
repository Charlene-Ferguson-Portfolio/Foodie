package com.gaborbiro.foodie.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.provider.places.model.places.Place;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlaceDetailHeaderAdapter {

    public static class ViewHolder {
        @InjectView(R.id.image) public ImageView imageView;
        @InjectView(R.id.name) public TextView nameView;
        @InjectView(R.id.address) public TextView addressView;
        @InjectView(R.id.rating) public RatingBar ratingView;
    }

    public static void adapt(Context context, Place place, String imageUri,
            ViewGroup view) {
        ViewHolder holder = getViewHolder(view);

        if (imageUri != null) {
            Picasso.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_restaurant_menu_black_24dp)
                    .into(holder.imageView);
        } else if (!TextUtils.isEmpty(place.icon)) {
            Picasso.with(context)
                    .load(place.icon)
                    .placeholder(R.drawable.ic_restaurant_menu_black_24dp)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_restaurant_menu_black_24dp);
        }
        StringBuilder name = new StringBuilder(place.name);
        if (place.openingHours != null) {
            name.append(" (");
            name.append(place.openingHours.openNow ? context.getString(R.string.open)
                                                   : context.getString(R.string.closed));
            name.append(")");
        }

        holder.nameView.setText(name.toString());
        holder.addressView.setText(place.vicinity);
        holder.ratingView.setRating((float) place.rating);
    }

    private static ViewHolder getViewHolder(ViewGroup view) {
        ViewHolder holder = new ViewHolder();
        ButterKnife.inject(holder, view);
        return holder;
    }
}
