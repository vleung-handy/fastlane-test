package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.constant.LocationKey;

public class CheckoutRequest
{
    //The server is expecting location data at the top level of the object, otherwise would just pass location data object
    @SerializedName("latitude")
    private String mLatitude;
    @SerializedName("longitude")
    private String mLongitude;
    @SerializedName("accuracy")
    private String mAccuracy;
    @SerializedName("pro_feedback")
    private ProBookingFeedback mProFeedback;

    public CheckoutRequest(LocationData locationData, ProBookingFeedback feedback)
    {
        mLatitude = locationData.getLocationMap().get(LocationKey.LATITUDE);
        mLongitude = locationData.getLocationMap().get(LocationKey.LONGITUDE);
        mAccuracy = locationData.getLocationMap().get(LocationKey.ACCURACY);
        mProFeedback = feedback;
    }

    public CheckoutRequest(String latitude, String longitude, String accuracy, ProBookingFeedback feedback)
    {
        mLatitude = latitude;
        mLongitude = longitude;
        mAccuracy = accuracy;
        mProFeedback = feedback;
    }
}
