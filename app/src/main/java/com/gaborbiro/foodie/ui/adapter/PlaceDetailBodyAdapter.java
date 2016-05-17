package com.gaborbiro.foodie.ui.adapter;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.provider.places.model.place_details.PlaceDetails;
import com.gaborbiro.foodie.util.ArrayUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlaceDetailBodyAdapter {

    public static class ViewHolder {
        @InjectView(R.id.opening_hours) public TextView openingHoursView;
    }

    public static void adapt(PlaceDetails details,
            RelativeLayout contentView) {
        ViewHolder holder = new ViewHolder();
        ButterKnife.inject(holder, contentView);

        if (details.openingHours != null) {
            holder.openingHoursView.setText(
                    ArrayUtils.join(details.openingHours.weekdayText, "\n"));
        }
    }
}
