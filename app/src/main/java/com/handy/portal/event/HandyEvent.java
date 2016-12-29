package com.handy.portal.event;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.bookings.constant.BookingActionButtonType;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.Booking.Action;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.onboarding.model.claim.JobClaimResponse;

import java.util.Date;
import java.util.List;

public abstract class HandyEvent
{
    public abstract static class RequestEvent extends HandyEvent {}


    public abstract static class ReceiveSuccessEvent extends HandyEvent {}


    public abstract static class ReceiveErrorEvent extends HandyEvent
    {
        public DataManager.DataManagerError error;
    }


    public abstract static class RequestBookingActionEvent extends RequestEvent
    {
        public String bookingId;
    }


    public abstract static class ReceiveBookingSuccessEvent extends ReceiveSuccessEvent
    {
        public Booking booking;
    }


    public abstract static class ApplicationLifeCycleEvent extends HandyEvent
    {
        public Activity sender;
    }


    //Config
    public static class ReceiveConfigurationSuccess extends HandyEvent
    {
        private final ConfigurationResponse mConfigurationResponse;

        public ReceiveConfigurationSuccess(ConfigurationResponse configurationResponse)
        {
            mConfigurationResponse = configurationResponse;
        }

        public ConfigurationResponse getConfigurationResponse()
        {
            return mConfigurationResponse;
        }
    }


    public static class ReceiveConfigurationError extends ReceiveErrorEvent
    {
        public ReceiveConfigurationError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

//Login


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
        public SuccessWrapper pinRequestDetails;

        public ReceivePinCodeSuccess(SuccessWrapper pinRequestDetails)
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


    //fired when the user has been logged out
    public static class UserLoggedOut extends HandyEvent
    {

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


    public static class RequestSendIncomeVerification extends RequestEvent {}


    public static class ReceiveSendIncomeVerificationSuccess extends ReceiveSuccessEvent {}


    public static class ReceiveSendIncomeVerificationError extends ReceiveErrorEvent {}


    public static class ProviderIdUpdated extends HandyEvent
    {
        public final String providerId;

        public ProviderIdUpdated(String providerId)
        {
            this.providerId = providerId;
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


    public static abstract class ReceiveBookingsSuccess extends ReceiveSuccessEvent
    {
        public BookingsWrapper bookingsWrapper;
        public Date day;
    }


    public static class ReceiveAvailableBookingsSuccess extends ReceiveBookingsSuccess
    {
        public ReceiveAvailableBookingsSuccess(BookingsWrapper bookingsWrapper, Date day)
        {
            this.bookingsWrapper = bookingsWrapper;
            this.day = day;
        }
    }


    public static class ReceiveOnboardingJobsSuccess extends ReceiveSuccessEvent
    {
        private BookingsListWrapper mBookingsListWrapper;

        public ReceiveOnboardingJobsSuccess(final BookingsListWrapper bookingsListWrapper)
        {
            mBookingsListWrapper = bookingsListWrapper;
        }

        public BookingsListWrapper getBookingsListWrapper()
        {
            return mBookingsListWrapper;
        }
    }


    public static class ReceiveScheduledBookingsSuccess extends ReceiveBookingsSuccess
    {
        public ReceiveScheduledBookingsSuccess(BookingsWrapper bookingsWrapper, Date day)
        {
            this.bookingsWrapper = bookingsWrapper;
            this.day = day;
        }
    }


    /*
        TODO: the above ReceiveScheduledBookingsSuccess event should be renamed
        so that this event can have naming parity with its request class

        this complements the original request event.

        this is required because some components need to get notified
        (just once, which is why we can't use the above event)
        that the original request was responded to
     */
    public static class ReceiveScheduledBookingsBatchSuccess extends ReceiveSuccessEvent
    {
        //currently don't care about holding data. add if needed
    }


    public static class ReceiveBookingsError extends ReceiveErrorEvent
    {
        public List<Date> days;
    }


    public static class ReceiveAvailableBookingsError extends ReceiveBookingsError
    {
        public ReceiveAvailableBookingsError(@Nullable DataManager.DataManagerError error, List<Date> days)
        {
            this.days = days;
            this.error = error;
        }
    }


    public static class ReceiveOnboardingJobsError extends ReceiveErrorEvent
    {
        public ReceiveOnboardingJobsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class ReceiveScheduledBookingsError extends ReceiveBookingsError
    {
        public ReceiveScheduledBookingsError(@Nullable DataManager.DataManagerError error, List<Date> days)
        {
            this.days = days;
            this.error = error;
        }
    }

//Booking Details


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
        public final String source;
        public Bundle sourceExtras;

        public RequestClaimJob(Booking booking, String source, @Nullable Bundle sourceExtras)
        {
            this.bookingId = booking.getId();
            this.booking = booking;
            this.source = source;
            this.sourceExtras = sourceExtras;
        }
    }


    public static class ReceiveDismissJobSuccess extends ReceiveSuccessEvent
    {
        private Booking mBooking;

        public ReceiveDismissJobSuccess(final Booking booking)
        {
            mBooking = booking;
        }

        public Booking getBooking()
        {
            return mBooking;
        }
    }


    public static class ReceiveDismissJobError extends ReceiveErrorEvent
    {
        private Booking mBooking;

        public ReceiveDismissJobError(final Booking booking,
                                      final DataManager.DataManagerError error)
        {
            mBooking = booking;
            this.error = error;
        }

        public Booking getBooking()
        {
            return mBooking;
        }
    }

//Job Action Receive Successes


    public static class ReceiveNotifyJobUpdateArrivalTimeSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobUpdateArrivalTimeSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }


