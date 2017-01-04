package com.handy.portal.core.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.constant.Country;

import java.io.Serializable;
import java.util.ArrayList;
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
    @SerializedName("operating_region")
    private String mOperatingRegion;
    @SerializedName("currency_code")
    private String mCurrencyCode;
    @SerializedName("profile_images")
    private ArrayList<ProfileImage> mProfileImages;

    public String getLastName()
    {
        return mLastName;
    }

    public String getFirstName()
    {
        return mFirstName;
    }

    public String getFullName()
    {
        return mFirstName + " " + mLastName;
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

    public String getOperatingRegion()
    {
        return mOperatingRegion;
    }

    public String getCurrencyCode()
    {
        return mCurrencyCode;
    }

    public boolean isUK()
    {
        return Country.GB.equalsIgnoreCase(getAddress().getCountry());
    }

    public boolean isUS()
    {
        return Country.US.equalsIgnoreCase(getAddress().getCountry());
    }

    @Nullable
    public ProfileImage getProfileImage(@NonNull final ProfileImage.Type type)
    {
        if (mProfileImages != null)
        {
            for (ProfileImage profileImage : mProfileImages)
            {
                if (profileImage.getType() == type)
                {
                    return profileImage;
                }
            }
        }
        return null;
    }

    public static class ProfileImage implements Serializable
    {
        public enum Type
        {
            @SerializedName("original")
            ORIGINAL,
            @SerializedName("thumbnail")
            THUMBNAIL,
            @SerializedName("small")
            SMALL,
            @SerializedName("medium")
            MEDIUM,
            @SerializedName("large")
            LARGE,
        }


        @SerializedName("url")
        private String mUrl;
        @SerializedName("type")
        private Type mType;

        public Type getType()
        {
            return mType;
        }

        public String getUrl()
        {
            return mUrl;
        }
    }
}
