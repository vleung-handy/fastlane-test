package com.handy.portal.logger.handylogger;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.constant.LocationKey;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.BasicLog;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.logger.handylogger.model.EventLog;
import com.handy.portal.logger.handylogger.model.HelpContactFormSubmittedLog;
import com.handy.portal.logger.handylogger.model.NearbyJobsLog;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.logger.handylogger.model.PerformanceLog;
import com.handy.portal.logger.handylogger.model.ProfileLog;
import com.handy.portal.logger.handylogger.model.PushNotificationLog;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.logger.handylogger.model.WebOnboardingLog;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.OnboardingParams;
import com.handy.portal.model.Provider;
import com.handy.portal.model.logs.VideoLog;
import com.handy.portal.util.MathUtils;
import com.urbanairship.push.PushMessage;

import java.util.Date;

public class EventLogFactory
{
    private ProviderManager mProviderManager;

    public EventLogFactory(ProviderManager providerManager)
    {
        mProviderManager = providerManager;
    }

    // Basic Logs
    public EventLog createAppOpenLog()
    {
        return new BasicLog.Open();
    }

    public EventLog createNavigationLog(String tabName)
    {
        return new BasicLog.Navigation(tabName);
    }

    // Nearby Bookings Logs
    public EventLog createNearbyJobsLaunchedLog(int numOfJobs)
    {
        return new NearbyJobsLog.Shown(numOfJobs);
    }

    public EventLog createPinSelectedLog()
    {
        return new NearbyJobsLog.PinSelected();
    }

    public EventLog createNearbyJobClaimSelectedLog(Booking booking, double distanceInKilometer)
    {
        String bookingId = booking.getId();
        float paymentAmount = booking.getPaymentToProvider().getAdjustedAmount();

        return new NearbyJobsLog.ClaimJobSelected(bookingId, distanceInKilometer, paymentAmount);
    }

    public EventLog createNearbyJobClaimSuccessLog(Booking booking, double distanceInKilometer)
    {
        String bookingId = booking.getId();
        float paymentAmount = booking.getPaymentToProvider().getAdjustedAmount();

        return new NearbyJobsLog.ClaimJobSuccess(bookingId,
                distanceInKilometer, paymentAmount);
    }

    // Available Booking Logs
    public EventLog createAvailableJobDateClickedLog(Date date, int jobCount)
    {
        return new AvailableJobsLog.DateClicked(date, jobCount);
    }