    public static class ReceiveClaimJobSuccess extends ReceiveSuccessEvent
    {
        public Booking originalBooking;
        public String source;
        public BookingClaimDetails bookingClaimDetails;

        public ReceiveClaimJobSuccess(Booking originalBooking, BookingClaimDetails bookingClaimDetails, String source)
        {
            this.originalBooking = originalBooking;
            this.bookingClaimDetails = bookingClaimDetails;
            this.source = source;
        }
    }


    public static class ReceiveClaimJobsSuccess extends ReceiveSuccessEvent
    {
        private JobClaimResponse mJobClaimResponse;

        public ReceiveClaimJobsSuccess(JobClaimResponse jobClaimResponse)
        {
            mJobClaimResponse = jobClaimResponse;
        }

        public JobClaimResponse getJobClaimResponse()
        {
            return mJobClaimResponse;
        }
    }


    public static class ReceiveRemoveJobSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveRemoveJobSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }


    public static class ReceiveNotifyJobOnMyWaySuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobOnMyWaySuccess(Booking booking)
        {
            this.booking = booking;
        }
    }


    public static class ReceiveNotifyJobCheckInSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobCheckInSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }


    public static class ReceiveNotifyJobCheckOutSuccess extends ReceiveBookingSuccessEvent
    {
        public ReceiveNotifyJobCheckOutSuccess(Booking booking)
        {
            this.booking = booking;
        }
    }


    //Job Action Receive Errors
    public static class ReceiveClaimJobError extends ReceiveErrorEvent
    {
        private Booking mBooking;
        private String mSource;

        public ReceiveClaimJobError(Booking booking, String source, DataManager.DataManagerError error)
        {
            mBooking = booking;
            mSource = source;
            this.error = error;
        }

        public String getSource()
        {
            return mSource;
        }

        public Booking getBooking()
        {
            return mBooking;
        }
    }


    public static class ReceiveClaimJobsError extends ReceiveErrorEvent
    {
        public ReceiveClaimJobsError(DataManager.DataManagerError error)
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


    public static class ReceiveNotifyJobCheckOutError extends ReceiveErrorEvent
    {
        public ReceiveNotifyJobCheckOutError(DataManager.DataManagerError error)
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


    public static class LoginError extends AnalyticsEvent
    {
        private String source;

        public LoginError(String source)
        {
            this.source = source;
        }
    }


    public static class Navigation extends AnalyticsEvent
    {
        private String page;

        public Navigation(String page)
        {
            this.page = page;
        }
    }


    public static class DateClicked extends AnalyticsEvent
    {
        private String type;
        private Date date;

        public DateClicked(String type, Date date)
        {
            this.type = type;
            this.date = date;
        }
    }


    public static class BookingSelected extends AnalyticsEvent
    {
        private String type;
        private String bookingId;

        public BookingSelected(String type, String bookingId)
        {
            this.type = type;
            this.bookingId = bookingId;
        }
    }


    public static class ClaimJobError extends AnalyticsEvent
    {
        private String message;

        public ClaimJobError(String message)
        {
            this.message = message;
        }
    }


    public static class RemoveJobError extends AnalyticsEvent
    {
        private String message;

        public RemoveJobError(String message)
        {
            this.message = message;
        }
    }


    public static class TermsDisplayed extends AnalyticsEvent
    {
        private String code;

        public TermsDisplayed(String code)
        {
            this.code = code;
        }
    }


    public static class AcceptTerms extends AnalyticsEvent
    {
        private String code;

        public final TermsDetails termsDetails;

        public AcceptTerms(TermsDetails termsDetails)
        {
            this.termsDetails = termsDetails;
            this.code = termsDetails.getCode();
        }
    }


    public static class ActionTriggered extends AnalyticsEvent
    {
        private String actionName;

        public ActionTriggered(BookingActionButtonType actionType)
        {
            this.actionName = actionType.getActionName();
        }
    }


    public static class ActionWarningAccepted extends AnalyticsEvent
    {
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


    public static class AcceptTermsError extends AnalyticsEvent
    {
    }


    public static class TextCustomerClicked extends AnalyticsEvent
    {
    }


    public static class CallCustomerClicked extends AnalyticsEvent
    {
    }


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
        private String termsCode;

        public AcceptTermsSuccess(String termsCode)
        {
            this.termsCode = termsCode;
        }

        public String getTermsCode()
        {
            return termsCode;
        }
    }


    public static class SupportActionTriggered
    {
        public final Action action;
        private String actionName;

        public SupportActionTriggered(@NonNull Action action)
        {
            this.action = action;
            this.actionName = action.getActionName();
        }
    }


    public static class RequestOnboardingSupplies extends RequestEvent
    {
        private final boolean mOptIn;

        public RequestOnboardingSupplies(final boolean optIn)
        {
            mOptIn = optIn;
        }

        public boolean getOptIn()
        {
            return mOptIn;
        }
    }


    public static class ReceiveOnboardingSuppliesSuccess extends ReceiveSuccessEvent {}


    public static class ReceiveOnboardingSuppliesError extends ReceiveErrorEvent
    {
        public ReceiveOnboardingSuppliesError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Request that Urban Airship takes off
    public static class StartUrbanAirship extends HandyEvent {}


    // Pro should be logged out. Error won't be shown but this will allow us to sync our
    // tracking with iOS.
    public static class LogOutProvider extends HandyEvent {}


    public static class StepCompleted extends HandyEvent
    {
        private final int mStepId;

        public StepCompleted(final int stepId)
        {
            mStepId = stepId;
        }

        public int getStepId()
        {
            return mStepId;
        }
    }
}
