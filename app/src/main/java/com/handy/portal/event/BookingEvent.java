package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.ZipClusterPolygons;

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
