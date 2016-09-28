package com.handy.portal.bookings.model;

import java.util.Date;

/**
 * Created by sng on 9/28/16.
 * This class is to differentiate between a normal booking and a Find Job row in the ScheduledBookingListView
 */

public class ScheduledBookingFindJob extends Booking {

    public ScheduledBookingFindJob(Date availableStartDateTime, Date availableEndDateTime){
        this.mStartDate = availableStartDateTime;
        this.mEndDate = availableEndDateTime;
    }
}
