package com.handy.portal.library.ui.view.timepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;


class HandyTimePickerRow extends FrameLayout {
    @BindView(R.id.gaps)
    ViewGroup mGapViews;
    @BindView(R.id.cells)
    ViewGroup mTimePickerCellViews;

    private final int mStartHour;
    private final int mEndHour;

    public HandyTimePickerRow(
            final Context context,
            final int startHour,
            final int endHour,
            @NonNull final HandyTimePickerCell.TimeClickListener timeClickListener
    ) {
        super(context);
        mStartHour = startHour;
        mEndHour = endHour;
        init(timeClickListener);
    }

    private void init(final HandyTimePickerCell.TimeClickListener timeClickListener) {
        inflate(getContext(), R.layout.element_time_picker_row, this);
        ButterKnife.bind(this);
        if (mEndHour < mStartHour) {
            return;
        }
        for (int hour = mStartHour; hour <= mEndHour; hour++) {
            mTimePickerCellViews.addView(new HandyTimePickerCell(getContext(), hour,
                    timeClickListener));
            if (hour < mEndHour) {
                LayoutInflater.from(getContext())
                        .inflate(R.layout.element_time_picker_gap, mGapViews, true);
            }
        }
    }

    public void setGapVisibility(final int position, final boolean isVisible) {
        if (position >= 0 && position < mGapViews.getChildCount()) {
            mGapViews.getChildAt(position).setVisibility(isVisible ? VISIBLE : INVISIBLE);
        }
    }

    @Nullable
    public HandyTimePickerCell getCellForHour(final int hour) {
        if (covers(hour)) {
            for (int i = 0; i < mTimePickerCellViews.getChildCount(); i++) {
                final HandyTimePickerCell timePickerCell =
                        (HandyTimePickerCell) mTimePickerCellViews.getChildAt(i);
                if (timePickerCell.getHour() == hour) {
                    return timePickerCell;
                }
            }
        }
        return null;
    }

    private boolean covers(final int hour) {
        return hour >= mStartHour && hour <= mEndHour;
    }
}
