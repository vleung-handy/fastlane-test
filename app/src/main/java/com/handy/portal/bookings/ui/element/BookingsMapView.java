package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.location.LocationUtils;
import com.handy.portal.model.ZipClusterPolygons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class BookingsMapView extends MapView implements OnMapReadyCallback
{
    private static final int DEFAULT_BOUND_PADDING = 15;

    private static final int MAP_POLYGON_STROKE_WIDTH = 3;
    private static final int MAP_POLYGON_STROKE_COLOR = 0XFFD1D1D1; //Can not store in colors.xml, colors.xml doesn't use alpha correctly
    private static final int MAP_POLYGON_FILL_COLOR = 0x80FF5C5C; //Can not store in colors.xml, colors.xml doesn't use alpha correctly

    private BookingsMapListener mBookingsMapListener;

    public void setBookingsMapListener(BookingsMapListener mapReadyListener)
    {
        mBookingsMapListener = mapReadyListener;
        getMapAsync(this);
    }

    public BookingsMapView(final Context context)
    {
        super(context);
    }

    public BookingsMapView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingsMapView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public BookingsMapView(final Context context, final GoogleMapOptions options)
    {
        super(context, options);
    }

    GoogleMap mMap;

    @Override
    @SuppressWarnings({"ResourceType", "MissingPermission"})
    public void onMapReady(GoogleMap map)
    {
        mMap = map;
        if (!LocationUtils.hasRequiredLocationPermissions(getContext()))
        {
            return;
        }
        map.setMyLocationEnabled(true);

        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener()
        {
            @Override
            public void onPolygonClick(final Polygon polygon)
            {
                Log.i("", "on polygon click: " + polygon.getId());
                List<Booking> associatedBookings = getBookingId(polygon);
                mBookingsMapListener.onZipClusterPolygonClicked(associatedBookings);
            }
        });


        //fixme hacky way of effectively propagating click to underneath marker
//        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(final Marker marker)
//            {
//                List<Booking> associatedBookings = getBookingId(marker);
//                mBookingsMapListener.onZipClusterPolygonClicked(associatedBookings);
//                return true;
//            }
//        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker)
            {
                List<Booking> associatedBookings = getBookingId(marker);
                mBookingsMapListener.onZipClusterPolygonClicked(associatedBookings);
            }
        });
        mBookingsMapListener.onMapReady();
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

    public void positionCamera(List<LatLng> points)
    {
        positionCamera(mMap, points);
    }

    private void moveCamera(final @NonNull GoogleMap map, final CameraUpdate cameraUpdate)
    {
        map.moveCamera(cameraUpdate);

//        if (booking.getAction(Booking.Action.ACTION_CHECK_IN) != null
//                && map.getCameraPosition().zoom > CHECK_IN_DEFAULT_ZOOM_LEVEL)
//        {
//            map.moveCamera(CameraUpdateFactory.zoomTo(CHECK_IN_DEFAULT_ZOOM_LEVEL));
//        }
//        else if (map.getCameraPosition().zoom > ON_MY_WAY_DEFAULT_ZOOM_LEVEL)
//        {
//        map.moveCamera(CameraUpdateFactory.zoomTo(1));
//        }
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

    private LatLng getCenterPoint(ZipClusterPolygons.Coordinates coordinates)//fixme hack
    {
        return new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
    }

    Random rand = new Random();
    List<LatLng> latLngs = new ArrayList<>();

    public Marker drawMapMarker(LatLng[] polygon)
    {
        LatLng center = getCenter(polygon);

        //todo get nicer icon with good size
        //todo only create this once
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.img_bling);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable.getCurrent();
        Bitmap b=bitmapDrawable.getBitmap();
        Bitmap scaledBitmap =Bitmap.createScaledBitmap(b, 64, 64, false);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);


        MarkerOptions markerOptions = new MarkerOptions().position(center)
                .draggable(false)
                .title("You're requested here!")
                .icon(icon)
                ;
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
        return marker;
    }

    /**
     * todo if we want to make sure it's inside (ex. for concave polygons)
     * one obvious way to do it is just get the largest convex polygon subset
     * and calculate this from those points
     *
     * but not doing the above for now and just doing something super simple
     * @param polygon
     * @return
     */
    private LatLng getCenter(LatLng[] polygon)
    {
        double totalLat = 0;
        double totalLong = 0;
        for(int i = 0; i<polygon.length; i++)
        {
            LatLng l = polygon[i];
            totalLat+=l.latitude;
            totalLong+=l.longitude;
        }
        totalLat = totalLat/polygon.length;
        totalLong = totalLong/polygon.length;
        return new LatLng(totalLat, totalLong);
    }

    public void drawPolygon(ZipClusterPolygons zipClusterPolygons, Booking booking)//fixme hacky
    {
        if (polygonToBookingIdsMap.get(zipClusterPolygons) != null)
        {
            //prevent multiple polygons from being drawn
            polygonToBookingIdsMap.get(zipClusterPolygons).add(booking);
            return;
        }
        List<Booking> bookingIds = new ArrayList<>();
        bookingIds.add(booking);
        polygonToBookingIdsMap.put(zipClusterPolygons, bookingIds);

        LatLng[][] polygons = zipClusterPolygons.getOutlines();

        int maxIntensity = 210;
        int color = Color.argb(180, rand.nextInt(maxIntensity), rand.nextInt(maxIntensity), rand.nextInt(maxIntensity));
        boolean marked = false;
        for (LatLng[] polygon : polygons)
        {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.add(polygon);
            polygonOptions.strokeWidth(MAP_POLYGON_STROKE_WIDTH);
            polygonOptions.strokeColor(MAP_POLYGON_STROKE_COLOR);

            polygonOptions.fillColor(color);
            Polygon googlePolygon = mMap.addPolygon(polygonOptions);
            googlePolygon.setClickable(true);
            polygonIdToZipPolygonMap.put(googlePolygon.getId(), zipClusterPolygons);
            if(booking.isRequested() && !marked)
            {
                Marker marker = drawMapMarker(polygon);
                markerIdToZipPolygonMap.put(marker.getId(), zipClusterPolygons);
                marked = true;
            }
        }

        for (int i = 0; i < polygons.length; i++)
        {
            for (int j = 0; j < polygons[i].length; j++)
            {
                latLngs.add(polygons[i][j]);
            }
        }
        positionCamera(latLngs);
    }

    Map<String, ZipClusterPolygons> polygonIdToZipPolygonMap = new HashMap<>();
    Map<String, ZipClusterPolygons> markerIdToZipPolygonMap = new HashMap<>();//fixme hacky way of effectively propagating click to underneath marker
    Map<ZipClusterPolygons, List<Booking>> polygonToBookingIdsMap = new HashMap<>();

    public interface BookingsMapListener
    {
        void onMapReady();
        void onZipClusterPolygonClicked(List<Booking> associatedBookings);
    }

    private List<Booking> getBookingId(Polygon polygon)
    {
        ZipClusterPolygons zipClusterPolygons = polygonIdToZipPolygonMap.get(polygon.getId());
        return polygonToBookingIdsMap.get(zipClusterPolygons);
    }

    private List<Booking> getBookingId(Marker marker)
    {
        ZipClusterPolygons zipClusterPolygons = markerIdToZipPolygonMap.get(marker.getId());
        return polygonToBookingIdsMap.get(zipClusterPolygons);
    }
}
