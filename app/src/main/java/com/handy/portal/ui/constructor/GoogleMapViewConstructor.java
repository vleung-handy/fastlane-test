package com.handy.portal.ui.constructor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;

import java.util.Locale;

public class GoogleMapViewConstructor extends BookingDetailsViewFragmentContainerConstructor implements OnMapReadyCallback
{
    private static final int DEFAULT_ZOOM_LEVEL = 15;
    private static final float OVERLAY_RADIUS_METERS = 500f;

    private Booking booking;
    private boolean useRestrictedView;
    private LatLng target;
    private ViewGroup container;

    public GoogleMapViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    @Override
    protected Class getFragmentClass()
    {
        return SupportMapFragment.class;
    }

    @Override
    protected void onFragmentCreated(Fragment fragment)
    {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext()))
        {
            SupportMapFragment mapFragment = (SupportMapFragment) fragment;
            if (mapFragment != null)
            {
                mapFragment.getMapAsync(this);
            }
            else
            {
                removeView();
            }
        }
        else
        {
            removeView();
        }
    }

    private void removeView()
    {
        container.removeAllViews();
        container.setVisibility(View.GONE);
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        BookingStatus bookingStatus = (BookingStatus) getArguments().getSerializable(BundleKeys.BOOKING_STATUS);
        this.container = container;
        this.useRestrictedView = bookingStatus != BookingStatus.CLAIMED;
        this.booking = booking;

        return true;
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        if (booking != null)
        {
            float latitude = booking.getAddress().getLatitude();
            float longitude = booking.getAddress().getLongitude();
            this.target = new LatLng(latitude, longitude);
            focusMap(map, target);
        }

        if (this.useRestrictedView)
        {
            map.getUiSettings().setAllGesturesEnabled(false); //disable all controls, we just want to see the image for now
            //In restricted view can't click on map to see exact location, show Toast to inform user
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(LatLng point)
                {
                    Toast toast = Toast.makeText(getContext(), R.string.exact_location_shown_claimed, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
        else
        {
            map.getUiSettings().setAllGesturesEnabled(false); //disable controls for now, only allowing clicks to launch maps

            //Can click on the map to launch it
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(LatLng point)
                {
                    String queryAddress = booking.getAddress().getStreetAddress() + " " + booking.getAddress().getZip();
                    openNativeMap(target, queryAddress);
                }
            });
        }
    }

    private void focusMap(GoogleMap map, LatLng target)
    {
        CameraPosition targetCameraPosition = new CameraPosition.Builder().
                target(target).
                zoom(DEFAULT_ZOOM_LEVEL).
                build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(targetCameraPosition);
        map.moveCamera(cameraUpdate);
        if (this.useRestrictedView)
        {
            showRangeOverlay(map, target);
        }
        else
        {
            addPinToMap(map, target);
        }
    }

    private void showRangeOverlay(GoogleMap map, LatLng target)
    {
        GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_radius))
                .position(target, OVERLAY_RADIUS_METERS);
        map.addGroundOverlay(groundOverlay);
    }

    private void addPinToMap(GoogleMap map, LatLng target)
    {
        MarkerOptions marker = new MarkerOptions()
                .position(target)
                .draggable(false);
        map.addMarker(marker);
    }

    //TODO: This is failing on emulator, no activity to handle the intent
    private void openNativeMap(LatLng target, String fullAddress)
    {
        //Query format: lat,long,?optionalZoomLevel&q=address
        //the lat long are used to bias the search querys address and are used as a fallback if address not found
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d&q=%s",
                target.latitude, target.longitude, DEFAULT_ZOOM_LEVEL, fullAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getContext().startActivity(intent);
    }
}
