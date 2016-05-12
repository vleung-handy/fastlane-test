package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class ProviderPersonalInfo implements Serializable
{
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("local_phone")
    private String mLocalPhone;
    @SerializedName("address")
    private Address mAddress;
    @SerializedName("activation_date")
    private Date mActivationDate;
    @SerializedName("cc_4")
    private String mCardLast4;

    public String getLastName()
    {
        return mLastName;
    }

    public String getFirstName()
    {
        return mFirstName;
    }

    public String getEmail()
    {
        return mEmail;
    }

    public String getPhone()
    {
        return mPhone;
    }

    public Address getAddress()
    {
        return mAddress;
    }

    public Date getActivationDate()
    {
        return mActivationDate;
    }

    public String getLocalPhone()
    {
        return mLocalPhone;
    }

    public String getCardLast4()
    {
        return mCardLast4;
    }
}
