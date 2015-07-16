package com.handy.portal.model;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class LocationData
{
    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public final static String ACCURACY = "accuracy";
    private Map<String, String> locationParams;

    public LocationData()
    {
        locationParams = new HashMap<>();
    }

    public LocationData(Location location)
    {
        locationParams = new HashMap<>();
        if(location != null)
        {
            locationParams.put(LATITUDE, Double.toString(location.getLatitude()));
            locationParams.put(LONGITUDE, Double.toString(location.getLongitude()));
            locationParams.put(ACCURACY, Float.toString(location.getAccuracy()));
        }
    }

    public Map<String, String> getLocationParamsMap()
    {
        return locationParams;
    }

    @Override
    public String toString()
    {
        if(locationParams == null)
        {
            return "";
        }

        String s =
                (locationParams.get(LATITUDE) != null ? locationParams.get(LATITUDE) : "|") + " " +
                (locationParams.get(LONGITUDE) != null ? locationParams.get(LONGITUDE) : "|") + " " +
                (locationParams.get(ACCURACY) != null ? locationParams.get(ACCURACY) : "|")
        ;

        return s;
    }
}
