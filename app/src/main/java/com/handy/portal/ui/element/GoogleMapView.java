package com.handy.portal.ui.element;

import android.app.Fragment;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

/**
 * Created by cdavis on 5/8/15.
 */
public class GoogleMapView extends BookingDetailsViewFragmentContainer implements OnMapReadyCallback
{
    private static int DEFAULT_ZOOM_LEVEL = 15;

    GoogleMap googleMap;
    Booking booking;

    @Override
    protected Class getFragmentClass()
    {
        return MapFragment.class;
    }

    @Override
    protected void onFragmentCreated(Fragment fragment)
    {
        MapFragment mapFragment = (MapFragment) fragment;
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        //booking stuff
        //store and wait for on map ready
        this.booking = booking;
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        this.googleMap = map;
        this.googleMap.getUiSettings().setAllGesturesEnabled(false); //disable all controls, we just want to see the image for now
        if(booking != null)
        {
            float latitude = booking.getAddress().getLatitude();
            float longitude = booking.getAddress().getLongitude();
            LatLng target = new LatLng(latitude, longitude);
            focusMap(googleMap, target);
        }
    }

    private void focusMap(GoogleMap map, LatLng target)
    {
        CameraPosition targetCameraPosition = new CameraPosition.Builder().
                target(target).
                zoom(DEFAULT_ZOOM_LEVEL).
                build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(targetCameraPosition);
        map.animateCamera(cameraUpdate);
        showRangeOverlay(map, target);
    }

    private void showRangeOverlay(GoogleMap map, LatLng target)
    {
        GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_radius))
                .position(target, 500f);
        map.addGroundOverlay(groundOverlay);
    }
}
