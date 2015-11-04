package com.handy.portal.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProxyLocationView extends FrameLayout
{
    @InjectView(R.id.job_location_title)
    TextView jobLocationTitleTextView;

    @InjectView(R.id.job_location)
    TextView jobLocationTextView;

    @InjectView(R.id.nearby_transit_title)
    TextView nearbyTransitTextView;

    @InjectView(R.id.nearby_transits)
    LinearLayout nearbyTransits;

    private Booking.ZipCluster mZipCluster;

    public ProxyLocationView(Context context, Booking.ZipCluster zipCluster)
    {
        super(context);
        mZipCluster = zipCluster;
        init();
    }

    public ProxyLocationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProxyLocationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ProxyLocationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init()
    {
        if (mZipCluster == null || mZipCluster.getTransitDescription().isEmpty() && mZipCluster.getLocationDescription().isEmpty())
        {
            setVisibility(GONE);
            return;
        }

        inflate(getContext(), R.layout.element_booking_details_proxy_location, this);
        ButterKnife.inject(this);

        setJobLocation();
        setNearbyTransit();
    }

    private void setJobLocation()
    {
        if (mZipCluster.getLocationDescription() == null || mZipCluster.getLocationDescription().isEmpty())
        {
            jobLocationTitleTextView.setVisibility(GONE);
            jobLocationTextView.setVisibility(GONE);
        }
        else
        {
            jobLocationTextView.setText(mZipCluster.getLocationDescription());
        }

    }

    private void setNearbyTransit()
    {
        if (mZipCluster.getTransitDescription() == null || mZipCluster.getTransitDescription().isEmpty())
        {
            nearbyTransitTextView.setVisibility(GONE);
        }
        else
        {
            List<String> transitsMarkers = mZipCluster.getTransitDescription();
            for (String transitMarker : transitsMarkers)
            {
                RoundedTextView transitMarkerView = new RoundedTextView(getContext());
                transitMarkerView.setText(transitMarker);
                nearbyTransits.addView(transitMarkerView);
            }
        }
    }
}
