package com.handy.portal.ui.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PriceMarker extends FrameLayout
{
    @InjectView(R.id.marker_text)
    TextView mMarkerText;

    private boolean mActive = false;

    public PriceMarker(final Context context, boolean isActive)
    {
        super(context);
        mActive = isActive;
        init();
    }

    private void init()
    {
        if (mActive)
        {
            inflate(getContext(), R.layout.price_marker_active, this);
        }
        else
        {
            inflate(getContext(), R.layout.price_marker_inactive, this);
        }
        ButterKnife.inject(this);
    }

    public void setText(CharSequence text) { mMarkerText.setText(text); }
}
