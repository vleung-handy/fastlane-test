package com.handy.portal.library.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class HandyTimePicker extends LinearLayout implements HandyTimePickerCell.TimeClickListener
{
    public enum SelectionType
    {
        START_TIME, END_TIME
    }


    private static final int DEFAULT_CELLS_PER_ROW_COUNT = 3;
    public static final int NO_TIME_SELECTED = -1;
    public static final int MINIMUM_START_TIME = 0;
    public static final int MAXIMUM_START_TIME = 24;
    private int mStartTime;
    private int mEndTime;
    private int mSelectedStartTime = NO_TIME_SELECTED;
    private int mSelectedEndTime = NO_TIME_SELECTED;
    private Callbacks mCallbacks;
    private SelectionType mSelectionType;

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

    public void setCallbacks(final Callbacks callbacks)
    {
        mCallbacks = callbacks;
    }

    public void setSelectionType(@Nullable final SelectionType selectionType)
    {
        mSelectionType = selectionType;
        if (mCallbacks != null)
        {
            mCallbacks.onSelectionTypeChanged(mSelectionType);
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

    public boolean hasSelectedOneTime()
    {
        return hasSelectedStartTime() ^ hasSelectedEndTime();
    }

    public boolean covers(final int time)
    {
        return time >= mStartTime && time <= mEndTime;
    }

    public boolean isValidRange(final int startTime, final int endTime)
    {
        return startTime < endTime && startTime >= mStartTime && endTime <= mEndTime;
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
            addView(new HandyTimePickerRow(getContext(), rowStartTime, rowEndTime, this));
        }
    }

    @Override
    public void onTimeClick(final int time)
    {
        int targetTime = time;

        // Default selection to start time.
        if (mSelectionType == null)
        {
            setSelectionType(SelectionType.START_TIME);
        }
        // Default selection to end time if a start time has been selected but end time hasn't
        // been selected.
        else if (mSelectionType == SelectionType.START_TIME
                && hasSelectedStartTime()
                && !hasSelectedEndTime())
        {
            setSelectionType(SelectionType.END_TIME);
        }

        // Tapping selected times on the picker will cancel the range and leave the start time,
        // or deselect a single selection.
        if (getSelectedStartTime() == targetTime
                || getSelectedEndTime() == targetTime)
        {
            if (hasSelectedRange()) // a range is selected
            {
                // Reset selection then reselect the original selected start time.
                final int selectedStartTime = getSelectedStartTime();
                clearSelection();
                setSelectionType(SelectionType.START_TIME);
                targetTime = selectedStartTime;
            }
            else // a single time is currently selected
            {
                clearSelection();
                setSelectionType(null);
                return;
            }
        }

        // Tapping an earlier time when there is already a selected start time will force the
        // selection to start time.
        if (hasSelectedStartTime() && targetTime < getSelectedStartTime())
        {
            setSelectionType(SelectionType.START_TIME);
        }

        // Tapping a later time when there is already a selected end time will force the selection
        // to end time.
        if (hasSelectedEndTime() && targetTime > getSelectedEndTime())
        {
            setSelectionType(SelectionType.END_TIME);
        }

        // Select start time if applicable.
        if (mSelectionType == SelectionType.START_TIME)
        {
            selectStartTime(targetTime);
        }

        // Select end time if applicable.
        if (mSelectionType == SelectionType.END_TIME)
        {
            selectEndTime(targetTime);
        }
    }

    public boolean selectTimeRange(final int startTime, final int endTime)
    {
        if (!isValidRange(startTime, endTime))
        {
            return false;
        }
        clearSelection();
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

    public void clearSelection()
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
        notifyRangeUpdate();
    }

    public void selectStartTime(final int time)
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
            notifyRangeUpdate();
        }
    }

    public void selectEndTime(final int time)
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
            notifyRangeUpdate();
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

    private void notifyRangeUpdate()
    {
        if (mCallbacks != null)
        {
            mCallbacks.onRangeUpdated(mSelectedStartTime, mSelectedEndTime);
        }
    }

    public interface Callbacks
    {
        void onRangeUpdated(int startTime, int endTime);

        void onSelectionTypeChanged(SelectionType selectionType);
    }
}
