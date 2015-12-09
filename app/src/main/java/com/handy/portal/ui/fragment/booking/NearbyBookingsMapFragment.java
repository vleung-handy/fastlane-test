package com.handy.portal.ui.fragment.booking;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.ui.view.PriceMarkerActive;
import com.handy.portal.ui.view.PriceMarkerInactive;
import com.handy.portal.util.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NearbyBookingsMapFragment extends SupportMapFragment implements OnMapReadyCallback
{
    private static final int DEFAULT_ZOOM_LEVEL = 14;

    private List<Booking> mBookings;
    private LatLng mCenter;
    private HashMap<Marker, Booking> mMarkerBookingMap = new HashMap<>();
    private MarkerClickedCallback mMarkerClickedCallback;
    private PriceMarkerInactive mPriceMarkerInactive;
    private PriceMarkerActive mPriceMarkerActive;


    public interface MarkerClickedCallback
    {
        void markerClicked(Booking booking);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mMarkerClickedCallback = (MarkerClickedCallback) getParentFragment();
        mPriceMarkerInactive = new PriceMarkerInactive(getContext());
        mPriceMarkerActive = new PriceMarkerActive(getContext());
    }

    public static NearbyBookingsMapFragment newInstance(ArrayList<Booking> bookings, LatLng center)
    {
        NearbyBookingsMapFragment fragment = new NearbyBookingsMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKINGS, bookings);
        args.putParcelable(BundleKeys.MAP_CENTER, center);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBookings = (ArrayList<Booking>) getArguments().getSerializable(BundleKeys.BOOKINGS);
        mCenter = getArguments().getParcelable(BundleKeys.MAP_CENTER);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        map.setMyLocationEnabled(true);

        addCustomMarkers(map);

        CameraPosition targetCameraPosition =
                new CameraPosition.Builder().target(mCenter).zoom(DEFAULT_ZOOM_LEVEL).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(targetCameraPosition);
        map.moveCamera(cameraUpdate);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                clickMarker(marker);
                return true;
            }
        });
    }

    private void addCustomMarkers(GoogleMap map)
    {
        for (int i = 0; i < mBookings.size(); ++i)
        {
            Booking booking = mBookings.get(i);
            Address address = booking.getAddress();
            PaymentInfo paymentInfo = booking.getPaymentToProvider();
            if (address != null)
            {
                mPriceMarkerInactive.setText(
                        paymentInfo.getCurrencySymbol() + paymentInfo.getAdjustedAmount());
                MarkerOptions markerOption = new MarkerOptions()
                        .position(new LatLng(address.getLatitude(), address.getLongitude()));
                Marker marker = map.addMarker(markerOption);
                mMarkerBookingMap.put(marker, booking);
                if (i == 0) { clickMarker(marker); }
            }
        }
    }

    private void clickMarker(final Marker marker)
    {
        resetIcons(marker);
        mMarkerClickedCallback.markerClicked(mMarkerBookingMap.get(marker));
    }

    private void resetIcons(final Marker marker)
    {
        Set<Map.Entry<Marker, Booking>> entries = mMarkerBookingMap.entrySet();
        for (Map.Entry<Marker, Booking> entry : entries)
        {
            PaymentInfo paymentInfo = entry.getValue().getPaymentToProvider();
            setIcon(entry.getKey(), entry.getKey() == marker,
                    paymentInfo.getCurrencySymbol() + paymentInfo.getAdjustedAmount());
        }
    }

    private void setIcon(final Marker marker, final boolean active, final String label)
    {
        mPriceMarkerInactive.setText(label);
        if (active)
        {
            mPriceMarkerActive.setText(label);
            marker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(UIUtils.createDrawableFromView(getContext(), mPriceMarkerActive)));
        }
        else
        {
            mPriceMarkerInactive.setText(label);
            marker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(UIUtils.createDrawableFromView(getContext(), mPriceMarkerInactive)));
        }
    }
}
