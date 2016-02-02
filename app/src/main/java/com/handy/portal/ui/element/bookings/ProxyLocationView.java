package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.view.RoundedTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProxyLocationView extends FrameLayout
{
    @Bind(R.id.job_location_title)
    TextView jobLocationTitleTextView;

    @Bind(R.id.job_location)
    TextView jobLocationTextView;

    @Bind(R.id.nearby_transit_title)
    TextView nearbyTransitTextView;

    @Bind(R.id.nearby_transits)
    LinearLayout nearbyTransits;

    public ProxyLocationView(Context context)
    {
        super(context);
        init();
    }

    public ProxyLocationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ProxyLocationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public ProxyLocationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void refreshDisplay(@NonNull Booking booking)
    {
        if (!booking.isProxy() || booking.getZipCluster() == null ||
                booking.getZipCluster().getTransitDescription() == null ||
                booking.getZipCluster().getLocationDescription() == null ||
                booking.getZipCluster().getTransitDescription().isEmpty()
                        && booking.getZipCluster().getLocationDescription().isEmpty())
        {
            setVisibility(GONE);
        }
        else
        {
            setVisibility(VISIBLE);
            setJobLocation(booking.getZipCluster());
            setNearbyTransit(booking.getZipCluster());
        }
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_details_proxy_location, this);
        ButterKnife.bind(this);
    }

    private void setJobLocation(Booking.ZipCluster zipCluster)
    {
        if (zipCluster.getLocationDescription() == null || zipCluster.getLocationDescription().isEmpty())
        {
            jobLocationTitleTextView.setVisibility(GONE);
            jobLocationTextView.setVisibility(GONE);
        }
        else
        {
            jobLocationTextView.setText(zipCluster.getLocationDescription());
        }

    }

    private void setNearbyTransit(Booking.ZipCluster zipCluster)
    {
        if (zipCluster.getTransitDescription() == null || zipCluster.getTransitDescription().isEmpty())
        {
            nearbyTransitTextView.setVisibility(GONE);
        }
        else
        {
            List<String> transitsMarkers = zipCluster.getTransitDescription();
            for (String transitMarker : transitsMarkers)
            {
                RoundedTextView transitMarkerView = new RoundedTextView(getContext());
                transitMarkerView.setText(transitMarker);
                nearbyTransits.addView(transitMarkerView);
            }
        }
    }
}
