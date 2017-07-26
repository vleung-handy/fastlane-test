package com.handy.portal.clients.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.handy.portal.R;

/**
 * Created by sng on 7/26/17.
 */

public class ClientMapView extends MapView implements OnMapReadyCallback {
    private LatLng mLatLng;

    public ClientMapView(final Context context, final GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    public static GoogleMapOptions getDefaultClientGoogleMapOptions() {
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .liteMode(true);

        return options;
    }

    public void getMapAsync(@NonNull LatLng latLng) {
        super.getMapAsync(this);
        mLatLng = latLng;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (mLatLng == null) {
            setVisibility(View.INVISIBLE);
            return;
        }
        else {
            setVisibility(View.VISIBLE);
        }

        CameraUpdate center = CameraUpdateFactory.newLatLng(mLatLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        // In meters
        CircleOptions circleOptions = new CircleOptions()
                .center(mLatLng)
                .radius(300)
                .strokeColor(ContextCompat.getColor(getContext(), R.color.light_gray_trans))
                .strokeWidth(5)
                .fillColor(ContextCompat.getColor(getContext(), R.color.handy_blue_trans_10));

        // Get back the mutable Circle
        Circle circle = googleMap.addCircle(circleOptions);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
    }
}
