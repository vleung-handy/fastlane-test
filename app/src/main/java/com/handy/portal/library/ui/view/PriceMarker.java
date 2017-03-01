package com.handy.portal.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PriceMarker extends FrameLayout {
    @BindView(R.id.marker_text)
    TextView mMarkerText;

    private boolean mActive = false;

    public PriceMarker(final Context context) {
        super(context);
    }

    public PriceMarker(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public PriceMarker(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PriceMarker(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PriceMarker(final Context context, boolean isActive) {
        super(context);
        mActive = isActive;
        init();
    }

    private void init() {
        if (mActive) {
            inflate(getContext(), R.layout.price_marker_active, this);
        }
        else {
            inflate(getContext(), R.layout.price_marker_inactive, this);
        }
        ButterKnife.bind(this);
    }

    public void setText(CharSequence text) { mMarkerText.setText(text); }
}
