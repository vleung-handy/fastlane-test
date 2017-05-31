package com.handy.portal.availability.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Date;

import butterknife.BindColor;

public class AvailableHoursWithDateStaticView extends AvailableHoursWithDateView {

    @BindColor(R.color.error_red)
    int mRedColor;

    public AvailableHoursWithDateStaticView(
            final Context context,
            final Date date,
            @Nullable final Availability.Timeline timeline) {
        super(context, date, timeline, !DateTimeUtils.isDaysPast(date));
    }

    @Override
    public void updateIntervals(final Availability.Timeline timeline) {
        super.updateIntervals(timeline);
        if (mEnabled && timeline == null) {
            mIntervals.removeAllViews();
            final TextView textView = createTextView();
            textView.setText(R.string.no_hours_set);
            textView.setTextColor(mRedColor);
            mIntervals.addView(textView);
        }
        else if (!mEnabled) {
            mIntervals.removeAllViews();
            final TextView textView = createTextView();
            textView.setText(R.string.no_data);
            mIntervals.addView(textView);
        }
    }
}
