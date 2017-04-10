package com.handy.portal.proavailability.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.FontUtils;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;

import java.util.Date;

import butterknife.BindColor;

public class AvailableHoursWithDateStaticView extends AvailableHoursWithDateView {

    @BindColor(R.color.tertiary_gray)
    int mGrayColor;

    public AvailableHoursWithDateStaticView(
            final Context context,
            final Date date,
            @Nullable final DailyAvailabilityTimeline availability) {
        super(context, date, availability, null, true);
    }

    @Override
    public void updateTimelines(final DailyAvailabilityTimeline availability) {
        super.updateTimelines(availability);
        if (availability == null || !availability.hasIntervals()) {
            final TextView textView = new TextView(getContext());
            textView.setText(R.string.hours_not_set);
            textView.setTextColor(mGrayColor);
            textView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
            mTimelines.addView(textView);
        }
    }
}