    public EventLog createAvailableJobClickedLog(@NonNull Booking booking, int listNumber)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new AvailableJobsLog.Clicked(bookingId,
                serviceId, regionId, zipCode, requested, dateStart, listNumber);
    }

    public EventLog createAvailableJobClaimSuccessLog(
            Booking booking,
            String source,
            @Nullable Bundle sourceExtras
    )
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();
        int frequency = booking.getFrequency();

        return new AvailableJobsLog.ClaimSuccess(
                bookingId, serviceId, regionId, zipCode, requested, dateStart, frequency, source,
                sourceExtras
        );
    }

    public EventLog createAvailableJobClaimErrorLog(Booking booking, String source)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();
        int frequency = booking.getFrequency();

        return new AvailableJobsLog.ClaimError(bookingId,
                serviceId, regionId, zipCode, requested, dateStart, frequency, source);
    }

    // Scheduled Booking Logs
    public EventLog createScheduledJobDateClickedLog(Date date, int jobCount)
    {
        return new ScheduledJobsLog.DateClicked(date, jobCount);
    }

    public EventLog createScheduledJobClickedLog(@NonNull Booking booking, int listNumber)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new ScheduledJobsLog.Clicked(bookingId,
                serviceId, regionId, zipCode, requested, dateStart, listNumber);
    }

    public EventLog createRemoveJobClickedLog(Booking booking, String warning)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new ScheduledJobsLog.RemoveJobClicked(bookingId, serviceId, regionId, zipCode, requested, dateStart, warning);
    }

    public EventLog createRemoveJobConfirmedLog(Booking booking, String warning)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new ScheduledJobsLog.RemoveJobConfirmed(bookingId, serviceId, regionId, zipCode, requested, dateStart, warning);
    }

    public EventLog createRemoveJobErrorLog(Booking booking)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new ScheduledJobsLog.RemoveJobError(bookingId, serviceId, regionId, zipCode, requested, dateStart);
    }

    public EventLog createOnMyWayLog(@NonNull Booking booking, LocationData location)
    {
        String bookingId = booking.getId();
        double proLatitude = getLatitude(location);
        double proLongitude = getLongitude(location);
        double accuracy = getAccuracy(location);
        double bookingLatitude = getLatitude(booking.getAddress());
        double bookingLongitude = getLongitude(booking.getAddress());
        double distance = MathUtils.getDistance(proLatitude, proLatitude, bookingLatitude, bookingLongitude);

        return new CheckInFlowLog.OnMyWay(bookingId,
                proLatitude, proLongitude, bookingLatitude, bookingLongitude, accuracy, distance);
    }

    public EventLog createCheckInLog(@NonNull Booking booking, LocationData location)
    {
        String bookingId = booking.getId();
        double proLatitude = getLatitude(location);
        double proLongitude = getLongitude(location);
        double accuracy = getAccuracy(location);
        double bookingLatitude = getLatitude(booking.getAddress());
        double bookingLongitude = getLongitude(booking.getAddress());
        double distance = MathUtils.getDistance(proLatitude, proLatitude, bookingLatitude, bookingLongitude);

        return new CheckInFlowLog.CheckIn(bookingId,
                proLatitude, proLongitude, bookingLatitude,
                bookingLongitude, accuracy, distance);
    }

    public EventLog createCheckOutLog(@NonNull Booking booking, LocationData location)
    {
        String bookingId = booking.getId();
        double proLatitude = getLatitude(location);
        double proLongitude = getLongitude(location);
        double accuracy = getAccuracy(location);
        double bookingLatitude = getLatitude(booking.getAddress());
        double bookingLongitude = getLongitude(booking.getAddress());
        double distance = MathUtils.getDistance(proLatitude, proLatitude, bookingLatitude, bookingLongitude);

        return new CheckInFlowLog.CheckOut(bookingId,
                proLatitude, proLongitude, bookingLatitude, bookingLongitude, accuracy, distance);
    }

    public EventLog createCustomerRatingShownLog()
    {
        return new ScheduledJobsLog.CustomerRatingShown();
    }

    public EventLog createCustomerRatingSubmittedLog(int rating)
    {
        return new ScheduledJobsLog.CustomerRatingSubmitted(rating);
    }

    public EventLog createBookingInstructionsSeenLog(@NonNull Booking booking)
    {
        String bookingId = booking.getId();
        return new ScheduledJobsLog.BookingInstructionsSeen(bookingId);
    }

    public EventLog createSupportSelectedLog(@NonNull Booking booking)
    {
        String bookingId = booking.getId();
        return new ScheduledJobsLog.SupportSelected(bookingId);
    }

    public EventLog createHelpItemSelectedLog(@NonNull Booking booking, String helpItemLabel)
    {
        String bookingId = booking.getId();
        return new ScheduledJobsLog.HelpItemSelected(bookingId, helpItemLabel);
    }

    public EventLog createRemoveConfirmationShownLog(@NonNull Booking booking, String removalType)
    {
        String bookingId = booking.getId();
        return new ScheduledJobsLog.RemoveConfirmationShown(bookingId, removalType);
    }

    public EventLog createRemoveConfirmationAcceptedLog(@NonNull Booking booking, String reason)
    {
        String bookingId = booking.getId();
        return new ScheduledJobsLog.RemoveConfirmationAccepted(bookingId, reason);
    }

    public EventLog createFindJobsSelectedLog()
    {
        return new ScheduledJobsLog.FindJobsSelected();
    }


    // Payments Logs
    public EventLog createPaymentBatchSelectedLog(boolean currentWeek, int listNumber)
    {
        return new PaymentsLog.BatchSelected(currentWeek, listNumber);
    }

    public EventLog createPaymentDetailSelectedLog(String paymentType)
    {
        return new PaymentsLog.DetailSelected(paymentType);
    }

    public EventLog createPaymentHelpSlideUpLog()
    {
        return new PaymentsLog.HelpSlideUpSelected();
    }

    public EventLog createPaymentHelpItemSelectedLog(String helpItemLabel)
    {
        return new PaymentsLog.HelpItemSelected(helpItemLabel);
    }

    // Profile Logs
    public EventLog createReferralSelectedLog()
    {
        return new ProfileLog.ReferralSelectedLog();
    }

    public EventLog createResupplyKitSelectedLog()
    {
        return new ProfileLog.ResupplyKitSelectedLog();
    }

    public EventLog createResupplyKitConfirmedLog()
    {
        return new ProfileLog.ResupplyKitConfirmedLog();
    }

    public EventLog createEditProfileSelectedLog()
    {
        return new ProfileLog.EditProfileSelectedLog();
    }

    public EventLog createEditProfileConfirmedLog()
    {
        return new ProfileLog.EditProfileConfirmedLog();
    }

    // Performance Dashboard Logs
    public EventLog createFeedbackTappedLog()
    {
        return new PerformanceLog.FeedbackTappedLog();
    }

    public EventLog createFiveStarReviewsTappedLog()
    {
        return new PerformanceLog.FiveStarReviewsTappedLog();
    }

    public EventLog createTierTappedLog()
    {
        return new PerformanceLog.TierTappedLog();
    }

    // Help logs
    public EventLog createHelpContactFormSubmittedLog(String path, int helpNodeId, String helpNodeTitle)
    {
        return new HelpContactFormSubmittedLog(path, helpNodeId, helpNodeTitle);
    }

    // Push logs

    public EventLog createPushNotificationReceivedLog(final PushMessage pushMessage)
    {
        return new PushNotificationLog.Received(pushMessage);
    }

    public EventLog createPushNotificationOpenedLog(final PushMessage pushMessage)
    {
        return new PushNotificationLog.Opened(pushMessage);
    }

    public EventLog createPushNotificationDismissedLog(final PushMessage pushMessage)
    {
        return new PushNotificationLog.Dismissed(pushMessage);
    }

    // Deeplink logs
    public EventLog createDeeplinkOpenedLog(final Uri data)
    {
        return new DeeplinkLog.Opened(data);
    }

    public EventLog createDeeplinkProcessedLog(final Uri data)
    {
        return new DeeplinkLog.Processed(data);
    }

    public EventLog createDeeplinkIgnoredLog(final Uri data)
    {
        return new DeeplinkLog.Ignored(data);
    }

    //Web onboarding logs
    public EventLog createWebOnboardingShownLog(final OnboardingParams onboardingParams)
    {
        return new WebOnboardingLog.Shown(onboardingParams);
    }

    // Video Logs
    public EventLog createVideoTappedLog(String section)
    {
        return new VideoLog.VideoTappedLog(section);
    }

    public EventLog createVideoLibraryTappedLog()
    {
        return new VideoLog.VideoLibraryTappedLog();
    }

    //user dismissed or navved away from
    public EventLog createWebOnboardingDismissedLog()
    {
        return new WebOnboardingLog.Dismissed();
    }

    //system closed it
    public EventLog createWebOnboardingClosedLog()
    {
        return new WebOnboardingLog.Closed();
    }

    // private helpers
    private String getProviderId()
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null && provider.getId() != null)
        {
            return provider.getId();
        }
        else
        {
            return "";
        }
    }

    private String getVersionTrack()
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null && provider.getVersionTrack() != null)
        {
            return provider.getVersionTrack();
        }
        else
        {
            return "";
        }
    }

    private static String getZipCode(Address address)
    {
        if (address != null)
        {
            return address.getZip();
        }
        else
        {
            return "";
        }
    }

    private double getLatitude(Address address)
    {

        if (address != null)
        {
            return address.getLatitude();
        }
        else
        {
            return 0;
        }
    }

    private double getLongitude(Address address)
    {

        if (address != null)
        {
            return address.getLongitude();
        }
        else
        {
            return 0;
        }
    }

    private double getLatitude(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.LATITUDE));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private double getLongitude(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.LONGITUDE));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private double getAccuracy(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.ACCURACY));
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
