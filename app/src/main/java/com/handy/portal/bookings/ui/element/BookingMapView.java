package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.fragment.BookingDetailsWrapperFragment;
import com.handy.portal.location.LocationUtils;
import com.handy.portal.model.ZipClusterPolygons;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.util.UIUtils;

import java.util.LinkedList;
import java.util.List;


public class BookingMapView extends MapView implements OnMapReadyCallback
{
    private static final int ON_MY_WAY_DEFAULT_ZOOM_LEVEL = 15;
    private static final int CHECK_IN_DEFAULT_ZOOM_LEVEL = 12;
    private static final int DEFAULT_BOUND_PADDING = 15;
    private static final float DEFAULT_RADIUS_METERS = 500f;

    private static final int MAP_POLYGON_STROKE_WIDTH = 3;
    private static final int MAP_POLYGON_STROKE_COLOR = 0XFFD1D1D1; //Can not store in colors.xml, colors.xml doesn't use alpha correctly
    private static final int MAP_POLYGON_FILL_COLOR = 0x80FF5C5C; //Can not store in colors.xml, colors.xml doesn't use alpha correctly

    private Booking mBooking;
    private String mSource;
    private Booking.BookingStatus mStatus;
    private ZipClusterPolygons mPolygons;
    private TouchableWrapper mTouchableWrapper;

    public BookingMapView(final Context context)
    {
        super(context);
    }

    public BookingMapView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingMapView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public BookingMapView(final Context context, final GoogleMapOptions options)
    {
        super(context, options);
    }

    @Override
    @SuppressWarnings({"ResourceType", "MissingPermission"})
    public void onMapReady(GoogleMap map)
    {
        if (mBooking == null)
        {
            Crashlytics.log("mBooking is null in onMapReady()");
            return;
        }

        if (!LocationUtils.hasRequiredLocationPermissions(getContext()))
        {
            return;
        }
        map.setMyLocationEnabled(true);

        // Default points
        List<LatLng> points = new LinkedList<>();
        LatLng center = getCenterPoint();
        points.add(center);
        if (shouldShowMarker())
        {
            MarkerOptions marker = new MarkerOptions().position(center).draggable(false);
            map.addMarker(marker);
        }
        else if (shouldShowPolygons())
        {
            showPolygon(map, mPolygons.getOutlines());
            points = mPolygons.getPoints();
        }
        else
        {
            showRangeOverlay(map, center, getRadius());
        }

        if (shouldIncludeCurrentLocation())
        {
            final Location lastLocation = ((BaseActivity) getContext()).getLastLocation();
            if (lastLocation != null)
            {
                points.add(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }
        }

        positionCamera(map, points);
    }

    public void setDisplay(
            @NonNull Booking booking, @Nullable String bookingSource, Booking.BookingStatus bookingStatus,
            @Nullable ZipClusterPolygons polygons)
    {
        mBooking = booking;
        mSource = bookingSource;
        mStatus = bookingStatus;
        mPolygons = polygons;
        getMapAsync(this);
    }

    public void disableParentScrolling(ScrollView scrollView)
    {
        if (mTouchableWrapper == null)
        {
            mTouchableWrapper = new TouchableWrapper(getContext(), scrollView);
            mTouchableWrapper.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            addView(mTouchableWrapper, UIUtils.MATCH_PARENT_PARAMS);
        }
    }

    private boolean shouldShowMarker()
    {
        return !mBooking.isProxy() &&
                (mStatus == Booking.BookingStatus.CLAIMED || shouldIncludeCurrentLocation());
    }

    private boolean shouldShowPolygons()
    {
        return mBooking.isProxy() && mPolygons != null;
    }

    private boolean shouldIncludeCurrentLocation()
    {
        return mSource != null &&
                mSource.equals(BookingDetailsWrapperFragment.SOURCE_LATE_DISPATCH);
    }

    /**
     * Given a map and a list of points, this functions bounds the map camera by the points
     * so they fit perfectly within the camera view with some default amount of padding.
     * <p/>
     * In the case of normal bookings with a center point, the map zooms in too far.
     * To combat that, the camera zoom is checked after the bounding and forced to the default
     * zoom if the zoom is greater than the default.
     */
    private void positionCamera(@NonNull final GoogleMap map, @NonNull List<LatLng> points)
    {
        final CameraUpdate cameraUpdate = buildCameraUpdate(points);

        // Sometimes the google map is ready but fragment it's placed in has not performed layout.
        // In that case, attempting to moveCamera will raise an IllegalStateException because
        // the map has no physical size.
        //
        // To get around this, catch the exception, add a listener to the fragments layout callback,
        // and re-attempt to move once layout has occurred
        try
        {
            moveCamera(map, cameraUpdate);
        }
        catch (IllegalStateException e)
        {
            if (getViewTreeObserver().isAlive())
            {
                getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener()
                        {
                            @Override
                            public void onGlobalLayout()
                            {
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                moveCamera(map, cameraUpdate);
                            }
                        }
                );
            }
        }
    }

