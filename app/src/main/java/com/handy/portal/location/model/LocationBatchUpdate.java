package com.handy.portal.location.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * holds an array of location updates to send to the server
 */
public class LocationBatchUpdate
{
    @SerializedName("geolocations")
    LocationUpdate mLocationUpdates[];

    public LocationBatchUpdate(@NonNull LocationUpdate locationUpdates[])
    {
        mLocationUpdates = locationUpdates;
    }

    @Override
    public String toString()
    {
        String result = "";
        for(LocationUpdate locationUpdate : mLocationUpdates)
        {
            result = result + "\n" + locationUpdate.toString();
        }
        return result;
    }
}
