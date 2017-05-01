package com.handy.portal.proavailability.viewmodel;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.handy.portal.proavailability.viewmodel.TimePickerViewModel.TimeRange.DEFAULT_MAXIMUM_HOUR;
import static com.handy.portal.proavailability.viewmodel.TimePickerViewModel.TimeRange.DEFAULT_MINIMUM_HOUR;
import static com.handy.portal.proavailability.viewmodel.TimePickerViewModel.TimeRange.DEFAULT_MINIMUM_TIME_RANGE_DURATION;

public class TimePickerViewModel {


    public enum SelectionType {
        START_TIME, END_TIME
    }


    private Pointer mPointer;
    private List<TimeRange> mTimeRanges;
    private List<Listener> mListeners;
    private boolean mClosed;
    private int mMinimumHour;
    private int mMaximumHour;
    private int mMinimumTimeRangeDuration;

    public TimePickerViewModel() {
        mPointer = new Pointer();
        mTimeRanges = new ArrayList<>();
        mListeners = new ArrayList<>();
        mClosed = false;
        mMinimumHour = DEFAULT_MINIMUM_HOUR;
        mMaximumHour = DEFAULT_MAXIMUM_HOUR;
        mMinimumTimeRangeDuration = DEFAULT_MINIMUM_TIME_RANGE_DURATION;
    }

    public void setLimits(
            final int minimumHour,
            final int maximumHour,
            final int minimumTimeRangeDuration
    ) {
        mMinimumHour = minimumHour;
        mMaximumHour = maximumHour;
        mMinimumTimeRangeDuration = minimumTimeRangeDuration;
    }

    public List<TimeRange> getTimeRanges() {
        return new ArrayList<>(mTimeRanges);
    }

    public void addTimeRange() {
        addTimeRange(TimeRange.NO_HOUR, TimeRange.NO_HOUR);
    }

    public void addTimeRange(final int startHour, final int endHour) {
        setClosed(false);
        mTimeRanges.add(new TimeRange(startHour, endHour));
        for (final Listener listener : mListeners) {
            listener.onTimeRangeAdded(mTimeRanges.size() - 1, startHour, endHour);
        }
    }

    public void setTimeRange(final int index, final int startHour, final int endHour) {
        final TimeRange timeRange = mTimeRanges.get(index);
        timeRange.setStartHour(startHour);
        timeRange.setEndHour(endHour);
    }

    public void clearTimeRange(final int index) {
        setTimeRange(index, TimeRange.NO_HOUR, TimeRange.NO_HOUR);
    }

    public void removeTimeRange(final int index) {
        setClosed(false);
        int oldPointerIndex = mPointer.getIndex();
        mPointer.point(Pointer.NO_INDEX, null);
        final TimeRange timeRange = mTimeRanges.remove(index);
        for (final Listener listener : mListeners) {
            listener.onTimeRangeRemoved(index, timeRange.getStartHour(), timeRange.getEndHour());
        }
        final int newPointerIndex = oldPointerIndex == mTimeRanges.size() ?
                oldPointerIndex - 1 : oldPointerIndex;
        mPointer.point(newPointerIndex, SelectionType.START_TIME);
    }

    public int getTimeRangesCount() {
        return mTimeRanges.size();
    }

    public Pointer getPointer() {
        return mPointer;
    }

    public boolean isClosed() {
        return mClosed;
    }

    public void setClosed(final boolean closed) {
        if (closed == mClosed) {
            return;
        }
        mClosed = closed;
        for (final Listener listener : mListeners) {
            listener.onClosedStateChanged(mClosed);
        }
    }

    public void addListener(final Listener listener) {
        mListeners.add(listener);
    }

    public boolean validate() {
        if (mClosed) {
            return true;
        }
        else {
            return hasCompleteTimeRanges();
        }
    }

    public boolean hasCompleteTimeRanges() {
        for (final TimeRange timeRange : mTimeRanges) {
            if (!timeRange.hasStartHour() || !timeRange.hasEndHour()) {
                return false;
            }
        }
        return true;
    }

