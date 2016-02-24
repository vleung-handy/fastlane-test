package com.handy.portal.location.manager;

import android.util.Log;

import com.handy.portal.event.HandyEvent;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.LocationScheduleFactory;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.model.Booking;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * experimental
 *
 * micro manager for getting the location schedule from bookings
 * TODO: currently not provided in application module, but in location manager. not sure if i want to do this
 * TODO: clean up
 * TODO: can remove this manager when we switch over to using endpoint for building schedules!
 * TODO: NEED TO CONNECT THIS WITH SCHEDULE HANDLER. THIS IS TOO FRAGMENTED
 */
public class LocationScheduleBuilderManager
{
    private final int NUM_DAYS_FOR_LOCATION_SCHEDULE = 3; //MUST be > 0

    //TODO: wrap these
    private final Set<Date> mRequestedDatesForScheduleSet = new HashSet<>(); //should never change
    private Map<Date, List<Booking>> mBookingDateMapForSchedule = new HashMap<>();
    LocationQuerySchedule mCachedLocationQuerySchedule;

    private final Bus mBus;
    public LocationScheduleBuilderManager(Bus bus)
    {
        mBus = bus;
        mBus.register(this);
    }

    //TODO: make sure no timing issues. make sure this isn't called while we're waiting for dates
    @Subscribe
    public void onRequestLocationSchedule(LocationEvent.RequestLocationSchedule event)
    {
//        clearScheduleBuilderData(); //actually don't clear out everything, we might just want updates
        mRequestedDatesForScheduleSet.clear();
        final List<Date> requestedDates = getDatesForSchedule();

        Map<Date, List<Booking>> newBookingDateMapForSchedule = new HashMap<>();
        for(Date d : requestedDates)
        {
            mRequestedDatesForScheduleSet.add(d);
            newBookingDateMapForSchedule.put(d, mBookingDateMapForSchedule.get(d));
            Log.i(getClass().getName(), "requested date: " + d);
        }
        mBookingDateMapForSchedule = newBookingDateMapForSchedule;
        //i want to easily get rid of the entries in the date booking map that aren't dates i requested here
        //but keep the ones that are of dates, for memory purposes.
        //probably not necessary. might remove this later.

        mBus.post(new HandyEvent.RequestScheduledBookingsBatch(requestedDates, true));
    }

    private List<Date> getDatesForSchedule()
    {
        final List<Date> requestedDates = new LinkedList<>();
        Date date = DateTimeUtils.getDateWithoutTime(new Date());
        requestedDates.add(date);
        Calendar calendar = Calendar.getInstance();
        for(int i = 1; i<NUM_DAYS_FOR_LOCATION_SCHEDULE; i++)
        {
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            date = DateTimeUtils.getDateWithoutTime(calendar.getTime());
            requestedDates.add(date);
        }

        return requestedDates;
    }

    private void clearScheduleBuilderData()
    {
        mRequestedDatesForScheduleSet.clear();
        mBookingDateMapForSchedule.clear();
        mCachedLocationQuerySchedule = null;
    }

    /**
     * receives a list of all the bookings for the dates requested
     *
     * @param event
     */
    @Subscribe
    public void onReceiveScheduledBookingsBatchSuccess(HandyEvent.ReceiveScheduledBookingsBatchSuccess event)
    {
        boolean responseHasUpdatedBookings = false;
        for(Date date : mRequestedDatesForScheduleSet)
        {
            List<Booking> bookingList = event.getDateToBookingMap().get(date);
            List<Booking> currentBookingListForDate = mBookingDateMapForSchedule.get(date);
            if (!bookingListStartEndDatesEqual(currentBookingListForDate, bookingList))
                //dates are different
            {
                Log.i(getClass().getName(), "got updated bookings! will need to update schedule");
                mBookingDateMapForSchedule.put(date, bookingList);
                responseHasUpdatedBookings = true;
            }
            else //dates are equal, don't need to reschedule
            {
                Log.i(getClass().getName(), "booking dates haven't changed. not updating schedule");
            }
        }

        if(responseHasUpdatedBookings || mCachedLocationQuerySchedule == null)
        {
            List<Booking> allBookings = new LinkedList<>();
            for(List<Booking> bookingList : mBookingDateMapForSchedule.values())
            {
                allBookings.addAll(bookingList);
            }
            //build a new schedule from these bookings
            mCachedLocationQuerySchedule = LocationScheduleFactory.getLocationScheduleFromBookings(allBookings);
        }

        //TODO: refine this
        mBus.post(new LocationEvent.ReceiveLocationSchedule(mCachedLocationQuerySchedule));
    }

    private boolean bookingListStartEndDatesEqual(List<Booking> bookingList1,
                                                  List<Booking> bookingList2)
    {
        if(bookingList1 == bookingList2) return true;
        if(bookingList1 == null || bookingList2 == null || bookingList1.size() != bookingList2.size()) return false;

        //they have the same size
        ListIterator<Booking> iterator1 = bookingList1.listIterator();
        ListIterator<Booking> iterator2 = bookingList2.listIterator();
        while(iterator1.hasNext())
        {
            Booking booking1 = iterator1.next();
            Booking booking2 = iterator2.next();
            if(!booking1.getStartDate().equals(booking2.getStartDate())
                    || !booking1.getEndDate().equals(booking2.getEndDate()))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * this event is fired from booking manager when ANY booking is updated! on job claim, removed, etc
     * @param event
     */
    @Subscribe
    public void onBookingChangedOrCreated(HandyEvent.BookingChangedOrCreated event)
    {
        //when this happens, we should rebuild the schedule
        //TODO: see if building schedule is costly. if so, note which bookings were invalidated and rebuild the schedule only for those bookings
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }
}
