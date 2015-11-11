package com.handy.portal.constant;

public enum NoShowKey
{
    LATITUDE, LONGITUDE, ACCURACY, ACTIVE, EMAIL, PHONE, ADDRESS1, ADDRESS2, CITY, STATE, ZIPCODE;

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
