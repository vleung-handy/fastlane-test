package com.handy.portal.proavailability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ProviderAvailability implements Serializable {
    @SerializedName("weekly_timelines")
    private ArrayList<WeeklyAvailabilityTimelinesWrapper> mWeeklyAvailabilityTimelinesWrappers;

    public ArrayList<WeeklyAvailabilityTimelinesWrapper> getWeeklyAvailabilityTimelinesWrappers() {
        return mWeeklyAvailabilityTimelinesWrappers;
    }

    /**
     *
     * @param maxWeeks Cap the number of items returned to max
     * @return
     */
    public ArrayList<WeeklyAvailabilityTimelinesWrapper> getWeeklyAvailabilityTimelinesWrappers(int maxWeeks) {
        if(mWeeklyAvailabilityTimelinesWrappers == null || maxWeeks >= mWeeklyAvailabilityTimelinesWrappers.size())
            return mWeeklyAvailabilityTimelinesWrappers;
        else {
            ArrayList<WeeklyAvailabilityTimelinesWrapper> cappedWeeks = new ArrayList<>();
            //get the number of weeks up to max weeks
            for(int i=0; i < maxWeeks; i++) {
                cappedWeeks.add(mWeeklyAvailabilityTimelinesWrappers.get(i));
            }

            return cappedWeeks;
        }
    }

    @Nullable
    public DailyAvailabilityTimeline getAvailabilityForDate(final Date date) {
        final WeeklyAvailabilityTimelinesWrapper weeklyAvailabilityTimelinesWrapper =
                getWeeklyAvailabilityForDate(date);
        if (weeklyAvailabilityTimelinesWrapper != null) {
            return weeklyAvailabilityTimelinesWrapper.getAvailabilityForDate(date);
        }
        return null;
    }

    @Nullable
    public WeeklyAvailabilityTimelinesWrapper getWeeklyAvailabilityForDate(final Date date) {
        for (WeeklyAvailabilityTimelinesWrapper weeklyAvailabilityTimelinesWrapper :
                mWeeklyAvailabilityTimelinesWrappers) {
            if (weeklyAvailabilityTimelinesWrapper.covers(date)) {
                return weeklyAvailabilityTimelinesWrapper;
            }
        }
        return null;
    }

    public boolean covers(final Date date) {
        return getWeeklyAvailabilityForDate(date) != null;
    }

    public boolean hasAvailableHours() {
        for (WeeklyAvailabilityTimelinesWrapper weeklyAvailabilityTimelinesWrapper :
                mWeeklyAvailabilityTimelinesWrappers) {
            if (weeklyAvailabilityTimelinesWrapper.hasAvailableHours()) {
                return true;
            }
        }
        return false;
    }
}
