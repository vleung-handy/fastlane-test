package com.handy.portal.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

public class ProxyLocationView extends FrameLayout
{
    public ProxyLocationView(Context context, String jobLocation, String nearbyTransit)
    {
        super(context);
        init(jobLocation, nearbyTransit);
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

    private void init(String jobLocation, String nearbyTransit)
    {
        inflate(getContext(), R.layout.element_booking_details_proxy_location, this);

        TextView jobLocationTitleTextView = (TextView) findViewById(R.id.job_location_title);
        TextView jobLocationTextView = (TextView) findViewById(R.id.job_location);
        TextView nearbyTransitTextView = (TextView) findViewById(R.id.nearby_transit_title);
        LinearLayout nearbyTransits = (LinearLayout) findViewById(R.id.nearby_transits);

        if (jobLocation == null || jobLocation.isEmpty())
        {
            jobLocationTitleTextView.setVisibility(GONE);
            jobLocationTextView.setVisibility(GONE);
        }
        else
        {
            jobLocationTextView.setText(jobLocation);
        }

        if (nearbyTransit == null || nearbyTransit.isEmpty())
        {
            nearbyTransitTextView.setVisibility(GONE);
        }
        else
        {
            String[] transits = nearbyTransit.split(",");
            for (String transit : transits)
            {
                RoundedTextView transitView = new RoundedTextView(getContext());
                transitView.setText(transit);
                nearbyTransits.addView(transitView);
            }
        }
    }

}
