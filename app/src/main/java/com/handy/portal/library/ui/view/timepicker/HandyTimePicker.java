package com.handy.portal.library.ui.view.timepicker;

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
    public static final int NO_HOUR_SELECTED = -1;
    public static final int MINIMUM_START_HOUR = 0;
    public static final int MAXIMUM_START_HOUR = 24;
    private int mStartHour;
    private int mEndHour;
    private int mSelectedStartHour = NO_HOUR_SELECTED;
    private int mSelectedEndHour = NO_HOUR_SELECTED;
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

    public int getSelectedStartHour()
    {
        return mSelectedStartHour;
    }

    public int getSelectedEndHour()
    {
        return mSelectedEndHour;
    }

    public boolean hasSelectedStartTime()
    {
        return mSelectedStartHour != NO_HOUR_SELECTED;
    }

    public boolean hasSelectedEndTime()
    {
        return mSelectedEndHour != NO_HOUR_SELECTED;
    }

    public boolean hasSelectedRange()
    {
        return hasSelectedStartTime() && hasSelectedEndTime();
    }

    public boolean hasSelectedSingleTime()
    {
        return hasSelectedStartTime() ^ hasSelectedEndTime();
    }

    public boolean covers(final int hour)
    {
        return hour >= mStartHour && hour <= mEndHour;
    }

    public void setTimeRange(final int startHour, final int endHour)
    {
        if (startHour < MINIMUM_START_HOUR
                || endHour > MAXIMUM_START_HOUR
                || startHour >= endHour)
        {
            return;
        }
        mStartHour = startHour;
        mEndHour = endHour;
        for (int rowStartHour = startHour; rowStartHour <= endHour;
             rowStartHour += DEFAULT_CELLS_PER_ROW_COUNT)
        {
            int rowEndHour = rowStartHour + DEFAULT_CELLS_PER_ROW_COUNT - 1;
            if (rowEndHour > endHour)
            {
                rowEndHour = endHour;
            }
            addView(new HandyTimePickerRow(getContext(), rowStartHour, rowEndHour, this));
        }
    }

    @Override
    public void onHourClicked(final int hour)
    {
        int targetHour = hour;

        // Default selection to start hour.
        if (mSelectionType == null)
        {
            setSelectionType(SelectionType.START_TIME);
        }
        // Default selection to end hour if a start hour has been selected but end hour hasn't
        // been selected.
        else if (mSelectionType == SelectionType.START_TIME
                && hasSelectedStartTime()
                && !hasSelectedEndTime())
        {
            setSelectionType(SelectionType.END_TIME);
        }

        // Tapping selected times on the picker will cancel the range and leave the start hour,
        // or deselect a single selection.
        if (getSelectedStartHour() == targetHour
                || getSelectedEndHour() == targetHour)
        {
            if (hasSelectedRange()) // a range is selected
            {
                // Reset selection then reselect the original selected start hour.
                final int selectedStartHour = getSelectedStartHour();
                clearSelection();
                setSelectionType(SelectionType.START_TIME);
                targetHour = selectedStartHour;
            }
            else // a single hour is currently selected
            {
                clearSelection();
                setSelectionType(null);
                return;
            }
        }

        // Tapping an earlier hour when there is already a selected start hour will force the
        // selection to start hour.
        if (hasSelectedStartTime() && targetHour < getSelectedStartHour())
        {
            setSelectionType(SelectionType.START_TIME);
        }

        // Tapping a later hour when there is already a selected end hour will force the selection
        // to end hour.
        if (hasSelectedEndTime() && targetHour > getSelectedEndHour())
        {
            setSelectionType(SelectionType.END_TIME);
        }

        // Select start hour if applicable.
        if (mSelectionType == SelectionType.START_TIME)
        {
            selectStartHour(targetHour);
        }

        // Select end hour if applicable.
        if (mSelectionType == SelectionType.END_TIME)
        {
            selectEndHour(targetHour);
        }
    }

    public boolean selectTimeRange(final int startHour, final int endHour)
    {
        if (startHour >= endHour || startHour < mStartHour || endHour > mEndHour)
        {
            return false;
        }
        clearSelection();
        mSelectedStartHour = startHour;
        mSelectedEndHour = endHour;
        for (int i = 0; i < getChildCount(); i++)
        {
            final HandyTimePickerRow timePickerRow = (HandyTimePickerRow) getChildAt(i);
            for (int j = 0; j < timePickerRow.mTimePickerCellViews.getChildCount(); j++)
            {
                final HandyTimePickerCell timePickerCell =
                        (HandyTimePickerCell) timePickerRow.mTimePickerCellViews.getChildAt(j);
                final int timePickerCellHour = timePickerCell.getHour();
                if (timePickerCellHour >= startHour && timePickerCellHour <= endHour)
                {
                    if (timePickerCellHour == startHour || timePickerCellHour == endHour)
                    {
                        timePickerCell.select();
                    }
                    else
                    {
                        timePickerCell.highlight();
                    }
                    if (timePickerCellHour > startHour && j > 0)
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
        mSelectedStartHour = mSelectedEndHour = NO_HOUR_SELECTED;
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

    private void selectStartHour(final int hour)
    {
        if (mSelectedEndHour == NO_HOUR_SELECTED || hour < mSelectedEndHour)
        {
            resetCellForHour(mSelectedStartHour);
            mSelectedStartHour = hour;
            if (getSelectedEndHour() != NO_HOUR_SELECTED)
            {
                selectTimeRange(mSelectedStartHour, mSelectedEndHour);
            }
            else
            {
                selectCellForHour(hour);
            }
            notifyRangeUpdate();
        }
    }

    private void selectEndHour(final int hour)
    {
        if (mSelectedStartHour == NO_HOUR_SELECTED || hour > mSelectedStartHour)
        {
            resetCellForHour(mSelectedEndHour);
            mSelectedEndHour = hour;
            if (getSelectedStartHour() != NO_HOUR_SELECTED)
            {
                selectTimeRange(mSelectedStartHour, mSelectedEndHour);
            }
            else
            {
                selectCellForHour(hour);
            }
            notifyRangeUpdate();
        }
    }

    private void resetCellForHour(final int hour)
    {
        if (hour != NO_HOUR_SELECTED)
        {
            final HandyTimePickerCell cell = getCellForHour(hour);
            if (cell != null)
            {
                cell.reset();
            }
        }
    }

    private void selectCellForHour(final int hour)
    {
        if (hour != NO_HOUR_SELECTED)
        {
            final HandyTimePickerCell cell = getCellForHour(hour);
            if (cell != null)
            {
                cell.select();
            }
        }
    }

    @Nullable
    private HandyTimePickerCell getCellForHour(final int hour)
    {
        if (covers(hour))
        {
            for (int i = 0; i < getChildCount(); i++)
            {
                final HandyTimePickerRow timePickerRow = (HandyTimePickerRow) getChildAt(i);
                final HandyTimePickerCell timePickerCell = timePickerRow.getCellForHour(hour);
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
            mCallbacks.onRangeUpdated(mSelectedStartHour, mSelectedEndHour);
        }
    }

    public interface Callbacks
    {
        void onRangeUpdated(int startHour, int endHour);

        void onSelectionTypeChanged(SelectionType selectionType);
    }
}
