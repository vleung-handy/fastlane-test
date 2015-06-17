package com.handy.portal.event;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.annotations.Track;
import com.handy.portal.annotations.TrackField;
import com.handy.portal.consts.MainViewTab;
import com.handy.portal.consts.TransitionStyle;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.TermsDetails;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.data.DataManager;

import java.util.Date;
import java.util.List;

public abstract class Event
{
    public boolean success;
    public String errorMessage;

    public static class BookingsRetrievedEvent extends Event
    {
        public List<BookingSummary> bookingSummaries;

        public BookingsRetrievedEvent(List<BookingSummary> bookingSummaries, boolean success)
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

        public UpdateCheckEvent(String appFlavor, int versionCode)
        {
            this.versionCode = versionCode;
            this.appFlavor = appFlavor;
        }
    }

    public static class UpdateCheckRequestReceivedEvent extends Event
    {
        public UpdateDetails updateDetails;

        public UpdateCheckRequestReceivedEvent(UpdateDetails updateDetails, boolean success)
        {
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

    @Track("portal login submitted - phone number")
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
        public MainViewTab targetTab;
        public Bundle arguments;
        public TransitionStyle transitionStyleOverride;

        public NavigateToTabEvent(MainViewTab targetTab)
        {
            this.targetTab = targetTab;
        }

        public NavigateToTabEvent(MainViewTab targetTab, Bundle arguments)
        {
            this.targetTab = targetTab;
            this.arguments = arguments;
        }

        public NavigateToTabEvent(MainViewTab targetTab, Bundle arguments, TransitionStyle transitionStyleOverride)
        {
            this.targetTab = targetTab;
            this.arguments = arguments;
            this.transitionStyleOverride = transitionStyleOverride;
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

    @Track("portal login submitted - pin code")
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

        public ClaimJobRequestReceivedEvent(Booking booking, boolean success, String errorMessage)
        {
            this.booking = booking;
            this.success = success;
            this.errorMessage = errorMessage;
        }
    }

    @Track("portal login error")
    public static class LoginError extends Event
    {
        @TrackField("source")
        private String source;

        public LoginError(String source)
        {
            this.source = source;
        }
    }

    @Track("portal navigation")
    public static class Navigation extends Event
    {
        @TrackField("page")
        private String page;

        public Navigation(String page)
        {
            this.page = page;
        }
    }

    public static class SetLoadingOverlayVisibilityEvent extends Event
    {
        public boolean isVisible;
        public SetLoadingOverlayVisibilityEvent(boolean isVisible)
        {
            this.isVisible = isVisible;
        }
    }

    @Track("date scroller date selected")
    public static class DateClickedEvent extends Event
    {
        @TrackField("type")
        private String type;
        @TrackField("date")
        private Date date;

        public DateClickedEvent(String type, Date date)
        {
            this.type = type;
            this.date = date;
        }
    }

    @Track("booking detail selected")
    public static class BookingSelectedEvent extends Event
    {
        @TrackField("type")
        private String type;
        @TrackField("booking_id")
        private String bookingId;

        public BookingSelectedEvent(String type, String bookingId)
        {
            this.type = type;
            this.bookingId = bookingId;
        }
    }

    @Track("claim job")
    public static class ClaimJobSuccessEvent extends Event
    {
    }

    @Track("claim job error")
    public static class ClaimJobErrorEvent extends Event
    {
        @TrackField("message")
        private String message;

        public ClaimJobErrorEvent(String message)
        {
            this.message = message;
        }
    }

    public static class RequestAvailableBookingsErrorEvent
    {
        public final DataManager.DataManagerError error;

        public RequestAvailableBookingsErrorEvent(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class CheckTermsRequestEvent
    {
    }

    public static class CheckTermsResponseEvent
    {
        public final TermsDetails termsDetails;

        public CheckTermsResponseEvent(@NonNull TermsDetails termsDetails)
        {
            this.termsDetails = termsDetails;
        }
    }

    public static class CheckTermsErrorEvent
    {
    }

    @Track("portal use terms displayed")
    public static class TermsDisplayedEvent
    {
        @TrackField("terms code")
        private String code;

        public TermsDisplayedEvent(String code)
        {
            this.code = code;
        }
    }

    @Track("portal use terms accepted")
    public static class AcceptTermsEvent
    {
        @TrackField("terms code")
        private String code;

        public final TermsDetails termsDetails;

        public AcceptTermsEvent(TermsDetails termsDetails)
        {
            this.termsDetails = termsDetails;
            this.code = termsDetails.getCode();
        }
    }

    public static class AcceptTermsSuccessEvent
    {
    }

    @Track("portal use terms error")
    public static class AcceptTermsErrorEvent
    {
        @TrackField("terms code")
        private String code;
    }
}
