package com.handy.portal.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ZipClusterPolygons implements Serializable
{
    @SerializedName("center")
    private Coordinates mCenter;

    @SerializedName("outlines")
    private List<List<Coordinates>> mOutlines;

    public LatLng getCenter() { return new LatLng(mCenter.mLatitude, mCenter.mLongitude); }

    public LatLng[][] getOutlines() {
        LatLng[][] polygons = new LatLng[mOutlines.size()][];

        for (int i = 0; i < mOutlines.size(); ++i) {
            List<Coordinates> polygon = mOutlines.get(i);
            polygons[i] = new LatLng[polygon.size()];
            for (int j = 0; j < polygon.size(); ++j) {
                polygons[i][j] = new LatLng(polygon.get(j).mLatitude, polygon.get(j).mLongitude);
            }
        }
        return polygons;
    }

    public List<LatLng> getPoints() {

        LinkedList<LatLng> points = new LinkedList<>();
        for (List<Coordinates> polygon : mOutlines)
        {
            for (Coordinates coordinates : polygon)
            {
                points.add(new LatLng(coordinates.mLatitude, coordinates.mLongitude));
            }
        }

        return points;
    }

    public static class Coordinates implements Serializable
    {
        @SerializedName("lat")
        private float mLatitude;
        @SerializedName("lng")
        private float mLongitude;

        public float getLatitude() { return mLatitude; }

        public float getLongitude() { return mLongitude; }
    }
}
