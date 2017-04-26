package com.handy.portal.proavailability.viewmodel;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.handy.portal.proavailability.viewmodel.TimePickerViewModel.TimeRange.MAXIMUM_HOUR;
import static com.handy.portal.proavailability.viewmodel.TimePickerViewModel.TimeRange.MINIMUM_HOUR;

public class TimePickerViewModel {

    public enum SelectionType {
        START_TIME, END_TIME
    }


    private Pointer mPointer;
    private List<TimeRange> mTimeRanges;
    private List<Listener> mListeners;
    private boolean mClosed;

    public TimePickerViewModel() {
        mPointer = new Pointer();
        mTimeRanges = new ArrayList<>();
        mListeners = new ArrayList<>();
        mClosed = false;
    }

    public Collection<TimeRange> getTimeRanges() {
        return Collections.unmodifiableCollection(mTimeRanges);
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
        final TimeRange timeRange = mTimeRanges.remove(index);
        for (final Listener listener : mListeners) {
            listener.onTimeRangeRemoved(index, timeRange.getStartHour(), timeRange.getEndHour());
        }
        int pointerIndex = mPointer.getIndex();
        final int newPointerIndex = pointerIndex == mTimeRanges.size() ?
                pointerIndex - 1 : pointerIndex;
        mPointer.point(newPointerIndex, SelectionType.START_TIME);
    }

    public int getTimeRangesCount() {
        return mTimeRanges.size();
    }

    public Pointer getPointer() {
        return mPointer;
    }

    public boolean hasStartTime(final int index) {
        return getStartHour(index) != TimeRange.NO_HOUR;
    }

    private int getStartHour(final int index) {
        return mTimeRanges.get(index).getStartHour();
    }

    public boolean hasEndTime(final int index) {
        return getEndHour(index) != TimeRange.NO_HOUR;
    }

    public int getEndHour(final int index) {
        return mTimeRanges.get(index).getEndHour();
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

    private boolean hasCompleteTimeRanges() {
        for (final TimeRange timeRange : mTimeRanges) {
            if (!timeRange.hasStartHour() || !timeRange.hasEndHour()) {
                return false;
            }
        }
        return true;
    }

    private List<TimeRange> getInvertedTimeRanges(final List<TimeRange> timeRanges) {
        final List<Integer> invertedTimeHours = new ArrayList<>();
        for (final TimeRange timeRange : timeRanges) {
            invertedTimeHours.add(timeRange.getStartHour());
            invertedTimeHours.add(timeRange.getEndHour());
        }
        invertedTimeHours.add(0, MINIMUM_HOUR);
        invertedTimeHours.add(MAXIMUM_HOUR);

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


    public class TimeRange {
        public static final int MINIMUM_HOUR = 0;
        public static final int MAXIMUM_HOUR = 24;
        public static final int NO_HOUR = -1;

        private int mStartHour;
        private int mEndHour;

        public TimeRange(final int startHour, final int endHour) {
            mStartHour = startHour;
            mEndHour = endHour;
        }

        public int getStartHour() {
            return mStartHour;
        }

        public void setStartHour(final int startHour) {
            if (!validateStartHour(startHour)) { return; }
            setClosed(false);
            final int oldStartHour = mStartHour;
            mStartHour = startHour;
            for (final Listener listener : mListeners) {
                listener.onTimeRangeUpdated(mTimeRanges.indexOf(this), oldStartHour, mEndHour,
                        mStartHour, mEndHour);
            }
        }

        public boolean hasStartHour() {
            return mStartHour != NO_HOUR;
        }

        public int getEndHour() {
            return mEndHour;
        }

        public void setEndHour(final int endHour) {
            if (!validateEndHour(endHour)) { return; }
            setClosed(false);
            final int oldEndHour = mEndHour;
            mEndHour = endHour;
            for (final Listener listener : mListeners) {
                listener.onTimeRangeUpdated(mTimeRanges.indexOf(this), mStartHour, oldEndHour,
                        mStartHour, mEndHour);
            }
        }

        public boolean hasEndHour() {
            return mEndHour != NO_HOUR;
        }

        public boolean hasRange() {
            return hasStartHour() && hasEndHour();
        }

        public boolean validateStartHour(final int startHour) {
            return validatePotentialTimeRange(startHour, mEndHour)
                    && (startHour >= MINIMUM_HOUR && startHour < MAXIMUM_HOUR
                    && (!hasEndHour() || startHour < mEndHour))
                    || startHour == NO_HOUR;
        }

        public boolean validateEndHour(final int endHour) {
            return validatePotentialTimeRange(mStartHour, endHour)
                    && (endHour <= MAXIMUM_HOUR && endHour > MINIMUM_HOUR
                    && (!hasStartHour() || endHour > mStartHour))
                    || endHour == NO_HOUR;
        }

        private boolean validatePotentialTimeRange(final int startHour, final int endHour) {
            if (startHour == NO_HOUR && endHour == NO_HOUR) {
                return true;
            }
            else {
                final List<TimeRange> timeRanges = new ArrayList<>(mTimeRanges);
                timeRanges.remove(getPointer().getTimeRange());

                for (final TimeRange timeRange : getInvertedTimeRanges(timeRanges)) {
                    if ((startHour == NO_HOUR || timeRange.covers(startHour, false))
                            && (endHour == NO_HOUR || timeRange.covers(endHour, false))) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean covers(final int hour, final boolean inclusive) {
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
