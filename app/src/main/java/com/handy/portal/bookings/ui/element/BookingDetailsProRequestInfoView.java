package com.handy.portal.bookings.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * the view in the booking details that displays
 * a message title/body using the booking's pro request display attributes model
 */
public class BookingDetailsProRequestInfoView extends FrameLayout {
    @BindView(R.id.booking_details_display_message_favorite_indicator)
    View mMessageFavoriteIndicator;
    @BindView(R.id.booking_details_display_message_swap_indicator)
    View mMessageSwapIndicator;
    @BindView(R.id.booking_details_display_message_title)
    TextView mMessageTitleText;
    @BindView(R.id.booking_details_display_message_body_text)
    TextView mMessageBodyText;

    public BookingDetailsProRequestInfoView(final Context context) {
        super(context);
        init();
    }

    public BookingDetailsProRequestInfoView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BookingDetailsProRequestInfoView(final Context context, final AttributeSet attrs,
                                            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingDetailsProRequestInfoView(final Context context, final AttributeSet attrs,
                                            final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_booking_details_display_message, this);
        ButterKnife.bind(this);
    }

    /**
     * sets the view's message title and body based on the given display model
     *
     * @param requestAttributes
     */
    public void setDisplayModel(Booking.RequestAttributes requestAttributes) {
        //set message title
        if (requestAttributes.getDetailsTitle() != null) {
            mMessageTitleText.setText(requestAttributes.getDetailsTitle());
            mMessageTitleText.setVisibility(VISIBLE);
        }
        else {
            mMessageTitleText.setVisibility(GONE);
        }

        //set message body
        if (requestAttributes.getDetailsBody() != null) {
            mMessageBodyText.setText(requestAttributes.getDetailsBody());
            mMessageBodyText.setVisibility(VISIBLE);
        }
        else {
            mMessageBodyText.setVisibility(GONE);
        }
    }

    public void showSwapIndicator() {
        mMessageSwapIndicator.setVisibility(VISIBLE);
    }

    public void showFavoriteIndicator() {
        mMessageFavoriteIndicator.setVisibility(VISIBLE);
    }
}
