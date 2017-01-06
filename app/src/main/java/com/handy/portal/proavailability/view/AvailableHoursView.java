package com.handy.portal.proavailability.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;
import com.handy.portal.proavailability.model.AvailabilityInterval;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailableHoursView extends RelativeLayout
{
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.timelines)
    ViewGroup mTimelines;
    @BindColor(R.color.tertiary_gray)
    int mTertiaryGray;

    public AvailableHoursView(final Context context)
    {
        super(context);
        init();
    }

    public AvailableHoursView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public AvailableHoursView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AvailableHoursView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_available_hours, this);
        ButterKnife.bind(this);
    }

    public void setAvailableHours(@Nullable final List<AvailabilityInterval> availabilities)
    {
        mTimelines.removeAllViews();
        if (availabilities == null || availabilities.isEmpty())
        {
            mTitle.setText(R.string.no_available_hours);
            final TextView textView = new TextView(getContext());
            textView.setText(R.string.set_hours);
            textView.setTextColor(mTertiaryGray);
            textView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
            mTimelines.addView(textView);
        }
        else
        {
            mTitle.setText(R.string.available_hours);
            for (AvailabilityInterval availability : availabilities)
            {
                final TextView timeline = new TextView(getContext());
                final String startTimeFormatted =
                        DateTimeUtils.formatDateTo12HourClock(availability.getStartTime());
                final String endTimeFormatted =
                        DateTimeUtils.formatDateTo12HourClock(availability.getEndTime());
                timeline.setText(startTimeFormatted + " - " + endTimeFormatted);
                timeline.setTextColor(mTertiaryGray);
                timeline.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
                timeline.setGravity(Gravity.END);
                mTimelines.addView(timeline);
            }
        }
    }
}
