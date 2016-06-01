package com.handy.portal.bookings;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ZipClusterPolygons;

import java.util.Date;
import java.util.List;

public abstract class BookingEvent extends HandyEvent
{
    public static class RequestZipClusterPolygons extends RequestEvent
    {
        public final String zipClusterId;

        public RequestZipClusterPolygons(String zipClusterId)
        {
            this.zipClusterId = zipClusterId;
        }
    }

    public static class RequestProRequestedJobs extends RequestEvent
    {
        private List<Date> mDatesForBookings;
        private boolean mUseCachedIfPresent;
        public RequestProRequestedJobs(List<Date> datesForBookings, boolean useCachedIfPresent)
        {
            mDatesForBookings = datesForBookings;
            mUseCachedIfPresent = useCachedIfPresent;
        }

        public boolean useCachedIfPresent()
        {
            return mUseCachedIfPresent;
        }

        public List<Date> getDatesForBookings()
        {
            return mDatesForBookings;
        }
    }

    public static class ReceiveProRequestedJobsSuccess extends ReceiveSuccessEvent
    {
        public final List<BookingsWrapper> mProRequestedJobs;

        public ReceiveProRequestedJobsSuccess(List<BookingsWrapper> proRequestedJobs)
        {
            mProRequestedJobs = proRequestedJobs;
        }

        public List<BookingsWrapper> getProRequestedJobs()
        {
            return mProRequestedJobs;
        }
    }

    public static class ReceiveProRequestedJobsError extends ReceiveErrorEvent
    {
        public ReceiveProRequestedJobsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ReceiveZipClusterPolygonsSuccess extends ReceiveSuccessEvent
    {
        public final ZipClusterPolygons zipClusterPolygons;

        public ReceiveZipClusterPolygonsSuccess(ZipClusterPolygons zipClusterPolygons)
        {
            this.zipClusterPolygons = zipClusterPolygons;
        }
    }


    public static class ReceiveZipClusterPolygonsError extends ReceiveErrorEvent
    {
        public ReceiveZipClusterPolygonsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestNearbyBookings extends RequestEvent
    {
        private int mRegionId;
        private double mLatitude;
        private double mLongitude;

        public RequestNearbyBookings(final int regionId, final double latitude, final double longitude)
        {
            mRegionId = regionId;
            mLatitude = latitude;
            mLongitude = longitude;
        }

        public int getRegionId() { return mRegionId; }

        public double getLatitude() { return mLatitude; }

        public double getLongitude() { return mLongitude; }
    }


    public static class ReceiveNearbyBookingsSuccess extends ReceiveSuccessEvent
    {
        private List<Booking> mBookings;

        public ReceiveNearbyBookingsSuccess(final List<Booking> bookings) { mBookings = bookings; }

        public List<Booking> getBookings() { return mBookings; }
    }


    public static class ReceiveNearbyBookingsError extends ReceiveErrorEvent
    {
        public ReceiveNearbyBookingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
