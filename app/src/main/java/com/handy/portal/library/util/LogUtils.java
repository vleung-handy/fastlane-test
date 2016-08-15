package com.handy.portal.library.util;


import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.model.Address;
import com.handy.portal.model.LocationData;

public class LogUtils
{
    public static double getLatitude(final Booking booking)
    {
        final Address address = booking.getAddress();
        final Booking.Coordinates midpoint = booking.getMidpoint();
        if (address != null)
        {
            return address.getLatitude();
        }
        else if (midpoint != null)
        {
            return midpoint.getLatitude();
        }
        else
        {
            return 0;
        }
    }

    public static double getLongitude(final Booking booking)
    {
        final Address address = booking.getAddress();
        final Booking.Coordinates midpoint = booking.getMidpoint();
        if (address != null)
        {
            return address.getLongitude();
        }
        else if (midpoint != null)
        {
            return midpoint.getLongitude();
        }
        else
        {
            return 0;
        }
    }

    public static double getLatitude(LocationData location)
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

    public static double getLongitude(LocationData location)
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

    public static double getAccuracy(LocationData location)
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
