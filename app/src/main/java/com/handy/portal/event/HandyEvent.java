package com.handy.portal.event;

import android.app.Activity;
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
import com.handy.portal.core.booking.Booking;
import com.handy.portal.data.DataManager;

import java.util.Date;
import java.util.List;

public abstract class HandyEvent
{
    public abstract static class RequestEvent extends HandyEvent
    {
    }

    public abstract static class RequestBookingActionEvent extends RequestEvent
    {
        public String bookingId;
    }

    public abstract static class ReceiveSuccessEvent extends HandyEvent
    {
    }

    public abstract static class ReceiveBookingSuccessEvent extends ReceiveSuccessEvent
    {
        public Booking booking;
    }

    public abstract static class ReceiveErrorEvent extends HandyEvent
    {
        public DataManager.DataManagerError error;
    }

    public abstract static class ApplicationLifeCycleEvent extends HandyEvent
    {
        public Activity sender;
    }

//Activity lifecycle management

    public static class ActivityPaused extends ApplicationLifeCycleEvent
    {
        public ActivityPaused(Activity sender)
        {
            this.sender = sender;
        }
    }

    public static class ActivityResumed extends ApplicationLifeCycleEvent
    {
        public ActivityResumed(Activity sender)
        {
            this.sender = sender;
        }
    }

    public static class ApplicationResumed extends ApplicationLifeCycleEvent
    {
        public ApplicationResumed(Activity sender)
        {
            this.sender = sender;
        }
    }

//Navigation

    public static class NavigateToTab extends HandyEvent
    {
        public MainViewTab targetTab;
        public Bundle arguments;
        public TransitionStyle transitionStyleOverride;

        public NavigateToTab(MainViewTab targetTab)
        {
            this.targetTab = targetTab;
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments)
        {
            this.targetTab = targetTab;
            this.arguments = arguments;
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments, TransitionStyle transitionStyleOverride)
        {
            this.targetTab = targetTab;
            this.arguments = arguments;
            this.transitionStyleOverride = transitionStyleOverride;
        }
    }

//Login

    @Track("portal login submitted - phone number")
    public static class RequestPinCode extends HandyEvent
    {
        public String phoneNumber;

        public RequestPinCode(String phoneNumber)
        {
            this.phoneNumber = phoneNumber;
        }
    }

    public static class PinCodeRequestSuccess extends HandyEvent
    {
        public PinRequestDetails pinRequestDetails;

        public PinCodeRequestSuccess(PinRequestDetails pinRequestDetails)
        {
            this.pinRequestDetails = pinRequestDetails;
        }
    }

    public static class PinCodeRequestError extends ReceiveErrorEvent
    {
        public PinCodeRequestError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    @Track("portal login submitted - pin code")
    public static class RequestLogin extends HandyEvent
    {
        public String phoneNumber;
        public String pinCode;

        public RequestLogin(String phoneNumber, String pinCode)
        {
            this.phoneNumber = phoneNumber;
            this.pinCode = pinCode;
        }
    }

    public static class LoginRequestSuccess extends HandyEvent
    {
        public LoginDetails loginDetails;

        public LoginRequestSuccess(LoginDetails loginDetails)
        {
            this.loginDetails = loginDetails;
        }
    }

    public static class LoginRequestError extends ReceiveErrorEvent
    {
        public LoginRequestError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


//Update and Version Management

    public static class RequestUpdateCheck extends RequestEvent
    {
        public Activity sender = null;

        public RequestUpdateCheck(Activity sender)
        {
            this.sender = sender;
        }
    }

    public static class ReceiveUpdateAvailableSuccess extends ReceiveSuccessEvent
    {
    }

    public static class ReceiveUpdateAvailableError extends ReceiveErrorEvent
    {
        public ReceiveUpdateAvailableError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class DownloadUpdateSuccessful extends HandyEvent
    {
    }

    @Track("portal app update download failed")
    public static class DownloadUpdateFailed extends HandyEvent
    {
    }

    public static class RequestCheckTerms extends RequestEvent
    {
    }

    public static class ReceiveCheckTermsSuccess extends ReceiveSuccessEvent
    {
        public final TermsDetails termsDetails;

        public ReceiveCheckTermsSuccess(@NonNull TermsDetails termsDetails)
        {
            this.termsDetails = termsDetails;
        }
    }

    public static class ReceiveCheckTermsError extends ReceiveErrorEvent
    {
    }

//Booking Lists

    public static class RequestAvailableBookings extends RequestEvent
    {
    }

    public static class RequestScheduledBookings extends RequestEvent
    {
    }

    public static abstract class ReceiveBookingsSuccess extends ReceiveSuccessEvent
    {
        public List<BookingSummary> bookingSummaries;
    }

    public static class ReceiveAvailableBookingsSuccess extends ReceiveBookingsSuccess
    {
        public ReceiveAvailableBookingsSuccess(List<BookingSummary> bookingSummaries)
        {
            this.bookingSummaries = bookingSummaries;
        }
    }

    public static class ReceiveScheduledBookingsSuccess extends ReceiveBookingsSuccess
    {
        public ReceiveScheduledBookingsSuccess(List<BookingSummary> bookingSummaries)
        {
            this.bookingSummaries = bookingSummaries;
        }
    }

