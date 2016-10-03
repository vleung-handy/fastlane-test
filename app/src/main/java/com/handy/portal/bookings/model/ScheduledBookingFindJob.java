package com.handy.portal.bookings.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by sng on 9/28/16.
 * This class is to differentiate between a normal booking and a Find Job row in the ScheduledBookingListView
 * This is a pseudo hack because refactoring the BookingsFragment will take too long. We need to set the startDate/endDate so
 *    the list view will display correctly
 */

public class ScheduledBookingFindJob extends Booking implements Serializable {

    private String availableStartJobId;
    private String availableStartJobType;
    private String availableEndJobId;
    private String availableEndJobType;
    //Used to prevent dupe
    private List<String> jobLocationNames;

    public ScheduledBookingFindJob(Date availableStartDateTime, Date availableEndDateTime){
        this.mStartDate = availableStartDateTime;
        this.mEndDate = availableEndDateTime;
    }

    public void addJobLocationName(String jobLocationName) {
        if(TextUtils.isEmpty(jobLocationName))
            return;

        if(jobLocationNames == null) {
            jobLocationNames = new ArrayList<>();
        }

        if(!jobLocationNames.contains(jobLocationName))
            jobLocationNames.add(jobLocationName);
    }

    public List<String> getJobLocationNames() {
        return jobLocationNames;
    }

    public Date getAvailableStartTime() {
        return mStartDate;
    }

    public Date getAvailableEndTime() {
        return mEndDate;
    }

    public String getAvailableStartJobId() {
        return availableStartJobId;
    }

    public void setAvailableStartJobId(String availableStartJobId) {
        this.availableStartJobId = availableStartJobId;
    }

    public String getAvailableStartJobType() {
        return availableStartJobType;
    }

    public void setAvailableStartJobType(String availableStartJobType) {
        this.availableStartJobType = availableStartJobType;
    }

    public String getAvailableEndJobId() {
        return availableEndJobId;
    }

    public void setAvailableEndJobId(String availableEndJobId) {
        this.availableEndJobId = availableEndJobId;
    }

    public String getAvailableEndJobType() {
        return availableEndJobType;
    }

    public void setAvailableEndJobType(String availableEndJobType) {
        this.availableEndJobType = availableEndJobType;
    }
}
