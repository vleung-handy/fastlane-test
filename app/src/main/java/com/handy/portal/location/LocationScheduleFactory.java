package com.handy.portal.location;

import android.support.annotation.NonNull;

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
    using the strategies defined in the below as a guide:

    https://handybook.atlassian.net/wiki/display/engineeringwiki/Polling+Provider+Geolocation
     */

    /**
     * TODO: crude, clean up
     *
     * @param booking
     * @return
     */
    public static List<LocationQueryStrategy> getLocationStrategiesFromBooking(@NonNull Booking booking)
    {
        //TODO: make sure the calendar.add(Calendar.HOUR_OF_DAY, 1) actually adds an hour. what's the difference between that and add(Calendar.HOUR, 1)?
        List<LocationQueryStrategy> locationQueryStrategies = new LinkedList<>();
        LocationQueryStrategy locationQueryStrategy = new LocationQueryStrategy();

        Calendar calendar = Calendar.getInstance();

        /**
         * 3 - 1 hours before a booking
         */
        calendar.setTime(booking.getStartDate());
        calendar.add(Calendar.HOUR_OF_DAY, -3);
        Date fourHoursBeforeBooking = calendar.getTime();

        locationQueryStrategy.setStartDate(fourHoursBeforeBooking);
        calendar.setTime(booking.getStartDate());
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date oneHourBeforeBooking = calendar.getTime();
        locationQueryStrategy.setEndDate(oneHourBeforeBooking)
                .setBookingId(booking.getId())
                .setLocationPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE)
                .setServerPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 5)
                .setLocationAccuracyPriority(LocationQueryStrategy.ACCURACY_BALANCED_POWER_PRIORITIY)
                .setDistanceFilterMeters(100)
                .setEventName("three_hours_out");  //TODO: remove magic strings
        locationQueryStrategies.add(locationQueryStrategy);

        /**
         * 60 - 15 minutes before a booking
         */
        calendar.setTime(booking.getStartDate());
        calendar.add(Calendar.MINUTE, -15);
        Date fifteenMinutesBeforeBooking = calendar.getTime();
        locationQueryStrategy = new LocationQueryStrategy()
                .setStartDate(oneHourBeforeBooking)
                .setBookingId(booking.getId())
                .setEndDate(fifteenMinutesBeforeBooking)
                .setLocationPollingIntervalSeconds(30)
                .setServerPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 5)
                .setLocationAccuracyPriority(LocationQueryStrategy.ACCURACY_HIGH_PRIORITY)
                .setDistanceFilterMeters(50)
                .setEventName("one_hour_out");
        locationQueryStrategies.add(locationQueryStrategy);

        /**
         * 15 minutes before
         */

        locationQueryStrategy = new LocationQueryStrategy();
        locationQueryStrategy.setStartDate(fifteenMinutesBeforeBooking);

        locationQueryStrategy.setEndDate(booking.getStartDate())
                .setLocationPollingIntervalSeconds(30)
                .setBookingId(booking.getId())
                .setServerPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE)
                .setLocationAccuracyPriority(LocationQueryStrategy.ACCURACY_HIGH_PRIORITY)
                .setDistanceFilterMeters(25)
                .setEventName("fifteen_minutes_out");
        locationQueryStrategies.add(locationQueryStrategy);

        /**
         *     During the first hour of booking
         */

        locationQueryStrategy = new LocationQueryStrategy()
                .setStartDate(booking.getStartDate());
        calendar.setTime(booking.getStartDate());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Date oneHourAfterBooking = calendar.getTime();

        locationQueryStrategy.setEndDate(oneHourAfterBooking)
                .setBookingId(booking.getId())
                .setLocationPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 5)
                .setServerPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 10)
                .setLocationAccuracyPriority(LocationQueryStrategy.ACCURACY_HIGH_PRIORITY)
                .setDistanceFilterMeters(25)
                .setEventName("started");
        locationQueryStrategies.add(locationQueryStrategy);

        /**
         *     Starting 15 minutes before booking end
         */

        locationQueryStrategy = new LocationQueryStrategy();
        calendar.setTime(booking.getEndDate());
        calendar.add(Calendar.MINUTE, -15);
        Date fifteenMinutesBeforeBookingEnd = calendar.getTime();

        locationQueryStrategy.setStartDate(fifteenMinutesBeforeBookingEnd);
        calendar.setTime(fifteenMinutesBeforeBookingEnd);
        calendar.add(Calendar.MINUTE, 30);
        Date fifteenMinutesAfterBookingEnd = calendar.getTime();

        locationQueryStrategy.setEndDate(fifteenMinutesAfterBookingEnd)
                .setBookingId(booking.getId())
                .setLocationPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 1)
                .setServerPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 5)
                .setLocationAccuracyPriority(LocationQueryStrategy.ACCURACY_HIGH_PRIORITY)
                .setDistanceFilterMeters(25)
                .setEventName("ending");
        locationQueryStrategies.add(locationQueryStrategy);

        /**
         * 15 - 45 minutes after booking end
         */
        locationQueryStrategy = new LocationQueryStrategy();

        locationQueryStrategy.setStartDate(fifteenMinutesAfterBookingEnd);
        calendar.setTime(fifteenMinutesAfterBookingEnd);
        calendar.add(Calendar.MINUTE, 30);
        Date fortyFiveMinutesAfterBookingEnd = calendar.getTime();

        locationQueryStrategy.setEndDate(fortyFiveMinutesAfterBookingEnd)
                .setBookingId(booking.getId())
                .setLocationPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 5)
                .setServerPollingIntervalSeconds(DateTimeUtils.SECONDS_IN_MINUTE * 15)
                .setLocationAccuracyPriority(LocationQueryStrategy.ACCURACY_BALANCED_POWER_PRIORITIY)
                .setDistanceFilterMeters(50)
                .setEventName("ended");
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
