package com.handy.portal.constant;

public enum LocationKey
{
    LATITUDE, LONGITUDE, ACCURACY;

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