    private boolean validatePotentialTimeRange(final int startHour, final int endHour) {
        if (startHour == TimeRange.NO_HOUR && endHour == TimeRange.NO_HOUR) {
            return true;
        }
        else {
            final List<TimeRange> timeRanges = new ArrayList<>(mTimeRanges);
            timeRanges.remove(getPointer().getTimeRange());
            for (final TimeRange timeRange : getInvertedTimeRanges(timeRanges)) {
                if ((startHour == TimeRange.NO_HOUR || timeRange.covers(startHour, false))
                        && (endHour == TimeRange.NO_HOUR || timeRange.covers(endHour, false))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public List<Integer> getSelectableHours(@Nullable final TimeRange editingTimeRange) {
        final SelectionType selectionType = getPointer().getSelectionType();
        if (selectionType == null) { return null; }
        final List<Integer> selectableHours = new ArrayList<>();
        final List<TimeRange> timeRanges = new ArrayList<>(mTimeRanges);
        final TimeRange currentTimeRange = editingTimeRange == null ?
                new TimeRange() : editingTimeRange;
        timeRanges.remove(currentTimeRange);
        for (final TimeRange timeRange : getInvertedTimeRanges(timeRanges)) {
            if ((currentTimeRange.hasStartHour() && !timeRange.covers(currentTimeRange.getStartHour(), false))
                    || (currentTimeRange.hasEndHour() && !timeRange.covers(currentTimeRange.getEndHour(), false))) {
                continue;
            }
            if (selectionType == SelectionType.START_TIME) {
                for (int hour = timeRange.getStartHour() + 1;
                     hour + mMinimumTimeRangeDuration < timeRange.getEndHour(); hour++) {
                    if (!currentTimeRange.hasEndHour()
                            || hour <= currentTimeRange.getEndHour() - mMinimumTimeRangeDuration) {
                        selectableHours.add(hour);
                    }
                }
            }
            if (selectionType == SelectionType.END_TIME) {
                for (int hour = timeRange.getEndHour() - 1;
                     hour - mMinimumTimeRangeDuration > timeRange.getStartHour(); hour--) {
                    if (!currentTimeRange.hasStartHour()
                            || hour >= currentTimeRange.getStartHour() + mMinimumTimeRangeDuration) {
                        selectableHours.add(hour);
                    }
                }
            }
        }
        return selectableHours;
    }

    private List<TimeRange> getInvertedTimeRanges(final List<TimeRange> timeRanges) {
        Collections.sort(timeRanges);
        final List<Integer> invertedTimeHours = new ArrayList<>();
        for (final TimeRange timeRange : timeRanges) {
            if (timeRange.hasRange()) {
                invertedTimeHours.add(timeRange.getStartHour());
                invertedTimeHours.add(timeRange.getEndHour());
            }
            else if (timeRange.hasStartHour() ^ timeRange.hasEndHour()) {
                final int hour = timeRange.hasStartHour() ?
                        timeRange.getStartHour() : timeRange.getEndHour();
                invertedTimeHours.add(hour);
                invertedTimeHours.add(hour);
            }
        }
        invertedTimeHours.add(0, mMinimumHour - 1);
        invertedTimeHours.add(mMaximumHour + 1);

        final List<TimeRange> invertedTimeRanges = new ArrayList<>();
        for (int i = 0; i < invertedTimeHours.size(); i += 2) {
            invertedTimeRanges.add(new TimeRange(invertedTimeHours.get(i),
                    invertedTimeHours.get(i + 1)));
        }

        return invertedTimeRanges;
    }

    public class Pointer {
        public static final int NO_INDEX = -1;

        private int mIndex;
        private SelectionType mSelectionType;

        public Pointer() {
            mIndex = NO_INDEX;
            mSelectionType = null;
        }

        public boolean validate() {
            return getIndex() != NO_INDEX
                    && getIndex() >= 0
                    && getIndex() < mTimeRanges.size()
                    && getSelectionType() != null;
        }

        public int getIndex() {
            return mIndex;
        }

        public SelectionType getSelectionType() {
            return mSelectionType;
        }

        public void setSelectionType(final SelectionType selectionType) {
            setClosed(false);
            mSelectionType = selectionType;
            for (final Listener listener : mListeners) {
                listener.onPointerUpdated(mIndex, mSelectionType);
            }
        }

        public void point(final int index, final SelectionType selectionType) {
            setClosed(false);
            mIndex = index;
            setSelectionType(selectionType);
        }

        public TimeRange getTimeRange() {
            return mTimeRanges.get(getIndex());
        }
    }


    public class TimeRange implements Comparable<TimeRange> {
        public static final int DEFAULT_MINIMUM_TIME_RANGE_DURATION = 1;
        public static final int DEFAULT_MINIMUM_HOUR = 0;
        public static final int DEFAULT_MAXIMUM_HOUR = 24;
        public static final int NO_HOUR = -999;

        private int mStartHour;
        private int mEndHour;

        public TimeRange() {
            this(NO_HOUR, NO_HOUR);
        }

        public TimeRange(final int startHour, final int endHour) {
            mStartHour = startHour;
            mEndHour = endHour;
        }

        public int getStartHour() {
            return mStartHour;
        }

        public boolean setStartHour(final int startHour) {
            if (!validateStartHour(startHour)) { return false; }
            setClosed(false);
            final int oldStartHour = mStartHour;
            mStartHour = startHour;
            for (final Listener listener : mListeners) {
                listener.onTimeRangeUpdated(mTimeRanges.indexOf(this), oldStartHour, mEndHour,
                        mStartHour, mEndHour);
            }
            return true;
        }

        public boolean hasStartHour() {
            return mStartHour != NO_HOUR;
        }

        public int getEndHour() {
            return mEndHour;
        }

        public boolean setEndHour(final int endHour) {
            if (!validateEndHour(endHour)) { return false; }
            setClosed(false);
            final int oldEndHour = mEndHour;
            mEndHour = endHour;
            for (final Listener listener : mListeners) {
                listener.onTimeRangeUpdated(mTimeRanges.indexOf(this), mStartHour, oldEndHour,
                        mStartHour, mEndHour);
            }
            return true;
        }

        public boolean hasEndHour() {
            return mEndHour != NO_HOUR;
        }

        public boolean hasRange() {
            return hasStartHour() && hasEndHour();
        }

        public boolean validateStartHour(final int startHour) {
            return validatePotentialTimeRange(startHour, mEndHour)
                    && (startHour >= mMinimumHour && startHour < mMaximumHour
                    && (!hasEndHour() || startHour < mEndHour))
                    || startHour == NO_HOUR;
        }

        public boolean validateEndHour(final int endHour) {
            return validatePotentialTimeRange(mStartHour, endHour)
                    && (endHour <= mMaximumHour && endHour > mMinimumHour
                    && (!hasStartHour() || endHour > mStartHour))
                    || endHour == NO_HOUR;
        }

        public boolean covers(final int hour, final boolean inclusive) {
            if (hour == NO_HOUR) {
                return false;
            }
            if (inclusive && (hour == mStartHour || hour == mEndHour)) {
                return true;
            }
            if (hasStartHour() && hasEndHour()) {
                return hour > mStartHour && hour < mEndHour;
            }
            return false;
        }

        public void clear() {
            setStartHour(NO_HOUR);
            setEndHour(NO_HOUR);
        }

        public boolean validate() {
            return hasStartHour() && hasEndHour();
        }

        @Override
        public int compareTo(@NonNull final TimeRange other) {
            final int difference = mStartHour - other.getStartHour();
            return difference == 0 ? 0 : (difference / Math.abs(difference));
        }
    }


    public interface Listener {
        void onTimeRangeUpdated(
                int index,
                int oldStartHour,
                int oldEndHour,
                int newStartHour,
                int newEndHour
        );

        void onTimeRangeAdded(int index, int startHour, int endHour);

        void onTimeRangeRemoved(int index, int startHour, int endHour);

        void onPointerUpdated(int index, SelectionType selectionType);

        void onClosedStateChanged(boolean closed);
    }
}
