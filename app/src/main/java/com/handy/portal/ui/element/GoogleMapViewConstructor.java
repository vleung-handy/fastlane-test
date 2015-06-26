package com.handy.portal.ui.element;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import java.util.List;
import java.util.Locale;

public class GoogleMapViewConstructor extends BookingDetailsViewFragmentContainerConstructor implements OnMapReadyCallback
{
    private static int DEFAULT_ZOOM_LEVEL = 15;
    private static float OVERLAY_RADIUS_METERS = 500f;

    private GoogleMap googleMap;
    private Booking booking;
    private boolean useRestrictedView;
    private LatLng target;

    @Override
    protected Class getFragmentClass()
    {
        return MapFragment.class;
    }

    @Override
    protected void onFragmentCreated(Fragment fragment)
    {
        //right now erring on side of caution and requiring play and maps since I haven't been able to test having maps installed without google play, not sure if possible
        //if play is installed but not maps it will prompt the user to install maps
        //in future may be able to remove the && mapsInstalled check but that is a product decision
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(fragment.getActivity()))
        {
            MapFragment mapFragment = (MapFragment) fragment;
            mapFragment.getMapAsync(this);
        }
        else
        {
            //clear out if we don't have access to play services otherwise we will crash
            removeView();
        }
    }

    @Override
    protected void constructViewFromBooking(Booking booking, List<Booking.ActionButtonData> allowedActions, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        this.useRestrictedView = true;
        if (bookingStatus == BookingStatus.CLAIMED)
        {
            this.useRestrictedView = false;
        }

        //booking stuff
        //store and wait for on map ready
        this.booking = booking;
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        if (!isGoogleMapsInstalled())
        {
            //clear out the map child if maps is not installed
            removeView();
            return;
        }

        this.googleMap = map;

        if (booking != null)
        {
            float latitude = booking.getAddress().getLatitude();
            float longitude = booking.getAddress().getLongitude();
            this.target = new LatLng(latitude, longitude);
            focusMap(googleMap, target);
        }

        if (this.useRestrictedView)
        {
            this.googleMap.getUiSettings().setAllGesturesEnabled(false); //disable all controls, we just want to see the image for now
            //Clicking on map shows error toast
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(LatLng point)
                {
                    //todo : Add showing a toast here
                }
            });
        }
        else
        {
            this.googleMap.getUiSettings().setAllGesturesEnabled(false); //disable controls for now, only allowing clicks to launch maps

            //Can click on the map to launch it
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(LatLng point)
                {
                    openNativeMap(target);
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
    private void openNativeMap(LatLng target)
    {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d&q=%f,%f (%s)",
                target.latitude, target.longitude, DEFAULT_ZOOM_LEVEL, target.latitude, target.longitude, R.string.location);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        activity.startActivity(intent);
    }

//
    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = this.activity.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }
}
