package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ProviderPersonalInfo
{
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private Address address;
    @SerializedName("activation_date")
    private Date activationDate;

    public String getLastName()
    {
        return lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhone()
    {
        return phone;
    }

    public Address getAddress()
    {
        return address;
    }

    public Date getActivationDate()
    {
        return activationDate;
    }
}
