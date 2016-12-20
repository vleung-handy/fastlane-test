package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.constant.Country;

public class Provider
{
    @SerializedName("id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("country")
    private String country;

    public String getId()
    {
        return id;
    }

    public String getEmail()
    {
        return email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    public String getCountry()
    {
        return country;
    }

    public boolean isUK()
    {
        return Country.GB.equalsIgnoreCase(getCountry());
    }

    public boolean isUS()
    {
        return Country.US.equalsIgnoreCase(getCountry());
    }
}
