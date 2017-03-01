package com.handy.portal.core.model;

import android.location.Location;

import com.handy.portal.core.constant.LocationKey;

public class LocationData {
    private TypeSafeMap<LocationKey> locationMap;

    public LocationData() {
        locationMap = new TypeSafeMap<>();
    }

    public LocationData(Location location) {
        this();
        if (location != null) {
            locationMap.put(LocationKey.LATITUDE, Double.toString(location.getLatitude()));
            locationMap.put(LocationKey.LONGITUDE, Double.toString(location.getLongitude()));
            locationMap.put(LocationKey.ACCURACY, Float.toString(location.getAccuracy()));
        }
    }

    public TypeSafeMap<LocationKey> getLocationMap() {
        return locationMap;
    }
}
