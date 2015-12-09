package com.handy.portal.ui.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PriceMarkerInactive extends FrameLayout
{
    @InjectView(R.id.marker_text)
    TextView mMarkerText;

    public PriceMarkerInactive(final Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.price_marker_inactive, this);
        ButterKnife.inject(this);
    }

    public void setText(CharSequence text) { mMarkerText.setText(text); }

}
