package com.handy.portal.event;

import android.os.Bundle;

import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.booking.BookingCalendarDay;
import com.handy.portal.ui.fragment.MainActivityFragment;

import java.util.Map;

/**
 * Created by cdavis on 5/6/15.
 */
public class Event
{
    public boolean success;

    public static class BookingsRetrievedEvent extends Event
    {
        public Map<BookingCalendarDay, BookingSummary> bookingSummaries;

        public BookingsRetrievedEvent(Map<BookingCalendarDay, BookingSummary> bookingSummaries, boolean success)
        {
            this.bookingSummaries = bookingSummaries;
            this.success = success;
        }
    }

    public static class RequestAvailableBookingsEvent extends Event
    {
    }

    public static class UpdateCheckEvent extends Event
    {
        public int versionCode = 0;
        public String appFlavor = "";
        public UpdateCheckEvent(String appFlavor, int versionCode) {
            this.versionCode = versionCode;
            this.appFlavor = appFlavor;
        }
    }

    public static class UpdateCheckRequestReceivedEvent extends Event
    {
        public UpdateDetails updateDetails;
        public UpdateCheckRequestReceivedEvent(UpdateDetails updateDetails, boolean success) {
            this.updateDetails = updateDetails;
            this.success = success;
        }
    }

    public static class RequestScheduledBookingsEvent extends Event
    {
    }

    public static class RequestBookingDetailsEvent extends Event
    {
        public String bookingId;

        public RequestBookingDetailsEvent(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class BookingsDetailsRetrievedEvent extends Event
    {
        public Booking booking;

        public BookingsDetailsRetrievedEvent(Booking booking, boolean success)
        {
            this.booking = booking;
            this.success = success;
        }
    }

    public static class RequestPinCodeEvent extends Event
    {
        public String phoneNumber;
        public RequestPinCodeEvent(String phoneNumber)
        {
            this.phoneNumber = phoneNumber;
        }

    }

    public static class NavigateToTabEvent extends Event
    {
        public MainActivityFragment.MainViewTab targetTab;
        public Bundle arguments;

        public NavigateToTabEvent(MainActivityFragment.MainViewTab targetTab)
        {
            this.targetTab = targetTab;
        }

        public NavigateToTabEvent(MainActivityFragment.MainViewTab targetTab, Bundle arguments)
        {
            this.targetTab = targetTab;
            this.arguments = arguments;
        }
    }

    public static class PinCodeRequestReceivedEvent extends Event
    {
        public PinRequestDetails pinRequestDetails;
        public PinCodeRequestReceivedEvent(PinRequestDetails pinRequestDetails, boolean success)
        {
            this.pinRequestDetails = pinRequestDetails;
            this.success = success;
        }
    }

    public static class RequestLoginEvent extends Event
    {
        public String phoneNumber;
        public String pinCode;

        public RequestLoginEvent(String phoneNumber, String pinCode)
        {
            this.phoneNumber = phoneNumber;
            this.pinCode = pinCode;
        }
    }

    public static class LoginRequestReceivedEvent extends Event
    {
        public LoginDetails loginDetails;

        public LoginRequestReceivedEvent(LoginDetails loginDetails, boolean success)
        {
            this.loginDetails = loginDetails;
            this.success = success;
        }
    }
	
	public static class RequestClaimJobEvent extends Event
    {
        public String bookingId;

        public RequestClaimJobEvent(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class ClaimJobRequestReceivedEvent extends Event
    {
        public Booking booking;

        public ClaimJobRequestReceivedEvent(Booking booking, boolean success)
        {
            this.booking = booking;
            this.success = success;
        }
    }

	public static class LoginError extends Event {
        public String source = "";

        public LoginError(String source) {
            this.source = source;
        }
    }

    public static class Navigation extends Event {
        public String page = "";

        public Navigation(String page) {
            this.page = page;
        }
    }

}
