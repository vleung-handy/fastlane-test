package com.handy.portal.library.ui.view;

import android.content.Context;
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
    private final OnClickListener mListener;

    public HandyTimePickerRow(final Context context,
                              final int startTime,
                              final int endTime,
                              final OnClickListener listener)
    {
        super(context);
        mStartTime = startTime;
        mEndTime = endTime;
        mListener = listener;
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_time_picker_row, this);
        ButterKnife.bind(this);
        if (mEndTime < mStartTime)
        {
            return;
        }
        for (int time = mStartTime; time <= mEndTime; time++)
        {
            mTimePickerCellViews.addView(new HandyTimePickerCell(getContext(), time, mListener));
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
}
