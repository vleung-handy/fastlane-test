package com.handy.portal.util;

import java.text.DecimalFormat;

public class MathUtils
{
    public static final DecimalFormat TWO_DECIMALS_FORMAT = new DecimalFormat("#.##");
    public static final double EARTH_RADIUS = 6371; //kilometers
    public static final double MILES_PER_KILOMETER = 0.621371;

    public static double getDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS * c;
    }
}
