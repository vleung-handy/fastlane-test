package com.handy.portal.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ZipClusterPolygons implements Serializable
{
    @SerializedName("center")
    private Coordinates center;

    @SerializedName("outlines")
    private List<List<Coordinates>> outlines;

    public LatLng getCenter() { return new LatLng(center.latitude, center.longitude); }

    public LatLng[][] getOutlines() {
        LatLng[][] polygons = new LatLng[outlines.size()][];

        for (int i = 0; i < outlines.size(); ++i) {
            List<Coordinates> polygon = outlines.get(i);
            polygons[i] = new LatLng[polygon.size()];
            for (int j = 0; j < polygon.size(); ++j) {
                polygons[i][j] = new LatLng(polygon.get(j).latitude, polygon.get(j).longitude);
            }
        }
        return polygons;
    }

    public static class Coordinates implements Serializable
    {
        @SerializedName("lat")
        private float latitude;
        @SerializedName("lng")
        private float longitude;

        public float getLatitude() { return latitude; }

        public float getLongitude() { return longitude; }
    }
}
