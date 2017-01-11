package com.handy.portal.library.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HandyTimePickerRow extends FrameLayout
{
    @BindView(R.id.gaps)
    ViewGroup mGapViews;
    @BindView(R.id.cells)
    ViewGroup mTimePickerCellViews;

    private final int mStartTime;
    private final int mEndTime;

    public HandyTimePickerRow(
            final Context context,
            final int startTime,
            final int endTime,
            @NonNull final HandyTimePickerCell.TimeClickListener timeClickListener
    )
    {
        super(context);
        mStartTime = startTime;
        mEndTime = endTime;
        init(timeClickListener);
    }

    private void init(final HandyTimePickerCell.TimeClickListener timeClickListener)
    {
        inflate(getContext(), R.layout.element_time_picker_row, this);
        ButterKnife.bind(this);
        if (mEndTime < mStartTime)
        {
            return;
        }
        for (int time = mStartTime; time <= mEndTime; time++)
        {
            mTimePickerCellViews.addView(new HandyTimePickerCell(getContext(), time,
                    timeClickListener));
            if (time < mEndTime)
            {
                LayoutInflater.from(getContext())
                        .inflate(R.layout.element_time_picker_gap, mGapViews, true);
            }
        }
    }

    public void setGapVisibility(final int position, final boolean isVisible)
    {
        if (position >= 0 && position < mGapViews.getChildCount())
        {
            mGapViews.getChildAt(position).setVisibility(isVisible ? VISIBLE : INVISIBLE);
        }
    }

    @Nullable
    public HandyTimePickerCell getCellForTime(final int time)
    {
        if (covers(time))
        {
            for (int i = 0; i < mTimePickerCellViews.getChildCount(); i++)
            {
                final HandyTimePickerCell timePickerCell =
                        (HandyTimePickerCell) mTimePickerCellViews.getChildAt(i);
                if (timePickerCell.getTime() == time)
                {
                    return timePickerCell;
                }
            }
        }
        return null;
    }

    private boolean covers(final int time)
    {
        return time >= mStartTime && time <= mEndTime;
    }
}
