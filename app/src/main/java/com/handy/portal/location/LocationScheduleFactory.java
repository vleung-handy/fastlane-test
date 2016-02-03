package com.handy.portal.location;

import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.location.model.LocationQueryStrategy;
import com.handy.portal.model.Booking;
import com.handy.portal.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO: THIS IS TEMPORARY CODE TO GENERATE SCHEDULES FROM BOOKINGS
 * TODO: remove this class when we switch over to an endpoint
 */
public class LocationScheduleFactory
{
    /*
    4 - 1 hours before a booking, poll every 15 minutes
    1 - 0 hours before a booking, poll every minute or as often as necessary based on their speed to draw a reasonable path of travel
    Geofence around the booking to know once they've arrived (Geofence arrival event) Waiting on legal
    During the first hour of booking, poll every 10 minutes
    Starting 15 minutes before booking end, poll every 10 minutes for an hour

    https://handybook.atlassian.net/wiki/display/engineeringwiki/Polling+Provider+Geolocation
     */

    /**
     * TODO: crude, clean up
     * @param booking
     * @return
     */
    public static List<LocationQueryStrategy> getLocationStrategiesFromBooking(@NonNull Booking booking)
    {
        List<LocationQueryStrategy> locationQueryStrategies = new LinkedList<>();
        LocationQueryStrategy locationQueryStrategy = new LocationQueryStrategy();

        Calendar calendar = Calendar.getInstance();

        /**
         * 4 - 1 hours before a booking, poll every 15 minutes
         */
        calendar.setTime(booking.getStartDate());
        calendar.add(Calendar.HOUR_OF_DAY, -4);
        Date fourHoursBeforeBooking = calendar.getTime();

        locationQueryStrategy.setStartDate(fourHoursBeforeBooking);
        calendar.setTime(booking.getStartDate());
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date oneHourBeforeBooking = calendar.getTime();
        locationQueryStrategy.setEndDate(oneHourBeforeBooking)
                .setPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 15)
                .setLocationAccuracyPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationQueryStrategies.add(locationQueryStrategy);

        /**
         * 1 - 0 hours before a booking, poll every minute or as often as necessary based on their speed to draw a reasonable path of travel
         */
        locationQueryStrategy = new LocationQueryStrategy()
                .setStartDate(oneHourBeforeBooking)
                .setEndDate(booking.getStartDate())
                .setPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE)
                .setLocationAccuracyPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationQueryStrategies.add(locationQueryStrategy);

        /**
         *     During the first hour of booking, poll every 10 minutes
         */

        locationQueryStrategy = new LocationQueryStrategy()
                .setStartDate(booking.getStartDate());
        calendar.setTime(booking.getStartDate());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Date endDate = calendar.getTime();

        locationQueryStrategy.setEndDate(endDate)
                .setPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 10)
                .setLocationAccuracyPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationQueryStrategies.add(locationQueryStrategy);

        /**
         *     Starting 15 minutes before booking end, poll every 10 minutes for an hour
         */

        locationQueryStrategy = new LocationQueryStrategy();
        calendar.setTime(booking.getEndDate());
        calendar.add(Calendar.MINUTE, -15);
        Date startDate = calendar.getTime();

        locationQueryStrategy.setStartDate(startDate);
        calendar.setTime(startDate);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        endDate = calendar.getTime();

        locationQueryStrategy.setEndDate(endDate)
                .setPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 10)
                .setLocationAccuracyPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationQueryStrategies.add(locationQueryStrategy);

        return locationQueryStrategies;
    }

    //TODO: temporary code only, switch over to an endpoint later
    public static LocationQuerySchedule getLocationScheduleFromBookings(List<Booking> bookingList)
    {
        LinkedList<LocationQueryStrategy> sortedLocationStrategies = new LinkedList<>();
        for (Booking b : bookingList)
        {
            List<LocationQueryStrategy> bookingLocationStrategies = getLocationStrategiesFromBooking(b);
            sortedLocationStrategies.addAll(bookingLocationStrategies);
            //see comment below
        }

        //TODO: put somewhere else
        Comparator<LocationQueryStrategy> locationQueryStrategyComparator = new Comparator<LocationQueryStrategy>()
        {
            @Override
            public int compare(final LocationQueryStrategy lhs, final LocationQueryStrategy rhs)
            {
                return lhs.getStartDate().compareTo(rhs.getStartDate());
            }
        };
        //TODO: can do this more efficiently but not going to bother because the server is expected to handle this eventually
        Collections.sort(sortedLocationStrategies, locationQueryStrategyComparator);
        LocationQuerySchedule locationQuerySchedule = new LocationQuerySchedule(sortedLocationStrategies);
        return locationQuerySchedule;
    }
}
