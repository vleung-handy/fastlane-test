package com.handy.portal.ui.element;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

/**
 * Created by cdavis on 5/8/15.
 */
public class GoogleMapView extends BookingDetailsView
{
    private static int DEFAULT_ZOOM_LEVEL = 15;

    protected int getLayoutResourceId()
    {
        return R.layout.element_map;
    }

    //MapView map;

    protected void initFromBooking(Booking booking)
    {
        //use lat/long of booking to target the google map
        //initMapForBooking(booking);
    }

    private void initMapForBooking(Booking booking)
    {
        MapView map = (MapView) parentViewGroup.findViewById(R.id.map);
        GoogleMap googleMap = map.getMap();

        float latitude = booking.getAddress().getLatitude();
        float longitude = booking.getAddress().getLongitude();
        LatLng target = new LatLng(latitude, longitude);

        focusMap(googleMap, target, DEFAULT_ZOOM_LEVEL);
    }

    private void focusMap(GoogleMap googleMap, LatLng target, int zoomLevel)
    {
        CameraPosition targetCameraPosition = new CameraPosition.Builder().
                target(target).
                zoom(DEFAULT_ZOOM_LEVEL).
                build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(targetCameraPosition);
        googleMap.animateCamera(cameraUpdate);
    }




}
