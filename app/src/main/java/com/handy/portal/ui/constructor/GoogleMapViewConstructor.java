package com.handy.portal.ui.constructor;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.model.ZipClusterPolygons;
import com.handy.portal.ui.fragment.BookingMapFragment;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;

import java.util.Locale;

public class GoogleMapViewConstructor extends DetailMapViewConstructor implements OnMapReadyCallback
{
    private static final int DEFAULT_ZOOM_LEVEL = 15;
    private static final float DEFAULT_RADIUS_METERS = 500f;
    private static final double MILES_IN_ONE_METER = 0.000621371;
    private static final int ONE_MILE_ZOOM_LEVEL = 14;

    private Booking booking;
    private boolean useRestrictedView;
    private LatLng target;
    private ViewGroup container;
    private ZipClusterPolygons mPolygons;

    public GoogleMapViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    @Override
    protected void inflateMapView(RelativeLayout mapViewStub)
    {
        try
        {
            BookingMapFragment fragment = BookingMapFragment.class.newInstance();

            UIUtils.replaceViewWithFragment(getContext(), mapViewStub, fragment);
            onFragmentCreated(fragment);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
    }

    protected void onFragmentCreated(SupportMapFragment mapFragment)
    {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext()))
        {
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
        Booking.BookingStatus bookingStatus = (Booking.BookingStatus) getArguments().getSerializable(BundleKeys.BOOKING_STATUS);
        this.container = container;
        this.useRestrictedView = bookingStatus != Booking.BookingStatus.CLAIMED;
        this.booking = booking;
        mPolygons = (ZipClusterPolygons) getArguments().getSerializable(BundleKeys.ZIP_CLUSTER_POLYGONS);

        return true;
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        if (booking != null)
        {
            this.target = getLatLng();
            focusMap(map, this.target);
        }

        if (this.useRestrictedView)
        {
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
            //Can click on the map to launch it
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
            {
                @Override
                public void onMapClick(LatLng point)
                {
                    String queryAddress;
                    if (booking.isProxy())
                    {
                        queryAddress = booking.getMidpoint().getLatitude() + "," + booking.getMidpoint().getLongitude();
                    }
                    else
                    {
                        queryAddress = booking.getFormattedLocation(Booking.BookingStatus.CLAIMED);
                    }
                    openNativeMap(target, queryAddress);
                }
            });
        }
    }

    private LatLng getLatLng()
    {
        double latitude;
        double longitude;
        if (booking.isProxy())
        {
            if (mPolygons == null)
            {
                latitude = booking.getMidpoint().getLatitude();
                longitude = booking.getMidpoint().getLongitude();
            }
            else
            {
                latitude = mPolygons.getOutlines()[0][0].latitude;
                longitude = mPolygons.getOutlines()[0][0].longitude;
            }
        }
        else
        {
            latitude = booking.getAddress().getLatitude();
            longitude = booking.getAddress().getLongitude();
        }
        return new LatLng(latitude, longitude);
    }

    private void focusMap(GoogleMap map, LatLng target)
    {
        CameraPosition targetCameraPosition = new CameraPosition.Builder().
                target(target).
                zoom(getZoomLevel()).
                build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(targetCameraPosition);
        map.moveCamera(cameraUpdate);
        if (booking.isProxy() || this.useRestrictedView)
        {
            if (mPolygons == null)
            {
                showRangeOverlay(map, target, getRadius());
            }
            else
            {
                showPolygon(map, mPolygons.getOutlines());
            }
        }
        else
        {
            addPinToMap(map, target);
        }
    }

    private static void showPolygon(GoogleMap map, LatLng[][] polygons)
    {
        for (LatLng[] polygon : polygons)
        {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.add(polygon);
            polygonOptions.strokeWidth(1);
            polygonOptions.fillColor(0x4C00CDED);
            map.addPolygon(polygonOptions);
        }
    }

    private void showRangeOverlay(GoogleMap map, LatLng target, float radius)
    {
        GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_radius))
                .position(target, radius);
        map.addGroundOverlay(groundOverlay);
    }

    private void addPinToMap(GoogleMap map, LatLng target)
    {
        MarkerOptions marker = new MarkerOptions()
                .position(target)
                .draggable(false);
        map.addMarker(marker);
    }

    private int getZoomLevel()
    {
        return booking.isProxy() ? calculateZoomLevelFromRadius() : DEFAULT_ZOOM_LEVEL;
    }

    private float getRadius()
    {
        return booking.getRadius() > 0 ? booking.getRadius() : DEFAULT_RADIUS_METERS;
    }

    private int calculateZoomLevelFromRadius()
    {
        return (int) (Math.round(ONE_MILE_ZOOM_LEVEL - Math.log(getRadius() * MILES_IN_ONE_METER) / Math.log(Math.E)) - 1);
    }

    private void openNativeMap(LatLng target, String fullAddress)
    {
        //Query format: lat,long,?optionalZoomLevel&q=address
        //the lat long are used to bias the search queries address and are used as a fallback if address not found
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d&q=%s",
                target.latitude, target.longitude, getZoomLevel(), fullAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        Utils.safeLaunchIntent(intent, getContext());
    }
}
