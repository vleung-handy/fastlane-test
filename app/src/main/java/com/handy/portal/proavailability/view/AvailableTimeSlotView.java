package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.AvailabilityInterval;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AvailableTimeSlotView extends FrameLayout {
    @BindView(R.id.timeline)
    TextView mTimeline;
    @BindView(R.id.remove)
    View mRemoveButton;

    @OnClick(R.id.remove)
    public void onRemoveClicked() {
        if (mRemoveTimeSlotListener != null) {
            mRemoveTimeSlotListener.onRemoveClicked(mDate, mInterval);
        }
    }

    private Date mDate;
    private final AvailabilityInterval mInterval;
    private final RemoveTimeSlotListener mRemoveTimeSlotListener;

    public AvailableTimeSlotView(final Context context,
                                 final Date date,
                                 final AvailabilityInterval interval,
                                 @Nullable final RemoveTimeSlotListener removeTimeSlotListener) {
        super(context);
        mDate = date;
        mInterval = interval;
        mRemoveTimeSlotListener = removeTimeSlotListener;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_available_time_slot, this);
        ButterKnife.bind(this);
        final String startTimeFormatted =
                DateTimeUtils.formatDateTo12HourClock(mInterval.getStartTime());
        final String endTimeFormatted =
                DateTimeUtils.formatDateTo12HourClock(mInterval.getEndTime());
        mTimeline.setText(startTimeFormatted + " - " + endTimeFormatted);
        if (mRemoveTimeSlotListener == null) {
            mRemoveButton.setVisibility(GONE);
        }
    }

    public interface RemoveTimeSlotListener {
        void onRemoveClicked(final Date date, final AvailabilityInterval interval);
    }
}
