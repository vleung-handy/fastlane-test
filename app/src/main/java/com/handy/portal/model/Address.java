package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable
{
    @SerializedName("address1")
    private String address1;
    @SerializedName("address2")
    private String address2;
    @SerializedName("city")
    private String city;
    @SerializedName("state")
    private String state;
    @SerializedName("country")
    private String country;
    @SerializedName("zipcode")
    private String zip;
    @SerializedName("latitude")
    private float latitude;
    @SerializedName("longitude")
    private float longitude;
    @SerializedName("short_region")
    private String shortRegion;
    @SerializedName("region_id")
    private int regionId;

    public float getLatitude()
    {
        return latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }

    public String getShortRegion()
    {
        return shortRegion;
    }

    public String getAddress1()
    {
        return address1;
    }

    public String getAddress2()
    {
        return address2;
    }

    public String getCity()
    {
        return city;
    }

    public String getState()
    {
        return state;
    }

    public String getZip()
    {
        return zip;
    }

    public String getStreetAddress()
    {
        return (getAddress1() + (getAddress2() != null ? " " + getAddress2() : ""));
    }

    public String getCityStateZip()
    {
        return getCity() + ", " + getState() + " " + getZip();
    }

    public String getCountry()
    {
        return country;
    }

    public int getRegionId()
    {
        return regionId;
    }
}
