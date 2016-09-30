package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.util.AttributeSet;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.ScheduledBookingFindJob;
import com.handy.portal.bookings.ui.adapter.BookingElementAdapter;
import com.handy.portal.bookings.ui.adapter.ScheduledBookingElementAdapter;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sng on 9/28/16.
 */

public class ScheduledBookingListView extends BookingListView {
    private Date selectedDate;

    public ScheduledBookingListView(Context context) {
        super(context);
    }

    public ScheduledBookingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScheduledBookingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     * @param selectedDate The selected date on the schedule
     */
    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    /**
     *
     * @param bookings
     * @param elementViewClass This isn't used any more, but to refactor this in the BookingsFragment would take too much time
     */
    public void populateList(List<Booking> bookings, Class<? extends BookingElementView> elementViewClass) {
        List<Booking> newBookingListWithFindJob = new ArrayList<>();
        //Set it to selected date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        Calendar now = Calendar.getInstance();
        //If the calendar is not today, then start at 7am, otherwise if it's today, start with the time now
        if(calendar.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR)) {
            //start at 7am
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
        }

        Date latestEndDateTime = calendar.getTime();
        //Day ends at 11pm
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        Date lastEndDateTime = calendar.getTime();

        //This is always 3 because we already add an hour to latestEndDateTime in the code
        int hoursThreshold = 3;
        Booking previousBooking = null;
        for (Booking booking : bookings) {
            Date startDateTime = booking.getStartDate();

            int hoursBetween = DateTimeUtils.hoursBetween(latestEndDateTime, startDateTime);
            if (hoursBetween >= hoursThreshold) {
                //Subtract an hour from the end time to give time between bookings
                ScheduledBookingFindJob job = new ScheduledBookingFindJob(latestEndDateTime, DateTimeUtils.updateDateByAddingHour(booking.getStartDate(), -1));
                //If there was a previous booking before this, add the job id/type
                if(previousBooking != null) {
                    job.setAvailableStartJobId(previousBooking.getId());
                    job.setAvailableStartJobType(previousBooking.getTypeName());
                }

                job.setAvailableEndJobId(booking.getId());
                job.setAvailableEndJobType(booking.getTypeName());
                newBookingListWithFindJob.add(job);
            }

            newBookingListWithFindJob.add(booking);
            previousBooking = booking;
            //Add 1 hour to latest so pro can get to next booking
            latestEndDateTime = DateTimeUtils.updateDateByAddingHour(booking.getEndDate(), 1);
        }

        //Check the last time with the end of day
        int hoursBetween = DateTimeUtils.hoursBetween(latestEndDateTime, lastEndDateTime);
        //Use 2 hours because being last job of the day, we already added a buffer to the last job
        if (hoursBetween >= 2) {
            ScheduledBookingFindJob job =new ScheduledBookingFindJob(latestEndDateTime, lastEndDateTime);
            //If there was a previous booking before this, add the job id/type
            if(previousBooking != null) {
                job.setAvailableStartJobId(previousBooking.getId());
                job.setAvailableStartJobType(previousBooking.getTypeName());
            }
            newBookingListWithFindJob.add(job);
        }

        ScheduledBookingElementAdapter itemsAdapter =
                new ScheduledBookingElementAdapter(getContext(), newBookingListWithFindJob);
        setAdapter(itemsAdapter);
    }
}
