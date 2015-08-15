package com.handy.portal.event;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.annotation.Track;
import com.handy.portal.annotation.TrackField;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.data.DataManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.Action;
import com.handy.portal.model.HelpNode;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.Provider;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.model.UpdateDetails;

import java.util.Date;
import java.util.List;

import retrofit.mime.TypedInput;

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

    public static class ReceivePinCodeSuccess extends ReceiveSuccessEvent
    {
        public PinRequestDetails pinRequestDetails;

        public ReceivePinCodeSuccess(PinRequestDetails pinRequestDetails)
        {
            this.pinRequestDetails = pinRequestDetails;
        }
    }

    public static class ReceivePinCodeError extends ReceiveErrorEvent
    {
        public ReceivePinCodeError(DataManager.DataManagerError error)
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

    public static class ReceiveLoginSuccess extends ReceiveSuccessEvent
    {
        public LoginDetails loginDetails;

        public ReceiveLoginSuccess(LoginDetails loginDetails)
        {
            this.loginDetails = loginDetails;
        }
    }

    public static class ReceiveLoginError extends ReceiveErrorEvent
    {
        public ReceiveLoginError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestProviderInfo extends RequestEvent
    {
    }

    public static class ReceiveProviderInfoSuccess extends ReceiveSuccessEvent
    {
        public final Provider provider;

        public ReceiveProviderInfoSuccess(Provider provider)
        {
            this.provider = provider;
        }
    }

    public static class ReceiveProviderInfoError extends ReceiveErrorEvent
    {
        public ReceiveProviderInfoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ProviderIdUpdated extends HandyEvent
    {
        public final String providerId;

        public ProviderIdUpdated(String providerId)
        {
            this.providerId = providerId;
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
        public UpdateDetails updateDetails;

        public ReceiveUpdateAvailableSuccess(UpdateDetails updateDetails)
        {
            this.updateDetails = updateDetails;
        }
    }

    public static class ReceiveUpdateAvailableError extends ReceiveErrorEvent
    {
        public ReceiveUpdateAvailableError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestEnableApplication extends RequestEvent
    {
        public String packageName;
        public String infoMessage;

        public RequestEnableApplication(String packageName, String infoMessage)
        {
            this.packageName = packageName;
            this.infoMessage = infoMessage;
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
        public final List<Date> dates;

        public RequestAvailableBookings(List<Date> dates)
        {
            this.dates = dates;
        }
    }

    public static class RequestScheduledBookings extends RequestEvent
    {
        public final List<Date> dates;

        public RequestScheduledBookings(List<Date> dates)
        {
            this.dates = dates;
        }
    }

    public static abstract class ReceiveBookingsSuccess extends ReceiveSuccessEvent
    {
        public List<Booking> bookings;
        public Date day;
    }

    public static class ReceiveAvailableBookingsSuccess extends ReceiveBookingsSuccess
    {
        public ReceiveAvailableBookingsSuccess(List<Booking> bookings, Date day)
        {
            this.bookings = bookings;
            this.day = day;
        }
    }

    public static class ReceiveScheduledBookingsSuccess extends ReceiveBookingsSuccess
    {
        public ReceiveScheduledBookingsSuccess(List<Booking> bookings, Date day)
        {
            this.bookings = bookings;
            this.day = day;
        }
    }

    public static class ReceiveBookingsError extends ReceiveErrorEvent
    {
        public List<Date> days;
    }

    public static class ReceiveAvailableBookingsError extends ReceiveBookingsError
    {
        public ReceiveAvailableBookingsError(DataManager.DataManagerError error, List<Date> days)
        {
            this.days = days;
            this.error = error;
        }
    }

    public static class ReceiveScheduledBookingsError extends ReceiveBookingsError
    {
        public ReceiveScheduledBookingsError(DataManager.DataManagerError error, List<Date> days)
        {
            this.days = days;
            this.error = error;
        }
    }

//Booking Details

    public static class RequestBookingDetails extends HandyEvent
    {
        public String bookingId;
        public Date date;

        public RequestBookingDetails(String bookingId, Date date)
        {
            this.bookingId = bookingId;
            this.date = date;
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
        public final Booking booking;
        public RequestClaimJob(Booking booking)
        {
            this.bookingId = booking.getId();
            this.booking = booking;
        }
    }

    @Track("cancel claim confirmation accepted")
    public static class RequestRemoveJob extends RequestBookingActionEvent
    {
        public final Booking booking;
        public RequestRemoveJob(Booking booking)
        {
            this.bookingId = booking.getId();
            this.booking = booking;
        }
    }

    @Track("on my way submitted")
    public static class RequestNotifyJobOnMyWay extends RequestBookingActionEvent
    {
        public LocationData locationData;
        public RequestNotifyJobOnMyWay(String bookingId, LocationData locationData)
        {
            this.bookingId = bookingId;
            this.locationData = locationData;
        }
    }

    @Track("provider checkin submitted")
    public static class RequestNotifyJobCheckIn extends RequestBookingActionEvent
    {
        public LocationData locationData;
        public RequestNotifyJobCheckIn(String bookingId, LocationData locationData)
        {
            this.bookingId = bookingId;
            this.locationData = locationData;
        }
    }

    @Track("provider checkout submitted")
    public static class RequestNotifyJobCheckOut extends RequestBookingActionEvent
    {
        public LocationData locationData;
        public RequestNotifyJobCheckOut(String bookingId, LocationData locationData)
        {
            this.bookingId = bookingId;
            this.locationData = locationData;
        }
    }

    @Track("self reported late submitted")
    public static class RequestNotifyJobUpdateArrivalTime extends RequestBookingActionEvent
    {
        @TrackField("time_submitted")
        public Booking.ArrivalTimeOption arrivalTimeOption;

        public RequestNotifyJobUpdateArrivalTime(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption)
        {
            this.bookingId = bookingId;
            this.arrivalTimeOption = arrivalTimeOption;
        }
    }

//Job Action Receive Successes

    @Track("eta")
    public static class ReceiveNotifyJobUpdateArrivalTimeSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobUpdateArrivalTimeSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

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

    // Customer No Show Events

    @Track("report customer no show")
    public static class RequestReportNoShow extends RequestEvent
    {
        public final String bookingId;
        public final LocationData locationData;

        public RequestReportNoShow(String bookingId, LocationData locationData)
        {
            this.bookingId = bookingId;
            this.locationData = locationData;
        }
    }

    public static class ReceiveReportNoShowSuccess extends ReceiveSuccessEvent
    {
        public final Booking booking;

        public ReceiveReportNoShowSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

    public static class ReceiveReportNoShowError extends ReceiveErrorEvent
    {
        public ReceiveReportNoShowError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    //Help Node
    public static class RequestHelpNode extends HandyEvent
    {
        public String nodeId;
        public String bookingId;

        public RequestHelpNode(String nodeId, String bookingId)
        {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }

    public static class ReceiveHelpNodeSuccess extends ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }

    public static class ReceiveHelpNodeError extends ReceiveErrorEvent
    {
        public ReceiveHelpNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    //Help Booking Node
    public static class RequestHelpBookingNode extends HandyEvent
    {
        public String nodeId;
        public String bookingId;

        public RequestHelpBookingNode(String nodeId, String bookingId)
        {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }

    public static class ReceiveHelpBookingNodeSuccess extends ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpBookingNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }

    public static class ReceiveHelpBookingNodeError extends ReceiveErrorEvent
    {
        public ReceiveHelpBookingNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    //Help Contact Message
    @Track("pro help contact form submitted")
    public static class RequestNotifyHelpContact extends HandyEvent
    {
        public TypedInput body;

        public RequestNotifyHelpContact(TypedInput body)
        {
            this.body = body;
        }
    }

    public static class ReceiveNotifyHelpContactSuccess extends ReceiveSuccessEvent
    {
        public ReceiveNotifyHelpContactSuccess()
        {
        }
    }

    public static class ReceiveNotifyHelpContactError extends ReceiveErrorEvent
    {
        public ReceiveNotifyHelpContactError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    @Track("cancel customer no show")
    public static class RequestCancelNoShow extends RequestEvent
    {
        public final String bookingId;
        public final LocationData locationData;

        public RequestCancelNoShow(String bookingId, LocationData locationData)
        {
            this.bookingId = bookingId;
            this.locationData = locationData;
        }
    }

    public static class ReceiveCancelNoShowSuccess extends ReceiveSuccessEvent
    {
        public final Booking booking;

        public ReceiveCancelNoShowSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }

    public static class ReceiveCancelNoShowError extends ReceiveErrorEvent
    {
        public ReceiveCancelNoShowError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

//Pure analytics events,
//TODO: when possible these should track the actual events instead of having duplicate unnecessary and get rid of duped analytics events

    public abstract static class AnalyticsEvent extends HandyEvent
    {
    }

    @Track("portal login error")
    public static class LoginError extends AnalyticsEvent
    {
        @TrackField("source")
        private String source;

        public LoginError(String source)
        {
            this.source = source;
        }
    }

    @Track("portal navigation")
    public static class Navigation extends AnalyticsEvent
    {
        @TrackField("page")
        private String page;

        public Navigation(String page)
        {
            this.page = page;
        }
    }

    @Track("date scroller date selected")
    public static class DateClicked extends AnalyticsEvent
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
    public static class BookingSelected extends AnalyticsEvent
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
    public static class ClaimJobError extends AnalyticsEvent
    {
        @TrackField("message")
        private String message;

        public ClaimJobError(String message)
        {
            this.message = message;
        }
    }

    @Track("remove job error")
    public static class RemoveJobError extends AnalyticsEvent
    {
        @TrackField("message")
        private String message;

        public RemoveJobError(String message)
        {
            this.message = message;
        }
    }

    @Track("portal use terms displayed")
    public static class TermsDisplayed extends AnalyticsEvent
    {
        @TrackField("terms code")
        private String code;

        public TermsDisplayed(String code)
        {
            this.code = code;
        }
    }

    @Track("portal use terms accepted")
    public static class AcceptTerms extends AnalyticsEvent
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

    @Track("action triggered")
    public static class ActionTriggered extends AnalyticsEvent
    {
        @TrackField("action name")
        private String actionName;

        public ActionTriggered(BookingActionButtonType actionType)
        {
            this.actionName = actionType.getActionName();
        }
    }

    @Track("warning dialog accepted")
    public static class ActionWarningAccepted extends AnalyticsEvent
    {
        @TrackField("action name")
        private String actionName;

        public ActionWarningAccepted(BookingActionButtonType actionType)
        {
            this.actionName = actionType.getActionName();
        }

        public ActionWarningAccepted(Action action)
        {
            this.actionName = action.getActionName();
        }
    }

    @Track("portal use terms error")
    public static class AcceptTermsError extends AnalyticsEvent
    {
    }

    @Track("sms customer clicked")
    public static class TextCustomerClicked extends AnalyticsEvent
    {
    }

    @Track("call customer clicked")
    public static class CallCustomerClicked extends AnalyticsEvent
    {
    }

    @Track("cancel claim confirmation shown")
    public static class ShowConfirmationRemoveJob extends AnalyticsEvent
    {
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

    @Track("support action triggered")
    public static class SupportActionTriggered
    {
        public final Action action;
        @TrackField("action name")
        private String actionName;

        public SupportActionTriggered(@NonNull Action action)
        {
            this.action = action;
            this.actionName = action.getActionName();
        }
    }

    public static class RequestComplementaryBookings extends RequestBookingActionEvent
    {
        public final Booking booking;
        public RequestComplementaryBookings(Booking booking)
        {
            this.booking = booking;
        }
    }

    public static class ReceiveComplementaryBookingsSuccess
    {
        public final List<Booking> bookings;

        public ReceiveComplementaryBookingsSuccess(List<Booking> bookings)
        {
            this.bookings = bookings;
        }
    }

    public static class ReceiveComplementaryBookingsError extends ReceiveErrorEvent
    {
    }

    //Request that Urban Airship takes off
    public static class StartUrbanAirship extends HandyEvent
    {
    }

    public static class UpdateMainActivityFragmentActive extends HandyEvent
    {
        public boolean active;
        public UpdateMainActivityFragmentActive(boolean active)
        {
            this.active = active;
        }
    }

}
