package com.handy.portal.event;

import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.BookingCalendarDay;

import java.util.Map;

/**
 * Created by cdavis on 5/6/15.
 */
public class RequestBookingDetailsEvent extends Event
{
  public String bookingId;
  public RequestBookingDetailsEvent(String bookingId)
  {
      this.bookingId = bookingId;
  }
}