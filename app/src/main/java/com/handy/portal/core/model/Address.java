package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable {
    @SerializedName("address1")
    private String mAddress1;
    @SerializedName("address2")
    private String mAddress2;
    @SerializedName("city")
    private String mCity;
    @SerializedName("state")
    private String mState;
    @SerializedName("country")
    private String mCountry;
    @SerializedName("zipcode")
    private String mZip;
    @SerializedName("latitude")
    private float mLatitude;
    @SerializedName("longitude")
    private float mLongitude;
    @SerializedName("short_region")
    private String mShortRegion;

    public String getAddress1() {
        return mAddress1;
    }

    public String getAddress2() {
        return mAddress2;
    }

    public String getCity() {
        return mCity;
    }

    public String getState() {
        return mState;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getZip() {
        return mZip;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public String getShortRegion() {
        return mShortRegion;
    }

    public String getStreetAddress() {
        return (getAddress1() + (getAddress2() != null ? " " + getAddress2() : ""));
    }

    public String getCityStateZip() {
        return (getCity() != null ? getCity() : "") +
                (getCity() != null && getState() != null ? ", " : "") +
                (getState() != null ? getState() : "") +
                (getZip() != null ? (" " + getZip()) : "");
    }

    public String getShippingAddress() {
        return getStreetAddress() + "\n" + getCityStateZip();
    }
}
