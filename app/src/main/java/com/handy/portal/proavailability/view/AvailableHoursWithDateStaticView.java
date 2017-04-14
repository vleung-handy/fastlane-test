package com.handy.portal.proavailability.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;

import java.util.Date;

import butterknife.BindColor;

public class AvailableHoursWithDateStaticView extends AvailableHoursWithDateView {

    @BindColor(R.color.error_red)
    int mRedColor;

    public AvailableHoursWithDateStaticView(
            final Context context,
            final Date date,
            @Nullable final DailyAvailabilityTimeline availability) {
        super(context, date, availability, !DateTimeUtils.isDaysPast(date));
    }

    @Override
    public void updateTimelines(final DailyAvailabilityTimeline availability) {
        super.updateTimelines(availability);
        if (mEnabled && availability == null) {
            mTimelines.removeAllViews();
            final TextView textView = createTextView();
            textView.setText(R.string.no_hours_set);
            textView.setTextColor(mRedColor);
            mTimelines.addView(textView);
        }
        else if (!mEnabled) {
            mTimelines.removeAllViews();
            final TextView textView = createTextView();
            textView.setText(R.string.no_data);
            mTimelines.addView(textView);
        }
    }
}
