package com.handy.portal.bookings.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The banner view that is located at the top of the bookings list
 */
public class BookingsBannerView extends LinearLayout
{
    @BindView(R.id.layout_bookings_banner_title)
    TextView mBannerTitleText;

    @BindView(R.id.layout_bookings_banner_description)
    TextView mBannerDescriptionText;

    @BindView(R.id.layout_bookings_banner_left_image)
    ImageView mBannerLeftImage;

    @BindView(R.id.layout_bookings_banner_content)
    View mBannerContent;

    public BookingsBannerView(final Context context)
    {
        super(context);
        init();
    }

    public BookingsBannerView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BookingsBannerView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingsBannerView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.layout_bookings_banner, this);
        ButterKnife.bind(this);
    }

    public BookingsBannerView setTitleText(String titleText)
    {
        mBannerTitleText.setText(titleText);
        return this;
    }

    public BookingsBannerView setDescriptionText(String descriptionText)
    {
        mBannerDescriptionText.setText(descriptionText);
        return this;
    }

    public BookingsBannerView setLeftDrawable(Drawable drawable)
    {
        mBannerLeftImage.setImageDrawable(drawable);
        return this;
    }

    /**
     * need this for a hack to make this banner scroll with a listview
     * @param visible
     * @return
     */
    public BookingsBannerView setContentVisible(boolean visible)
    {
        mBannerContent.setVisibility(visible ? VISIBLE : GONE);
        return this;
    }
}
