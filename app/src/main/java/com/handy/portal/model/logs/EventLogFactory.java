package com.handy.portal.model.logs;

import android.support.annotation.NonNull;

import com.handy.portal.constant.LocationKey;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.Provider;
import com.handy.portal.util.MathUtils;

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
        return new BasicLog.Open(getProviderId(), getVersionTrack());
    }

    public EventLog createNavigationLog(String tabName)
    {
        return new BasicLog.Navigation(getProviderId(), getVersionTrack(), tabName);
    }

    // Nearby Bookings Logs
    public EventLog createNearbyJobsLaunchedLog(int numOfJobs)
    {
        return new NearbyJobsLog.Shown(getProviderId(), getVersionTrack(), numOfJobs);
    }

    public EventLog createPinSelectedLog()
    {
        return new NearbyJobsLog.PinSelected(getProviderId(), getVersionTrack());
    }

    public EventLog createNearbyJobClaimSelectedLog(Booking booking, double distanceInKilometer)
    {
        String bookingId = booking.getId();
        int paymentAmount = booking.getPaymentToProvider().getAdjustedAmount();

        return new NearbyJobsLog.ClaimJobSelected(getProviderId(), getVersionTrack(), bookingId,
                distanceInKilometer, paymentAmount);
    }

    public EventLog createNearbyJobClaimSuccessLog(Booking booking, double distanceInKilometer)
    {
        String bookingId = booking.getId();
        int paymentAmount = booking.getPaymentToProvider().getAdjustedAmount();

        return new NearbyJobsLog.ClaimJobSuccess(getProviderId(), getVersionTrack(), bookingId,
                distanceInKilometer, paymentAmount);
    }

    // Available Booking Logs
    public EventLog createAvailableJobDateClickedLog(Date date, int jobCount)
    {
        return new AvailableJobsLog.DateClicked(getProviderId(), getVersionTrack(), date, jobCount);
    }

    public EventLog createAvailableJobClickedLog(@NonNull Booking booking, int listNumber)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new AvailableJobsLog.Clicked(getProviderId(), getVersionTrack(), bookingId,
                serviceId, regionId, zipCode, requested, dateStart, listNumber);
    }

    public EventLog createAvailableJobClaimSuccessLog(Booking booking, String source)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();
        int frequency = booking.getFrequency();

        return new AvailableJobsLog.ClaimSuccess(getProviderId(), getVersionTrack(), bookingId,
                serviceId, regionId, zipCode, requested, dateStart, frequency, source);
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

        return new AvailableJobsLog.ClaimError(getProviderId(), getVersionTrack(), bookingId,
                serviceId, regionId, zipCode, requested, dateStart, frequency, source);
    }

    // Scheduled Booking Logs
    public EventLog createScheduledJobDateClickedLog(Date date, int jobCount)
    {
        return new ScheduledJobsLog.DateClicked(getProviderId(), getVersionTrack(), date, jobCount);
    }

    public EventLog createScheduledJobClickedLog(@NonNull Booking booking, int listNumber)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new ScheduledJobsLog.Clicked(getProviderId(), getVersionTrack(), bookingId,
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

        return new ScheduledJobsLog.RemoveJobClicked(getProviderId(), getVersionTrack(),
                bookingId, serviceId, regionId, zipCode, requested, dateStart, warning);
    }

    public EventLog createRemoveJobConfirmedLog(Booking booking, String warning)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new ScheduledJobsLog.RemoveJobConfirmed(getProviderId(), getVersionTrack(),
                bookingId, serviceId, regionId, zipCode, requested, dateStart, warning);
    }

    public EventLog createRemoveJobErrorLog(Booking booking)
    {
        String bookingId = booking.getId();
        String serviceId = booking.getService();
        int regionId = booking.getRegionId();
        String zipCode = getZipCode(booking.getAddress());
        boolean requested = booking.isRequested();
        Date dateStart = booking.getStartDate();

        return new ScheduledJobsLog.RemoveJobError(getProviderId(), getVersionTrack(),
                bookingId, serviceId, regionId, zipCode, requested, dateStart);
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

        return new CheckInFlowLog.OnMyWay(getProviderId(), getVersionTrack(), bookingId,
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

        return new CheckInFlowLog.CheckIn(getProviderId(), getVersionTrack(), bookingId,
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

        return new CheckInFlowLog.CheckOut(getProviderId(), getVersionTrack(), bookingId,
                proLatitude, proLongitude, bookingLatitude, bookingLongitude, accuracy, distance);
    }

    public EventLog createCustomerRatingShownLog()
    {
        return new ScheduledJobsLog.CustomerRatingShown(getProviderId(), getVersionTrack());
    }

    public EventLog createCustomerRatingSubmittedLog(int rating)
    {
        return new ScheduledJobsLog.CustomerRatingSubmitted(getProviderId(), getVersionTrack(), rating);
    }

    public EventLog createBookingInstructionsSeenLog(Booking booking)
    {
        String bookingId = booking.getId();
        return new ScheduledJobsLog.BookingInstructionsSeen(getProviderId(), getVersionTrack(), bookingId);
    }


    // Payments Logs
    public EventLog createPaymentBatchSelectedLog(boolean currentWeek, int listNumber)
    {
        return new PaymentsLog.BatchSelected(getProviderId(), getVersionTrack(), currentWeek, listNumber);
    }

    public EventLog createPaymentDetailSelectedLog(String paymentType)
    {
        return new PaymentsLog.DetailSelected(getProviderId(), getVersionTrack(), paymentType);
    }

    public EventLog createPaymentHelpSlideUpLog()
    {
        return new PaymentsLog.HelpSlideUpSelected(getProviderId(), getVersionTrack());
    }

    public EventLog createPaymentHelpItemSelectedLog(String helpItemLabel)
    {
        return new PaymentsLog.HelpItemSelected(getProviderId(), getVersionTrack(), helpItemLabel);
    }

    // Profile Logs
    public EventLog createReferralSelectedLog()
    {
        return new ProfileLog.ReferralSelectedLog(getProviderId(), getVersionTrack());
    }

    public EventLog createResupplyKitSelectedLog()
    {
        return new ProfileLog.ResupplyKitSelectedLog(getProviderId(), getVersionTrack());
    }

    public EventLog createResupplyKitConfirmedLog()
    {
        return new ProfileLog.ResupplyKitConfirmedLog(getProviderId(), getVersionTrack());
    }

    public EventLog createEditProfileSelectedLog()
    {
        return new ProfileLog.EditProfileSelectedLog(getProviderId(), getVersionTrack());
    }

    public EventLog createEditProfileConfirmedLog()
    {
        return new ProfileLog.EditProfileConfirmedLog(getProviderId(), getVersionTrack());
    }

    // Help logs
    public EventLog createHelpContactFormSubmittedLog(String path, int helpNodeId, String helpNodeTitle)
    {
        return new HelpContactFormSubmittedLog(getProviderId(), getVersionTrack(), path, helpNodeId, helpNodeTitle);
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
