package com.handy.portal.util;

public class MathUtils
{
    public static final double EARTH_RADIUS = 6371000; //meters

    public static double distFrom(double lat1, double lng1, double lat2, double lng2)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        if (a < 0 || a > 1) { return 0; }

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