    public static class ReceiveAvailableBookingsError extends ReceiveErrorEvent
    {
        public ReceiveAvailableBookingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ReceiveScheduledBookingsError extends ReceiveErrorEvent
    {
        public final DataManager.DataManagerError error;

        public ReceiveScheduledBookingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

//Booking Details

    public static class RequestBookingDetails extends HandyEvent
    {
        public String bookingId;

        public RequestBookingDetails(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class ReceiveBookingDetailsSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveBookingDetailsSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

    public static class ReceiveBookingDetailsError extends ReceiveErrorEvent
    {
        public ReceiveBookingDetailsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

//Job Action Requests

    public static class RequestClaimJob extends RequestBookingActionEvent
    {
        public RequestClaimJob(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class RequestRemoveJob extends RequestBookingActionEvent
    {
        public RequestRemoveJob(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class RequestNotifyJobOnMyWay extends RequestBookingActionEvent
    {
        public RequestNotifyJobOnMyWay(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class RequestNotifyJobCheckIn extends RequestBookingActionEvent
    {
        public RequestNotifyJobCheckIn(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class RequestNotifyJobCheckOut extends RequestBookingActionEvent
    {
        public RequestNotifyJobCheckOut(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }

    public static class RequestNotifyJobUpdateArrivalTime extends RequestBookingActionEvent
    {
        public Booking.ArrivalTimeOption arrivalTimeOption;

        public RequestNotifyJobUpdateArrivalTime(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption)
        {
            this.bookingId = bookingId;
            this.arrivalTimeOption = arrivalTimeOption;
        }
    }

//Job Action Receive Successes

    @Track("claim job")
    public static class ReceiveClaimJobSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveClaimJobSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

    @Track("remove job")
    public static class ReceiveRemoveJobSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveRemoveJobSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

    @Track("on my way")
    public static class ReceiveNotifyJobOnMyWaySuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobOnMyWaySuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

    @Track("check in")
    public static class ReceiveNotifyJobCheckInSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobCheckInSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

    @Track("check out")
    public static class ReceiveNotifyJobCheckoutSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobCheckoutSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

    @Track("eta")
    public static class ReceiveNotifyJobUpdateArrivalTimeSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobUpdateArrivalTimeSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }


//Job Action Receive Errors

    public static class ReceiveClaimJobError extends ReceiveErrorEvent
    {
        public ReceiveClaimJobError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ReceiveRemoveJobError extends ReceiveErrorEvent
    {
        public ReceiveRemoveJobError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ReceiveNotifyJobOnMyWayError extends ReceiveErrorEvent
    {
        public ReceiveNotifyJobOnMyWayError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ReceiveNotifyJobCheckInError extends ReceiveErrorEvent
    {
        public ReceiveNotifyJobCheckInError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ReceiveNotifyJobCheckoutError extends ReceiveErrorEvent
    {
        public ReceiveNotifyJobCheckoutError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ReceiveNotifyJobUpdateArrivalTimeError extends ReceiveErrorEvent
    {
        public ReceiveNotifyJobUpdateArrivalTimeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


//Pure analytics events,
//TODO: when possible these should track the actual events instead of having duplicate unnecessary and get rid of duped analytics events

    public abstract static class AnalyticsEvents extends HandyEvent
    {
    }

    @Track("portal login error")
    public static class LoginError extends AnalyticsEvents
    {
        @TrackField("source")
        private String source;

        public LoginError(String source)
        {
            this.source = source;
        }
    }

    @Track("portal navigation")
    public static class Navigation extends AnalyticsEvents
    {
        @TrackField("page")
        private String page;

        public Navigation(String page)
        {
            this.page = page;
        }
    }

    @Track("date scroller date selected")
    public static class DateClicked extends AnalyticsEvents
    {
        @TrackField("type")
        private String type;
        @TrackField("date")
        private Date date;

        public DateClicked(String type, Date date)
        {
            this.type = type;
            this.date = date;
        }
    }

    @Track("booking detail selected")
    public static class BookingSelected extends AnalyticsEvents
    {
        @TrackField("type")
        private String type;
        @TrackField("booking_id")
        private String bookingId;

        public BookingSelected(String type, String bookingId)
        {
            this.type = type;
            this.bookingId = bookingId;
        }
    }

    @Track("claim job error")
    public static class ClaimJobError extends AnalyticsEvents
    {
        @TrackField("message")
        private String message;

        public ClaimJobError(String message)
        {
            this.message = message;
        }
    }

    @Track("remove job error")
    public static class RemoveJobError extends AnalyticsEvents
    {
        @TrackField("message")
        private String message;

        public RemoveJobError(String message)
        {
            this.message = message;
        }
    }

    @Track("portal use terms displayed")
    public static class TermsDisplayed extends AnalyticsEvents
    {
        @TrackField("terms code")
        private String code;

        public TermsDisplayed(String code)
        {
            this.code = code;
        }
    }

    @Track("portal use terms accepted")
    public static class AcceptTerms extends AnalyticsEvents
    {
        @TrackField("terms code")
        private String code;

        public final TermsDetails termsDetails;

        public AcceptTerms(TermsDetails termsDetails)
        {
            this.termsDetails = termsDetails;
            this.code = termsDetails.getCode();
        }
    }

    @Track("portal use terms error")
    public static class AcceptTermsError extends AnalyticsEvents
    {
        @TrackField("terms code")
        private String code;
    }

//Unclassified events - events should go below here by default unless they fit another category

    public static class SetLoadingOverlayVisibility extends HandyEvent
    {
        public boolean isVisible;

        public SetLoadingOverlayVisibility(boolean isVisible)
        {
            this.isVisible = isVisible;
        }
    }

    public static class AcceptTermsSuccess extends HandyEvent
    {
    }

}
