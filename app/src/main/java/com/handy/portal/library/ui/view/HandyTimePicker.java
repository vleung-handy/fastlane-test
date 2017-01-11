package com.handy.portal.library.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


public class HandyTimePicker extends LinearLayout
{
    private static final int DEFAULT_CELLS_PER_ROW_COUNT = 3;
    public static final int NO_TIME_SELECTED = -1;
    public static final int MINIMUM_START_TIME = 0;
    public static final int MAXIMUM_START_TIME = 24;
    private int mStartTime;
    private int mEndTime;
    private int mSelectedStartTime = NO_TIME_SELECTED;
    private int mSelectedEndTime = NO_TIME_SELECTED;
    private TimeClickListener mListener;

    public HandyTimePicker(final Context context)
    {
        super(context);
        init();
    }

    public HandyTimePicker(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public HandyTimePicker(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HandyTimePicker(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        setOrientation(VERTICAL);
    }

    public void setTimeRange(final int startTime, final int endTime)
    {
        if (startTime < MINIMUM_START_TIME
                || endTime > MAXIMUM_START_TIME
                || startTime >= endTime)
        {
            return;
        }
        mStartTime = startTime;
        mEndTime = endTime;
        for (int rowStartTime = startTime; rowStartTime <= endTime;
             rowStartTime += DEFAULT_CELLS_PER_ROW_COUNT)
        {
            int rowEndTime = rowStartTime + DEFAULT_CELLS_PER_ROW_COUNT - 1;
            if (rowEndTime > endTime)
            {
                rowEndTime = endTime;
            }
            addView(new HandyTimePickerRow(getContext(), rowStartTime, rowEndTime,
                    new OnClickListener()
                    {
                        @Override
                        public void onClick(final View view)
                        {
                            if (mListener != null)
                            {
                                final int time = ((HandyTimePickerCell) view).getTime();
                                mListener.onTimeClick(time);
                            }
                        }
                    }));
        }
    }

    public void setTimeClickListener(final TimeClickListener listener)
    {
        mListener = listener;
    }

    public boolean selectTimeRange(final int startTime, final int endTime)
    {
        if (!isValidRange(startTime, endTime))
        {
            return false;
        }
        resetSelection();
        mSelectedStartTime = startTime;
        mSelectedEndTime = endTime;
        for (int i = 0; i < getChildCount(); i++)
        {
            final HandyTimePickerRow timePickerRow = (HandyTimePickerRow) getChildAt(i);
            for (int j = 0; j < timePickerRow.mTimePickerCellViews.getChildCount(); j++)
            {
                final HandyTimePickerCell timePickerCell =
                        (HandyTimePickerCell) timePickerRow.mTimePickerCellViews.getChildAt(j);
                final int timePickerCellTime = timePickerCell.getTime();
                if (timePickerCellTime >= startTime && timePickerCellTime <= endTime)
                {
                    if (timePickerCellTime == startTime || timePickerCellTime == endTime)
                    {
                        timePickerCell.select();
                    }
                    else
                    {
                        timePickerCell.highlight();
                    }
                    if (timePickerCellTime > startTime && j > 0)
                    {
                        timePickerRow.setGapVisibility(j - 1, true);
                    }
                }
            }
        }
        return true;
    }

    public void resetSelection()
    {
        mSelectedStartTime = mSelectedEndTime = NO_TIME_SELECTED;
        for (int i = 0; i < getChildCount(); i++)
        {
            final HandyTimePickerRow timePickerRow = (HandyTimePickerRow) getChildAt(i);
            for (int j = 0; j < timePickerRow.mTimePickerCellViews.getChildCount(); j++)
            {
                final HandyTimePickerCell timePickerCell =
                        (HandyTimePickerCell) timePickerRow.mTimePickerCellViews.getChildAt(j);
                timePickerCell.reset();
                if (j > 0)
                {
                    timePickerRow.setGapVisibility(j - 1, false);
                }
            }
        }
    }

    public int getSelectedStartTime()
    {
        return mSelectedStartTime;
    }

    public int getSelectedEndTime()
    {
        return mSelectedEndTime;
    }

    public boolean hasSelectedStartTime()
    {
        return mSelectedStartTime != NO_TIME_SELECTED;
    }

    public boolean hasSelectedEndTime()
    {
        return mSelectedEndTime != NO_TIME_SELECTED;
    }

    public boolean hasSelectedRange()
    {
        return hasSelectedStartTime() && hasSelectedEndTime();
    }

    public boolean selectStartTime(final int time)
    {
        if (mSelectedEndTime == NO_TIME_SELECTED || time < mSelectedEndTime)
        {
            resetCellForTime(mSelectedStartTime);
            mSelectedStartTime = time;
            if (getSelectedEndTime() != NO_TIME_SELECTED)
            {
                selectTimeRange(mSelectedStartTime, mSelectedEndTime);
            }
            else
            {
                selectCellForTime(time);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean selectEndTime(final int time)
    {
        if (mSelectedStartTime == NO_TIME_SELECTED || time > mSelectedStartTime)
        {
            resetCellForTime(mSelectedEndTime);
            mSelectedEndTime = time;
            if (getSelectedStartTime() != NO_TIME_SELECTED)
            {
                selectTimeRange(mSelectedStartTime, mSelectedEndTime);
            }
            else
            {
                selectCellForTime(time);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void resetCellForTime(final int time)
    {
        if (time != NO_TIME_SELECTED)
        {
            final HandyTimePickerCell cell = getCellForTime(time);
            if (cell != null)
            {
                cell.reset();
            }
        }
    }

    private void selectCellForTime(final int time)
    {
        if (time != NO_TIME_SELECTED)
        {
            final HandyTimePickerCell cell = getCellForTime(time);
            if (cell != null)
            {
                cell.select();
            }
        }
    }

    @Nullable
    private HandyTimePickerCell getCellForTime(final int time)
    {
        if (covers(time))
        {
            for (int i = 0; i < getChildCount(); i++)
            {
                final HandyTimePickerRow timePickerRow = (HandyTimePickerRow) getChildAt(i);
                final HandyTimePickerCell timePickerCell = timePickerRow.getCellForTime(time);
                if (timePickerCell != null)
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

    private boolean isValidRange(final int startTime, final int endTime)
    {
        return startTime < endTime && startTime >= mStartTime && endTime <= mEndTime;
    }

    public interface TimeClickListener
    {
        void onTimeClick(final int time);
    }
}
