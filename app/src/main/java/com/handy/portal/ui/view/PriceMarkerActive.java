package com.handy.portal.ui.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PriceMarkerActive extends FrameLayout
{
    @InjectView(R.id.marker_text)
    TextView mMarkerText;

    public PriceMarkerActive(final Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.price_marker_active, this);
        ButterKnife.inject(this);
    }

    public void setText(CharSequence text) { mMarkerText.setText(text); }

}
