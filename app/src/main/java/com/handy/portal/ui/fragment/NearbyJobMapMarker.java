package com.handy.portal.ui.fragment;

import com.google.android.gms.maps.model.LatLng;

public class NearbyJobMapMarker
{
    private String mLabel;
    private String mIcon;
    private LatLng mLatLng;

    public NearbyJobMapMarker(String label, String icon, LatLng latLng)
    {
        this.mLabel = label;
        this.mIcon = icon;
        this.mLatLng = latLng;
    }

    public String getLabel()
    {
        return mLabel;
    }

    public String getIcon()
    {
        return mIcon;
    }

    public LatLng getLatLng()
    {
        return mLatLng;
    }
}