    private void moveCamera(final @NonNull GoogleMap map, final CameraUpdate cameraUpdate)
    {
        map.moveCamera(cameraUpdate);
        if (mBooking.getAction(Booking.Action.ACTION_CHECK_IN) != null
                && map.getCameraPosition().zoom > CHECK_IN_DEFAULT_ZOOM_LEVEL)
        {
            map.moveCamera(CameraUpdateFactory.zoomTo(CHECK_IN_DEFAULT_ZOOM_LEVEL));
        }
        else if (map.getCameraPosition().zoom > ON_MY_WAY_DEFAULT_ZOOM_LEVEL)
        {
            map.moveCamera(CameraUpdateFactory.zoomTo(ON_MY_WAY_DEFAULT_ZOOM_LEVEL));
        }
    }

    private CameraUpdate buildCameraUpdate(@NonNull List<LatLng> points)
    {
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (LatLng point : points)
        {
            boundsBuilder.include(point);
        }
        LatLngBounds bounds = boundsBuilder.build();

        return CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_BOUND_PADDING);
    }

    /**
     * Currently calculates center based on whether booking is a proxy
     *
     * @return LatLng
     */
    private LatLng getCenterPoint()
    {
        //Currently even for unclaimed booking we get full address information so we are going to use lat/lng on address regardless of status
        if (!mBooking.isProxy())
        {
            return new LatLng(mBooking.getAddress().getLatitude(), mBooking.getAddress().getLongitude());
        }
        else if (mPolygons != null)
        {
            return new LatLng(mPolygons.getCenter().latitude, mPolygons.getCenter().longitude);
        }
        else if (mBooking.getMidpoint() != null)
        {
            return new LatLng(mBooking.getMidpoint().getLatitude(), mBooking.getMidpoint().getLongitude());
        }
        else
        {
            //fallback so we don't crash
            Crashlytics.logException(new Exception("BookingMapView booking has no valid midpoint " + mBooking.getId()));
            return new LatLng(0.0f, 0.0f);
        }
    }

    private float getRadius()
    {
        return mBooking.getRadius() > 0 ? mBooking.getRadius() : DEFAULT_RADIUS_METERS;
    }

    private static void showPolygon(GoogleMap map, LatLng[][] polygons)
    {
        for (LatLng[] polygon : polygons)
        {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.add(polygon);
            polygonOptions.strokeWidth(MAP_POLYGON_STROKE_WIDTH);
            polygonOptions.strokeColor(MAP_POLYGON_STROKE_COLOR);
            polygonOptions.fillColor(MAP_POLYGON_FILL_COLOR);
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


    // This is used to disable the scrolling of the scroll view so we can scroll our map
    public class TouchableWrapper extends FrameLayout
    {
        private ScrollView mScrollView;

        public TouchableWrapper(Context context, ScrollView scrollView)
        {
            super(context);
            mScrollView = scrollView;
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
