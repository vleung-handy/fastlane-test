package com.handy.portal.availability.viewmodel;

import android.support.annotation.Nullable;

import com.handy.portal.availability.model.Availability;

import java.util.List;

public class AvailableHoursViewModel {
    private String mTitle;
    private List<Availability.Interval> mIntervals;
    private boolean mEnabled;
    private Object mIdentifier;

    public AvailableHoursViewModel(
            final String title,
            @Nullable final List<Availability.Interval> intervals,
            final boolean enabled,
            final Object identifier
    ) {
        mTitle = title;
        mIntervals = intervals;
        mEnabled = enabled;
        mIdentifier = identifier;
    }

    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public List<Availability.Interval> getIntervals() {
        return mIntervals;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public Object getIdentifier() {
        return mIdentifier;
    }
}
