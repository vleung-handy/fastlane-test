package com.handy.portal.constant;

public enum NoShowKey
{
    LATITUDE, LONGITUDE, ACCURACY, ACTIVE;

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
