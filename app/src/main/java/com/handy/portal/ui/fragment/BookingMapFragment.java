package com.handy.portal.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.crashlytics.android.Crashlytics;
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
import com.handy.portal.util.Utils;

import java.util.Locale;

public class BookingMapFragment extends SupportMapFragment implements OnMapReadyCallback
{
    private static final ViewGroup.LayoutParams LAYOUT_PARAMS =
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private static final int DEFAULT_ZOOM_LEVEL         = 15;
    private static final float DEFAULT_RADIUS_METERS    = 500f;
    private static final double MILES_IN_ONE_METER      = 0.000621371;
    private static final int ONE_MILE_ZOOM_LEVEL        = 14;

    private static final int MAP_POLYGON_STROKE_WIDTH   = 3;

    private ScrollView mScrollView;
    private Booking mBooking;
    private Booking.BookingStatus mStatus;
    private ZipClusterPolygons mPolygons;

    public static BookingMapFragment newInstance(final Booking booking, Booking.BookingStatus status, ZipClusterPolygons polygons)
    {
        BookingMapFragment fragment = new BookingMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        args.putSerializable(BundleKeys.BOOKING_STATUS, status);
        args.putSerializable(BundleKeys.ZIP_CLUSTER_POLYGONS, polygons);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        mStatus = (Booking.BookingStatus) getArguments().getSerializable(BundleKeys.BOOKING_STATUS);
        mPolygons = (ZipClusterPolygons) getArguments().getSerializable(BundleKeys.ZIP_CLUSTER_POLYGONS);
        getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance)
    {
        ViewGroup layout = (ViewGroup) super.onCreateView(layoutInflater, viewGroup, savedInstance);

        // For disabling scroll view's up and down scrolling.
        if (layout != null)
        {
            TouchableWrapper frameLayout = new TouchableWrapper(getContext());
            frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            layout.addView(frameLayout, LAYOUT_PARAMS);
        }
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ScrollView) getActivity().findViewById(R.id.booking_details_scroll_view);
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        if (mBooking == null) { return; }

        map.setMyLocationEnabled(true);

        LatLng center = getCenterPoint();
        CameraPosition targetCameraPosition =
                new CameraPosition.Builder().target(center).zoom(getZoomLevel()).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(targetCameraPosition);
        map.moveCamera(cameraUpdate);

        if (mStatus == Booking.BookingStatus.CLAIMED && !mBooking.isProxy())
        {
            MarkerOptions marker = new MarkerOptions().position(center).draggable(false);
            map.addMarker(marker);
        }
        else if (mBooking.isProxy() && mPolygons != null)
        {
            showPolygon(map, mPolygons.getOutlines());
        }
        else
        {
            showRangeOverlay(map, center, getRadius());
        }
    }

    private LatLng getCenterPoint()
    {
        if (!mBooking.isProxy() && mStatus == Booking.BookingStatus.CLAIMED)
        {
            return new LatLng(mBooking.getAddress().getLatitude(), mBooking.getAddress().getLongitude());
        }
        else if (mBooking.isProxy() && mPolygons != null)
        {
            return new LatLng(mPolygons.getCenter().latitude, mPolygons.getCenter().longitude);
        }
        else if(mBooking.getMidpoint() != null)
        {
            return new LatLng(mBooking.getMidpoint().getLatitude(), mBooking.getMidpoint().getLongitude());
        }
        else
        {
            //fallback so we don't crash
            Crashlytics.log("BookingMapFragment booking has no valid midpoint");
            return new LatLng(0.0f, 0.0f);
        }
    }

    private int getZoomLevel()
    {
        return mBooking.isProxy() ? calculateZoomLevelFromRadius() : DEFAULT_ZOOM_LEVEL;
    }

    private float getRadius()
    {
        return mBooking.getRadius() > 0 ? mBooking.getRadius() : DEFAULT_RADIUS_METERS;
    }

    private int calculateZoomLevelFromRadius()
    {
        return (int) (Math.round(ONE_MILE_ZOOM_LEVEL - Math.log(getRadius() * MILES_IN_ONE_METER) / Math.log(Math.E)) - 1);
    }

    private static void showPolygon(GoogleMap map, LatLng[][] polygons)
    {
        for (LatLng[] polygon : polygons)
        {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.add(polygon);
            polygonOptions.strokeWidth(MAP_POLYGON_STROKE_WIDTH);
            polygonOptions.strokeColor(R.color.proxy_polygon_stroke);
            polygonOptions.fillColor(R.color.proxy_polygon_fill);
            map.addPolygon(polygonOptions);
        }
    }

    private static void showRangeOverlay(GoogleMap map, LatLng target, float radius)
    {
        GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_radius))
                .position(target, radius);
        map.addGroundOverlay(groundOverlay);
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

    // This is used to disable the scrolling of the scroll view so we can scroll our map
    public class TouchableWrapper extends FrameLayout
    {
        public TouchableWrapper(Context context)
        {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event)
        {
            if (mScrollView == null)
            {
                return super.dispatchTouchEvent(event);
            }
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
